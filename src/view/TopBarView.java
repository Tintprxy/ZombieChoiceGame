package view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class TopBarView extends HBox {
    public final Button toggleButton;
    public final Button resetButton;
    public final Button chooseStoryButton; 

    public TopBarView() {
        this.toggleButton = new Button("Theme");
        this.resetButton = new Button("Reset");
        this.chooseStoryButton = new Button("Choose Story");
        chooseStoryButton.setVisible(false);
        chooseStoryButton.setManaged(false);

        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(12);
        setPadding(new Insets(10, 16, 10, 16));

        for (Button b : new Button[]{toggleButton, resetButton, chooseStoryButton}) {
            b.setMinWidth(120);
            b.setPrefWidth(140);
            b.setMaxWidth(180);
        }

        getChildren().addAll(toggleButton, resetButton, chooseStoryButton);
    }

    public void showChooseStoryButton(boolean show) {
        chooseStoryButton.setVisible(show);
        chooseStoryButton.setManaged(show);
    }

    public void applyTheme(boolean isDarkMode) {
        for (Button b : new Button[]{toggleButton, resetButton, chooseStoryButton}) {
            Theme.applyButtonStyle(b, isDarkMode);
        }
        toggleButton.setText(isDarkMode ? "Light Mode" : "Dark Mode");
    }
}