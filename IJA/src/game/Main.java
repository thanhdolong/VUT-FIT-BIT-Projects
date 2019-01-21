// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Main class containing methods running the GUI version of Solitaire.
 *
 * @author Do Long Thanh
 */
public class Main extends Application {

    private static final int MARGIN = 11;
    private static final String TITLE = "Solitaire";
    private static final int WIDTH = 520;
    private static final int HEIGHT = 390;
    private static SolitaireGameBox[] gameBox = new SolitaireGameBox[4];
    private static Stage window;

    /**
     * Launches the Solitaire game.
     * @param args none used.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Sets new game windows if "New Game" is used.
     * @param gameIndex identificator of the game instance
     */
    static void ChangeIt(int gameIndex) {

        for (int i = gameIndex; i < 4; ++i) {

            if (!gameBox[i].getLayout().isVisible()) {

                if (i == 1) {

                    GridPane layout = new GridPane();
                    layout.add(gameBox[0].getLayout(), 0, 0);
                    layout.add(gameBox[1].getLayout(), 1, 0);
                    layout.add(gameBox[2].getLayout(), 0, 1);
                    layout.add(gameBox[3].getLayout(), 1, 1);

                    layout.setHgap(5);
                    layout.setVgap(5);
                    layout.setPadding(new Insets(5));
                    layout.setStyle("-fx-background-color: #bdc3c7;");
                    Scene scene1 = new Scene(layout, (WIDTH * 2) + 10, (HEIGHT * 2) + 10);

                    window.setScene(scene1);
                    window.centerOnScreen();
                }

                gameBox[i].getLayout().setVisible(true);
                break;
            }

        }

    }

    /**
     * Draws winning animation.
     */
    public static void WIN() {

        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("You WON");
        window.setWidth(480);
        window.setHeight(360);

        StackPane layout = new StackPane();
        layout.setStyle("-fx-background-image: url(\"/img/images/win.gif\");");
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        scene.setOnMouseClicked(event -> window.close());
        window.setResizable(false);
        window.setScene(scene);
        window.centerOnScreen();
        window.showAndWait();

    }

    /**
     * Override method constructing the GUI.
     * @param primaryStage the main GUI window of Solitaire
     * @throws Exception ...
     */
    @Override public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(TITLE);

        for (int i = 0; i < 4; i++) {
            gameBox[i] = new SolitaireGameBox(MARGIN, i);
        }

        for (int i = 1; i < 4; i++) {
            gameBox[i].getLayout().setVisible(false);
        }


        GridPane firstgame = new GridPane();


        firstgame.add(gameBox[0].getLayout(), 0, 0);
        Scene scene2 = new Scene(firstgame, WIDTH, HEIGHT);

        window.setScene(scene2);
        window.centerOnScreen();
        window.show();

    }

}