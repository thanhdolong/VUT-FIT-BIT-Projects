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
 * Class implementing the working pack in Solitaire game. <br>
 * {@link #WStack} represents the working pack
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public class WorkPack extends StartPack implements CardStack {

    private Stack<Card> WStack;

    public WorkPack() {
        WStack = new Stack<>();
    }

    /**
     * Factory method used to manually fill the WorkPack. <br>
     * Used by {@link src.board.Game} constructor.
     * @param card inserted card
     */
    public void generate(Card card) {

        WStack.push(card);

    }

    /**
     * {@inheritDoc}
     */
    public boolean put(CardStack stack) {

        WorkPack pack = (WorkPack) stack;
        if (pack.isEmpty())
            return false;

        Stack<Card> tmpStack = new Stack<>();
        while (!pack.isEmpty()) tmpStack.push(pack.pop());

        while (!tmpStack.empty()) {

            pack.WStack.push(tmpStack.peek());
            if (!this.put(tmpStack.peek())) {
                tmpStack.pop();
                while (!tmpStack.empty()) pack.WStack.push(tmpStack.pop());
                return false;
            } else tmpStack.pop();

        }

        while (!pack.isEmpty()) pack.pop();
        return true;

    }

    /**
     * {@inheritDoc}
     */
    public CardStack pop(Card card) {

        Stack<Card> tmpStack = new Stack<>();
        WorkPack ReturnStack = new WorkPack();

        while (!WStack.empty()) {
            if (WStack.peek().equals(card)) {
                tmpStack.push(WStack.pop());
                break;
            } else {
                tmpStack.push(WStack.pop());
            }
        }

        if (WStack.empty() && !tmpStack.peek().equals(card)) {
            while (!tmpStack.empty()) {
                WStack.push(tmpStack.pop());
            }
            return null;
        } else {
            while (!tmpStack.empty()) {
                ReturnStack.WStack.push(tmpStack.pop());
            }

            return ReturnStack;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override public int size() {
        return WStack.size();
    }

    /**
     * {@inheritDoc}
     * In order to put card on WorkPack successfully, the following rules must be met: <br>
     * If the WorkPack is empty, only King-value card can be inserted. <br>
     * The top card must have opposite color and higher value than the inserted card.
     */
    @Override public boolean put(Card card) {

        if (WStack.empty() && card.value() != 13)
            return false;

        if (!WStack.empty() && WStack.peek().value() - 1 != card.value())
            return false;

        if (!WStack.empty() && WStack.peek().similarColorTo(card))
            return false;

        WStack.push(card);
        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override public Card pop() {

        if (WStack.empty()) return null;
        return WStack.pop();

    }

    /**
     * {@inheritDoc}
     */
    @Override public Card get() {

        if (WStack.empty()) return null;
        return WStack.peek();

    }

    /**
     * {@inheritDoc}
     */
    @Override public Card get(int index) {

        if (index + 1 == WStack.size()) {
            return WStack.peek();
        } else if (index >= WStack.size() || WStack.empty()) {
            return null;
        } else {
            return WStack.get(index);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean isEmpty() {
        return WStack.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean equals(Object obj) {

        return (obj instanceof WorkPack) && (this.toString().equals(obj.toString()));

    }

    /**
     * {@inheritDoc}
     */
    @Override public String toString() {
        return Arrays.toString(this.WStack.toArray());
    }

}
