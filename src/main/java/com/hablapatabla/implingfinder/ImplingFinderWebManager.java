package com.hablapatabla.implingfinder;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hablapatabla.implingfinder.model.ImplingFinderData;
import com.hablapatabla.implingfinder.model.ImplingFinderEnum;
import lombok.Value;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;


@Singleton
public class ImplingFinderWebManager {
    protected static final String CONTENT = "Content-Type";
    protected static final String JSON = "application/json";
    private static final MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private ImplingFinderPlugin plugin;

    @Inject
    private ImplingFinderConfig config;

    @Inject
    private GsonBuilder gsonBuilder;

    private Logger logger = LoggerFactory.getLogger(ImplingFinderWebManager.class);


    // Oracle's schema only accepts these 5 original types (confirmed via HTTP 400 on
    // everything else). Sending the newly-tracked 7 types to Oracle would just waste one
    // of its severely limited (3-6 total) concurrent connection slots on a guaranteed
    // rejection — so those are filtered out of the Oracle POST specifically. They still
    // go to Supabase for anyone with dual-write enabled, since Supabase accepts all 12.
    private static boolean isOracleCompatible(int npcid) {
        switch (npcid) {
            case 1642: case 1652: // Magpie
            case 1643: case 1653: // Ninja
            case 1644: case 1654: // Dragon
            case 7233: case 7302: // Lucky
                return true;
            default:
                return npcid == 8741 || (npcid >= 8742 && npcid <= 8757); // Crystal + jar-colour variants
        }
    }

    protected void postImplings() {
        try {
            // Copy the list before iterating to avoid ConcurrentModificationException —
            // the client thread may add new implings while the scheduler thread is
            // iterating.
            List<ImplingFinderData> toUpload = new ArrayList<>(plugin.getImplingsToUpload());
            for (ImplingFinderData data : toUpload) {
                String json = getGson().toJson(data);

                // Supabase is now the primary write target for everyone, for all 12
                // types. Oracle is only ever written to as a fallback, and only if
                // the Supabase write actually fails - not as a parallel write like
                // the old dual-write testing setup. Oracle's own schema still only
                // accepts the original 5 types (a hard CHECK constraint on their
                // end, not something this plugin can work around), so the other 7
                // simply have no fallback destination during a Supabase outage -
                // a known, accepted gap rather than a silent one.
                Request supabaseRequest = new Request.Builder()
                        .url(ImplingFinderPlugin.implingSupabasePostEndpoint)
                        .addHeader(CONTENT, JSON)
                        .addHeader("apikey", ImplingFinderPlugin.SUPABASE_ANON_KEY)
                        .addHeader("Authorization", "Bearer " + ImplingFinderPlugin.SUPABASE_ANON_KEY)
                        .addHeader("Prefer", "return=minimal")
                        .post(RequestBody.create(JSONTYPE, json))
                        .build();

                okHttpClient.newCall(supabaseRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.error("Supabase write failed, falling back to Oracle", e);
                        writeToOracleFallback(data, json);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (!response.isSuccessful()) {
                                logger.error("Supabase write unsuccessful (" + response.code() + "), falling back to Oracle");
                                writeToOracleFallback(data, json);
                            }
                        }
                        catch (Exception e) {
                            logger.error("Error handling Supabase write response", e);
                        }
                        finally {
                            response.close();
                        }
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Outer catch block POST ", e);
        }
        plugin.getImplingsToUpload().clear();
    }

    /**
     * Oracle fallback write, only triggered when the primary Supabase write
     * fails. Only attempted for the 5 types Oracle's own schema actually
     * accepts (see isOracleCompatible) - attempting the other 7 would just
     * waste a request on a guaranteed rejection.
     */
    private void writeToOracleFallback(ImplingFinderData data, String json) {
        if (!isOracleCompatible(data.getNpcid())) {
            return;
        }

        Request oracleRequest = new Request.Builder()
                .url(ImplingFinderPlugin.implingPostEndpoint)
                .addHeader(CONTENT, JSON)
                .post(RequestBody.create(JSONTYPE, json))
                .build();

        okHttpClient.newCall(oracleRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.error("Oracle fallback write also failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful())
                        logger.error("Oracle fallback write unsuccessful");
                }
                catch (Exception e) {
                    logger.error("Error handling Oracle fallback response", e);
                }
                finally {
                    response.close();
                }
            }
        });
    }

    protected List<ImplingFinderData> getData(List<Integer> ids) {
        boolean wantsAny = ids.contains(ImplingFinderPlugin.RECENT_IMPLINGS_ID);
        List<ImplingFinderData> implings;

        // Supabase is now the primary read source for everyone, for all 12
        // types. Oracle is only queried as a fallback, and only if the
        // Supabase read genuinely fails (network/HTTP error) - an empty
        // result from Supabase is not a failure, it just means nothing's
        // been found recently, and does not trigger a fallback.
        try {
            List<ImplingFinderData> supabaseResults = fetchSupabaseRecent();
            implings = new ArrayList<>();
            for (ImplingFinderData data : supabaseResults) {
                if (wantsAny || ids.contains(data.getNpcid())) {
                    implings.add(data);
                }
            }
        } catch (Exception e) {
            logger.error("Supabase read failed, falling back to Oracle", e);
            implings = getDataFromOracle(ids);
        }

        Collections.sort(implings, Collections.reverseOrder());
        if (implings.size() > 25)
            implings = implings.subList(0, 25);
        return implings;
    }

    /**
     * Oracle fallback read, only used when the primary Supabase read fails
     * outright. Same logic the plugin always used before Supabase became
     * primary - separate per-type endpoints, or the "any" endpoint for
     * Recent. Only ever returns the 5 types Oracle's schema supports.
     */
    private List<ImplingFinderData> getDataFromOracle(List<Integer> ids) {
        List<Future<ImplingsWrapper>> futures = new ArrayList<>();
        List<ImplingFinderData> implings = new ArrayList<>();

        for (Integer id : ids) {
            String endpoint;
            if (id != ImplingFinderPlugin.RECENT_IMPLINGS_ID)
                endpoint = ImplingFinderPlugin.implingGetIdEndpoint + Integer.toString(id);
            else
                endpoint = ImplingFinderPlugin.implingGetAnyEndpoint;

            futures.add(fetchAndDeserializeSpecificImpling(endpoint, getGson(), new TypeToken<ImplingsWrapper>() {}));
        }

        try {
            for (Future<ImplingsWrapper> f : futures) {
                implings.addAll(f.get().implings);
            }
        }
        catch (Exception e) {
            logger.error("Error opening Oracle fallback futures", e);
        }

        return implings;
    }

    /**
     * Fetches the current contents of Supabase's implings_recent view - a
     * restricted, read-only view that only ever exposes sightings from the
     * last 10 minutes (see the migration that created it). The anon key has
     * no read access to the raw implings table itself, only this view.
     * Unlike Oracle's ORDS response ({"items": [...]}), PostgREST returns a
     * bare JSON array directly, so this needs its own deserialization path
     * rather than reusing ImplingsWrapper.
     */
    private List<ImplingFinderData> fetchSupabaseRecent() throws Exception {
        Request request = new Request.Builder()
                .url(ImplingFinderPlugin.implingSupabaseGetEndpoint + "?select=*")
                .addHeader("apikey", ImplingFinderPlugin.SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + ImplingFinderPlugin.SUPABASE_ANON_KEY)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Supabase dual-read request unsuccessful: " + response.code());
                return Collections.emptyList();
            }
            String body = response.body().string();
            Type listType = new TypeToken<List<ImplingFinderData>>() {}.getType();
            List<ImplingFinderData> result = getGson().fromJson(body, listType);
            return result != null ? result : Collections.emptyList();
        }
    }


    /**
     * Calls getSpecificImplingResponseAsync in order to get a CompletableFuture containing the
     * full response body from the api at url. Uses a TypeToken to deserialize body, see
     * ImplingsWrapper for only use case. Oracle api response is a full JSON array, with
     * impling data of interest in a JSON object called items.
     */
    private <T> Future<T> fetchAndDeserializeSpecificImpling(String url, Gson gson, TypeToken<T> typeToken) {
        CompletableFuture<String> future = getSpecificImplingResponseAsync(url);
        return future.thenApply(new Function<String, T>() {
            public T apply (String body) {
                return gson.fromJson(body, typeToken.getType());
            }
        });
    }


    private CompletableFuture<String> getSpecificImplingResponseAsync(String url) {
        CompletableFuture<String> future = new CompletableFuture<>();
        Request r = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        future.complete(response.body().string());
                    }
                    else {
                        throw new IOException("Http error");
                    }
                }
                catch (Exception e) {
                    future.completeExceptionally(e);
                }
                finally {
                    response.close();
                }
            }
        });
        return future;
    }

    @Value
    private static class ImplingsWrapper {
        @SerializedName("items")
        List<ImplingFinderData> implings;
    }

    private Gson getGson() {
        return gsonBuilder.registerTypeAdapter(Instant.class, new InstantSecondsConverter()).create();
    }

    /**
     * Serializes/Deserializes {@link Instant} using {@link Instant#getEpochSecond()}/{@link Instant#ofEpochSecond(long)}
     */
    private static class InstantSecondsConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant>
    {
        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.getEpochSecond());
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.ofEpochSecond(json.getAsLong());
        }
    }
}
