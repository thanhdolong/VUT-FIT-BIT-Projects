// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.game;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing image linking with game logic.
 *
 * @author Do Long Thanh
 */
public class CardImage {
    private static final String IMAGE_LOCATION = "/img/images/";
    private static final String IMAGE_SUFFIX = ".gif";
    private static Map<String, Image> aCards = new HashMap<>();

    /**
     * Image getter method.
     * @return Image of the cardback
     */
    static Image getBack() {
        return getCard("default");
    }

    /**
     * Gets the image used in Solitaire GUI.
     * This method uses hashing function in order to achieve
     * faster return of the image.
     * @param pCode string code of card to be returned
     * @return image of the card
     */
    static Image getCard(String pCode) {
        return aCards.computeIfAbsent(pCode, c -> new Image(CardImage.class.getResourceAsStream(IMAGE_LOCATION + c + IMAGE_SUFFIX)));
    }
}
