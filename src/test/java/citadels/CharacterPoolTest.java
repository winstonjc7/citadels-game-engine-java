package citadels;

import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CharacterPoolLogic using various player counts.
 * Verifies correct discard behavior and pool size for 4 to 7 players.
 */
public class CharacterPoolTest {

    // Utility method: returns a shuffled copy of the standard character deck.
    private List<CharacterCard> getShuffledDeck() {
        List<CharacterCard> deck = CharacterDeck.getStandardDeck();
        Collections.shuffle(deck);
        return deck;
    }

    // Tests that 4-player pool has 2 face-up discards, 1 face-down discard, and 5 cards remaining.
    @Test
    public void test4PlayerCharacterPool() {
        int numberOfPlayers = 4;
        List<CharacterCard> deck = getShuffledDeck();

        CharacterPoolResult result = CharacterPoolLogic.simulate(numberOfPlayers, deck);

        assertEquals(2, result.faceUp.size(), "4-player: should have 2 face-up discards");
        assertEquals(1, result.faceDown.size(), "4-player: should have 1 face-down discard");
        assertEquals(5, result.remaining.size(), "4-player: 5 cards left for selection");
        assertTrue(result.faceUp.stream().noneMatch(c -> c.getRank() == 4), "King must not be in face-up");
    }

    // Tests that 5-player pool has 1 face-up discard, 1 face-down discard, and 6 cards remaining.
    @Test
    public void test5PlayerCharacterPool() {
        int numberOfPlayers = 5;
        List<CharacterCard> deck = getShuffledDeck();

        CharacterPoolResult result = CharacterPoolLogic.simulate(numberOfPlayers, deck);

        assertEquals(1, result.faceUp.size(), "5-player: should have 1 face-up discard");
        assertEquals(1, result.faceDown.size(), "5-player: should have 1 face-down discard");
        assertEquals(6, result.remaining.size(), "5-player: 6 cards left for selection");
        assertTrue(result.faceUp.stream().noneMatch(c -> c.getRank() == 4), "King must not be in face-up");
    }

    // Tests that 6-player pool has 1 face-down discard and 7 cards remaining (no face-up discards).
    @Test
    public void test6PlayerCharacterPool() {
        int numberOfPlayers = 6;
        List<CharacterCard> deck = getShuffledDeck();

        CharacterPoolResult result = CharacterPoolLogic.simulate(numberOfPlayers, deck);

        assertEquals(0, result.faceUp.size(), "6-player: should have 0 face-up discards");
        assertEquals(1, result.faceDown.size(), "6-player: should have 1 face-down discard");
        assertEquals(7, result.remaining.size(), "6-player: 7 cards left for selection");
    }

    // Tests that 7-player pool has 1 face-down discard and 7 characters remaining for selection.
    @Test
    public void test7PlayerCharacterPool() {
        int numberOfPlayers = 7;
        List<CharacterCard> deck = getShuffledDeck();

        CharacterPoolResult result = CharacterPoolLogic.simulate(numberOfPlayers, deck);

        assertEquals(0, result.faceUp.size(), "7-player: should have 0 face-up discards");
        assertEquals(1, result.faceDown.size(), "7-player: should have 1 face-down (held for player 7)");
        assertEquals(7, result.remaining.size(), "7-player: 7 cards passed for selection");
    }
}
