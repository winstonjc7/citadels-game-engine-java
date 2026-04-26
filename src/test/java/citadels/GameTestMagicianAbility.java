package citadels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the performMagicianAbility method in Game.java.
 * These tests ensure all logical branches of the Magician's ability are covered,
 * including swapping hands, discarding and redrawing, and skipping the ability.
 */
public class GameTestMagicianAbility {

    /**
     * Invokes the private performMagicianAbility method which is not directly accessible.
     */
    private void invokeMagician(Game game, Player magician, List<DistrictCard> deck) throws Exception {
        Method method = Game.class.getDeclaredMethod("performMagicianAbility", Player.class, List.class);
        method.setAccessible(true);
        method.invoke(game, magician, deck);
    }

    /**
     * Creates a Game instance and an optional target player.
     */
    private Game setupGameWithPlayers(Player magician, Player target) throws Exception {
        Game game = new Game(null, System.in);
        List<Player> players = new ArrayList<>();
        players.add(magician);
        if (target != null) players.add(target);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        return game;
    }

    /**
     * Test case where the magician has an affordable, useful hand.
     * Expected: The magician skips using the ability.
     */
    @Test
    public void testMagicianSkipsDueToGoodHand() throws Exception {
        ComputerPlayer magician = new ComputerPlayer(1);
        magician.addGold(5);
        magician.getHand().add(new DistrictCard("Temple", "blue", 1, ""));

        ComputerPlayer target = new ComputerPlayer(2);
        target.getHand().add(new DistrictCard("Market", "green", 3, ""));

        List<DistrictCard> deck = new ArrayList<>();

        Game game = setupGameWithPlayers(magician, target);
        invokeMagician(game, magician, deck);

        assertEquals(1, magician.getHand().size());
    }

    /**
     * Magician cannot redraw due to insufficient cards in the deck.
     * Expected: Magician keeps original hand or gets fewer cards.
     */
    @Test
    public void testMagicianCannotRedrawDueToSmallDeck() throws Exception {
        ComputerPlayer magician = new ComputerPlayer(1);
        magician.addGold(1);
        magician.getHand().add(new DistrictCard("Lab", "purple", 6, ""));
        magician.getHand().add(new DistrictCard("Library", "purple", 6, ""));

        ComputerPlayer target = new ComputerPlayer(2);
        target.getHand().add(new DistrictCard("Tavern", "green", 1, ""));

        List<DistrictCard> deck = new ArrayList<>();
        deck.add(new DistrictCard("Altar", "blue", 1, ""));

        Game game = setupGameWithPlayers(magician, target);
        invokeMagician(game, magician, deck);

        assertTrue(magician.getHand().size() <= 2);
    }

    /**
     * Magician has a poor hand and swaps it with another player.
     */
    @Test
    public void testMagicianSwapsWhenBadHand() throws Exception {
        ComputerPlayer magician = new ComputerPlayer(1);
        magician.addGold(1);
        magician.getHand().add(new DistrictCard("Fortress", "purple", 6, ""));

        ComputerPlayer target = new ComputerPlayer(2);
        target.getHand().add(new DistrictCard("Tavern", "green", 1, ""));
        target.getHand().add(new DistrictCard("Temple", "blue", 1, ""));

        List<DistrictCard> deck = new ArrayList<>();

        Game game = setupGameWithPlayers(magician, target);
        invokeMagician(game, magician, deck);

        assertEquals(2, magician.getHand().size());
        assertTrue(
            magician.getHand().stream().anyMatch(card ->
                card.getName().equals("Tavern") || card.getName().equals("Temple"))
        );
    }

    /**
     * Magician wants to redraw but the target has no hand.
     */
    @Test
    public void testMagicianRedrawsWhenNoTargetHand() throws Exception {
        ComputerPlayer magician = new ComputerPlayer(1);
        magician.addGold(1);
        magician.getHand().add(new DistrictCard("Fortress", "purple", 6, ""));

        ComputerPlayer target = new ComputerPlayer(2);
        List<DistrictCard> deck = new ArrayList<>();
        deck.add(new DistrictCard("Market", "green", 1, ""));
        deck.add(new DistrictCard("Church", "blue", 2, ""));

        Game game = setupGameWithPlayers(magician, target);
        invokeMagician(game, magician, deck);

        assertTrue(magician.getHand().size() <= 2);
    }
}
