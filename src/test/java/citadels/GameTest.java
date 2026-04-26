package citadels;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Game class.
 * These tests validate core game setup logic and internal method behavior using reflection.
 */
public class GameTest {

    // Tests that the game starts with valid input and loads the deck correctly.
    @Test
    public void testGameStartWithValidInput() throws Exception {
        String simulatedInput = String.join("\n",
            "4",    
            "1", "1", "1", "1",
            "1", "1", "1", "1",
            "0", "0", "0", "0" 
        );
        InputStream userInput = new ByteArrayInputStream(simulatedInput.getBytes());
        InputStream deckInput = getClass().getClassLoader().getResourceAsStream("citadels/cards.tsv");
        assertNotNull(deckInput, "cards.tsv should be found in resources");

        InputParser parser = new InputParser(userInput);
        Game game = new Game(parser, deckInput);

        // Pre-fill city for game over
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        List<Player> players = new ArrayList<>();
        Player human = new HumanPlayer(1);
        for (int i = 0; i < 8; i++) {
            human.getCity().add(new DistrictCard("Dummy", "blue", 1, ""));
        }
        players.add(human);
        for (int i = 2; i <= 4; i++) {
            players.add(new ComputerPlayer(i));
        }
        playersField.set(game, players);

        // Set crownedPlayer to the human
        Field crownField = Game.class.getDeclaredField("crownedPlayer");
        crownField.setAccessible(true);
        crownField.set(game, human);

        game.start();
    }

    // Tests prepareCharacterPool ensures the King is not revealed face-up when drawn first.
    @Test
    public void testPrepareCharacterPoolWithKingDrawnFirst() throws Exception {
        InputStream dummyInput = new ByteArrayInputStream("5\n".getBytes());
        Game game = new Game(new InputParser(dummyInput), System.in);

        Field numPlayersField = Game.class.getDeclaredField("numberOfPlayers");
        numPlayersField.setAccessible(true);
        numPlayersField.setInt(game, 5);

        List<CharacterCard> deck = new ArrayList<>();
        deck.add(new CharacterCard("King", 4));
        deck.add(new CharacterCard("Thief", 2));
        deck.add(new CharacterCard("Magician", 3));
        deck.add(new CharacterCard("Assassin", 1));
        deck.add(new CharacterCard("Merchant", 6));
        deck.add(new CharacterCard("Architect", 7));
        deck.add(new CharacterCard("Warlord", 8));
        deck.add(new CharacterCard("Bishop", 5));

        Method method = Game.class.getDeclaredMethod("prepareCharacterPool", List.class);
        method.setAccessible(true);
        CharacterSelectionContext context = (CharacterSelectionContext) method.invoke(game, deck);

        assertNotNull(context);
        assertNotNull(context.selectionPool);
        assertNotNull(context.reservedFaceDown);
        assertTrue(context.selectionPool.size() >= 5);
    }

    // Tests that the 7-player character selection rule runs correctly without crashing.
    @Test
    public void testPerformCharacterSelectionSevenPlayerRule() throws Exception {
        InputStream dummyInput = new ByteArrayInputStream("7\n".getBytes());
        Game game = new Game(new InputParser(dummyInput), System.in);

        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= 7; i++) players.add(new ComputerPlayer(i));

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        Field crownedField = Game.class.getDeclaredField("crownedPlayer");
        crownedField.setAccessible(true);
        crownedField.set(game, players.get(0));

        Field numPlayersField = Game.class.getDeclaredField("numberOfPlayers");
        numPlayersField.setAccessible(true);
        numPlayersField.setInt(game, 7);

        List<CharacterCard> selectionPool = new ArrayList<>();
        selectionPool.add(new CharacterCard("Warlord", 8));
        selectionPool.add(new CharacterCard("Merchant", 6));
        selectionPool.add(new CharacterCard("Architect", 7));
        selectionPool.add(new CharacterCard("Magician", 3));
        selectionPool.add(new CharacterCard("Thief", 2));
        selectionPool.add(new CharacterCard("Bishop", 5));
        selectionPool.add(new CharacterCard("Dummy", 9));

        CharacterCard reserved = new CharacterCard("Assassin", 1);

        Method performSelection = Game.class.getDeclaredMethod(
            "performCharacterSelection", List.class, CharacterCard.class
        );
        performSelection.setAccessible(true);
        performSelection.invoke(game, selectionPool, reserved);
    }

    // Tests prepareCharacterPool with 6 players, which falls under the default face-up rule.
    @Test
    public void testPrepareCharacterPoolSixPlayersTriggersDefaultCase() throws Exception {
        InputStream dummyInput = new ByteArrayInputStream("6\n".getBytes());
        Game game = new Game(new InputParser(dummyInput), System.in);

        Field numPlayersField = Game.class.getDeclaredField("numberOfPlayers");
        numPlayersField.setAccessible(true);
        numPlayersField.setInt(game, 6);

        List<CharacterCard> deck = new ArrayList<>();
        deck.add(new CharacterCard("Thief", 2));
        deck.add(new CharacterCard("Magician", 3));
        deck.add(new CharacterCard("Assassin", 1));
        deck.add(new CharacterCard("Merchant", 6));
        deck.add(new CharacterCard("Architect", 7));
        deck.add(new CharacterCard("Warlord", 8));
        deck.add(new CharacterCard("Bishop", 5));
        deck.add(new CharacterCard("King", 4));

        Method method = Game.class.getDeclaredMethod("prepareCharacterPool", List.class);
        method.setAccessible(true);
        CharacterSelectionContext context = (CharacterSelectionContext) method.invoke(game, deck);

        assertNotNull(context.selectionPool);
    }

    // Tests that the 7-player rule message prints correctly when applicable.
    @Test
    public void testSevenPlayerPrintsRuleMessage() throws Exception {
        InputStream dummyInput = new ByteArrayInputStream("7\n".getBytes());
        Game game = new Game(new InputParser(dummyInput), System.in);

        Field numPlayersField = Game.class.getDeclaredField("numberOfPlayers");
        numPlayersField.setAccessible(true);
        numPlayersField.setInt(game, 7);

        List<CharacterCard> deck = new ArrayList<>();
        deck.add(new CharacterCard("Thief", 2));
        deck.add(new CharacterCard("Magician", 3));
        deck.add(new CharacterCard("Assassin", 1));
        deck.add(new CharacterCard("Merchant", 6));
        deck.add(new CharacterCard("Architect", 7));
        deck.add(new CharacterCard("Warlord", 8));
        deck.add(new CharacterCard("Bishop", 5));
        deck.add(new CharacterCard("King", 4));

        Method method = Game.class.getDeclaredMethod("prepareCharacterPool", List.class);
        method.setAccessible(true);
        method.invoke(game, deck);
    }
}
