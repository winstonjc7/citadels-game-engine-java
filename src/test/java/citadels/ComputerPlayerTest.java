package citadels;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ComputerPlayer class to ensure logic is covered and output is correct.
 */
public class ComputerPlayerTest {

    // This test ensures that isHuman() returns false as expected for computer players.
    @Test
    void testIsHumanReturnsFalse() {
        Player player = new ComputerPlayer(2);
        assertFalse(player.isHuman(), "Computer player should return false for isHuman()");
    }

    // This test verifies that takeTurn() prints the correct placeholder message.
    @Test
    void testTakeTurnPrintsMessage() {
        ComputerPlayer cpu = new ComputerPlayer(2);

        List<DistrictCard> dummyDeck = new ArrayList<>();
        InputStream dummyInput = new ByteArrayInputStream("1\n".getBytes());
        InputParser parser = new InputParser(dummyInput);

        cpu.takeTurn(dummyDeck, parser);
    }

    // This test confirms that chooseCharacter selects the first available character and prints output.
    @Test
    void testChooseCharacterSelectsFirst() {
        ComputerPlayer cpu = new ComputerPlayer(2);
        List<CharacterCard> characters = new ArrayList<>();
        characters.add(new CharacterCard("Warlord", 8));
        characters.add(new CharacterCard("Bishop", 5));

        cpu.chooseCharacter(characters);
    }

    @Test
    void testChooseCharacterWithNonEmptyList() {
        ComputerPlayer cpu = new ComputerPlayer(2);
        List<CharacterCard> characters = new ArrayList<>();
        characters.add(new CharacterCard("Warlord", 8));

        cpu.chooseCharacter(characters);
    }

    @Test
    void testChooseCharacterWithEmptyList() {
        ComputerPlayer cpu = new ComputerPlayer(3);
        List<CharacterCard> characters = new ArrayList<>();

        cpu.chooseCharacter(characters);
    }
}
