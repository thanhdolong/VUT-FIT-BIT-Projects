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
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Class implementing the viewing of the StartPack.
 *
 * @author Do Long Thanh
 */
public class StartPackView extends HBox implements GameListen {

    private static final String STYLE_HOVER = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.7) , 4, 0.0 , 0 , 2);";
    private static final String STYLE_NORMAL = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.6) , 0, 0 , 0 , 0);";

    /**
     * Constructor of Start Pack GUI viewer.
     * @param game the game instance
     */
    StartPackView(Game game) {
        setPadding(new Insets(3));
        final ImageView image = new ImageView(CardImage.getBack());

        image.setOnMouseEntered(pEvent -> { image.setStyle(STYLE_HOVER); });

        image.setOnMouseExited(pEvent -> image.setStyle(STYLE_NORMAL));

        image.setOnMousePressed(pEvent -> {});

        image.setOnMouseReleased((MouseEvent pEvent) -> {

            if (Moves.instance(game.getGameIndex()).fillPostCard("s")) {
                Moves.instance(game.getGameIndex()).clearPostCard();
            } else {
                Moves.instance(game.getGameIndex()).clearPostCard();
                Moves.instance(game.getGameIndex()).Start2Turn(game);
            }

        });

        getChildren().add(image);
        Moves.instance(game.getGameIndex()).addListener(this);

    }

    /**
     * Override method notifying StartPack to be redrawn.
     * Redraws only if the pack is empty.
     * @param game instance of game
     * @param gameIndex identificator of the game instance
     */
    @Override public void gameStateChanged(Game game, int gameIndex) {

        ImageView image = (ImageView) getChildren().get(0);

        if (game.get().getS().isEmpty()) image.setImage(CardImage.getCard("b"));
        else image.setImage(CardImage.getBack());

    }

}