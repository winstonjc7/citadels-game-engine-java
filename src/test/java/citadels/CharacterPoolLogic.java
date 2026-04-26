package citadels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates the character selection logic for different
 * player counts in the Citadels game.
 */
public class CharacterPoolLogic {

    /**
     * Simulates the character pool preparation based on the number of players.
     * Handles discards (face-up and face-down) and ensures the King is not revealed face-up.
     *
     * @param numberOfPlayers the number of players in the game (4 to 7)
     * @param deck the original shuffled character deck
     * @return a CharacterPoolResult object containing face-up discards, one face-down discard,
     *         and the remaining characters available for selection
     */
    public static CharacterPoolResult simulate(int numberOfPlayers, List<CharacterCard> deck) {
        List<CharacterCard> copy = new ArrayList<>(deck);

        List<CharacterCard> faceUp = new ArrayList<>();
        List<CharacterCard> faceDown = new ArrayList<>();

        int faceUpCount = 0;
        if (numberOfPlayers == 4) {
            faceUpCount = 2;
        } else if (numberOfPlayers == 5) {
            faceUpCount = 1;
        }

        // Avoid revealing the King (rank 4) face-up
        while (faceUp.size() < faceUpCount) {
            CharacterCard card = copy.remove(0);
            if (card.getRank() == 4) {
                copy.add(card);
                Collections.shuffle(copy);
            } else {
                faceUp.add(card);
            }
        }

        // Always one face-down discard
        if (!copy.isEmpty()) {
            faceDown.add(copy.remove(0));
        }

        // Remaining characters are available for selection
        return new CharacterPoolResult(faceUp, faceDown, new ArrayList<>(copy));
    }
}
