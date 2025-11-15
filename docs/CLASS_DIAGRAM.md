# Class Diagram Test

```mermaid
classDiagram
    class MainController
    class GameModel
    class GameScene
    class GameChoice
    class InventoryItem
    class ItemType

    MainController --> GameModel
    MainController --> GameScene
    GameScene --> GameChoice
    GameModel --> InventoryItem
    InventoryItem --> ItemType
