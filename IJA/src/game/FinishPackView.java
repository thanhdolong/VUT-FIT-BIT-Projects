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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Class implementing the viewing of all FinishPacks.
 *
 * @author Do Long Thanh
 */
public class FinishPackView extends StackPane implements GameListen {

    private static final String STYLE_HOVER = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.7) , 4, 0.0 , 0 , 2);";
    private static final String STYLE_NORMAL = "-fx-effect: dropshadow(three-pass-box , rgba(0.6,0,0,0.6) , -1, 0.0 , 0 , 0.1);";
    private int indexOfColumn;

    /**
     * Constructor of Finish Pack GUI viewer.
     * @param game the game instance
     * @param i identificator of the game instance
     */
    FinishPackView(Game game, int i) {
        indexOfColumn = i;
        buildFinishPackLayout(game, indexOfColumn);
        Moves.instance(game.getGameIndex()).addListener(this);
    }

    /**
     * This method redraws one FinishPack.
     * @param game instance of the game
     * @param i index of the FinishPack to be redrawn
     */
    private void buildFinishPackLayout(Game game, int i) {
        final ImageView image;

        if (game.get().getF(i).isEmpty()) image = new ImageView(CardImage.getCard("finish"));
        else image = new ImageView(CardImage.getCard(game.get().getF(i).get().toString()));

        image.setOnMouseEntered(pEvent -> image.setStyle(STYLE_HOVER));
        image.setOnMouseExited(pEvent -> image.setStyle(STYLE_NORMAL));

        image.setOnMouseReleased((MouseEvent pEvent) -> {

            String postcard = "f" + (i);

            if (Moves.instance(game.getGameIndex()).fillPostCard(postcard)) {

                String from = Moves.instance(game.getGameIndex()).getPostCard();

                switch (from.substring(0, 1)) {
                    case "t":
                        Moves.instance(game.getGameIndex()).Turn2Finish(game, i);
                        break;
                    case "w":
                        int WPIndex = Integer.parseInt(from.substring(1, 2));
                        Moves.instance(game.getGameIndex()).Work2Finish(game, WPIndex, i);
                        break;
                    case "f":
                        int FPIndex = Integer.parseInt(from.substring(1, 2));
                        Moves.instance(game.getGameIndex()).Finish2Finish(game, FPIndex, i);
                        break;
                    default:
                        break;
                }

                Moves.instance(game.getGameIndex()).addListener(this);
                Moves.instance(game.getGameIndex()).clearPostCard();

            }

        });

        getChildren().add(image);

    }

    /**
     * Override method notifying one of the four FinishPacks to be redrawn.
     * @param game instance of the game
     * @param gameIndex identificator of the game instance
     */
    @Override public void gameStateChanged(Game game, int gameIndex) {
        buildFinishPackLayout(game, indexOfColumn);
    }

}