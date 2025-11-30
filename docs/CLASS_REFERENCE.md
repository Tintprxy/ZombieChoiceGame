# Zombie Choice Game – Class Reference

This reference summarizes responsibilities, key fields, and public/protected APIs per class. It reflects the current code under `src/`.

## controller

### MainController
- Package: `controller`
- Role: Application orchestrator. Wires views, manages flow, loads stories, handles choices/fights, applies themes, and saves/loads.
- Key fields (selection):
  - `Stage stage`, `BorderPane rootPane`
  - `GameModel model`
  - `SceneLoader activeSceneLoader`, `String activeStoryFilePath`
  - `int activeSaveSlot`, `String playerName`
  - `GameScene currentScene`, `String lastHealthAppliedSceneId`, `Set<String> addItemProcessedScenes`
  - `boolean navigatingToGameOver`
- Key methods:
  - `public MainController(Stage stage)`
  - `public void startApp()`
  - `public void updateView()`
  - `protected void showTitleView()`
  - `private void showInstructionsView()`
  - `private void showChooseStoryView()`
  - `private void showInventoryChoiceView(SceneLoader sceneLoader, String startSceneId)`
  - `private void showSceneView(GameScene scene, SceneLoader sceneLoader)`
  - `public void addWeaponToInventory(InventoryItem item, String nextSceneId, SceneLoader sceneLoader)`
  - `private void handleFight(GameScene scene, int fightNumber, int decreaseDurAmount, int subHealthWin, int subHealthLose, String defaultWinSceneId, SceneLoader sceneLoader)`
  - Utility (selection): `computeDurabilityDecrease(int)`, `computeWinHealthPenalty(int)`, `computeLoseHealthPenalty(int)`, `selectSaveSlotAndPlayerName()`, `selectLoadSlotAndStart()`, `loadFromSlot(int)`, `autosaveIfPossible()`, `isWinningEnding(GameScene)`, `saveWinningEnding(GameScene)`, `ChooseStoryAfterWin(GameScene)`, `interceptGameOverIfNoHealth(ChoiceScreenView)`, `getCurrentSaveData()`

## model

### GameModel
- Role: Game session state and rules.
- Fields: `boolean isDarkMode`, `GameState currentState`, `int health`, `Map<ItemType, List<InventoryItem>> inventory`, `InventoryItem keyItem`, `boolean antidoteUsed`.
- Methods:
  - `public boolean isDarkMode()`
  - `public void toggleDarkMode()`
  - `public GameState getCurrentState()`
  - `public void setCurrentState(GameState state)`
  - `public void setHealth(int value)`
  - `public int getHealth()`
  - `public void subtractHealth(int amount)`
  - `public void addHealth(int amount)`
  - `public boolean addItem(InventoryItem item)`
  - `public boolean removeItem(String itemName)`
  - `public void removeFromInventory(InventoryItem item)`
  - `public Map<ItemType, List<InventoryItem>> getInventory()`
  - `public boolean consumeItem(InventoryItem item)`
  - `public void clearInventory()`
  - `public void resetHealth()`
  - `public void reloadInventory()`
  - `public void removeBrokenWeapons()`
  - `public boolean decrementKeyItemDurabilityByName(String name, int amount, boolean removeOnZero)`
  - `public InventoryItem getKeyItem()`, `public void setKeyItem(InventoryItem)`
  - `public boolean isAntidoteUsed()`, `public void setAntidoteUsed(boolean)`

### GameScene
- Role: Immutable-ish scene node with prompt, choices, and metadata.
- Notable fields: `id`, `prompt`, `healthChange`, `choices`, `addItem`, `threatLevel`, `fightNumber`, `bitten`, `eaten`, `ending`, `newKeyItem`, `imagePath`, `rawJson`.
- Methods:
  - Constructors: multiple overloads for health/addItem/threat/eaten variants
  - `public String getId()`, `public String getPrompt()`, `public int getHealthChange()`
  - `public List<GameChoice> getChoices()`
  - `public int getThreatLevel()`, `public void setThreatLevel(int)`
  - `public int getFightNumber()`, `public void setFightNumber(int)`
  - `public boolean hasAddItem()`, `public InventoryItem getAddItem()`
  - `public void setNewKeyItem(String)`, `public String getNewKeyItem()`
  - `public boolean isBitten()`, `public void setBitten(boolean)`
  - `public boolean isEaten()`, `public void setEaten(boolean)`
  - `public boolean isWinEnding()`, `public String getEnding()`, `public void setEnding(String)`
  - `public String getImagePath()`, `public void setImagePath(String)`
  - `public JsonObject getRawJson()`, `public void setRawJson(JsonObject)`

### GameChoice
- Fields: `label`, `imagePath`, `nextId`, `healthEffect`, `currentSceneId`.
- Methods: `getLabel()`, `getImagePath()`, `getNextId()`, `getHealthEffect()`, `setCurrentSceneId(String)`, `getCurrentSceneId()`

### InventoryItem
- Fields: `name`, `type`, `healthRestore`, `durability`, `power`.
- Methods: `getName()`, `getType()`, `getHealthRestore()`, `getDurability()`, `getPower()`
- Helpers: `isConsumable()`, `isWeapon()`, `isKeyItem()`, `isBroken()`, `isRepairable()`, `isUsable()`
- Mutators: `setDurability(int)`, `decreaseDurability(int)`, `repair(int)`

### ItemType (enum)
- Values: `WEAPON`, `CONSUMABLE`, `KEY_ITEM`

### GameState (enum)
- Values: `TITLE`, `INSTRUCTIONS`, `INTRO`, `FIRST_CHOICE`, `ENDING`

### InventoryLoader
- Static utilities for inventory JSON.
- Methods:
  - `public static List<InventoryItem> load(String path)`
  - `public static InventoryItem loadKeyItemFromJson(String path)`

### SceneLoader
- Parses story JSON into `GameScene` map.
- Methods:
  - `public SceneLoader(String pathToJson)`
  - `public GameScene getSceneById(String id)`
  - `public java.util.Map<String, GameScene> getScenes()`

### SaveData
- Serializable DTO for a save slot.
- Fields (all public):
  - `String playerName`, `String storyFilePath`, `String currentSceneId`
  - `int health`, `boolean darkMode`
  - `java.util.List<String> addItemProcessedScenes`, `String lastHealthAppliedSceneId`
  - `java.util.Map<ItemType, java.util.List<InventoryItem>> inventory`
  - `long lastUpdatedEpochMillis`, `java.util.List<String> completedWinSceneIds`

### SaveManager
- Static JSON persistence helpers for three slots (1..3).
- Methods:
  - `public static void ensureDir()`
  - `public static boolean exists(int slot)`
  - `public static void delete(int slot)`
  - `public static void save(int slot, SaveData data)`
  - `public static java.util.Optional<SaveData> load(int slot)`
  - `public static java.util.Optional<String> peekPlayerName(int slot)`
  - `public static java.util.Optional<String> peekSceneId(int slot)`

## view

### TopBarView
- Role: Shared header with theme toggle, reset, and choose-story.
- Fields: `public Button toggleButton`, `public Button resetButton`, `public Button chooseStoryButton`.
- Methods: `public void showChooseStoryButton(boolean)`, `public void setLeft(Node)`, `public void applyTheme(boolean)`

### TitleView
- Role: Landing screen with Start/Load/Instructions and Winning Photo Album.
- Fields: `public Button startButton`, `public Button loadButton`, `public Button instructionsButton`, `public TopBarView topBar`
- Methods: `public TitleView()`, `public void applyTheme(boolean)`, `public void setWinningAlbumHandler(java.util.function.IntConsumer)`

### InstructionsView
- Role: Instructions screen (style and content applied via controller). No special API beyond standard Node.

### StoryTurnstileView
- Role: Choose story (Drive/Walk), shows completion badge per save.
- Constructors: `public StoryTurnstileView(boolean darkMode)`, `public StoryTurnstileView(boolean darkMode, int activeSaveSlot)`
- Methods: `public Button getStory1Button()`, `public Button getStory2Button()`, `public TopBarView getTopBar()`, `public VBox getStory1Box()`, `public VBox getStory2Box()`, `public void applyTheme(boolean)`

### InventoryChoiceView
- Role: Pick a preset inventory loadout.
- Constructor: `public InventoryChoiceView(boolean darkMode, int health, Map<ItemType, List<InventoryItem>> inventory)`
- Methods: `public TopBarView getTopBar()`, `public Button getHealthHeavyButton()`, `public Button getAttackHeavyButton()`, `public Button getBalancedButton()`, `public void applyTheme(boolean)`

### ChoiceScreenView
- Role: Core scene UI with prompt, choices, inventory, and top bar.
- Constructor:
  - `public ChoiceScreenView(int health, String prompt, List<GameChoice> choices, boolean darkMode, Map<ItemType, List<InventoryItem>> inventory, GameModel model, java.util.function.Consumer<GameChoice> onChoice, Runnable onToggleTheme, Runnable onReset, Runnable onChooseStory, java.util.function.Consumer<InventoryItem> onConsumeItem)`
- Methods: `public int getHealth()`, `public String getPromptText()`, `public java.util.List<GameChoice> getChoices()`, `public boolean isDarkMode()`, `public java.util.Map<ItemType, java.util.List<InventoryItem>> getInventory()`, `public java.util.function.Consumer<GameChoice> getOnChoiceSelected()`, `public Runnable getOnToggleTheme()`, `public void applyTheme(boolean)`, `public TopBarView getTopBar()`

### WinningPhotoAlbumView
- Role: Shows unlocked WIN scenes as a horizontal photo album; supports custom titles and theming.
- Constructor: `public WinningPhotoAlbumView(SaveData saveData, Runnable onBackPressed)`
- Methods: `public TopBarView getTopBar()`, `public void applyTheme(boolean)`, `public javafx.scene.control.Label createDisplayLabelForSceneId(String)`, `public void setTitleForSceneId(String, String)`, `public void setTitles(java.util.Map<String,String>)`, `public boolean loadTitlesFromJson(java.io.File)`
