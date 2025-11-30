# Zombie Choice Game – Architecture

This document describes the high-level architecture, data flow, and key interactions in the Zombie Choice Game. It is based on the current code in `src/` and aims for accuracy and clarity.

## Overview

- UI built with JavaFX (views in `src/view`).
- Presentation logic orchestrated by `controller.MainController`.
- Domain and persistence in `src/model` (game state, scene/story loading, inventory, saves).
- Story content, inventory presets, and saves stored as JSON in `src/data`.

## Layering

- Controller
  - `controller.MainController`: App entry and flow control; wires views to model actions; loads stories; handles choices, combat, theming, and save operations.

- Model
  - `model.GameModel`: Game state (health, dark mode, inventory), inventory rules, and item consumption.
  - `model.SceneLoader`: Parses story JSON into `GameScene` graph; exposes `getSceneById` and `getScenes`.
  - `model.GameScene`, `model.GameChoice`, `model.InventoryItem`, `model.ItemType`, `model.GameState`.
  - `model.SaveManager`: JSON-based persistence for three save slots; static helpers for peeking and CRUD.
  - `model.SaveData`: Serializable DTO for the entire save snapshot.

- View
  - `view.TitleView`, `view.InstructionsView`
  - `view.StoryTurnstileView` (choose story), `view.InventoryChoiceView` (choose preset)
  - `view.ChoiceScreenView` (core scene UI with choices, inventory, and top bar)
  - `view.WinningPhotoAlbumView` (unlocked win screens)
  - `view.TopBarView` (theme/reset/choose-story controls)
  - `view.Theme` (utility for consistent styles)

## Data Flow

1. MainController constructs views and binds handlers.
2. Player actions (button clicks) trigger controller methods.
3. Controller updates `GameModel`, transitions scenes via `SceneLoader`, and refreshes views.
4. Saves/loads go through `SaveManager` with `SaveData` snapshots.
5. Theming is toggled in the top bar and applied to active views.

## Story JSON Structure (observed)

- Scene (array element in the story file):
  - `id: string` (required)
  - `prompt: string`
  - `healthChange: int` (0 default; -1 is instant death)
  - `choices: [{ label, imagePath, nextId|id, healthEffect }]`
  - `addItem: { name, type, healthRestore?, durability?, power? }`
  - `threatLevel: int` (-1 = no fight)
  - `fightNumber: int` (default 1)
  - `bitten: boolean`
  - `eaten: boolean`
  - `ending: string` (e.g., "WIN")
  - `imagePath: string`

`SceneLoader` stores the raw `JsonObject` in each `GameScene` for feature flags (e.g., `decrementKeyItemDurability`, `useAntidote`).

## Gameplay Loop (simplified)

1. Title: `startApp()` -> `updateView()` -> `showTitleView()`.
2. Start: select save slot + player name -> `showChooseStoryView()`.
3. Choose Story: select Drive/Walk -> set `activeStoryFilePath` and `activeSceneLoader`.
4. Inventory Preset: pick Health/Attack/Balanced -> model inventory adjusted -> `showSceneView(start)`.
5. Scene Screen: display prompt, choices, inventory; apply `healthChange` once per scene; process `addItem` once.
6. Choice Handling:
   - If a fight (threatLevel >= 0 and label contains "fight"): `handleFight(...)` computes outcome; may reduce durability/health; routes to win/lose scene.
   - Else: go to `nextId` via `SceneLoader.getSceneById` and recurse `showSceneView(next)`.
7. Ending:
   - On `isWinEnding()`, persist win id to `SaveData.completedWinSceneIds`.
   - If health <= 0, disable choices and intercept to a reset/title flow.

## Persistence

- `SaveManager.save(int, SaveData)`: JSON serialize to `src/data/saves/slotN.json`.
- `SaveManager.load(int)`: JSON deserialize.
- `SaveManager.peekPlayerName/peekSceneId(int)`: convenience to label save slots.
- Data captured: player, story path, current scene, health, dark mode, processed add-item scenes, last scene to which health was applied, inventory, last updated millis, and completed win ids.

## Dependencies

- JavaFX (controls/graphics) for UI.
- Gson (JSON) for story parsing and save files. If building without Maven/Gradle, place `gson-2.10.1.jar` under `lib/` and include it on the classpath when compiling/running.

## Theming

- Toggled via `TopBarView.toggleButton` handlers.
- Views expose `applyTheme(boolean)` to re-style.
- Inventory and top bar use `view.Theme` for colors and button styling.

## Assets

- Images under `imgs/` are used by views and as `imagePath` in scenes. `WinningPhotoAlbumView` uses `SceneLoader` to resolve per-win images.

---

## Sequence Diagram (Gameplay)

See `docs/GAMEPLAY_SEQUENCE.md`.

## State Machine (GameState)

See `docs/GAMESTATE_DIAGRAM.md`.
