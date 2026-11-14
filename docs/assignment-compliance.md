# Assignment Compliance Checklist

This checklist is based on `HY252-Project_2023_Sorry!_Game.pdf`, the supplied 2023–2024 HY252 assignment specification. The PDF is the authority when it differs from commercial Sorry! rules.

| Requirement | Status | Evidence |
|---|---|---|
| Java source implementing the game | Met | `src/main`, `src/view`, `src/controller`, and `src/model` |
| Build/run instructions using README and Ant/Maven | Met | `README.md`, `pom.xml`, and `build.xml` |
| Updated report describing final design, changes, algorithms, rule differences, decisions, problems, and JUnit tests | Met | `docs/Sorry_Game_Project_Report.docx` |
| Current UML diagrams with explanation | Met | Mermaid diagrams in `docs/architecture.md` and diagrams in the report |
| MVC separation | Met with limitation | Separate packages exist; `Board` is the view and `Controller` mediates the model. Model squares still store view coordinates. |
| Abstract `Card` and `Square` hierarchies | Met | `model.Card.Card`, `NumberCard`, concrete cards; `model.Square.Square` and concrete square/slide classes |
| Required core classes (`Square`, `Card`, `Deck`, `Pawn`, `Player`, `Controller`, View) | Met | Corresponding source classes; view is `view.Board` |
| Javadoc with pre/postconditions | Met | Every public API is documented; strict `javadoc -Xdoclint:all` reports zero warnings, and Maven attaches a Javadoc JAR with `failOnWarnings` enabled. |
| JUnit tests | Met | Tests under `test/`; `mvn test` runs 27 tests |
| Initialize 16×16-style board, cards, players, and pawns | Met | `Controller` builds 60 perimeter squares plus player areas; `Deck` builds 44 cards; `Player`/`Pawn` setup is tested |
| Select who plays first (for example, randomly) | Met | `Main.main` offers explicit player choice or Random |
| 44 cards: four each of 1,2,3,4,5,7,8,10,11,12 and Sorry! | Met | `Deck`; distribution is validated and tested. Internally Sorry! retains legacy numeric value `6`. |
| Card 1: leave Start or move 1 | Met | `Controller.canMovePawn` and `NumberOne` |
| Card 2: leave Start/move 2 and play again | Met | `NumberTwo`, `TurnManager`; repeat also applies after a no-move fold |
| Cards 3 and 5: move every movable pawn | Met | `SimpleCard.moveAllpawn`; simultaneous vacated-square case is tested |
| Card 4: move one pawn backward 4 | Met | `NumberFour` and `MoveDirection` handling |
| Card 7: move 7 or split 1–6 / 6–1 | Met | `NumberSeven.splitMove`, `Controller.makeSevenSplit`, and split dialog |
| Cards 8 and 12: move or draw again | Met | card-choice dialog and repeat turn state |
| Card 10: move forward 10 or backward 1; use backward if forward unavailable | Met | direction dialog disables illegal choices; backward remains selectable when legal |
| Card 11: forward 11 or optional legal swap; protected zones excluded | Met | `Controller.canSwapWithEnemy` and `NumberEleven`; swap no longer depends on an unrelated forward-11 move |
| Sorry!: Start pawn replaces an opponent on the outer track | Met | `SorryCard` and multi-opponent selection dialogs |
| Fold only when no move exists, except optional card-11 swap | Met | `hasLegalMove`, fold-state UI, and card-11 rule handling |
| Re-shuffle after all cards are used | Met | `Controller.resetDeck` when `tableIsEmpty()` |
| Pawns may pass but cannot finish on an own pawn; enemy is captured | Met | destination occupancy checks and `Controller.checkKill` |
| Slides: only another color triggers; all pawns in the slide return to Start | Met | `StartSlide.Act` via `Controller.checkSlide` |
| Safety belongs to one player and forward movement enters it | Met | one `SafetyTrack` per player and `Card.findBox` |
| Exact Home entry and both pawns Home to win | Met | `Card.findBox`, fixed pawn home slots, and `Controller.win` |
| GUI with board, draw pile/current card, info area, and Fold button | Met | `view.Board` |
| Up to 4 players (bonus) | Met | 2/3/4-player setup, colors, opponents, saves, automated tests, and visual setup checks recorded in `docs/manual-testing.md` |
| Save game (bonus) | Met | Game menu, `GameSnapshot`, and `SnapshotStore`; snapshots are written atomically where supported, and an isolated UI save/resume round trip is recorded in `docs/manual-testing.md` |

## Ambiguities and documented choices

- The specification says backward movement cannot *enter* Safety, but does not explicitly say whether a pawn already in Safety may move backward out of it. The implementation allows it; changing this would require a clarified course rule.
- The specification permits multiple pawns only in Start and Home. This implementation models two separate Home slots, one per pawn, so its logical state never stores two pawns in one `Square` object while presenting the same Home area visually.
- Save-file format and exact resume UI are bonus behavior not defined by the PDF. The implementation saves only between turns and requires the resumed game to use the same player count.
