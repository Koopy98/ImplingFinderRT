package com.hablapatabla.implingfinder;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hablapatabla.implingfinder.model.ImplingFinderData;
import com.hablapatabla.implingfinder.model.ImplingFinderEnum;
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


    // The fallback project only ever carries these 5 types - Magpie, Ninja,
    // Dragon, Lucky, Crystal. Same set as before (this used to gate what
    // went to Oracle), kept identical on purpose: it's still the right
    // subset - rare/high-value enough to be worth a backup copy, small
    // enough that a free-tier project stays nowhere near its own limits.
    private static boolean isFallbackCompatible(int npcid) {
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

                // Supabase is the primary write target for everyone, for all 12
                // types. The fallback project is written to unconditionally and in
                // parallel for the 5 compatible types (not only when the primary
                // write fails) - continuous real traffic is what keeps that
                // project's free tier from auto-pausing, and it means the fallback
                // is never stale if it's ever actually needed. The other 7 types
                // still have no fallback destination during a Supabase outage - a
                // known, accepted gap rather than a silent one.
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
                        logger.error("Supabase write failed", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (!response.isSuccessful()) {
                                logger.error("Supabase write unsuccessful (" + response.code() + ")");
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

                if (isFallbackCompatible(data.getNpcid())) {
                    writeToFallbackSupabase(json);
                }
            }
        } catch (Exception e) {
            logger.error("Outer catch block POST ", e);
        }
        plugin.getImplingsToUpload().clear();
    }

    /**
     * Unconditional dual-write to the fallback Supabase project, for the 5
     * compatible types only (see isFallbackCompatible). Fires alongside the
     * primary write every time, not just on failure - this is what keeps
     * the fallback project's own free tier from auto-pausing on inactivity,
     * and means it's already warm and current if it's ever actually needed
     * for a read.
     */
    private void writeToFallbackSupabase(String json) {
        Request fallbackRequest = new Request.Builder()
                .url(ImplingFinderPlugin.implingFallbackPostEndpoint)
                .addHeader(CONTENT, JSON)
                .addHeader("apikey", ImplingFinderPlugin.FALLBACK_SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + ImplingFinderPlugin.FALLBACK_SUPABASE_ANON_KEY)
                .addHeader("Prefer", "return=minimal")
                .post(RequestBody.create(JSONTYPE, json))
                .build();

        okHttpClient.newCall(fallbackRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.error("Fallback Supabase write failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful())
                        logger.error("Fallback Supabase write unsuccessful (" + response.code() + ")");
                }
                catch (Exception e) {
                    logger.error("Error handling fallback Supabase write response", e);
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

        // Supabase is the primary read source for everyone, for all 12
        // types. The fallback project is only queried if the primary read
        // genuinely fails (network/HTTP error) - an empty result is not a
        // failure, it just means nothing's been found recently, and does
        // not trigger a fallback. The fallback only ever has the 5
        // compatible types anyway, since that's all it's ever written.
        try {
            List<ImplingFinderData> supabaseResults = fetchRecent(ImplingFinderPlugin.implingSupabaseGetEndpoint, ImplingFinderPlugin.SUPABASE_ANON_KEY);
            implings = new ArrayList<>();
            for (ImplingFinderData data : supabaseResults) {
                if (wantsAny || ids.contains(data.getNpcid())) {
                    implings.add(data);
                }
            }
        } catch (Exception e) {
            logger.error("Supabase read failed, falling back to backup Supabase project", e);
            implings = new ArrayList<>();
            try {
                List<ImplingFinderData> fallbackResults = fetchRecent(ImplingFinderPlugin.implingFallbackGetEndpoint, ImplingFinderPlugin.FALLBACK_SUPABASE_ANON_KEY);
                for (ImplingFinderData data : fallbackResults) {
                    if (wantsAny || ids.contains(data.getNpcid())) {
                        implings.add(data);
                    }
                }
            } catch (Exception fallbackException) {
                logger.error("Fallback Supabase read also failed", fallbackException);
            }
        }

        Collections.sort(implings, Collections.reverseOrder());
        if (implings.size() > 25)
            implings = implings.subList(0, 25);
        return implings;
    }

    /**
     * Fetches the current contents of an implings_recent view - a
     * restricted, read-only view that only ever exposes sightings from the
     * last 10 minutes. Used for both the primary and fallback Supabase
     * projects, since they're both PostgREST and both expose the same
     * bare-JSON-array shape - the only difference is which URL/key gets
     * passed in. Neither anon key has read access to its own raw implings
     * table, only this view.
     */
    private List<ImplingFinderData> fetchRecent(String endpoint, String apiKey) throws Exception {
        Request request = new Request.Builder()
                .url(endpoint + "?select=*")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Recent-implings request unsuccessful: " + response.code());
                return Collections.emptyList();
            }
            String body = response.body().string();
            Type listType = new TypeToken<List<ImplingFinderData>>() {}.getType();
            List<ImplingFinderData> result = getGson().fromJson(body, listType);
            return result != null ? result : Collections.emptyList();
        }
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
