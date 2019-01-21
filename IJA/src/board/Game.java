// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.board;

/**
 * Class implementing basic mechanisms handling game data.<br>
 * {@link #gameSave} represents storage for table distributions of actual table
 * and history of 5 previous moves.<br>
 * {@link #gameIndex} represents identificator of the game instance.<br>
 * {@link #temporary} is used by some methods requiring to temporarily modify game data.
 *
 * @author Andrej Hučko
 */
public class Game {

    /*
     * Array of GameSet, capable of storing actual card distribution on the table
     * and previous 5 moves.
     * gameSave[0] -> actual game
     * gameSave[1]-[5] -> history
     *
     * gameIndex: identificator of game instance
     * temporary: used in some some methods, e.g. hint
     */
    private GameSet[] gameSave;
    private GameSet temporary;
    private int gameIndex;

    /**
     * Game constructor, invokes only one instance of GameSet
     * (zeroth index of {@link #gameSave}).
     * @param index identificator of new game instance stored in {@link #gameIndex}
     */
    public Game(int index) {

        gameIndex = index;

        gameSave = new GameSet[6];
        gameSave[0] = new GameSet();

        gameSave[1] = null;
        gameSave[2] = null;
        gameSave[3] = null;
        gameSave[4] = null;
        gameSave[5] = null;

        temporary = null;

    }

    /**
     * Gets the identificaton number of game instance.
     * @return identification of game instance
     */
    public int getGameIndex() {
        return gameIndex;
    }

    /**
     * Gets the present game state (table distribution).
     * @return present game card distribution on the table
     */
    public GameSet get() {
        return gameSave[0];
    }

    /**
     * Stores actual table distribution into history (gameSave[1]-[5]). <br>
     * Invoked before every move.
     */
    public void backup() {

        for (int i = 5; i > 1; i--) gameSave[i] = gameSave[i - 1];
        gameSave[1] = gameSave[0].copy();

    }

    /**
     * Reverts active solitaire game by one move back.<br>
     * Maximum number of invoking this method repeatedly is 5, or
     * the number of moves (less than 5). <br>
     * User invoked method.
     */
    public void undo() {

        if (gameSave[1] == null) return;
        for (int i = 0; i < 5; i++) gameSave[i] = gameSave[i + 1];
        gameSave[5] = null;

    }

    /**
     * Clears undo history before game load.
     */
    public void clear() {

        for (int i = 1; i < 6; i++) {
            gameSave[i] = null;
        }

    }

    /**
     * Factory method preventing loss of game history while performing hint command.
     */
    public void storeTmp() {
        if (gameSave[5] != null)
            temporary = gameSave[5].copy();
    }

    /**
     * Analogic method to storeTmp(), reverts the game to original state.
     */
    public void revertTmp() {
        gameSave[5] = temporary;
        temporary = null;
    }

}
