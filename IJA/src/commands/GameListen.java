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
 * Interface containing method used by GUI.
 *
 * @author Do Long Thanh
 */
public interface GameListen {

    void gameStateChanged(Game game, int gameIndex);

}
