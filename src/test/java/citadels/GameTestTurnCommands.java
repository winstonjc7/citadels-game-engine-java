package citadels;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GameTestTurnCommands {

    private Game createGameWithInput(String input, HumanPlayer player) throws Exception {
        InputStream simulated = new ByteArrayInputStream(input.getBytes());
        InputParser parser = new InputParser(simulated);
        Game game = new Game(parser, System.in);

        List<Player> players = new ArrayList<>();
        players.add(player);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        return game;
    }

    private void invokeTurn(Game game, Player player) throws Exception {
        Method method = Game.class.getDeclaredMethod("processTurnCommands", Player.class);
        method.setAccessible(true);
        method.invoke(game, player);
    }

    @Test
    public void testCommand_endTerminatesTurn() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        Game game = createGameWithInput("end\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_hand() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addToHand(new DistrictCard("Temple", "blue", 2, ""));
        Game game = createGameWithInput("hand\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_gold() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addGold(3);
        Game game = createGameWithInput("gold\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_cityAndCitadel() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.getCity().add(new DistrictCard("Watchtower", "red", 2, ""));
        Game game = createGameWithInput("city\ncitadel\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_allPlayersSummary() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addToHand(new DistrictCard("Temple", "blue", 1, ""));
        player.addGold(5);
        Game game = createGameWithInput("all\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_debugToggle() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        Game game = createGameWithInput("debug\ndebug\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_help() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        Game game = createGameWithInput("help\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_invalidCommand() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        Game game = createGameWithInput("foobar\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_buildValid() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addToHand(new DistrictCard("Temple", "blue", 2, ""));
        player.addGold(5);
        Game game = createGameWithInput("build 1\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_buildInvalidIndex() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addToHand(new DistrictCard("Temple", "blue", 2, ""));
        player.addGold(5);
        Game game = createGameWithInput("build 99\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_buildInvalidSyntax() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addToHand(new DistrictCard("Temple", "blue", 2, ""));
        player.addGold(5);
        Game game = createGameWithInput("build one\nend\n", player);
        invokeTurn(game, player);
    }

    @Test
    public void testCommand_buildCannotAfford() throws Exception {
        HumanPlayer player = new HumanPlayer(1);
        player.addToHand(new DistrictCard("Palace", "yellow", 5, ""));
        player.addGold(2);
        Game game = createGameWithInput("build 1\nend\n", player);
        invokeTurn(game, player);
    }
}
