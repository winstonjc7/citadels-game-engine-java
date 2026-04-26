package citadels;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the DistrictDeckLoader class.
 * Verifies deck loading behavior, including success cases and input edge cases.
 */
public class DistrictDeckLoaderTest {

    // Tests that the deck loads successfully from cards.tsv and is not empty.
    @Test
    public void testLoadDistrictDeckSize() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("citadels/cards.tsv");
        assertNotNull(input, "cards.tsv not found");

        List<DistrictCard> deck = DistrictDeckLoader.loadDeck(input);

        assertNotNull(deck); // Deck should not be null
        assertTrue(deck.size() > 0, "Deck should not be empty");
    }

    // Tests that the "Docks" card is correctly loaded with proper attributes.
    @Test
    public void testDocksIsLoaded() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("citadels/cards.tsv");
        assertNotNull(input, "cards.tsv not found");

        List<DistrictCard> deck = DistrictDeckLoader.loadDeck(input);

        boolean found = deck.stream().anyMatch(card ->
            card.getName().equals("Docks") &&
            card.getColor().equals("green") &&
            card.getCost() == 3
        );

        assertTrue(found, "Docks [green3] should exist in the deck");
    }

    // Tests that the "Haunted City" purple card includes its special effect text.
    @Test
    public void testPurpleCardHasSpecialText() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("citadels/cards.tsv");
        assertNotNull(input, "cards.tsv not found");

        List<DistrictCard> deck = DistrictDeckLoader.loadDeck(input);

        DistrictCard hauntedCity = deck.stream()
            .filter(card -> card.getName().equals("Haunted City"))
            .findFirst()
            .orElse(null);

        assertNotNull(hauntedCity, "Haunted City should exist in the deck");
        assertEquals("purple", hauntedCity.getColor());
        assertNotNull(hauntedCity.getText(), "Haunted City should have special text");
    }

    // Tests that lines with too few fields are skipped during parsing.
    @Test
    public void testMalformedLineIsSkipped() throws Exception {
        String malformedData = "OnlyOneField\nWatchtower\t3\tred";

        InputStream input = new ByteArrayInputStream(malformedData.getBytes(StandardCharsets.UTF_8));
        List<DistrictCard> deck = DistrictDeckLoader.loadDeck(input);

        assertEquals(0, deck.size(), "No cards should be loaded from malformed input");
    }

    // Tests that parsing errors are caught and ignored.
    @Test
    public void testInvalidIntegerParseIsCaught() throws Exception {
        String brokenData = "Name\tNaN\tred\t1\nTemple\t3\tblue\toops";

        InputStream input = new ByteArrayInputStream(brokenData.getBytes(StandardCharsets.UTF_8));
        List<DistrictCard> deck = DistrictDeckLoader.loadDeck(input);

        assertEquals(0, deck.size(), "Invalid numbers should result in no cards being loaded");
    }
}
