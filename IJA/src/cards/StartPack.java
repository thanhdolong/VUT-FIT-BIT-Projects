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
 * Class implementing starting pack in Solitaire game. <br>
 * {@link #Pack} represents the StartPack.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public class StartPack implements CardPile, java.io.Serializable {

    private Stack<Card> Pack;

    public StartPack() {
        Pack = new Stack<Card>();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return Pack.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return Pack.empty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean put(Card card) {

        Pack.push(card);
        return true;

    }

    /**
     * {@inheritDoc}
     */
    public Card pop() {

        if (Pack.empty()) return null;
        return Pack.pop();

    }

    /**
     * {@inheritDoc}
     */
    public Card get() {

        if (Pack.empty()) return null;
        return Pack.peek();

    }

    /**
     * {@inheritDoc}
     */
    public Card get(int index) {

        if (index + 1 == Pack.size()) {
            return Pack.peek();
        } else if (index >= Pack.size() || Pack.empty()) {
            return null;
        } else {
            return Pack.get(index);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override public String toString() {
        return Arrays.toString(this.Pack.toArray());
    }

}
