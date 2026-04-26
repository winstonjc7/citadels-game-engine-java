package citadels;

import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer(int id) {
        super(id);
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public void takeTurn(List<DistrictCard> deck, InputParser parser) {
        System.out.println("It's your turn (Player " + id + ")");

        int choice = parser.promptIntInRange("Choose: 1) Take 2 gold  2) Draw 2 cards and keep 1: ", 1, 2);
        if (choice == 1) {
            addGold(2);
            System.out.println("You received 2 gold. Total gold: " + gold);
        } else {
            if (deck.size() < 2) {
                System.out.println("Not enough cards to draw.");
            } else {
                DistrictCard c1 = deck.remove(0);
                DistrictCard c2 = deck.remove(0);
                System.out.println("You drew:\n1: " + c1 + "\n2: " + c2);
                int pick = parser.promptIntInRange("Choose a card to keep (1 or 2): ", 1, 2);
                DistrictCard kept = (pick == 1) ? c1 : c2;
                addToHand(kept);
                System.out.println("You kept: " + kept.getName());
            }
        }

        if (hand.isEmpty()) {
            System.out.println("You have no cards to build.");
            return;
        }

        int builtThisTurn = 0;
        while (builtThisTurn < getBuildLimit() && !hand.isEmpty()) {
            System.out.println("You may build up to " + (getBuildLimit() - builtThisTurn) + " more district(s).");

            for (int i = 0; i < hand.size(); i++) {
                DistrictCard card = hand.get(i);
                System.out.printf("%d: %s (cost: %d)\n", i + 1, card.getName(), card.getCost());
            }

            System.out.println("Gold available: " + gold);
            int buildChoice = parser.promptIntInRange("Enter card number to build (0 to stop): ", 0, hand.size());

            if (buildChoice == 0) break;

            DistrictCard toBuild = hand.get(buildChoice - 1);
            if (gold >= toBuild.getCost()) {
                buildDistrict(toBuild);
                System.out.println("Built: " + toBuild.getName());
                builtThisTurn++;
            } else {
                System.out.println("Not enough gold to build " + toBuild.getName());
            }
        }

        resetBuildLimit();
    }



    @Override
    public void chooseCharacter(List<CharacterCard> availableCharacters) {
        System.out.println("You must choose a character. Use the CLI to select.");
    }
}
