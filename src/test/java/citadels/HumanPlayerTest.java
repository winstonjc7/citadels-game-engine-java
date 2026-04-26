package citadels;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the HumanPlayer class.
 */
public class HumanPlayerTest {

    // Tests if chooseCharacter() prints correctly.
    @Test
    public void testChooseCharacterPrints() {
        HumanPlayer player = new HumanPlayer(1);
        List<CharacterCard> characters = new ArrayList<>();
        characters.add(new CharacterCard("King", 4));

        player.chooseCharacter(characters);
    }

    // Tests that takeTurn() handles gold collection.
    @Test
    public void testTakeTurnTakesGold() {
        String input = "1\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));
        HumanPlayer player = new HumanPlayer(1);
        player.takeTurn(new ArrayList<>(), parser);
        assertEquals(4, player.getGold(), "Should receive 2 extra gold");
    }

    // Tests if takeTurn() handles drawing/keeping cards.
    @Test
    public void testTakeTurnDrawsCardsAndBuilds() {
        String input = String.join("\n",
            "2",
            "1",
            "1",
            "0"
        );
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer player = new HumanPlayer(1);
        player.addGold(10);

        List<DistrictCard> deck = new ArrayList<>();
        deck.add(new DistrictCard("Library", "purple", 3, "Draws extra"));
        deck.add(new DistrictCard("Temple", "blue", 1, null));

        player.takeTurn(deck, parser);

        assertEquals(1, player.getCity().size(), "Should have built one district");
    }


    /** 
     * Tests if the player chooses to draw cards but the deck has fewer than 2 cards. 
     */
    @Test
    public void testTakeTurnNotEnoughCardsToDraw() {
        String input = "2\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer player = new HumanPlayer(1);
        List<DistrictCard> deck = new ArrayList<>();

        player.takeTurn(deck, parser);

        assertEquals(2, player.getGold(), "Gold should remain unchanged if draw fails");
    }

    /** 
     * Tests that if the player chooses to draw 2 cards and picks the second card, 
     * only the second card is added to the hand. 
     */
    @Test
    public void testTakeTurnKeepsSecondCard() {
        String input = String.join("\n",
            "2",
            "2", 
            "0" 
        );
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer player = new HumanPlayer(1);

        List<DistrictCard> deck = new ArrayList<>();
        DistrictCard first = new DistrictCard("C1", "green", 2, null);
        DistrictCard second = new DistrictCard("C2", "blue", 1, null);
        deck.add(first);
        deck.add(second);

        player.takeTurn(deck, parser);
        assertTrue(player.getHand().contains(second));
        assertFalse(player.getHand().contains(first));
    }

    /** 
     * Tests that when the player skips building by entering 0, 
     * the turn ends without constructing any district. 
     */
    @Test
    public void testTakeTurnSkipsBuild() {
        String input = String.join("\n",
            "2",
            "1",
            "0" 
        );
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));
        HumanPlayer player = new HumanPlayer(1);
        player.addGold(5);

        List<DistrictCard> deck = new ArrayList<>();
        deck.add(new DistrictCard("C1", "green", 2, null));
        deck.add(new DistrictCard("C2", "blue", 3, null));

        player.takeTurn(deck, parser);

        assertTrue(player.getHand().size() == 1);
        assertEquals(0, player.getCity().size(), "Should not build if player inputs 0");
    }

    /** 
     * Tests that if the player attempts to build a card but doesn’t have enough gold, 
     * the card is not built and remains in hand. 
     */
    @Test
    public void testTakeTurnNotEnoughGoldToBuild() {
        String input = String.join("\n",
            "2", 
            "1", 
            "1", 
            "0"  
        );
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));
        HumanPlayer player = new HumanPlayer(1);
        player.addGold(1); 

        List<DistrictCard> deck = new ArrayList<>();
        deck.add(new DistrictCard("Expensive", "purple", 5, ""));
        deck.add(new DistrictCard("Alt", "yellow", 3, ""));

        player.takeTurn(deck, parser);

        assertEquals(1, player.getHand().size(), "Card should remain if not enough gold");
        assertEquals(0, player.getCity().size(), "No district built");
    }

    /**
     * Tests that the player can build up to their build limit,
     * and that the loop exits after reaching the limit even if more cards are available.
     */
    @Test
    public void testTakeTurnBuildLimitRespected() {
        String input = String.join("\n",
            "1",    // Take 2 gold
            "1",    
            "1",    
            "1",    
            "1" 
        );
        InputParser parser = new InputParser(new ByteArrayInputStream(input.getBytes()));

        HumanPlayer player = new HumanPlayer(1);
        player.setBuildLimit(3);
        player.addGold(15);

        List<DistrictCard> deck = new ArrayList<>();
        player.addToHand(new DistrictCard("C1", "green", 2, null));
        player.addToHand(new DistrictCard("C2", "blue", 2, null));
        player.addToHand(new DistrictCard("C3", "yellow", 2, null));
        player.addToHand(new DistrictCard("C4", "purple", 2, null));

        player.takeTurn(deck, parser);

        assertEquals(3, player.getCity().size());
        assertEquals(1, player.getHand().size());
    }





}
