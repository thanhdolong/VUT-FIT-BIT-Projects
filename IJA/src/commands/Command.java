// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.commands;

import src.board.Game;

/**
 * Interface containing game move methods. <br>
 * Implemented in class {@link Moves}.
 *
 * @author Andrej Hučko
 */
public interface Command {

    /**
     * This method performs drawing a card from start pack.
     * Drawed card is placed onto next deck, called turn pack.
     *
     * @param play given instance of game
     * @return value whether the move has succeeded
     */
    boolean Start2Turn(Game play);

    /**
     * Performs move of top card from turn pack onto working pack.
     *
     * @param play  given instance of game
     * @param which index of working pack, accepted values 0 - 6
     * @return value whether the move has succeeded
     */
    boolean Turn2Work(Game play, int which);

    /**
     * Performs move of top card from turn pack onto finish pack.
     *
     * @param play  given instance of game
     * @param which index of finish pack, accepted values 0 - 3
     * @return value whether the move has succeeded
     */
    boolean Turn2Finish(Game play, int which);

    /**
     * Performs move of top card from work pack onto finish pack.
     *
     * @param play given instance of game
     * @param from index of working pack, accepted values 0 - 6
     * @param to   index of finish pack, accepted values 0 - 3
     * @return value whether the move has succeeded
     */
    boolean Work2Finish(Game play, int from, int to);

    /**
     * Performs move of top card from finish pack onto work pack.
     *
     * @param play given instance of game
     * @param from index of finish pack, accepted values 0 - 3
     * @param to   index of working pack, accepted values 0 - 6
     * @return value whether the move has succeeded
     */
    boolean Finish2Work(Game play, int from, int to);

    /**
     * Performs move of top card from one work pack to another.
     *
     * @param play  given instance of game
     * @param from  index of working pack taking from
     * @param to    index of working pack moving card / stack to
     * @param which 0 == deepest in the stack, for example King
     *              or some kind of shuffled card on the beginning of the game.
     *              12 == maximum possible number, beyond this, there is no placeable card
     * @return value whether the move has succeeded
     */
    boolean Work2Work(Game play, int from, int to, int which);

    /**
     * Performs move of top card from one finish pack to another. The indexes must not equal.
     * Accepted values of index are 0 - 3.
     *
     * @param play given instance of game
     * @param from index of finish pack
     * @param to   index of finish pack
     * @return value whether the move has succeeded
     */
    boolean Finish2Finish(Game play, int from, int to);

    // PostCard collection ----- ----- ----- ----- ----- -----

    /**
     * Method from PostCard collection.
     * This method is invoked by clicking on any card on the scene.
     * It is used to recognize whether to execute a single game move.
     * The coordinates ( representation code of pack/stack and its index, index of card ) are written into the "postcard".
     *
     * @param add string code (coordinate)
     * @return boolean value, whether the postcard has already a sender, and a move can be executed
     */
    boolean fillPostCard(String add);

    /**
     * Method from PostCard collection.
     *
     * @return string code (coordinate) of card clicked before
     */
    String getPostCard();

    /**
     * Method from PostCard collection.
     * Utility method clearing the content of postcard.
     */
    void clearPostCard();

}
