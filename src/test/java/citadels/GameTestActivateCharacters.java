package citadels;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for the activateCharacters() method in Game.java.
 * Covers robbery mechanics, skipped characters, and null cases.
 */
public class GameTestActivateCharacters {

    /**
     * Sets up a Game instance with a given set of players and input.
     * Input is used to simulate console commands during takeTurn().
     *
     * @param input Simulated user input (e.g., "6\nend\nend\n")
     * @param players List of players to include in the game
     * @return A configured Game instance
     */
    private Game setupGameWithPlayers(String input, List<Player> players) throws Exception {
        InputStream simulated = new ByteArrayInputStream(input.getBytes());
        InputParser parser = new InputParser(simulated);
        Game game = new Game(parser, System.in);

        Field parserField = Game.class.getDeclaredField("inputParser");
        parserField.setAccessible(true);
        parserField.set(game, parser);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        return game;
    }

    /**
     * Calls the activateCharacters() method using reflection.
     */
    private void invokeActivate(Game game) throws Exception {
        Method method = Game.class.getDeclaredMethod("activateCharacters");
        method.setAccessible(true);
        method.invoke(game);
    }

    /**
     * Verifies that a killed character is skipped and does not take a turn.
     */
    @Test
    public void testKilledCharacterIsSkipped() throws Exception {
        ComputerPlayer victim = new ComputerPlayer(1);
        victim.setSelectedCharacter(new CharacterCard("Magician", 3));

        List<Player> players = new ArrayList<>();
        players.add(victim);

        Game game = setupGameWithPlayers("end\n", players);

        Field killedField = Game.class.getDeclaredField("killedCharacterRank");
        killedField.setAccessible(true);
        killedField.setInt(game, 3);

        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, new ArrayList<>());

        invokeActivate(game);
    }

    /**
     * Tests that robbery fails if the target has no gold.
     * Thief should not gain gold, and victim's gold should remain 0.
     */
    @Test
    public void testRobberyFailsWhenTargetHasNoGold() throws Exception {
        ComputerPlayer thief = new ComputerPlayer(1);
        thief.setSelectedCharacter(new CharacterCard("Thief", 2));

        ComputerPlayer victim = new ComputerPlayer(2);
        victim.setSelectedCharacter(new CharacterCard("Merchant", 6));
        victim.addGold(0);

        List<Player> players = new ArrayList<>();
        players.add(thief);
        players.add(victim);

        Game game = setupGameWithPlayers("6\nend\nend\n", players);

        Field robbedField = Game.class.getDeclaredField("robbedCharacterRank");
        robbedField.setAccessible(true);
        robbedField.setInt(game, 6);

        Field thiefPlayerField = Game.class.getDeclaredField("thiefPlayer");
        thiefPlayerField.setAccessible(true);
        thiefPlayerField.set(game, thief);

        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, new ArrayList<>());

        invokeActivate(game);

        assertEquals(0, thief.getGold());
        assertEquals(0, victim.getGold());
    }

    /**
     * Tests that a player with no selected character is safely ignored.
     */
    @Test
    public void testPlayerWithNullSelectedCharacterIsIgnored() throws Exception {
        ComputerPlayer p1 = new ComputerPlayer(1);
        p1.setSelectedCharacter(null);

        List<Player> players = new ArrayList<>();
        players.add(p1);

        Game game = setupGameWithPlayers("end\n", players);

        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, new ArrayList<>());

        invokeActivate(game);
    }

    /**
     * Tests a successful robbery where the thief steals all of the victim's gold.
     */
    @Test
    public void testPlayerGetsRobbedSuccessfully() throws Exception {
        ComputerPlayer thief = new ComputerPlayer(1);
        thief.setSelectedCharacter(new CharacterCard("Thief", 2));

        ComputerPlayer victim = new ComputerPlayer(2);
        victim.setSelectedCharacter(new CharacterCard("Merchant", 6));
        victim.addGold(4);

        List<Player> players = new ArrayList<>();
        players.add(thief);
        players.add(victim);

        Game game = setupGameWithPlayers("6\nend\nend\n", players);

        Field robbedField = Game.class.getDeclaredField("robbedCharacterRank");
        robbedField.setAccessible(true);
        robbedField.setInt(game, 6);

        Field thiefPlayerField = Game.class.getDeclaredField("thiefPlayer");
        thiefPlayerField.setAccessible(true);
        thiefPlayerField.set(game, thief);

        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, new ArrayList<>());

        invokeActivate(game);

        assertEquals(4, thief.getGold());
        assertEquals(0, victim.getGold());
    }
}
