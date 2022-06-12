import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Main {

    static String botToken = "OTU3MDkyMDA0NzkyMjQ2Mjgy.Yj5vYw.FiWhkYNvAaYmxXqsjdtdXa3nOFc";

    public static void main(String[] args) throws LoginException, InterruptedException {

        JDA builder = JDABuilder.createDefault(botToken)
                .setActivity(Activity.listening("Breeze Pre Game Music"))
                .addEventListeners(new MessageEvents())
                .build();
        builder.awaitReady();
    }
}
