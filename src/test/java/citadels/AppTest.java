package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AppTest {

    @Test
    public void testStartGame() throws Exception {
        String input = String.join("\n",
            "4", "1", "1", "1", "1",
            "1", "0", "1", "0", "1", "0", "1", "0"
        );
        InputStream in = new ByteArrayInputStream(input.getBytes());
        InputParser parser = new InputParser(in);
        InputStream cardsInput = getClass().getClassLoader().getResourceAsStream("citadels/cards.tsv");
        App app = new App(parser, cardsInput);

        Game game = new Game(parser, cardsInput);
        List<Player> players = new ArrayList<>();
        Player human = new HumanPlayer(1);
        for (int i = 0; i < 8; i++) human.getCity().add(new DistrictCard("Dummy", "blue", 1, ""));
        players.add(human);
        for (int i = 2; i <= 4; i++) players.add(new ComputerPlayer(i));

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        Field crownField = Game.class.getDeclaredField("crownedPlayer");
        crownField.setAccessible(true);
        crownField.set(game, human);

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    public void testConstructorThrowsIfCardsInputIsNull() {
        InputStream fakeInput = new ByteArrayInputStream("4\n".getBytes());
        InputParser parser = new InputParser(fakeInput);

        Exception ex = assertThrows(RuntimeException.class, () -> new App(parser, null));
        assertTrue(ex.getMessage().contains("cards.tsv not found"));
    }

    @Test
    public void testMainMethod() {
        System.setIn(new ByteArrayInputStream("4\n1\n1\n1\n1\n1\n0\n1\n0\n1\n0\n1\n0\n".getBytes()));
        assertDoesNotThrow(() -> App.main(new String[] {}));
    }
}
