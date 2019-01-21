// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.cards;

import java.util.Arrays;
import java.util.Stack;

/**
 * Class implementing the turn pack in Solitaire game. <br>
 * {@link #TurnedPack} represents the TurnPack.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public class TurnPile implements CardPile, java.io.Serializable {

    private Stack<Card> TurnedPack;

    public TurnPile() {
        TurnedPack = new Stack<Card>();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return TurnedPack.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean put(Card card) {

        if (!card.isTurnedFaceUp())
            card.turnFaceUp();

        TurnedPack.push(card);
        return true;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return TurnedPack.empty();
    }

    /**
     * {@inheritDoc}
     */
    public Card pop() {

        if (TurnedPack.empty()) return null;
        return TurnedPack.pop();

    }

    /**
     * {@inheritDoc}
     */
    public Card get() {

        if (TurnedPack.empty()) return null;
        return TurnedPack.peek();

    }

    /**
     * {@inheritDoc}
     */
    public Card get(int index) {

        if (index + 1 == TurnedPack.size()) {
            return TurnedPack.peek();
        } else if (index >= TurnedPack.size() || TurnedPack.empty()) {
            return null;
        } else {
            return TurnedPack.get(index);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override public String toString() {
        return Arrays.toString(this.TurnedPack.toArray());
    }

}
