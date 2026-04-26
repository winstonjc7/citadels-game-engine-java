package citadels;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the DistrictCard class.
 * Verifies correct behavior of getters and string representation.
 */
public class DistrictCardTest {

    // Tests that all getter methods return correct values and toString() includes key fields.
    @Test
    public void testGettersAndToString() {
        DistrictCard card = new DistrictCard("Library", "purple", 6, "Keep both drawn cards");

        assertEquals("Library", card.getName());
        assertEquals("purple", card.getColor());
        assertEquals(6, card.getCost());
        assertEquals("Keep both drawn cards", card.getText());

        assertTrue(card.toString().contains("Library"));
        assertTrue(card.toString().contains("6"));
    }
}
