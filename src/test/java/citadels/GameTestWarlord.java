package citadels;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GameTestWarlord {

    private Game setupGameWithPlayers(String input, HumanPlayer warlord, Player target) throws Exception {
        InputStream simulated = new ByteArrayInputStream(input.getBytes());
        InputParser parser = new InputParser(simulated);
        Game game = new Game(parser, System.in);

        List<Player> players = new ArrayList<>();
        players.add(warlord);
        if (target != null) players.add(target);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        return game;
    }

    private void invokeWarlord(Game game, Player warlord) throws Exception {
        Method method = Game.class.getDeclaredMethod("performWarlordAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, warlord);
    }

    @Test
    public void testWarlordSkipsAction() throws Exception {
        HumanPlayer warlord = new HumanPlayer(1);
        Game game = setupGameWithPlayers("0\n", warlord, null);
        invokeWarlord(game, warlord);
    }

    @Test
    public void testWarlordInvalidTarget() throws Exception {
        HumanPlayer warlord = new HumanPlayer(1);
        ComputerPlayer target = new ComputerPlayer(2);
        target.getCity().add(new DistrictCard("Watchtower", "red", 2, ""));
        Game game = setupGameWithPlayers("5\n", warlord, target);
        invokeWarlord(game, warlord);
    }

    @Test
    public void testWarlordNoDistricts() throws Exception {
        HumanPlayer warlord = new HumanPlayer(1);
        ComputerPlayer target = new ComputerPlayer(2);
        Game game = setupGameWithPlayers("2\n", warlord, target);
        invokeWarlord(game, warlord);
    }

    @Test
    public void testWarlordCannotAfford() throws Exception {
        HumanPlayer warlord = new HumanPlayer(1);
        warlord.addGold(0);
        ComputerPlayer target = new ComputerPlayer(2);
        target.getCity().add(new DistrictCard("Castle", "red", 3, ""));
        Game game = setupGameWithPlayers("2\n1\n", warlord, target);
        invokeWarlord(game, warlord);
    }

    @Test
    public void testWarlordDestroysDistrict() throws Exception {
        HumanPlayer warlord = new HumanPlayer(1);
        warlord.addGold(10);
        ComputerPlayer target = new ComputerPlayer(2);
        target.getCity().add(new DistrictCard("Castle", "red", 3, ""));
        Game game = setupGameWithPlayers("2\n1\n", warlord, target);
        invokeWarlord(game, warlord);
        assertEquals(0, target.getCity().size(), "District should be destroyed");
    }
}
