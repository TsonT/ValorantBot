import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Command {

    private Commands commandName;
    private String details;
    private String parameters;

    public static final ArrayList<Command> ALL_COMMANDS = new ArrayList<>();
    static {
        ALL_COMMANDS.add(new Command(Commands.HELLO));
        ALL_COMMANDS.add(new Command(Commands.PLAYER_RANK));
        ALL_COMMANDS.add(new Command(Commands.PLAYER_RANK_BY_ID));
        ALL_COMMANDS.add(new Command(Commands.ALL_PLAYER_RANKS_CURRENT_GAME));
        ALL_COMMANDS.add(new Command(Commands.PAST_MATCH_DETAILS));
        ALL_COMMANDS.add(new Command(Commands.All_PLAYER_STATS_CURRENT_GAME));
    }



    public Command(Commands commandName) {
        this.commandName = commandName;

        switch (commandName) {
            case HELLO:
                parameters = "";
                details = "Says Hello";
                break;
            case PLAYER_RANK:
                parameters = ",name,tag";
                details = "Gets player rank using in game name and tag";
                break;
            case PLAYER_RANK_BY_ID:
                parameters = ",puuid";
                details = "Gets player rank using puuid";
                break;
            case ALL_PLAYER_RANKS_CURRENT_GAME:
                parameters = "";
                details = "Gets the rank of all players in the current game";
                break;
            case PAST_MATCH_DETAILS:
                parameters = ",name,tag,endIndex,gameMode";
                details = "Gets the match details of the player for the specified past number of games. gameMode = unrated or competitive";
                break;
            case All_PLAYER_STATS_CURRENT_GAME:
                parameters = ",endIndex,gameMode";
                details = "Gets the match details of every player in the current game. gameMode = unrated or competitive";
                break;
        }
    }

    public String getHelp() {

        return ("**" + commandName.toString() + "**" + "\nFORMAT: !" + commandName + parameters + "\nDETAILS: " + details);
    }
}
