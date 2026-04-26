package citadels;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DistrictDeckLoader {

    public static List<DistrictCard> loadDeck(InputStream tsvInput) throws IOException {
        List<DistrictCard> deck = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(tsvInput, StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

                if (parts.length < 4) {
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    int qty = Integer.parseInt(parts[1].trim());
                    String color = parts[2].trim();
                    int cost = Integer.parseInt(parts[3].trim());
                    String text = parts.length > 4 ? parts[4].trim() : null;

                    for (int i = 0; i < qty; i++) {
                        deck.add(new DistrictCard(name, color, cost, text));
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to parse line: " + line);
                    e.printStackTrace();
                }
            }
        }

        Collections.shuffle(deck);
        return deck;
    }
}
