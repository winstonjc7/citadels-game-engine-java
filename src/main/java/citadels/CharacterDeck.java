package citadels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the standard and shuffled character decks
 * used in the Citadels game.
 */

public class CharacterDeck {

    /**
     * Returns the standard set of 8 character cards used in the classic Citadels game.
     */
    public static List<CharacterCard> getStandardDeck() {
        List<CharacterCard> deck = new ArrayList<>();

        deck.add(new CharacterCard("Assassin", 1));
        deck.add(new CharacterCard("Thief", 2));
        deck.add(new CharacterCard("Magician", 3));
        deck.add(new CharacterCard("King", 4));
        deck.add(new CharacterCard("Bishop", 5));
        deck.add(new CharacterCard("Merchant", 6));
        deck.add(new CharacterCard("Architect", 7));
        deck.add(new CharacterCard("Warlord", 8));

        return deck;
    }

    /**
     * Returns a shuffled version of the standard character deck.
     */
    public static List<CharacterCard> getShuffledDeck() {
        List<CharacterCard> shuffled = getStandardDeck();
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
