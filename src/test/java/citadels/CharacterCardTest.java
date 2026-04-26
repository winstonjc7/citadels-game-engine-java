package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterCardTest {

    @Test
    public void testCharacterCardGettersAndToString() {
        CharacterCard character = new CharacterCard("Warlord", 8);

        assertEquals("Warlord", character.getName());
        assertEquals(8, character.getRank());
        assertTrue(character.toString().contains("Warlord"));
    }
}
