// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.cards;

/**
 * Interface with methods handling a single card pile or stack.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public interface CardPile {

    /**
     * Method returning actual size of stack
     * @return size of game pile/stack
     */
    int size();

    /**
     * Method inserting a card to the class stack.
     * @param card reference to card to be inserted
     * @return value whether the operation has succeeded
     */
    boolean put(Card card);

    /**
     * Method returning top card of the stack. <br>
     * The returned card is removed from the stack.
     * @return top card of the stack, or null if the stack is empty.
     */
    Card pop();

    /**
     * Method returning top card of the stack. <br>
     * The returned card is NOT removed from the stack.
     * @return top card of the stack, or null if the stack is empty.
     */
    Card get();

    /**
     * Method returning card from the stack given by index. <br>
     * The card is not removed from the stack.
     * @param index index of the card, accepted values 0 - sizeof(stack)
     * @return card from the stack,  or null if the stack is empty.
     */
    Card get(int index);

    /**
     * Method returning number of cards in the current stack.
     * @return number of cards in the stack
     */
    boolean isEmpty();

}
