package citadels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTestAbilities {
    private Game game;
    private List<Player> players;
    private InputParser parser;

    @BeforeEach
    public void setup() throws Exception {
        String simulatedInput = String.join("\n",
            "5",
            "end"
        );
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        parser = new InputParser(input);
        game = new Game(parser, System.in);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);

        players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            players.add(new ComputerPlayer(i));
        }
        playersField.set(game, players);
    }

    @Test
    public void testProcessTurnCommands() throws Exception {
        Player player = new ComputerPlayer(1);
        Method method = Game.class.getDeclaredMethod("processTurnCommands", Player.class);
        method.setAccessible(true);
        method.invoke(game, player);
    }

    @Test
    public void testPerformWarlordAbility() throws Exception {
        ComputerPlayer warlord = new ComputerPlayer(1);
        warlord.addGold(10);

        ComputerPlayer target = new ComputerPlayer(2);
        target.getCity().add(new DistrictCard("Watchtower", "red", 2, null));
        target.getCity().add(new DistrictCard("Temple", "blue", 1, null));

        players.clear();
        players.add(warlord);
        players.add(target);

        Method method = Game.class.getDeclaredMethod("performWarlordAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, warlord);

        assertTrue(target.getCity().size() < 2);
    }

    @Test
    public void testPerformMagicianAbility() throws Exception {
        ComputerPlayer magician = new ComputerPlayer(1);
        magician.getHand().add(new DistrictCard("Lab", "blue", 5, null));
        magician.getHand().add(new DistrictCard("Manor", "green", 3, null));
        magician.addGold(5);

        ComputerPlayer target = new ComputerPlayer(2);
        target.getHand().add(new DistrictCard("Temple", "blue", 1, null));

        List<DistrictCard> deck = new ArrayList<>();

        players.clear();
        players.add(magician);
        players.add(target);

        Method method = Game.class.getDeclaredMethod("performMagicianAbility", Player.class, List.class);
        method.setAccessible(true);
        method.invoke(game, magician, deck);

        assertEquals(1, magician.getHand().size());
    }

    @Test
    public void testPerformThiefAbility() throws Exception {
        ComputerPlayer thief = new ComputerPlayer(1);

        Method method = Game.class.getDeclaredMethod("performThiefAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, thief);

        Field robbedField = Game.class.getDeclaredField("robbedCharacterRank");
        robbedField.setAccessible(true);
        int rank = robbedField.getInt(game);

        assertTrue(rank >= 3 && rank <= 8);
    }

    @Test
    public void testPerformKingAbility() throws Exception {
        ComputerPlayer king = new ComputerPlayer(1);
        king.getCity().add(new DistrictCard("Palace", "yellow", 5, null));

        Method method = Game.class.getDeclaredMethod("performKingAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, king);
        System.out.println("King's gold: " + king.getGold());

        assertEquals(3, king.getGold());
    }

    @Test
    public void testPerformMerchantAbility() throws Exception {
        ComputerPlayer merchant = new ComputerPlayer(1);
        merchant.getCity().add(new DistrictCard("Market", "green", 3, null));

        Method method = Game.class.getDeclaredMethod("performMerchantAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, merchant);
        System.out.println("Merchant's gold: " + merchant.getGold());

        assertEquals(4, merchant.getGold());
    }

    @Test
    public void testPerformArchitectAbility() throws Exception {
        ComputerPlayer architect = new ComputerPlayer(1);
        List<DistrictCard> deck = new ArrayList<>();
        deck.add(new DistrictCard("Library", "purple", 6, null));
        deck.add(new DistrictCard("Keep", "purple", 5, null));

        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, deck);

        Method method = Game.class.getDeclaredMethod("performArchitectAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, architect);

        assertEquals(2, architect.getHand().size());
        assertEquals(3, architect.getBuildLimit());
    }

    @Test
    public void testPerformBishopAbility() throws Exception {
        ComputerPlayer bishop = new ComputerPlayer(1);
        bishop.getCity().add(new DistrictCard("Temple", "blue", 1, null));
        bishop.getCity().add(new DistrictCard("Church", "blue", 2, null));

        Method method = Game.class.getDeclaredMethod("performBishopAbility", Player.class);
        method.setAccessible(true);
        method.invoke(game, bishop);
        System.out.println("Bishop's gold: " + bishop.getGold());

        assertEquals(4, bishop.getGold());
    }

    @Test
    public void testGetCharacterNameByRank() throws Exception {
        Method method = Game.class.getDeclaredMethod("getCharacterNameByRank", int.class);
        method.setAccessible(true);

        String[] expected = {"Assassin", "Thief", "Magician", "King", "Bishop", "Merchant", "Architect", "Warlord"};
        for (int i = 1; i <= 8; i++) {
            String result = (String) method.invoke(game, i);
            assertEquals(expected[i - 1], result);
        }

        String unknown = (String) method.invoke(game, 99);
        assertEquals("Unknown", unknown);
    }

    @Test
    public void testIsGameOverLambdaTrueAndFalse() throws Exception {
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);

        // no player has 8
        players.clear();
        for (int i = 0; i < 4; i++) {
            ComputerPlayer p = new ComputerPlayer(i + 1);
            p.getCity().add(new DistrictCard("A", "blue", 1, null));
            players.add(p);
        }
        playersField.set(game, players);
        Method isGameOver = Game.class.getDeclaredMethod("isGameOver");
        isGameOver.setAccessible(true);
        assertFalse((boolean) isGameOver.invoke(game));

        // one player has 8
        ComputerPlayer finisher = new ComputerPlayer(99);
        for (int i = 0; i < 8; i++) {
            finisher.getCity().add(new DistrictCard("District" + i, "yellow", 1, null));
        }
        players.add(finisher);
        assertTrue((boolean) isGameOver.invoke(game));
    }

    @Test
    public void testIsGameOver() throws Exception {
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        List<Player> testPlayers = new ArrayList<>();
        ComputerPlayer p = new ComputerPlayer(1);
        for (int i = 0; i < 8; i++) {
            p.getCity().add(new DistrictCard("Test" + i, "blue", 1, null));
        }
        testPlayers.add(p);
        playersField.set(game, testPlayers);

        Method method = Game.class.getDeclaredMethod("isGameOver");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(game);
        assertTrue(result);
    }

    @Test
    public void testGetPlayerOrderStartingFromCrowned() throws Exception {
        List<Player> testPlayers = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            testPlayers.add(new ComputerPlayer(i));
        }

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, testPlayers);

        Field crownField = Game.class.getDeclaredField("crownedPlayer");
        crownField.setAccessible(true);
        crownField.set(game, testPlayers.get(2)); // Start from player 3

        Method method = Game.class.getDeclaredMethod("getPlayerOrderStartingFromCrowned");
        method.setAccessible(true);
        List<Player> order = (List<Player>) method.invoke(game);

        assertEquals(4, order.size());
        assertEquals(3, order.get(0).getId()); // Crowned
        assertEquals(4, order.get(1).getId());
        assertEquals(1, order.get(2).getId());
        assertEquals(2, order.get(3).getId());
    }

    @Test
    public void testDealStartingHands() throws Exception {
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        List<Player> testPlayers = new ArrayList<>();
        for (int i = 1; i <= 2; i++) testPlayers.add(new ComputerPlayer(i));
        playersField.set(game, testPlayers);

        List<DistrictCard> deck = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            deck.add(new DistrictCard("Card" + i, "blue", 1, null));
        }

        Method method = Game.class.getDeclaredMethod("dealStartingHands", List.class);
        method.setAccessible(true);
        method.invoke(game, deck);

        for (Player p : testPlayers) {
            assertEquals(4, p.getHand().size());
        }
    }


    @Test
    public void testActivateCharactersCoversAllAbilities() throws Exception {
        List<DistrictCard> dummyDeck = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dummyDeck.add(new DistrictCard("District " + i, "color", 1, ""));
        }

        Map<Integer, Player> rankToPlayer = new HashMap<>();

        for (int rank = 1; rank <= 8; rank++) {
            ComputerPlayer player = new ComputerPlayer(rank);
            player.setSelectedCharacter(new CharacterCard("Character " + rank, rank));
            if (rank == 4) {
                player.getCity().add(new DistrictCard("Castle", "yellow", 3, ""));
            } else if (rank == 5) {
                player.getCity().add(new DistrictCard("Temple", "blue", 1, ""));
                player.getCity().add(new DistrictCard("Church", "blue", 2, ""));
            } else if (rank == 6) {
                player.getCity().add(new DistrictCard("Market", "green", 2, ""));
            } else if (rank == 7) {
                dummyDeck.add(new DistrictCard("Library", "purple", 5, ""));
                dummyDeck.add(new DistrictCard("Keep", "purple", 5, ""));
            } else if (rank == 8) {
                player.addGold(5);
                ComputerPlayer victim = new ComputerPlayer(99);
                victim.getCity().add(new DistrictCard("Watchtower", "red", 2, ""));
                players.add(victim);
            }

            players.add(player);
            rankToPlayer.put(rank, player);
        }

        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, dummyDeck);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        Method activateMethod = Game.class.getDeclaredMethod("activateCharacters");
        activateMethod.setAccessible(true);
        String fakeInput = String.join("\n", "2", "3");
        InputStream input = new ByteArrayInputStream(fakeInput.getBytes());
        Field parserField = Game.class.getDeclaredField("inputParser");
        parserField.setAccessible(true);
        parserField.set(game, new InputParser(input));
        activateMethod.invoke(game);

         

    }

}
