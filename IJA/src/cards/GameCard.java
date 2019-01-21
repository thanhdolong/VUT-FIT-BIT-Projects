// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.cards;

/**
 * Class implementing single game card. <br>
 * {@link #CardValue} represents the number value of card. The values are
 * A, 2-10, J, Q, K. <br>
 *  {@link #CardColor} represents the color of card (Hearts, Diamonds, Spaces, Clubs).
 *  {@link #isFaceUp} represents whether the card is turned face up.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public class GameCard implements Card, java.io.Serializable {

    private int CardValue;
    private Card.Color CardColor;
    private boolean isFaceUp;

    /**
     * Card constructor. Creates a card with given parameters.
     * Generated card is implicitly face down.
     *
     * @param color color of the card ( H, D, S, C )
     * @param value value of the card ( A, 2-10, J, Q, K )
     */
    public GameCard(Card.Color color, int value) {

        CardValue = value;
        CardColor = color;
        isFaceUp = false;

    }

    /**
     * {@inheritDoc}
     */
    public Card.Color color() {
        return CardColor;
    }

    /**
     * {@inheritDoc}
     */
    public int value() {
        return CardValue;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isTurnedFaceUp() {
        return isFaceUp;
    }

    /**
     * {@inheritDoc}
     */
    public boolean turnFaceUp() {
        if (isFaceUp) return false;
        else {
            isFaceUp = true;
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean turnFaceDown() {

        if (!isFaceUp) return false;
        else {
            isFaceUp = false;
            return true;
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean similarColorTo(Card c) {

        if (CardColor == Color.SPADES || CardColor == Color.CLUBS) {
            if (c.color() == Color.SPADES || c.color() == Color.CLUBS)
                return true;
        } else {
            if (c.color() == Color.DIAMONDS || c.color() == Color.HEARTS)
                return true;
        }

        return false;

    }

    /**
     * {@inheritDoc}
     */
    public boolean sameTypeTo(Card c) {
        return (CardColor == c.color());
    }

    @Override public String toString() {

        /**/
        if (!this.isTurnedFaceUp())
            return "default";
        /**/

        switch (this.CardValue) {
            case 1:
                return "A" + this.CardColor.toString();
            case 11:
                return "J" + this.CardColor.toString();
            case 12:
                return "Q" + this.CardColor.toString();
            case 13:
                return "K" + this.CardColor.toString();
            default:
                return "" + this.CardValue + this.CardColor.toString();
        }

    }

    @Override public boolean equals(Object anObject) {

        return (anObject instanceof Card) && (this.toString().equals(anObject.toString()));

    }

    @Override public int hashCode() {

        return this.toString().hashCode();

    }

}