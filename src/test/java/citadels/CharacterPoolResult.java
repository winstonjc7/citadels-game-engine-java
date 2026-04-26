package citadels;

import java.util.List;

/**
 * Holds the results of character pool preparation during the selection phase.
 * Includes face-up discards, a face-down discard, and remaining characters available for selection.
 */
public class CharacterPoolResult {

    /** The list of face-up discarded character cards (visible to players). */
    public final List<CharacterCard> faceUp;

    /** The list containing one face-down discarded character card (hidden from players). */
    public final List<CharacterCard> faceDown;

    /** The list of character cards available for player selection. */
    public final List<CharacterCard> remaining;

    /**
     * Constructs a result object for the character pool setup.
     *
     * @param faceUp     the list of face-up discarded characters
     * @param faceDown   the list containing one face-down discarded character
     * @param remaining  the list of characters available for selection
     */
    public CharacterPoolResult(List<CharacterCard> faceUp, List<CharacterCard> faceDown, List<CharacterCard> remaining) {
        this.faceUp = faceUp;
        this.faceDown = faceDown;
        this.remaining = remaining;
    }
}
