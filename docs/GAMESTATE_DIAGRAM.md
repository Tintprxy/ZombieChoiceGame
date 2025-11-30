# Zombie Choice Game – GameState Diagram

This state machine diagram reflects the `model.GameState` enum and how the controller uses states today. Some enum values are defined but not actively transitioned in code.

```mermaid
stateDiagram-v2
    [*] --> TITLE
    TITLE --> INSTRUCTIONS: instructionsButton
    INSTRUCTIONS --> TITLE: back
    note right of TITLE: Primary navigation is view-driven.
    note right of INSTRUCTIONS: State machine used sparingly.

    TITLE --> ENDING: programmatic (on certain flows)
    state UNUSED_STATES {
        [*] --> INTRO
        INTRO --> FIRST_CHOICE
        FIRST_CHOICE --> ENDING
    }
    note right of UNUSED_STATES: INTRO and FIRST_CHOICE are defined
        but not actively transitioned in MainController.
```
