package citadels;

import java.io.InputStream;

public class App {
    private final InputParser parser;
    private final InputStream cardsInput;

    public App() {
        this(new InputParser(System.in), App.class.getResourceAsStream("/citadels/cards.tsv"));
    }

    public App(InputParser parser, InputStream cardsInput) {
        if (cardsInput == null) {
            throw new RuntimeException("cards.tsv not found in resources!");
        }
        this.parser = parser;
        this.cardsInput = cardsInput;
    }

    public void startGame() {
        Game game = new Game(parser, cardsInput);
        game.start();
    }

    public static void main(String[] args) {
        new App().startGame();
    }
}
