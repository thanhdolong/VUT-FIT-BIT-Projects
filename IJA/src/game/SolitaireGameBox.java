// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.game;

import src.board.Game;
import src.commands.GameListen;
import src.commands.Moves;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javax.swing.*;

/**
 * Class implementing one GUI instance of Solitaire game.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
class SolitaireGameBox implements GameListen {

    private static final int WIDTH = 520;
    private static final int HEIGHT = 390;
    private Game gui_game;
    private String hint_str;
    private BorderPane layout;

    /**
     * Constructor of single GUI Solitaire game.
     * @param MARGIN defines space around the objects in the GUI
     * @param gameIndex identification number of viewed game instance
     */
    SolitaireGameBox(final int MARGIN, final int gameIndex) {

        // The great init ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -----

        layout = new BorderPane();
        gui_game = new Game(gameIndex);
        hint_str = "";


        // start Menu items
        Menu filemenu = new Menu("Game");

        MenuItem newgame = new MenuItem("New Game");
        newgame.setOnAction(event ->
                {   // User Clicked on "New Game"
                    Main.ChangeIt(gameIndex);
                }
        );


        MenuItem loadgame = new MenuItem("Load Game");
        loadgame.setOnAction(event ->
                {   // User Clicked on "Load Game"
                    AlertBox.load(gui_game);
                }
        );


        MenuItem savegame = new MenuItem("Save Game");
        savegame.setOnAction(event ->
                {   // User Clicked on "Save Game"
                    AlertBox.save(gui_game);
                }
        );


        MenuItem exitgame = new MenuItem("Exit Game");
        exitgame.setOnAction(event ->
                {   // User Clicked on "Exit Game"
                    System.exit(0);
                }
        );

        exitgame.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));

        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction((ActionEvent event) ->
                {
                    Moves.instance(gui_game.getGameIndex()).CallUndo(this.gui_game);
                }
        );


        // Construction of Dropdown
        filemenu.getItems().addAll(
                newgame,                // New Game (CTRL + N)
                new SeparatorMenuItem(),
                undo,                   // Undo (CTRL + Z)
                new SeparatorMenuItem(),
                loadgame,               // Load Game (CTRL + L)
                savegame,               // Save Game (CTRL + S)
                exitgame                // Exit Game (CTRL + Q)
        );

        final Tooltip tooltip = new Tooltip();

        Button hint = new Button("Hint");
        hint.setStyle("-fx-background-color: transparent;");
        hint.setOnMouseEntered(event ->
                {   // User has Clicked on "Hint"
                    hint_str = Moves.instance(gui_game.getGameIndex()).Hint(gui_game);
                    tooltip.setText(hint_str);
                }
        );

        //final Tooltip tooltip = new Tooltip();
        //tooltip.setText(hint_str);

        hint.setTooltip(tooltip);
        ToolTipManager.sharedInstance().setInitialDelay(0);

        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(filemenu);

        HBox menubars = new HBox(menubar, hint);
        menubars.setStyle("    -fx-padding: 0.0em 0.666667em 0.0em 0.666667em; /* 0 8 0 8 */" +
                "    -fx-background-color:" +
                "        linear-gradient(to bottom, derive(-fx-base,75%) 0%, -fx-outer-border 90%)," +
                "        linear-gradient(to bottom, derive(-fx-base,46.9%) 2%, derive(-fx-base,-2.1%) 95%);" +
                "    -fx-background-insets: 0 0 0 0, 1 0 1 0;" +
                "    -fx-background-radius: 0, 0 ;");

        // Insert menubar with layout on the top of screen
        layout.setTop(menubars);

        // scena s kartami na vykreslenie
        GridPane root = new GridPane();
        root.setStyle("     -fx-background-color: #334864");
        root.setHgap(MARGIN);
        root.setVgap(MARGIN);
        root.setPrefSize(WIDTH, HEIGHT);
        root.setPadding(new Insets(MARGIN));

        StartPackView firstStartPackView = new StartPackView(gui_game);
        TurnPackView firstTurnPackView = new TurnPackView(gui_game);

        root.add(firstStartPackView, 0, 1);
        root.add(firstTurnPackView, 1, 1);

        WorkPackView[] WPackView = new WorkPackView[7];
        src.game.FinishPackView[] finishPackView = new FinishPackView[4];

        for (int i = 0; i < 4; i++) {
            finishPackView[i] = new FinishPackView(gui_game, i);
            root.add(finishPackView[i], 3 + i, 1);
        }

        for (int i = 0; i < 7; i++) {
            WPackView[i] = new WorkPackView(gui_game, i);
            root.add(WPackView[i], i, 2);
        }

        // Insert game scene into layout
        layout.setCenter(root);
    }

    /**
     * Gets whole GUI layout.
     * @return layout
     */
    BorderPane getLayout() {
        return layout;
    }

    @Override public void gameStateChanged(Game game, int gameIndex) {

    }
}
