# Manual UI Test Record

This record separates checks performed in the running Swing application from behavior covered only by automated tests or code inspection.

## Environment

- Date: 14 November 2026
- Platform: macOS on a Retina display
- Build: `target/sorry-game-1.0.0-SNAPSHOT.jar`
- Window: fixed 1100 × 780 logical pixels, centered and non-resizable

## Verified in the running application

| Area | Result | Evidence observed |
|---|---|---|
| Two-player baseline | Passed | Setup, draw, Fold, pawn choice, pawn movement, turn advancement, and card-2 repeat were exercised in the earlier smoke test. |
| Three-player setup | Passed | The title displayed `SORRY GAME - 3 Players`; red, yellow, and blue Start/Home/Safety areas and both pawns for each player were visible. |
| First-player selection | Passed with split evidence | Random setup selected Player 3 and Game Info showed `Player 3 (Blue)`. Player 1 was explicitly selected in the running setup dialog. All valid explicit indices for 2-, 3-, and 4-player games are exercised by `TurnManagerTest`. |
| Three-player wraparound | Passed | Player 3 folded an unplayable card 4; the next draw correctly selected Player 1. |
| Four-player setup | Passed | The title displayed `SORRY GAME - 4 Players`; the green Start, Home, Safety track, and two green pawns were visible and aligned. |
| Four-player dialog layout | Passed | The card-2 `Choose Pawn` dialog was centered, readable, and contained two fully visible pawn buttons. |
| Window-close Cancel | Passed | Choosing Cancel from the close confirmation left the game open and retained the current state. |
| Save during a turn | Passed | Choosing Save from the close confirmation during an unfinished turn displayed `Save is available between turns. Finish or fold this turn first.` and did not close the game. |
| Isolated save creation | Passed | The game was launched with a temporary `user.home`, Save created a valid version-2 snapshot under that temporary home, and the process exited normally. |
| Resume shortcut and chooser | Passed | Command-O opened `Choose Saved Game`, selected the isolated snapshot, and displayed the save-current-game confirmation. Control-O is the corresponding shortcut on Windows and Linux. |
| Snapshot restoration | Passed | A valid disposable fixture with `nextPlayerIndex=1` and `nextCardIndex=5` restored as `Player 2 (Yellow)` with 39 cards left. Choosing No at the save-copy prompt did not create another file. |
| Save-copy Cancel | Passed | Cancel at the save-current-game confirmation returned to the existing game without restoring the selected snapshot. |
| Invalid save | Passed | A disposable unsupported-version file displayed `Resume failed` with `Missing or unsupported snapshot version`. |
| Wrong player count | Passed | Loading a valid three-player snapshot into a two-player window displayed a specific restart instruction and preserved the current game. |
| Playbook layout | Passed by direct Swing rendering | The button was rendered above the deck without overlap. All six two-page spreads were rendered from the real dialog code; figures, English text, page numbers, spine, navigation controls, and final-page content were visible without clipping or unwanted scrollbars. |
| Retina rendering | Passed at the tested display setting | Board borders, card images, labels, pawns, buttons, and dialogs remained aligned with no visible clipping. |

The pre-existing save at `~/.sorry-game/snapshot.properties` was inspected read-only and was not overwritten during these checks. The temporary home, disposable snapshot, and screen captures were deleted after verification.

## Resolved observation

The smoke test found that Fold enabled the next draw but left the previous card and player text visible. The UI now clears the completed card and identifies the next player immediately after a move, Fold, or voluntary redraw. Turn-order behavior itself was already correct.

## Deterministic automated game scenarios

The JUnit suite now runs complete scripted routes from Start through the outer track and Safety into Home for 2-, 3-, and 4-player games. Every move is applied through `Controller.makeMove`, turn order advances through `TurnManager`, both winning pawns must reach Home, and the expected winner is asserted. These repeatable scenarios verify the rule state from setup to completion; they are not a claim that three long random human matches were played through the Swing dialogs.

The same suite covers card 7 split movement, both card 10 directions, card 11 swapping, card 12 movement, a multiplayer Sorry replacement, every explicit first-player index, invalid and mismatched snapshots, unusable save locations, and POSIX permission denial when supported by the host.

## Still requiring manual testing

- Perform long random human playthroughs for all three player counts; deterministic start-to-winner scenarios already cover the underlying rules and turn order.
- Hand-operate every setup selector value; the running UI verified Random and Player 1, while all indices are covered below Swing by `TurnManagerTest`.
- Finish hand-operated Back, Cancel, and window-close checks for cards 7, 10, 11, 12, and Sorry!. Their movement branches are covered by controller tests, but modal focus behavior remains a Swing-only concern.
- Exercise the save-copy Yes branch and a visible permission-error dialog. Cancel, invalid-file, wrong-player-count, and storage/permission failures are already covered as described above.
- Test additional macOS display scaling modes and at least one Windows or Linux desktop.
- Verify keyboard-only navigation and screen-reader descriptions beyond the draw-button accessible text.
- Hand-operate the Playbook button and Cmd/Ctrl+P on Windows or Linux; its complete Swing layout and packaged resources were verified on macOS, while shell-driven clicking was blocked by macOS accessibility policy.

Automated model/controller coverage remains documented in the main README and the assignment compliance checklist. A successful automated test does not replace the manual UI checks listed above.
