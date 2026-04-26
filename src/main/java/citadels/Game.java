package citadels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final InputParser inputParser;
    private final InputStream cardsInput;
    private int numberOfPlayers;
    private List<Player> players = new ArrayList<>();
    private Player crownedPlayer;
    private List<DistrictCard> deck;
    private int killedCharacterRank = -1;
    private int robbedCharacterRank = -1;
    private Player thiefPlayer = null;
    private boolean debugMode = false;


    public Game(InputParser inputParser, InputStream cardsInput) {
        this.inputParser = inputParser;
        this.cardsInput = cardsInput;
    }

    private String getCharacterNameByRank(int rank) {
        switch (rank) {
            case 1:
                return "Assassin";
            case 2:
                return "Thief";
            case 3:
                return "Magician";
            case 4:
                return "King";
            case 5:
                return "Bishop";
            case 6:
                return "Merchant";
            case 7:
                return "Architect";
            case 8:
                return "Warlord";
            default:
                return "Unknown";
        }
    }

    private void processTurnCommands(Player currentPlayer) {
        System.out.println("Type commands during your turn. Type 'end' to finish your turn.");

        while (true) {
            String input = inputParser.promptLine("> ").trim().toLowerCase();

            if (input.equals("end")) {
                System.out.println("You ended your turn.");
                break;
            } else if (input.equals("hand")) {
                System.out.println("You have " + currentPlayer.getGold() + " gold. Cards in hand:");
                List<DistrictCard> hand = currentPlayer.getHand();
                for (int i = 0; i < hand.size(); i++) {
                    DistrictCard card = hand.get(i);
                    System.out.printf("%d. %s (%s), cost: %d\n", i + 1, card.getName(), card.getColor(), card.getCost());
                }
            } else if (input.equals("gold")) {
                System.out.println("You have " + currentPlayer.getGold() + " gold.");
            } else if (input.equals("city") || input.equals("citadel")) {
                System.out.println("Player " + currentPlayer.getId() + " has built:");
                for (DistrictCard c : currentPlayer.getCity()) {
                    System.out.println(c.getName() + " (" + c.getColor() + "), points: " + c.getCost());
                }
            } else if (input.startsWith("build ")) {
                try {
                    int idx = Integer.parseInt(input.split(" ")[1]) - 1;
                    List<DistrictCard> hand = currentPlayer.getHand();
                    if (idx >= 0 && idx < hand.size()) {
                        DistrictCard toBuild = hand.get(idx);
                        if (currentPlayer.getGold() >= toBuild.getCost()) {
                            currentPlayer.buildDistrict(toBuild);
                            System.out.println("Built " + toBuild.getName() + " [" + toBuild.getColor() + toBuild.getCost() + "]");
                        } else {
                            System.out.println("You cannot afford to build this building.");
                        }
                    } else {
                        System.out.println("Invalid card number.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid build command. Usage: build <number>");
                }
            } else if (input.equals("all")) {
                for (Player p : players) {
                    String city = p.getCity().isEmpty() ? "" : p.getCity().toString();
                    System.out.println("Player " + p.getId() + (p.isHuman() ? " (you)" : "") + ": cards=" + p.getHand().size() + " gold=" + p.getGold() + " city=" + city);
                }
            } else if (input.equals("debug")) {
                debugMode = !debugMode;
                System.out.println(debugMode ? "Enabled debug mode." : "Disabled debug mode.");
            } else if (input.equals("help")) {
                System.out.println("Available commands:\n");
                System.out.println("t                  : Processes the next turn.");
                System.out.println("hand               : Shows the cards currently in your hand.");
                System.out.println("gold               : Shows how much gold you currently have.");
                System.out.println("city / citadel     : Shows your built districts.");
                System.out.println("city <p>           : Show city of player with ID <p>.");
                System.out.println("build <n>          : Build the nth card from your hand (if you have enough gold).");
                System.out.println("action             : Use your character's special ability. You will be prompted for how.");
                System.out.println("info <H>           : Get info about a purple building's ability.");
                System.out.println("info <name>        : Get info about a character's ability.");
                System.out.println("all                : Show summary of all players: gold, hand size, and built districts.");
                System.out.println("end                : Ends your turn.");
                System.out.println("debug              : Toggle debug mode to see all players’ hands.");
                System.out.println("help               : Show this help message.\n");
            }

             else {
                System.out.println("Invalid command. Type 'help' for available options.");
            }
        }
    }



    private void performAssassinAbility(Player assassinPlayer) {
        System.out.println("Assassin: Choose a character to kill (1-8, except 1):");

        List<Integer> validTargets = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            validTargets.add(i);
        }

        for (int r : validTargets) {
            System.out.println("- " + r + ": " + getCharacterNameByRank(r));
        }

        int targetRank = inputParser.promptIntInRange("Kill which character (number)? ", 2, 8);
        killedCharacterRank = targetRank;
        System.out.println("Assassin has killed: " + getCharacterNameByRank(targetRank));
    }


    public void start() {
        numberOfPlayers = inputParser.promptIntInRange("Enter how many players [4-7]: ", 4, 7);
        System.out.println("Starting Citadels with " + numberOfPlayers + " players...");
        System.out.println("You are player 1");

        try {
            deck = loadDistrictDeck();
            initializePlayers();
            crownedPlayer = players.stream()
                .filter(Player::isHuman)
                .findFirst()
                .orElse(players.get(0));
            dealStartingHands(deck);
            while (!isGameOver()) {
                List<CharacterCard> characterDeck = CharacterDeck.getShuffledDeck();
                for (Player player : players) {
                    player.setSelectedCharacter(null);
                }

                CharacterSelectionContext context = prepareCharacterPool(characterDeck);
                performCharacterSelection(context.selectionPool, context.reservedFaceDown);
                activateCharacters();
            }

        } catch (IOException e) {
            System.err.println("Failed to load district deck: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<DistrictCard> loadDistrictDeck() throws IOException {
        System.out.println("Shuffling deck...");
        return DistrictDeckLoader.loadDeck(cardsInput);
    }

    private boolean isGameOver() {
        return players.stream().anyMatch(p -> p.getCity().size() >= 8);
    }


    private void activateCharacters() {
        System.out.println("\nCharacter Activation Phase:");

        for (int rank = 1; rank <= 8; rank++) {
            if (rank == killedCharacterRank) {
                System.out.println("Skipping " + getCharacterNameByRank(rank) + " killed this round.");
                continue;
            }

            for (Player player : players) {
                CharacterCard selected = player.getSelectedCharacter();
                if (selected != null && selected.getRank() == rank) {
                    System.out.println("Activating: " + selected.getName() + " (Player " + player.getId() + ")");

                    if (rank == 1) {
                        performAssassinAbility(player);
                    }

                    if (rank == 2) {
                        performThiefAbility(player);
                    } 
                    
                    if (rank == 3) {
                        performMagicianAbility(player, deck);
                    }

                    if (rank == 4) {
                        performKingAbility(player);
                    }

                    if (rank == 5) {
                        performBishopAbility(player);
                    }

                    if (rank == 6) {
                        performMerchantAbility(player);
                    }
                    if (rank == 7) {
                        performArchitectAbility(player);
                    }

                    if (rank == 8) {
                        performWarlordAbility(player);
                    }

                    if (rank == robbedCharacterRank && thiefPlayer != null) {
                        int stolen = player.getGold();
                        if (stolen > 0) {
                            player.spendGold(stolen);
                            thiefPlayer.addGold(stolen);
                            System.out.println("Thief steals " + stolen + " gold from Player " + player.getId());
                        } else {
                            System.out.println("Thief tried to steal from Player " + player.getId() + ", but they had no gold.");
                        }
                    }

                    player.takeTurn(deck, inputParser);
                }
            }
        }
    }
    private void performWarlordAbility(Player warlord) {
        long redCount = warlord.getCity().stream()
            .filter(d -> d.getColor().equalsIgnoreCase("red"))
            .count();

        warlord.addGold((int) redCount);
        System.out.println("Warlord gains " + redCount + " gold for red districts. Total gold: " + warlord.getGold());

        List<Player> targets = new ArrayList<>();
        for (Player p : players) {
            if (p == warlord || p.getCity().isEmpty()) continue;

            boolean isBishop = p.getSelectedCharacter() != null && p.getSelectedCharacter().getRank() == 5;
            boolean isBishopProtected = isBishop && killedCharacterRank != 5;
            boolean has8Districts = p.getCity().size() >= 8;

            if (!has8Districts && !isBishopProtected) {
                targets.add(p);
            }
        }

        if (targets.isEmpty()) {
            System.out.println("No valid targets for Warlord to destroy.");
            return;
        }

        if (warlord.isHuman()) {
            System.out.println("Warlord can target the following players:");
            for (Player p : targets) {
                System.out.println("Player " + p.getId() + " city:");
                for (int i = 0; i < p.getCity().size(); i++) {
                    DistrictCard d = p.getCity().get(i);
                    System.out.printf("  %d: %s (cost: %d)\n", i + 1, d.getName(), d.getCost());
                }
            }

            int targetId = inputParser.promptIntInRange("Choose player to target (0 to skip): ", 0, players.size());
            if (targetId == 0) return;

            Player target = players.get(targetId - 1);
            if (!targets.contains(target)) {
                System.out.println("Invalid target.");
                return;
            }

            if (target.getCity().isEmpty()) {
                System.out.println("Target has no districts.");
                return;
            }

            int cardIndex = inputParser.promptIntInRange("Choose district to destroy (1-" + target.getCity().size() + "): ", 1, target.getCity().size());
            DistrictCard chosen = target.getCity().get(cardIndex - 1);

            int destroyCost = chosen.getCost() - 1;
            if (warlord.getGold() < destroyCost) {
                System.out.println("Not enough gold to destroy " + chosen.getName() + ".");
                return;
            }

            warlord.spendGold(destroyCost);
            target.getCity().remove(chosen);
            System.out.println("Warlord destroyed " + chosen.getName() + " from Player " + target.getId() + "'s city.");
        } else {
            for (Player target : targets) {
                for (DistrictCard card : target.getCity()) {
                    int destroyCost = card.getCost() - 1;
                    if (warlord.getGold() >= destroyCost) {
                        warlord.spendGold(destroyCost);
                        target.getCity().remove(card);
                        System.out.println("Warlord (AI) destroyed " + card.getName() + " from Player " + target.getId() + "'s city.");
                        return;
                    }
                }
            }
            System.out.println("Warlord found no affordable targets to destroy.");
        }
    }



    private void performArchitectAbility(Player player) {
        System.out.println("Architect draws 2 extra cards.");
        for (int i = 0; i < 2 && !deck.isEmpty(); i++) {
            player.addToHand(deck.remove(0));
        }
        player.setBuildLimit(3);
    }


    private void performMerchantAbility(Player player) {
        long greenCount = player.getCity().stream()
            .filter(card -> card.getColor().equalsIgnoreCase("green"))
            .count();

        int totalGold = (int) greenCount + 1;
        player.addGold(totalGold);
        System.out.println("Merchant gains " + greenCount + " gold from trade districts + 1 bonus gold = " + totalGold + " gold.");
    }


    private void performBishopAbility(Player player) {
        long blueCount = player.getCity().stream()
            .filter(card -> card.getColor().equalsIgnoreCase("blue"))
            .count();

        player.addGold((int) blueCount);
        System.out.println("Bishop gains " + blueCount + " gold from religious districts.");
    }


    private void performMagicianAbility(Player magician, List<DistrictCard> deck) {
        List<DistrictCard> hand = magician.getHand();

        Player bestTarget = null;
        int maxHandSize = -1;
        for (Player other : players) {
            if (other != magician && other.getHand().size() > maxHandSize) {
                maxHandSize = other.getHand().size();
                bestTarget = other;
            }
        }

        boolean badHand = hand.size() <= 2 || hand.stream().allMatch(card -> card.getCost() > magician.getGold());
        boolean canRedraw = deck.size() >= hand.size();

        if (badHand && bestTarget != null && bestTarget.getHand().size() > 0) {
            List<DistrictCard> temp = new ArrayList<>(hand);
            magician.getHand().clear();
            magician.getHand().addAll(bestTarget.getHand());
            bestTarget.getHand().clear();
            bestTarget.getHand().addAll(temp);
            System.out.println("Computer Magician swapped hands with Player " + bestTarget.getId());
        } else if (canRedraw && hand.size() > 0) {
            int discardCount = hand.size();
            magician.getHand().clear();
            for (int i = 0; i < discardCount && !deck.isEmpty(); i++) {
                magician.addToHand(deck.remove(0));
            }
            System.out.println("Computer Magician discarded and drew " + discardCount + " new cards.");
        } else {
            System.out.println("Computer Magician chose not to use ability.");
        }
    }

    private void performKingAbility(Player player) {
        long yellowCount = player.getCity().stream()
            .filter(card -> card.getColor().equalsIgnoreCase("yellow"))
            .count();

        player.addGold((int) yellowCount);
        System.out.println("King gains " + yellowCount + " gold from noble districts.");

        crownedPlayer = player;
        System.out.println("Player " + player.getId() + " receives the crown for next round.");
    }





    private void performThiefAbility(Player thief) {
        System.out.println("Thief: Choose a character to rob (2-8, excluding killed character and Assassin):");
        List<Integer> validTargets = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            if (i != killedCharacterRank && i != 1 && i != 2) {
                validTargets.add(i);
            }
        }

        for (int r : validTargets) {
            System.out.println("- " + r + ": " + getCharacterNameByRank(r));
        }

        int targetRank = inputParser.promptIntInRange("Rob which character (number)? ", 2, 8);
        if (!validTargets.contains(targetRank)) {
            System.out.println("Invalid target. No robbery will happen.");
            return;
        }

        robbedCharacterRank = targetRank;
        thiefPlayer = thief;
        System.out.println("Thief will rob: " + getCharacterNameByRank(targetRank));
    }



    private void initializePlayers() {
        System.out.println("Adding characters...");
        players.add(new HumanPlayer(1));
        for (int i = 2; i <= numberOfPlayers; i++) {
            players.add(new ComputerPlayer(i));
        }
        System.out.println("Created " + numberOfPlayers + " players.");
    }

    private void dealStartingHands(List<DistrictCard> deck) {
        System.out.println("Dealing cards...");
        for (Player player : players) {
            for (int i = 0; i < 4; i++) {
                DistrictCard drawn = deck.remove(0);
                player.addToHand(drawn);
            }
        }
    }

    private CharacterSelectionContext prepareCharacterPool(List<CharacterCard> deck) {
        List<CharacterCard> faceUp = new ArrayList<>();
        List<CharacterCard> faceDown = new ArrayList<>();

        int faceUpCount;
        switch (numberOfPlayers) {
            case 4:
                faceUpCount = 2;
                break;
            case 5:
                faceUpCount = 1;
                break;
            default:
                faceUpCount = 0;
                break;
        }


        while (faceUp.size() < faceUpCount) {
            CharacterCard drawn = deck.remove(0);
            if (drawn.getRank() == 4) {
                deck.add(drawn);
                Collections.shuffle(deck);
            } else {
                faceUp.add(drawn);
            }
        }

        CharacterCard hiddenDiscard = deck.remove(0);
        faceDown.add(hiddenDiscard);

        System.out.println("Face-up discarded characters:");
        if (faceUp.isEmpty()) {
            System.out.println("- None");
        } else {
            for (CharacterCard card : faceUp) {
                System.out.println("- " + card.getName());
            }
        }

        System.out.println("Face-down discarded character: [Hidden]");

        if (numberOfPlayers == 7) {
            System.out.println("7-player rule: last player will choose between last card and the face-down discard.");
        }

        System.out.println("Characters available for selection:");
        for (CharacterCard card : deck) {
            System.out.println("- " + card.getName());
        }

        return new CharacterSelectionContext(deck, hiddenDiscard);
    }

    private void performCharacterSelection(List<CharacterCard> selectionPool, CharacterCard reservedFaceDown) {
        Map<Player, CharacterCard> chosenCharacters = new HashMap<>();

        List<Player> selectionOrder = getPlayerOrderStartingFromCrowned();
        List<CharacterCard> pool = new ArrayList<>(selectionPool);

        System.out.println("\nCharacter Selection Phase:");

        for (int i = 0; i < selectionOrder.size(); i++) {
            Player player = selectionOrder.get(i);

            if (numberOfPlayers == 7 && i == 6) {
                if (pool.isEmpty() || reservedFaceDown == null) {
                    System.err.println("[ERROR] Not enough cards for 7-player rule.");
                    return;
                }

                CharacterCard lastCard = pool.remove(pool.size() - 1);

                List<CharacterCard> options = new ArrayList<>();
                options.add(reservedFaceDown);
                options.add(lastCard);

                CharacterCard chosen = chooseCharacter(player, options);
                player.setSelectedCharacter(chosen);
                CharacterCard discarded = (chosen == reservedFaceDown) ? lastCard : reservedFaceDown;
                System.out.println("Player " + player.getId() + " discards 1 card face-down.");
                chosenCharacters.put(player, chosen);

            } else {
                CharacterCard chosen = chooseCharacter(player, pool);
                player.setSelectedCharacter(chosen);
                chosenCharacters.put(player, chosen);
            }
        }

        if (numberOfPlayers < 7 && !pool.isEmpty()) {
            CharacterCard discarded = pool.remove(0);
            System.out.println("Unchosen character discarded face-down.");
        }

        System.out.println("\nCharacter assignments complete:");
        for (Map.Entry<Player, CharacterCard> entry : chosenCharacters.entrySet()) {
            Player player = entry.getKey();
            System.out.println("Player " + player.getId() + " has chosen their character.");
        }
    }

    private CharacterCard chooseCharacter(Player player, List<CharacterCard> options) {
        if (player.isHuman()) {
            System.out.println("Available characters:");

            for (int i = 0; i < options.size(); i++) {
                CharacterCard c = options.get(i);
                System.out.printf("%d: %s%n", i + 1, c.getName());
            }

            int choice = inputParser.promptIntInRange("Choose a character: ", 1, options.size());

            CharacterCard selected = options.remove(choice - 1);
            System.out.println("You selected: " + selected.getName());
            return selected;
        } else {
            CharacterCard selected = options.remove(0);
            System.out.println("Player " + player.getId() + " (AI) selected: " + selected.getName());
            return selected;
        }
    }

    private List<Player> getPlayerOrderStartingFromCrowned() {
        List<Player> order = new ArrayList<>();
        int startIndex = players.indexOf(crownedPlayer);
        for (int i = 0; i < players.size(); i++) {
            int idx = (startIndex + i) % players.size();
            order.add(players.get(idx));
        }
        return order;
    }
}
