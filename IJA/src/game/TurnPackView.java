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
import javafx.scene.layout.HBox;

/**
 * Class implementing the viewing of the TurnPack.
 *
 * @author Do Long Thanh
 */
public class TurnPackView extends HBox implements GameListen {
    private static final String STYLE_HOVER = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.7) , 4, 0.0 , 0 , 2);";
    private static final String STYLE_NORMAL = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.6) , 0, 0 , 0 , 0);";

    /**
     * Constructor of Turn Pack GUI viewer.
     * @param game the game instance
     */
    TurnPackView(Game game) {
        setPadding(new Insets(3));
        final ImageView image = new ImageView(CardImage.getBack());

        if (game.get().getT().isEmpty()) image.setImage(CardImage.getCard("transparent"));
        else image.setImage(CardImage.getCard(game.get().getT().get().toString()));

        image.setOnMouseEntered(pEvent -> {
            if (!game.get().getT().isEmpty()) image.setStyle(STYLE_HOVER);
        });

        image.setOnMouseExited(pEvent -> {
            if (!game.get().getT().isEmpty()) image.setStyle(STYLE_NORMAL);
        });

        image.setOnMouseReleased(pEvent -> {

            if (Moves.instance(game.getGameIndex()).fillPostCard("t")) {
                Moves.instance(game.getGameIndex()).clearPostCard();
            }

        });

        getChildren().add(image);
        Moves.instance(game.getGameIndex()).addListener(this);
    }

    /**
     * Override method notifying the TurnPack to be redrawn.
     * @param game instance of the game
     * @param gameIndex identificator of the game instance
     */
    @Override public void gameStateChanged(Game game, int gameIndex) {

        ImageView image = (ImageView) getChildren().get(0);

        if (game.get().getT().isEmpty()) image.setImage(CardImage.getCard("transparent"));
        else image.setImage(CardImage.getCard(game.get().getT().get().toString()));

    }
}
