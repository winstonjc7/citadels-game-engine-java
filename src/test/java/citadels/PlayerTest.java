package citadels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Player class and its methods using a HumanPlayer instance.
 * Tests include gold handling, card management, and district building behavior.
 */
public class PlayerTest {

    // Tests initial values returned by getId() and getGold().
    @Test
    void testGetters() {
        Player player = new HumanPlayer(1);
        assertEquals(1, player.getId());
        assertEquals(2, player.getGold());
    }

    // Tests gold incrementing and spending logic.
    @Test
    void testAddAndSpendGold() {
        Player player = new HumanPlayer(1);
        player.addGold(5);
        assertEquals(7, player.getGold());

        player.spendGold(3);
        assertEquals(4, player.getGold());
    }

    // Tests that cards can be added to the player's hand.
    @Test
    void testAddToHand() {
        Player player = new HumanPlayer(1);
        DistrictCard card = new DistrictCard("Castle", "yellow", 4, "");
        player.addToHand(card);

        assertTrue(player.getHand().contains(card));
    }

    // Tests building a district: should move from hand to city and spend gold.
    @Test
    void testBuildDistrict() {
        Player player = new HumanPlayer(1);
        DistrictCard card = new DistrictCard("Castle", "yellow", 4, "");
        player.addGold(10);
        player.addToHand(card);

        player.buildDistrict(card);

        assertTrue(player.getCity().contains(card));
        assertFalse(player.getHand().contains(card));
        assertEquals(8, player.getGold());
    }

    // This test verifies that cards can be added to the hand and then built into the city.
    @Test
    public void testAddToHandAndBuild() {
        Player player = new HumanPlayer(1);
        DistrictCard card = new DistrictCard("Temple", "blue", 1, null);

        player.addToHand(card);
        assertTrue(player.getHand().contains(card), "Card should be in hand");

        player.buildDistrict(card);
        assertFalse(player.getHand().contains(card), "Card should be removed from hand after building");
        assertTrue(player.getCity().contains(card), "Card should be added to city");
    }

    // Tests adding and spending gold in combination.
    @Test
    void testAddGoldAndSpendGold() {
        Player player = new HumanPlayer(1);
        player.addGold(5);
        assertEquals(7, player.getGold());

        player.spendGold(3);
        assertEquals(4, player.getGold());
    }

    // Tests that building a district removes it from hand, adds to city, and deducts gold.
    @Test
    void testBuildDistrictAddsToCityAndSpendsGold() {
        Player player = new HumanPlayer(2);
        DistrictCard card = new DistrictCard("Temple", "Religious", 2, "A sacred district.");

        player.addToHand(card);
        player.addGold(10);
        player.buildDistrict(card);

        assertTrue(player.getCity().contains(card));
        assertFalse(player.getHand().contains(card));
        assertEquals(12 - card.getCost(), player.getGold());
    }

    // Tests that attempting to build the same district twice is ignored.
    @Test
    void testBuildDistrictDuplicateIgnored() {
        Player player = new HumanPlayer(1);
        DistrictCard card = new DistrictCard("Castle", "yellow", 4, "");
        player.addGold(10);
        player.addToHand(card);
        player.buildDistrict(card);

        int goldAfterFirst = player.getGold();
        player.buildDistrict(card);

        assertEquals(1, player.getCity().size(), "Should not add duplicate");
        assertEquals(goldAfterFirst, player.getGold(), "Gold should not change on duplicate build");
    }

    // Tests that the build limit can be set and retrieved correctly.
    @Test
    void testSetAndGetBuildLimit() {
        Player player = new HumanPlayer(1);
        player.setBuildLimit(3);
        assertEquals(3, player.getBuildLimit(), "Build limit should be set to 3");
    }

    // Tests that resetBuildLimit() resets the limit back to 1.
    @Test
    void testResetBuildLimit() {
        Player player = new HumanPlayer(1);
        player.setBuildLimit(5);
        player.resetBuildLimit();
        assertEquals(1, player.getBuildLimit(), "Build limit should reset to 1");
    }

}
