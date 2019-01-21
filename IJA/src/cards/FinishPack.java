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
 * Class implementing Finish Pack in Solitaire game. <br>
 * {@link #FStack} represents the Finish Stack.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public class FinishPack extends StartPack {

    private Stack<Card> FStack;

    public FinishPack() {
        FStack = new Stack<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override public int size() {
        return FStack.size();
    }

    /**
     * {@inheritDoc} <br>
     * In order to put card on FinishPack successfully, the following rules must be met: <br>
     * Only Ace can be put on an empty FinishPack. <br>
     * Only a card with same color can be placed on the pack. <br>
     * The card must have value higher by one than the top card of the pack.
     */
    @Override public boolean put(Card card) {

        if (FStack.empty() && card.value() != 1)
            return false;

        if (!FStack.empty() && FStack.peek().value() + 1 != card.value())
            return false;

        if (!FStack.empty() && !FStack.peek().sameTypeTo(card))
            return false;

        FStack.push(card);
        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override public Card pop() {

        if (FStack.empty()) return null;
        return FStack.pop();

    }

    /**
     * {@inheritDoc}
     */
    @Override public Card get() {

        if (FStack.empty()) return null;
        return FStack.peek();

    }

    /**
     * {@inheritDoc}
     */
    @Override public Card get(int index) {

        if (index + 1 == FStack.size()) {
            return FStack.peek();
        } else if (index >= FStack.size() || FStack.empty()) {
            return null;
        } else {
            return FStack.get(index);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean isEmpty() {
        return FStack.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean equals(Object obj) {

        return (obj instanceof FinishPack) && (this.toString().equals(obj.toString()));

    }

    /**
     * {@inheritDoc}
     */
    @Override public String toString() {
        return Arrays.toString(this.FStack.toArray());
    }

}
