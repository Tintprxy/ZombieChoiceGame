# Zombie Choice Game – Gameplay Sequence

This sequence diagram captures the primary gameplay flow from launch through story selection, inventory preset, choices (including combat), and win handling.

```mermaid
sequenceDiagram
    actor Player
    participant MC as MainController
    participant TV as TitleView
    participant ST as StoryTurnstileView
    participant IL as InventoryChoiceView
    participant CSV as ChoiceScreenView
    participant SL as SceneLoader
    participant GM as GameModel
    participant SM as SaveManager

    Player->>MC: startApp()
    MC->>TV: showTitleView()
    Player->>TV: Click "Start Game"
    TV->>MC: selectSaveSlotAndPlayerName()
    MC->>ST: showChooseStoryView()
    Player->>ST: Select Drive/Walk
    ST->>MC: set activeStoryFilePath
    MC->>SL: new SceneLoader(path)
    MC->>IL: showInventoryChoiceView(SL, "start")
    Player->>IL: Choose preset (Health/Attack/Balanced)
    IL->>MC: Apply preset
    MC->>GM: clearInventory() + addItem(...)
    MC->>CSV: showSceneView(start, SL)
    note over MC,GM: Apply scene healthChange once
    alt scene has addItem and not yet processed
        MC->>GM: addItem(scene.addItem)
    end
    Player->>CSV: Click a choice
    alt Fight choice and threatLevel >= 0
        CSV->>MC: handleFight(...)
        MC->>GM: durability/health updates
        MC->>SL: getSceneById(win/lose)
        MC->>CSV: showSceneView(next, SL)
    else Normal choice
        CSV->>MC: choice selected
        MC->>SL: getSceneById(nextId)
        MC->>CSV: showSceneView(next, SL)
    end
    alt Win ending
        MC->>SM: Save victory in completedWinSceneIds
    end
```
