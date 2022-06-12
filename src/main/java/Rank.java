import java.util.HashMap;

public class Rank {

    private Ranks rankName;
    private int rankNum;
    private int rankTier;
    private String rankEmoji;

    private static final int UNRATED_MIN = 0;
    private static final int UNRATED_MAX = 2;

    private static final int IRON_OFFSET = 3;
    private static final int BRONZE_OFFSET = 6;
    private static final int SILVER_OFFSET = 9;
    private static final int GOLD_OFFSET = 12;
    private static final int PLAT_OFFSET = 15;
    private static final int DIAMOND_OFFSET = 18;
    private static final int IMMORTAL_OFFSET = 21;
    private static final int RADIANT_OFFSET = 24;


    public static final HashMap<Ranks, String> RANK_EMOJIS = new HashMap<>();

    static {
        RANK_EMOJIS.put(Ranks.UNRATED, "<:rank_unrated:957460254742954055>");
        RANK_EMOJIS.put(Ranks.IRON, "<:rank_iron:957446220861829124>");
        RANK_EMOJIS.put(Ranks.BRONZE, "<:rank_bronze:957446221037994048>");
        RANK_EMOJIS.put(Ranks.SILVER, "<:rank_silver:957446221549690880>");
        RANK_EMOJIS.put(Ranks.GOLD, "<:rank_gold:957446221755195484> ");
        RANK_EMOJIS.put(Ranks.PLAT, "<:rank_plat:957446221201559592>");
        RANK_EMOJIS.put(Ranks.DIAMOND, "<:rank_diamond:957446221025378334>");
        RANK_EMOJIS.put(Ranks.IMMORTAL, "<:rank_immortal:957446221843275896>");
        RANK_EMOJIS.put(Ranks.RADIANT, "<:rank_radiant:957446221541310534>");

    }

    public Rank(int rankNum) {

        if (rankNum >= UNRATED_MIN && rankNum <= UNRATED_MAX) {
            rankName = Ranks.UNRATED;
            rankTier = -1;
        } else if (rankNum >= UNRATED_MIN + IRON_OFFSET && rankNum <= UNRATED_MAX + IRON_OFFSET) {
            rankName = Ranks.IRON;
            rankTier = rankNum - IRON_OFFSET + 1;
        } else if (rankNum >= UNRATED_MIN + BRONZE_OFFSET && rankNum <= UNRATED_MAX + BRONZE_OFFSET) {
            rankName = Ranks.BRONZE;
            rankTier = rankNum - BRONZE_OFFSET + 1;
        } else if (rankNum >= UNRATED_MIN + SILVER_OFFSET && rankNum <= UNRATED_MAX + SILVER_OFFSET) {
            rankName = Ranks.SILVER;
            rankTier = rankNum - SILVER_OFFSET + 1;
        } else if (rankNum >= UNRATED_MIN + GOLD_OFFSET && rankNum <= UNRATED_MAX + GOLD_OFFSET) {
            rankName = Ranks.GOLD;
            rankTier = rankNum - GOLD_OFFSET + 1;
        } else if (rankNum >= UNRATED_MIN + PLAT_OFFSET && rankNum <= UNRATED_MAX + PLAT_OFFSET) {
            rankName = Ranks.PLAT;
            rankTier = rankNum - PLAT_OFFSET + 1;
        } else if (rankNum >= UNRATED_MIN + DIAMOND_OFFSET && rankNum <= UNRATED_MAX + DIAMOND_OFFSET) {
            rankName = Ranks.DIAMOND;
            rankTier = rankNum - DIAMOND_OFFSET + 1;
        } else if (rankNum >= UNRATED_MIN + IMMORTAL_OFFSET && rankNum <= UNRATED_MAX + IMMORTAL_OFFSET) {
            rankName = Ranks.RADIANT;
            rankTier = rankNum - IMMORTAL_OFFSET + 1;
        } else {
            rankName = Ranks.RADIANT;
            rankTier = -1;
        }

        rankEmoji = RANK_EMOJIS.get(rankName);
    }

    public String getRankNameAndTier() {
        if (rankTier == -1) {
            return rankName.toString();
        } else {
            return rankName + " " + rankTier;
        }
    }

    public String getRankEmoji() {
        return RANK_EMOJIS.get(rankName);
    }
}
