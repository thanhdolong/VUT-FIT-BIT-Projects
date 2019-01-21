// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.cards;

/**
 * Interface with methods handling a card. <br>
 * Implemented in class GameCard.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public interface Card {

    /**
     * Gets value attribute of the card.
     * @return values of card
     */
    int value();

    /**
     * Gets color attribute of the card.
     * @return the color of card
     */
    Card.Color color();

    /**
     * Turns face up a card.
     * @return value whether the operation has succeeded
     */
    boolean turnFaceUp();

    /**
     * Factory method turning a card face down.
     * Used only in reloading the start pack.
     * @return value whether the operation has succeded.
     */
    boolean turnFaceDown();

    /**
     * Gets attribute of card.
     * @return value whether the card is face up
     */
    boolean isTurnedFaceUp();

    /**
     * Compares colors of two cards.
     * @param c second card passed by parameter
     * @return value whether the cards have similar color
     */
    boolean similarColorTo(Card c);

    /**
     * Compares colors of two cards
     * @param c second card passed by parameter
     * @return value whether the cards have the exact color type
     */
    boolean sameTypeTo(Card c);

    /**
     * Enumeration type representing card color.
     */
    enum Color {
        // ------------------------------------ 4 Types of colors
        CLUBS,
        DIAMONDS,
        HEARTS,
        SPADES;

        // ------------------------------------ Override methods
        @Override
        public String toString() {
            switch (this) {
                case CLUBS:
                    return "C";
                case DIAMONDS:
                    return "D";
                case HEARTS:
                    return "H";
                case SPADES:
                    return "S";
                default:
                    return null;
            }
        }

    }

}