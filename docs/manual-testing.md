# Manual UI Test Record

This record separates checks performed in the running Swing application from behavior covered only by automated tests or code inspection.

## Environment

- Date: 13 November 2026
- Platform: macOS on a Retina display
- Build: `target/sorry-game-1.0.0-SNAPSHOT.jar`
- Window: fixed 1100 × 780 logical pixels, centered and non-resizable

## Verified in the running application

| Area | Result | Evidence observed |
|---|---|---|
| Two-player baseline | Passed | Setup, draw, Fold, pawn choice, pawn movement, turn advancement, and card-2 repeat were exercised in the earlier smoke test. |
| Three-player setup | Passed | The title displayed `SORRY GAME - 3 Players`; red, yellow, and blue Start/Home/Safety areas and both pawns for each player were visible. |
| Explicit first player | Passed | Player 3 could be selected and was shown as `Player 3 (Blue)` in Game Info. |
| Three-player wraparound | Passed | Player 3 folded an unplayable card 4; the next draw correctly selected Player 1. |
| Four-player setup | Passed | The title displayed `SORRY GAME - 4 Players`; the green Start, Home, Safety track, and two green pawns were visible and aligned. |
| Four-player dialog layout | Passed | The card-2 `Choose Pawn` dialog was centered, readable, and contained two fully visible pawn buttons. |
| Window-close Cancel | Passed | Choosing Cancel from the close confirmation left the game open and retained the current state. |
| Save during a turn | Passed | Choosing Save from the close confirmation during an unfinished turn displayed `Save is available between turns. Finish or fold this turn first.` and did not close the game. |
| Retina rendering | Passed at the tested display setting | Board borders, card images, labels, pawns, buttons, and dialogs remained aligned with no visible clipping. |

The pre-existing save at `~/.sorry-game/snapshot.properties` was inspected read-only and was not overwritten during these checks.

## Resolved observation

The smoke test found that Fold enabled the next draw but left the previous card and player text visible. The UI now clears the completed card and identifies the next player immediately after a move, Fold, or voluntary redraw. Turn-order behavior itself was already correct.

## Still requiring manual testing

- Complete two-, three-, and four-player matches through the winner dialog.
- Exercise every card-choice Back, Cancel, and window-close path, especially cards 7, 10, 11, 12, and Sorry!.
- Perform a UI-level save/resume round trip using a disposable save file, including save-copy Cancel and invalid-file error dialogs.
- Test additional macOS display scaling modes and at least one Windows or Linux desktop.
- Verify keyboard-only navigation and screen-reader descriptions beyond the draw-button accessible text.

Automated model/controller coverage remains documented in the main README and the assignment compliance checklist. A successful automated test does not replace the manual UI checks listed above.
