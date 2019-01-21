// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.commands;

import src.board.Game;

import java.util.Stack;

/**
 * Interface containing game features, such as game save/load, hint, undo. <br>
 * Implemented in class {@link Moves}.
 *
 * @author Andrej Hučko
 */
public interface Features {

    /**
     * This method performs Undo feature.
     * Called by GUI after clicking on undo inside menu box dropdown.
     *
     * @param play instance of game to call undo for
     */
    void CallUndo(Game play);

    /**
     * This method performs saving a game. Called by GUI.
     *
     * @param play instance of game to save
     * @param where name of saved game
     */
    void SaveGame(Game play, String where);

    /**
     * This method returns list of gave saves, used by GUI.
     *
     * @return stack of strings with names of game saves
     */
    Stack<String> getSavedGames();

    /**
     * This method performs load of a game into given game window.
     *
     * @param play  instance of game calling this method
     * @param which name of game save, chosen in GUI list view
     */
    void LoadGame(Game play, String which);

    /**
     * This method generates hint by performing analysis of available game moves.
     * On first occurence of available move, the hint is returned.
     *
     * @param play instance of game to generate hint for
     * @return text containing hint
     */
    String Hint(Game play);

    /**
     * Cosmetic method. Rewrites letter representation of color to given symbol.
     *
     * @param cardColor Input string representation of card
     * @return Output string representation of card with symbol of colour
     */
    String changeLetter(String cardColor);

}
