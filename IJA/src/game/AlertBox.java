// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.game;

import src.board.Game;
import src.commands.Moves;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Stack;

/**
 * Class implementing GUI alert box.
 *
 * @author Do Long Thanh
 */
public class AlertBox {
    private static final int WIDTH = 350;
    private static final int HEIGHT = 250;
    private static final int MARGIN = 11;

    /**
     * This method creates alert box with list of saved games. <br>
     * User can choose a game from the list and load it into game instance by clicking load.
     * @param game identificator of game instance, where should be the game loaded
     */
    static void load(Game game) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Load game");
        window.setWidth(WIDTH);
        window.setHeight(HEIGHT);


        GridPane layout = new GridPane();
        layout.setStyle("-fx-background-color: #34495e;");
        layout.setHgap(MARGIN);
        layout.setVgap(MARGIN);
        layout.setPadding(new Insets(MARGIN));

        Label label = new Label();
        label.setText("Load game");
        label.setTextFill(Color.web("#ecf0f1"));
        layout.add(label, 0, 0);
        ObservableList<String> wordsList = FXCollections.observableArrayList();

        Stack<String> string = Moves.instance(game.getGameIndex()).getSavedGames();
        while (!string.empty()) {
            wordsList.add(string.pop());
        }

        ListView<String> listViewOfStrings = new ListView<>(wordsList);
        layout.add(listViewOfStrings, 0, 1, 2, 1);

        Button close = new Button("Cancel");
        close.setOnAction(event -> window.close());
        layout.add(close, 0, 2);

        Button load = new Button("Load game");
        load.setOnAction((ActionEvent event) -> {
            if (listViewOfStrings.getSelectionModel().getSelectedItem() != null) {
                Moves.instance(game.getGameIndex()).LoadGame(game, listViewOfStrings.getSelectionModel().getSelectedItem());
                System.out.println("Load: " + listViewOfStrings.getSelectionModel().getSelectedItem());
            }
            window.close();
        });
        layout.add(load, 1, 2);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * This method creates text box to name the actual game to be saved
     * @param game identificator of game instance to be saved
     */
    static void save(Game game) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Save game");
        window.setWidth(WIDTH);
        window.setHeight(200);

        GridPane layout = new GridPane();
        layout.setStyle("-fx-background-color: #34495e;");
        layout.setHgap(MARGIN);
        layout.setVgap(MARGIN);
        //layout.setPadding(new Insets(MARGIN));

        Label label = new Label();
        label.setText("Save game");
        label.setTextFill(Color.web("#ecf0f1"));
        layout.add(label, 0, 0);

        TextField saveinput = new TextField("untitled");
        layout.add(saveinput, 0, 1, 2, 1);

        Button close = new Button("Cancel");
        close.setOnAction(event -> window.close());
        layout.add(close, 0, 2);

        Button save = new Button("Save game");
        save.setOnAction((ActionEvent event) -> {
            System.out.println(saveinput.getText());
            Moves.instance(game.getGameIndex()).SaveGame(game, saveinput.getText());
            window.close();
        });
        layout.add(save, 1, 2);


        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);

        window.setResizable(false);
        window.setScene(scene);
        window.showAndWait();
    }
}
