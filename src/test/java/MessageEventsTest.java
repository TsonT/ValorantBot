import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MessageEventsTest {

    String puuid = "71ad59da-a32a-58ce-bdd4-c52292e64f41";

    @org.junit.jupiter.api.Test
    void getCurrentMatchID() {
        MessageEvents messageEvents = new MessageEvents();

        ArrayList<String> params = new ArrayList<>();

        params.add("asdf");
        params.add(puuid);
        params.add(Integer.toString(10));


        messageEvents.getCurrentVersion();
    }
}