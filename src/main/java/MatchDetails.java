public class MatchDetails {

    private int kills;
    private int deaths;
    private int assists;
    private boolean won;
    private String playerName;
    private String tag;


    public MatchDetails(int kills, int deaths, int assists, boolean won, String playerName, String tag) {
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.won = won;
        this.playerName = playerName;
        this.tag = tag;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public double getKD() {
        if (deaths == 0) {
            return (double) kills;
        }
        return (double) kills / (double) deaths;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
