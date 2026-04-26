package citadels;

import java.util.List;
/**
 * Represents a non-human (computer-controlled) player in the Citadels game.
 * This class implements minimal placeholder logic for turn-taking and character selection.
 */
public class ComputerPlayer extends Player {

    public ComputerPlayer(int id) {
        super(id);
    }

    /**
     * Indicates that this player is not a human.
     *
     * @return false, since this is an AI player
     */
    
    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public void takeTurn(List<DistrictCard> deck, InputParser parser) {
        System.out.println("Computer Player " + id + " is taking its turn. (Logic coming soon)");
    }


    @Override
    public void chooseCharacter(List<CharacterCard> availableCharacters) {
        if (!availableCharacters.isEmpty()) {
            CharacterCard chosen = availableCharacters.get(0);
            System.out.println("Computer Player " + id + " chose: " + chosen.getName());
        }
    }
}
