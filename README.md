# Zombie Choice Game

A JavaFX choice-driven survival mini‑game. Make tough calls, manage your inventory, and try to win without getting bitten.

## Features
- JSON-driven branching story engine
- Inventory system with item types and durability
- Threat-level combat with weapon selection logic
- 3-save-slot persistence + winning album
- Dark/Light mode UI

## Demo
*(Video coming soon)*

## Screenshot
![Game Screenshot](imgs/screenshot_placeholder.png)

## Quick Start

Recommended: run in VS Code or IntelliJ with JavaFX support.

### Prerequisites
- JDK 17+ (LTS recommended)
- JavaFX SDK (matching your JDK and OS)
- VS Code with Java Extension Pack (or IntelliJ IDEA)

### Run in VS Code (easiest)
1. Open this folder in VS Code.
2. Install extensions if prompted (Java, JavaFX helpers, Markdown Mermaid for docs preview).
3. Open `src/Main.java` and click “Run”.
   - If JavaFX modules aren’t found, point VS Code to your JavaFX SDK or run with VM args like below.

### Run from command line (Windows PowerShell)
Replace `C:\path\to\javafx\lib` with your JavaFX SDK `lib` directory. Gson is required; place the jar under `lib/` first.

Download Gson jar (one-time):

```powershell
# from e:\programs\ZombieChoiceGame
New-Item -ItemType Directory -Path lib -Force | Out-Null
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar" -OutFile "lib\gson-2.10.1.jar"
```

```powershell
# Compile
javac -d out -cp "src;lib\gson-2.10.1.jar" --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.graphics,javafx.fxml src\Main.java src\controller\MainController.java src\model\*.java src\view\*.java

# Run
java -cp "out;lib\gson-2.10.1.jar" --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.graphics,javafx.fxml Main
```

If you prefer Maven/Gradle, you can add a build later; this repo currently runs as a simple JavaFX app without a build tool.

## Project Structure
```
src/
  Main.java
  controller/
  model/
  view/
docs/
  ARCHITECTURE.md
  CLASS_DIAGRAM.md
  GAMEPLAY_SEQUENCE.md 
  GAMESTATE_DIAGRAM.md 
imgs/
lib/
```

## Documentation
- Architecture: `docs/ARCHITECTURE.md` (high‑level design, data flow)
- Class Diagram: `docs/CLASS_DIAGRAM.md` (Mermaid, preview in VS Code)
- Extras (optional):
  - Gameplay Sequence: `docs/GAMEPLAY_SEQUENCE.md`
  - GameState Diagram: `docs/GAMESTATE_DIAGRAM.md`
  - Class Reference: `docs/CLASS_REFERENCE.md`

## Notes
- Story content and saves are JSON under `src/data/`.
- Diagrams are written in Mermaid; preview via VS Code (Ctrl+Shift+V) with the Mermaid preview extension enabled.
- Images are loaded from `imgs/` and scene `imagePath` fields.
