# Citadels (Java CLI Game)

## Overview

Citadels is a console-based Java implementation of the classic strategy card game.
The project demonstrates object-oriented design, game state management, command-driven interaction, and testable architecture.

The system simulates a full multiplayer game (4–7 players) with both human and AI-controlled participants, following official turn order and character abilities.

---

## Key Features

* Full game loop with round-based progression
* Command-line interface (CLI) for interactive gameplay
* Human vs AI player system
* Character-based turn mechanics (Assassin, Thief, etc.)
* Dynamic deck loading from external data (`cards.tsv`)
* Input validation and robust error handling
* Unit testing across core components

---

## Architecture

### Entry Point

* `App.java`
  Initialises dependencies and starts the game engine.

---

### Core Engine

* `Game.java`
  Central controller responsible for:
* Game lifecycle (start → loop → end)
* Player management
* Turn sequencing (rank-based execution as per game rules)
* Character abilities
* Command handling

---

### Player System (Polymorphism)

* `Player` (abstract base class)
* `HumanPlayer`
* `ComputerPlayer`

Demonstrates:

* Inheritance
* Method overriding
* Runtime polymorphism

```text
Game → player.takeTurn(...)
       ↳ HumanPlayer (interactive)
       ↳ ComputerPlayer (automated)
```

---

### Data Models

* `CharacterCard` – immutable representation of characters
* `DistrictCard` – immutable representation of buildable districts

---

### Data Layer

* `CharacterDeck` – generates character cards (factory pattern)
* `DistrictDeckLoader` – loads district data from external `.tsv` file

```text
cards.tsv → DistrictDeckLoader → DistrictCard objects → Game
```

---

### Input System

* `InputParser`

Encapsulates all user input and validation:

* Prevents invalid input from reaching core logic
* Enables testability via injected input streams

---

## Object-Oriented Design

This project demonstrates:

* **Encapsulation**
  Private fields with controlled access via methods

* **Inheritance & Polymorphism**
  Unified `Player` interface with different runtime behaviours

* **Abstraction**
  Abstract base class (`Player`) defines required behaviour

* **Separation of Concerns**

  * Game logic (`Game`)
  * Input handling (`InputParser`)
  * Data models (`CharacterCard`, `DistrictCard`)
  * Data loading (`DistrictDeckLoader`)

* **Dependency Injection**
  Dependencies passed into `Game` and `InputParser` to improve flexibility and testability

---

## Data Flow

```text
cards.tsv
   ↓
InputStream (App)
   ↓
DistrictDeckLoader
   ↓
List<DistrictCard> (deck)
   ↓
Game
   ↓
Player.hand → Player.city
```

---

## Testing

Located in:

```
src/test/java/citadels/
```

Test suite was completely AI generated, using a wide range of models; Claude, Cursor & Chatgpt.

Covers:

* Game logic (`GameTest`, ability tests)
* Player behaviour
* Input parsing
* Card and deck functionality

Key aspects:

* Unit testing of core logic
* Isolation of components
* Simulation of user input via injected streams

---

## Example Commands

During gameplay:

```
hand       → view cards in hand
gold       → view current amount of gold
all        → view all players' status
debug      → toggle debug mode
end        → end turn
```

---

## Design Observations & Improvements

* **SRP Violation in Game class**
  Character abilities are implemented inside `Game`, increasing complexity
  → Could be refactored into separate strategy/ability classes

* **Character system not fully object-oriented**
  Behaviour is driven by rank instead of encapsulated in objects
  → Violates Open/Closed Principle

* **AI logic is minimal**
  `ComputerPlayer` can be extended with decision-making algorithms

---

## Skills Demonstrated

* Object-Oriented Programming (Java)
* System design & architecture analysis
* State-driven program design
* CLI-based application development
* Data parsing and transformation
* Unit testing and input simulation
* Debugging and code comprehension

---

## Potential Extensions

* Implement full AI decision-making
* Refactor character abilities into separate classes
* Add scoring system and end-game evaluation
* Improve CLI with richer commands
* Add GUI (JavaFX)

---

## How to Run

Using Gradle:

```bash
gradle run
```

---

## Summary

This project showcases the ability to:

* Work with structured game logic and state
* Apply object-oriented principles in real systems
* Build testable and maintainable software
* Reflect on work, showcasing a critical eye to detail by identifying places for improvement



---
