// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.cards;

/**
 * Interface extending CardPile Interface. <br>
 * Contains methods used by WorkPack.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public interface CardStack extends CardPile {

    /**
     * Method inserting stack of cards to class stack.
     * @param stack stack of cards to be added to present stack
     * @return value whether the operation
     */
    boolean put(CardStack stack);

    /**
     * Method returning stack of cards. <br>
     * The returned cards are removed from the stack.
     * @param card value of card which should be in the bottom of the stack
     * @return stack of cards removed from the class stack,
     * or null if the stack is empty or the card is not found.
     */
    CardStack pop(Card card);

}
