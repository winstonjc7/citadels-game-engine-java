package citadels;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the CharacterDeck class.
 * Verifies the size, rank uniqueness, and character names of the standard deck.
 */
public class CharacterDeckTest {

    // Tests that the standard deck contains exactly 8 character cards.
    @Test
    public void testDeckHasEightCharacters() {
        List<CharacterCard> deck = CharacterDeck.getStandardDeck();
        assertEquals(8, deck.size(), "There should be 8 character cards in the standard deck");
    }

    // Tests that the character ranks 1 through 8 are all unique and present.
    @Test
    public void testRanksAreUniqueAndComplete() {
        List<CharacterCard> deck = CharacterDeck.getStandardDeck();
        Set<Integer> ranks = deck.stream()
                                 .map(CharacterCard::getRank)
                                 .collect(Collectors.toSet());

        assertEquals(8, ranks.size(), "Each character should have a unique rank");
        for (int i = 1; i <= 8; i++) {
            assertTrue(ranks.contains(i), "Rank " + i + " should be in the deck");
        }
    }

    // Tests that all expected character names are included in the deck.
    @Test
    public void testCharacterNamesPresent() {
        List<CharacterCard> deck = CharacterDeck.getStandardDeck();
        Set<String> names = deck.stream()
                                .map(CharacterCard::getName)
                                .collect(Collectors.toSet());

        assertTrue(names.contains("Assassin"));
        assertTrue(names.contains("Thief"));
        assertTrue(names.contains("Magician"));
        assertTrue(names.contains("King"));
        assertTrue(names.contains("Bishop"));
        assertTrue(names.contains("Merchant"));
        assertTrue(names.contains("Architect"));
        assertTrue(names.contains("Warlord"));
    }
}
