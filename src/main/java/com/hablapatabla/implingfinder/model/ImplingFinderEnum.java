package com.hablapatabla.implingfinder.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;
@Getter
@AllArgsConstructor
public enum ImplingFinderEnum {
    BABY(1635, "Baby impling"),
    YOUNG(1636, "Young impling"),
    GOURMET(1637, "Gourmet impling"),
    EARTH(1638, "Earth impling"),
    ESSENCE(1639, "Essence impling"),
    ECLECTIC(1640, "Eclectic impling"),
    NATURE(1641, "Nature impling"),
    MAGPIE(1642, "Magpie impling"),
    NINJA(1643, "Ninja impling"),
    DRAGON(1644, "Dragon impling"),
    LUCKY(7233, "Lucky impling"),
    CRYSTAL(8741, "Crystal impling"),
    RECENT(-1, "Recent");
    private int npcId;
    private String name;

    // Notable drop info per type, single source of truth used by both the
    // filter button tooltips and the Drops reference panel. Clue tiers and
    // rates verified against the OSRS Wiki. Where the wiki confirms a clue
    // tier exists but doesn't give a clean numeric rate (Baby, Young, Earth,
    // Dragon), that's stated plainly rather than a guessed number.
    public static final Map<String, String> DROP_INFO = new HashMap<String, String>() {{
        put("Baby",     "Clue: Beginner (rate not listed by Jagex)");
        put("Young",    "Clue: Beginner (better odds than Baby)");
        put("Gourmet",  "Clue: Easy (1/25)");
        put("Earth",    "Clue: Medium (rate not listed by Jagex)");
        put("Essence",  "Clue: Medium (1/50)");
        put("Eclectic", "Clue: Medium (1/25) - best medium clue farming method (Ranger boots)");
        put("Nature",   "Clue: Hard (1/100)");
        put("Magpie",   "Clue: Hard (1/50)");
        put("Ninja",    "Clue: Hard (1/25)");
        put("Crystal",  "No clue scrolls. Crystal shards + crystal acorns, not GE-tradeable");
        put("Dragon",   "Clue: Elite (only impling source of elite clues)");
        put("Lucky",    "Equal-odds roll into easy/medium/hard/elite/master tables (no master clue or bloodhound possible)");
    }};

    private static final Map<Integer, ImplingFinderEnum> map;
    static {
        map = new HashMap<>();
        for (ImplingFinderEnum e : ImplingFinderEnum.values())  {
            map.put(e.npcId, e);
        }
        // Jar variant NPC IDs map to the same impling type
        map.put(1645, BABY);
        map.put(1646, YOUNG);
        map.put(1647, GOURMET);
        map.put(1648, EARTH);
        map.put(1649, ESSENCE);
        map.put(1650, ECLECTIC);
        map.put(1651, NATURE);
        map.put(1652, MAGPIE);
        map.put(1653, NINJA);
        map.put(1654, DRAGON);
        map.put(7302, LUCKY);
        map.put(8742, CRYSTAL);
        map.put(8743, CRYSTAL);
        map.put(8744, CRYSTAL);
        map.put(8745, CRYSTAL);
        map.put(8746, CRYSTAL);
        map.put(8747, CRYSTAL);
        map.put(8748, CRYSTAL);
        map.put(8749, CRYSTAL);
        map.put(8750, CRYSTAL);
        map.put(8751, CRYSTAL);
        map.put(8752, CRYSTAL);
        map.put(8753, CRYSTAL);
        map.put(8754, CRYSTAL);
        map.put(8755, CRYSTAL);
        map.put(8756, CRYSTAL);
        map.put(8757, CRYSTAL);
    }
    public static ImplingFinderEnum findById(int id) {
        return map.get(id);
    }
    public static int getIdByNameFuzzy(String name) {
        for (ImplingFinderEnum imp : values()) {
            if (imp.name.contains(name))
                return imp.npcId;
        }
        return -1;
    }
    public static int getIdByNameStrict(String name) {
        for (ImplingFinderEnum imp : values()) {
            if (name.contains(imp.name))
                return imp.npcId;
        }
        return -1;
    }
}
