package citadels;

/**
 * Represents a character card in the Citadels game.
 */
public class CharacterCard {

    /** The name of the character.*/
    private final String name;

    /** The rank of the character (1-8). */
    private final int rank;

    /**
     * Constructs a CharacterCard with a specified name & rank.
     *
     * @param name the name of the character
     * @param rank the rank of the character (must be a positive integer)
     */
    public CharacterCard(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }

    /**
     * Returns the name of the character.
     *
     * @return the character's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the rank of the character.
     * @return the character's rank
     */
    public int getRank() {
        return rank;
    }
}
