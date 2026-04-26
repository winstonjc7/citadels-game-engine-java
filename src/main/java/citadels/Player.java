package citadels;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected final int id;
    protected int gold;
    protected List<DistrictCard> hand;
    protected List<DistrictCard> city;
    private CharacterCard selectedCharacter;
    private int buildLimit = 1;


    public Player(int id) {
        this.id = id;
        this.gold = 2;
        this.hand = new ArrayList<>();
        this.city = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void spendGold(int amount) {
        gold -= amount;
    }

    public List<DistrictCard> getHand() {
        return hand;
    }

    public List<DistrictCard> getCity() {
        return city;
    }

    public void addToHand(DistrictCard card) {
        hand.add(card);
    }

    public void buildDistrict(DistrictCard card) {
        if (!city.contains(card)) {
            city.add(card);
            hand.remove(card);
            spendGold(card.getCost());
        }
    }

    public void setSelectedCharacter(CharacterCard character) {
        this.selectedCharacter = character;
    }

    public CharacterCard getSelectedCharacter() {
        return selectedCharacter;
    }

    public abstract boolean isHuman();

    public abstract void takeTurn(List<DistrictCard> deck, InputParser parser);

    public abstract void chooseCharacter(List<CharacterCard> availableCharacters);

    public void setBuildLimit(int limit) {
        this.buildLimit = limit;
    }

    public int getBuildLimit() {
        return buildLimit;
    }

    public void resetBuildLimit() {
        this.buildLimit = 1;
    }

}
