package citadels;

import java.util.List;

/**
 * A data container class representing the state of character selection during a game round.
 */

public class CharacterSelectionContext {
    public final List<CharacterCard> selectionPool;
    public final CharacterCard reservedFaceDown;

    public CharacterSelectionContext(List<CharacterCard> selectionPool, CharacterCard reservedFaceDown) {
        this.selectionPool = selectionPool;
        this.reservedFaceDown = reservedFaceDown;
    }
}
