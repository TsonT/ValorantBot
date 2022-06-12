import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MessageEvents extends ListenerAdapter {

    MessageReceivedEvent event;
    String myPuuid = "71ad59da-a32a-58ce-bdd4-c52292e64f41";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        this.event = event;
        String message = event.getMessage().getContentRaw();
        ArrayList<String> params = new ArrayList<>();

        if (message.charAt(0) != '!') {
            return;
        }

        message = message.substring(1);
        params.addAll(Arrays.asList(message.toLowerCase().split(",")));

        String messageCommand = params.get(0);

        switch (messageCommand) {
            case "hello":
                helloMessage();
                break;
            case "player_rank":
                getPlayerRankByName(params);
                break;
            case "all_player_ranks_current_game":
                getAllPlayerRanksCurrentGame();
                break;
            case "player_rank_by_id":
                getPlayerRankByPuuid(params.get(1));
                break;
            case "help":
                printHelp();
                break;
            case "past_match_details":
                getRangePastMatchDetailsByName(params);
                break;
            case "all_player_stats_current_game":
                getAllPlayerStatsCurrentGame(params);
                break;
            default:
                invalidCommand();
                break;
        }

    }

    public void helloMessage() {
        event.getChannel().sendMessage("Hello " + event.getAuthor().getName()).queue();
    }

    public void getPlayerRankByName(ArrayList<String> params) {

        if (params.size() != 3) {
            event.getChannel().sendMessage("Invalid parameters").queue();
            return;
        }

        String name = params.get(1);
        String tag = params.get(2);

        try {
            String puuid = getPuuid(name, tag);
            getPlayerRankByPuuid(puuid);
        } catch (RuntimeException e) {
            return;
        }
    }

    public void getPlayerRankByPuuid(String puuid) {

        HttpResponse<String> response = Unirest.get("https://pd.na.a.pvp.net/mmr/v1/players/" + puuid)
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("X-Riot-ClientPlatform", "ew0KCSJwbGF0Zm9ybVR5cGUiOiAiUEMiLA0KCSJwbGF0Zm9ybU9TIjogIldpbmRvd3MiLA0KCSJwbGF0Zm9ybU9TVmVyc2lvbiI6ICIxMC4wLjE5MDQyLjEuMjU2LjY0Yml0IiwNCgkicGxhdGZvcm1DaGlwc2V0IjogIlVua25vd24iDQp9")
                .header("X-Riot-ClientVersion", getCurrentVersion())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();

        String name = getPlayerNameAndTagFromPuuid(puuid)[0];
        String tag = getPlayerNameAndTagFromPuuid(puuid)[1];

        event.getChannel().sendMessage("**" + name + "#" + tag + "**" + " Ranks:").queue();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response.getBody())
                    .getJSONObject("QueueSkills")
                    .getJSONObject("competitive")
                    .getJSONObject("SeasonalInfoBySeasonID");
        } catch (JSONException e) {
            event.getChannel().sendMessage("This person hasn't played enough ranked").queue();
            return;
        }

        String[] seasonIDs = getCurrentandPreviousSeasonIDs();



        try {
            int numCurrentActRank = jsonObject.getJSONObject(seasonIDs[0]).getInt("CompetitiveTier");
            printRank("Current", new Rank(numCurrentActRank));
        } catch (JSONException e) {
            event.getChannel().sendMessage("Rank Current Act: No Data").queue();
        }

        try {
            int numPreviousActRank = jsonObject.getJSONObject(seasonIDs[1]).getInt("CompetitiveTier");
            printRank("Previous", new Rank(numPreviousActRank));
        } catch (JSONException e) {
            event.getChannel().sendMessage("Rank Previous Act: No Data").queue();
        }
    }

    public void invalidCommand() {
        event.getChannel().sendMessage("Invalid Command").queue();
    }

    public String getPuuid(String name, String tag) throws RuntimeException{
        HttpResponse<String> response = Unirest.get("https://api.henrikdev.xyz/valorant/v1/account/" + name + "/" + tag)
               .asString();

        if(response.getStatus() == 429) {
            event.getChannel().sendMessage("Max Number Of Requests... Please Try Again Later").queue();
            throw new RuntimeException();
        }

        return new JSONObject(response.getBody()).getJSONObject("data").getString("puuid");
    }

    public String[] getCurrentandPreviousSeasonIDs() {
        HttpResponse<String> response = Unirest.get("https://valorant-api.com/v1/seasons")
                .asString();

        JSONObject jsonObject = new JSONObject(response.getBody());

        JSONArray array = jsonObject.getJSONArray("data");

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

        String[] result = new String[2];

        for (int i = 0; i < array.length(); i++) {
            JSONObject act = (JSONObject) array.get(i);

            LocalDate startTime = LocalDate.parse(act.getString("startTime").substring(0,10), inputFormatter);
            LocalDate endTime = LocalDate.parse(act.getString("endTime").substring(0,10), inputFormatter);
            LocalDate currentTime = LocalDate.parse(inputFormatter.format(LocalDateTime.now()));

            if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                result[0] = act.getString("uuid");
                result[1] = ((JSONObject) array.get(i-1)).getString("uuid");
            }
        }

        return result;
    }

    public String getCurrentMatchID(String puuid) {
        HttpResponse<String> response = Unirest.get("https://glz-na-1.na.a.pvp.net/core-game/v1/players/" + puuid)
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();

        JSONObject jsonObject = new JSONObject(response.getBody());
        return jsonObject.getString("MatchID");
    }

    public String getPreGameMatchID(String puuid) {
        HttpResponse<String> response = Unirest.get("https://glz-na-1.na.a.pvp.net/pregame/v1/players/" + puuid)
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();

        JSONObject jsonObject = new JSONObject(response.getBody());
        return jsonObject.getString("MatchID");
    }

    public String[] getPlayerNameAndTagFromPuuid(String puuid) {

        String[] puuids = new String[1];
        puuids[0] = puuid;

        HttpResponse<String> response = Unirest.put("https://pd.NA.a.pvp.net/name-service/v2/players")
                .body(puuids)
                .header("Content-Type", "application/json")
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();

        String[] result = new String[2];
        JSONArray jsonArray = new JSONArray(response.getBody());
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        String gameName = jsonObject.getString("GameName");
        String tag = jsonObject.getString("TagLine");

        result[0] = gameName;
        result[1] = tag;
        return result;
    }

    public ArrayList<String> getCurrentGamePlayerIDs(String matchID) {
        HttpResponse<String> response = Unirest.get("https://glz-na-1.na.a.pvp.net/core-game/v1/matches/" + matchID)
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();

         JSONArray allPlayerInfo = new JSONArray(new JSONObject(response.getBody()).getJSONArray("Players"));
         ArrayList<String> result = new ArrayList<>();

         for (int i = 0; i < allPlayerInfo.length(); i++) {
             result.add(((JSONObject) allPlayerInfo.get(i)).getJSONObject("PlayerIdentity").getString("Subject"));
         }

        return result;
    }

    public void getAllPlayerRanksCurrentGame() {
        ArrayList<String> allPlayerIds = getCurrentGamePlayerIDs(getCurrentMatchID(myPuuid));

        for (String puuid : allPlayerIds) {
            getPlayerRankByPuuid(puuid);
        }
    }

    public void printRank(String act, Rank rank) {
        event.getChannel().sendMessage("Rank For " + act + " Act: " + rank.getRankNameAndTier() + rank.getRankEmoji()).queue();
    }

    public void printHelp() {
        for (Command command : Command.ALL_COMMANDS) {
            event.getChannel().sendMessage(command.getHelp()).queue();
        }
    }

    public void getRangePastMatchDetailsByName(ArrayList<String> params) {

        if (params.size() != 5) {
            event.getChannel().sendMessage("Invalid parameters").queue();
            return;
        }

        String name = params.get(1);
        String tag = params.get(2);
        int endIndex = Integer.parseInt(params.get(3));
        String gameMode = params.get(4).toLowerCase();

        String puuid = getPuuid(name, tag);

        getRangePastMatchDetailsByPuuid(puuid, endIndex, true, gameMode);

    }

    public void getRangePastMatchDetailsByPuuid(String puuid, int endIndex, boolean printAllDetails, String gameMode) {

        if (!gameMode.equals("competitive") && !gameMode.equals("unrated")){
            event.getChannel().sendMessage("Invalid Game Mode").queue();
            return;
        }

        ArrayList<String> pastMatchIds =  getPastMatchIds(puuid, endIndex, gameMode);
        ArrayList<MatchDetails> pastMatchDetails = new ArrayList<>();

        double averageKD = 0;
        int wins = 0;
        int losses = 0;

        for (String id : pastMatchIds) {
            pastMatchDetails.add(getPastMatchDetailsForPlayer(getPastMatchDetailsJSON(id), puuid));
        }

        if (!pastMatchDetails.isEmpty()) {
            event.getChannel().sendMessage("**" + pastMatchDetails.get(0).getPlayerName() + "#" + pastMatchDetails.get(0).getTag() +  "**").queue();
        }

        for (int i = 0; i < pastMatchDetails.size(); i++) {
            MatchDetails matchDetails = pastMatchDetails.get(i);

            String matchResult;

            if (printAllDetails) {

                if (matchDetails.isWon()) {
                    matchResult = "Win";
                } else {
                    matchResult = "Loss";
                }

                event.getChannel().sendMessage("**MATCH " + (i + 1) + "**\n" +
                        "kills: " + matchDetails.getKills() + "\n" +
                        "deaths: " + matchDetails.getDeaths() + "\n" +
                        "assists: " + matchDetails.getAssists() + "\n" +
                        "KD Ratio: + " + matchDetails.getKD() + "\n" +
                        matchResult ).queue();
            }
            averageKD += matchDetails.getKD();
            if (matchDetails.isWon()) {
                wins++;
            } else {
                losses++;
            }
        }

        averageKD = averageKD/pastMatchDetails.size();

        event.getChannel().sendMessage("**OVERALL STATS** \n" +
                "average KD: " + averageKD + "\n" +
                "wins: " + wins + "\n" +
                "losses: " + losses).queue();
    }

    public ArrayList<String> getPastMatchIds(String puuid, int endIndex, String gameMode) {

        HttpResponse<String> response = Unirest.get("https://pd.na.a.pvp.net/match-history/v1/history/" + puuid + "?endIndex=" + endIndex + "&queue=" + gameMode)
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("X-Riot-ClientPlatform", "ew0KCSJwbGF0Zm9ybVR5cGUiOiAiUEMiLA0KCSJwbGF0Zm9ybU9TIjogIldpbmRvd3MiLA0KCSJwbGF0Zm9ybU9TVmVyc2lvbiI6ICIxMC4wLjE5MDQyLjEuMjU2LjY0Yml0IiwNCgkicGxhdGZvcm1DaGlwc2V0IjogIlVua25vd24iDQp9")
                .header("X-Riot-ClientVersion", getCurrentVersion())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();

        JSONArray matchIDs = new JSONObject(response.getBody()).getJSONArray("History");
        ArrayList<String> ids = new ArrayList<>();

        for (int i = 0; i < matchIDs.length(); i++) {
            ids.add(((JSONObject) matchIDs.get(i)).getString("MatchID"));
        }

        return ids;
    }

    public JSONObject getPastMatchDetailsJSON(String matchId) {
        HttpResponse<String> response = Unirest.get("https://pd.na.a.pvp.net/match-details/v1/matches/" + matchId)
                .header("X-Riot-Entitlements-JWT", AuthenticationManager.getEntitlementToken())
                .header("X-Riot-ClientPlatform", "ew0KCSJwbGF0Zm9ybVR5cGUiOiAiUEMiLA0KCSJwbGF0Zm9ybU9TIjogIldpbmRvd3MiLA0KCSJwbGF0Zm9ybU9TVmVyc2lvbiI6ICIxMC4wLjE5MDQyLjEuMjU2LjY0Yml0IiwNCgkicGxhdGZvcm1DaGlwc2V0IjogIlVua25vd24iDQp9")
                .header("X-Riot-ClientVersion", getCurrentVersion())
                .header("Authorization", "Bearer " + AuthenticationManager.getAccessToken())
                .asString();
        try {
            return new JSONObject(response.getBody());
        } catch (Exception e) {
            System.out.println("asdf");
        }
        return null;
    }

    public MatchDetails getPastMatchDetailsForPlayer(JSONObject jsonMatchDetails, String puuid) {
        jsonMatchDetails.getJSONArray("players");

        JSONArray players = new JSONArray(jsonMatchDetails.getJSONArray("players"));
        JSONArray rounds = jsonMatchDetails.getJSONArray("roundResults");
        String lastRoundWinners = ((JSONObject) rounds.get(rounds.length() - 1)).getString("winningTeam");

        for (int i = 0; i < players.length(); i++) {
            JSONObject jsonPlayer = (JSONObject) players.get(i);

            if (jsonPlayer.getString("subject").equals(puuid)) {
                boolean isWon = (lastRoundWinners.equals(jsonPlayer.getString("teamId")));
                JSONObject jsonStats = jsonPlayer.getJSONObject("stats");
                String playerName = jsonPlayer.getString("gameName");
                String tag = jsonPlayer.getString("tagLine");
                int kills = jsonStats.getInt("kills");
                int deaths = jsonStats.getInt("deaths");
                int assists = jsonStats.getInt("assists");

                return new MatchDetails(kills, deaths, assists, isWon, playerName,tag);
            }
        }

        return null;
    }

    public void getAllPlayerStatsCurrentGame(ArrayList<String> params) {

        if (params.size() != 2) {
            event.getChannel().sendMessage("Invalid parameters").queue();
            return;
        }

        int endIndex = Integer.parseInt(params.get(1));
        String gameMode = params.get(2).toLowerCase();

        for (String id : getCurrentGamePlayerIDs(getCurrentMatchID(myPuuid))) {
            getRangePastMatchDetailsByPuuid(id, endIndex, false, gameMode);
        }
    }

    public String getCurrentVersion() {
        HttpResponse<String> response = Unirest.get("https://valorant-api.com/v1/version")
                .asString();

        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONObject data = jsonObject.getJSONObject("data");

        return data.getString("riotClientVersion");
    }
}
