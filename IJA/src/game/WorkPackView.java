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
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Class implementing the viewing of all WorkPacks.
 *
 * @author Do Long Thanh
 */
public class WorkPackView extends StackPane implements GameListen {

    private static final String STYLE_HOVER = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.7) , 3.5, 0.0 , 0 , 1.5);";
    private static final String STYLE_NORMAL = "-fx-effect: dropshadow(three-pass-box , rgba(0,0,0,0.6) , 0, 0 , 0 , 0);";
    private static final int Y_OFFSET = 11;
    private int indexOfColumn;

    /**
     * Constructor of Work Pack GUI viewer.
     * @param game the game instance
     * @param i identificator of the game instance
     */
    WorkPackView(Game game, int i) {
        indexOfColumn = i;
        setPadding(new Insets(3));
        setAlignment(Pos.TOP_CENTER);
        buildLayout(game, indexOfColumn);
        Moves.instance(game.getGameIndex()).addListener(this);
    }

    /**
     * This method redraws one WorkPack.
     * @param game instance of the game
     * @param i index of the FinishPack to be redrawn
     */
    private void buildLayout(Game game, int i) {
        getChildren().clear();

        int offset = 0;
        int SizeOfPack = game.get().getW(i).size();

        final ImageView emptyimage;
        emptyimage = new ImageView(CardImage.getCard("transparent"));
        emptyimage.setTranslateY(Y_OFFSET * offset);
        getChildren().add(emptyimage);

        emptyimage.setOnMouseReleased((MouseEvent pEvent) -> {

            String postcard = "w" + i + "0";
            if (Moves.instance(game.getGameIndex()).fillPostCard(postcard)) {
                String from = Moves.instance(game.getGameIndex()).getPostCard();
                switch (from.substring(0, 1)) {
                    case "t":
                        Moves.instance(game.getGameIndex()).Turn2Work(game, i);
                        break;
                    case "w":
                        if (from.substring(0, 2).equals(postcard.substring(0, 2))) break;
                        int WPIndex = Integer.parseInt(from.substring(1, 2));
                        int CardIndex = Integer.parseInt(from.substring(2, 3));
                        Moves.instance(game.getGameIndex()).Work2Work(game, WPIndex, i, CardIndex);
                        break;
                    case "f":
                        int FPIndex = Integer.parseInt(from.substring(1, 2));
                        Moves.instance(game.getGameIndex()).Work2Finish(game, FPIndex, i);
                        break;
                    default:
                        break;
                }
                Moves.instance(game.getGameIndex()).clearPostCard();
            }

        });

        for (int j = 1; j <= SizeOfPack; j++) {
            final ImageView image;
            if (j == SizeOfPack) image = new ImageView(CardImage.getCard(game.get().getW(i).get().toString()));
            else image = new ImageView(CardImage.getCard(game.get().getW(i).get(j - 1).toString()));

            image.setOnMouseEntered(pEvent -> {
                image.setStyle(STYLE_HOVER);
            });

            image.setOnMouseExited(pEvent -> image.setStyle(STYLE_NORMAL));


            image.setTranslateY(Y_OFFSET * offset);
            offset++;
            getChildren().add(image);

            int finalJ = j;
            image.setOnMouseReleased((MouseEvent pEvent) -> {

                String postcard = "w" + i + (finalJ - 1);
                if (Moves.instance(game.getGameIndex()).fillPostCard(postcard)) {
                    String from = Moves.instance(game.getGameIndex()).getPostCard();
                    switch (from.substring(0, 1)) {
                        case "t":
                            Moves.instance(game.getGameIndex()).Turn2Work(game, i);
                            break;
                        case "w":
                            if (from.substring(0, 2).equals(postcard.substring(0, 2))) break;
                            int WPIndex = Integer.parseInt(from.substring(1, 2));
                            int CardIndex = Integer.parseInt(from.substring(2, 3));
                            Moves.instance(game.getGameIndex()).Work2Work(game, WPIndex, i, CardIndex);
                            break;
                        case "f":
                            int FPIndex = Integer.parseInt(from.substring(1, 2));
                            Moves.instance(game.getGameIndex()).Finish2Work(game, FPIndex, i);
                            break;
                        default:
                            break;
                    }
                    Moves.instance(game.getGameIndex()).clearPostCard();
                }

            });


        }
    }

    /**
     * Override method notifying one of the four WorkPacks to be redrawn.
     * @param game instance of the game
     * @param gameIndex identificator of the game instance
     */
    @Override public void gameStateChanged(Game game, int gameIndex) {
        buildLayout(game, indexOfColumn);
    }

}
