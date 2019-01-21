// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.commands;

import src.board.Game;
import src.cards.*;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Class implementing basic game move mechanics from Command interface. <br>
 * Besides game moves, it implements all other features from Features interface.
 *
 * @author Andrej Hučko
 * @author Do Long Thanh
 */
public class Moves implements Command, Features {

    private static final Moves[] INSTANCE = new Moves[4]; // Array of instances, in order to recognize game moves
    private List<GameListen> aListeners = new ArrayList<>();
    private String from = ""; // Used by PostCard methods

    /**
     * Method used by GUI part.
     * @param instanceIndex identification number of given game instance
     * @return the singleton instance for this class
     */
    public static Moves instance(int instanceIndex) {
        if (INSTANCE[instanceIndex] == null)
            INSTANCE[instanceIndex] = new Moves();
        return INSTANCE[instanceIndex];
    }

    /**
     * {@inheritDoc}
     */
    public boolean Start2Turn(Game play) {

        StartPack start = play.get().getS();
        TurnPile turn = play.get().getT();

        if (start.isEmpty()) {

            if (turn.isEmpty()) {
                return false;
            } else {
                play.backup();
                while (!turn.isEmpty()) {
                    start.put(turn.pop());
                    start.get().turnFaceDown();
                }
            }
        } else {
            play.backup();
            turn.put(start.pop());
        }

        notifyListeners(play, play.getGameIndex());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean Turn2Work(Game play, int which) {

        if (which < 0 || which > 6)
            return false;

        TurnPile turn = play.get().getT();
        WorkPack work = play.get().getW(which);

        if (turn.isEmpty())
            return false;

        boolean retval = work.put(turn.get());
        if (retval) {
            work.pop();
            play.backup();
            work.put(turn.pop());
            notifyListeners(play, play.getGameIndex());
            return true;
        } else return false;

    }

    /**
     * {@inheritDoc}
     */
    public boolean Turn2Finish(Game play, int which) {

        if (which < 0 || which > 3)
            return false;

        TurnPile turn = play.get().getT();
        FinishPack fin = play.get().getF(which);

        if (turn.isEmpty())
            return false;

        boolean retval = fin.put(turn.get());
        if (retval) {
            fin.pop();
            play.backup();
            fin.put(turn.pop());

            if (fin.size() == 13) { // Possibility of the end of game
                for (int i = 0; i < 4; i++) {

                    if (play.get().getF(i).size() != 13) break;
                    if (i != 3) continue;
                    notifyListeners(play, play.getGameIndex());
                    src.game.Main.WIN();

                }
            }

            notifyListeners(play, play.getGameIndex());
            return true;
        } else return false;

    }

    /**
     * {@inheritDoc}
     */
    public boolean Work2Finish(Game play, int from, int to) {

        if (from < 0 || from > 6 || to < 0 || to > 3)
            return false;

        WorkPack work = play.get().getW(from);
        FinishPack fin = play.get().getF(to);

        if (work.isEmpty())
            return false;

        boolean retval = fin.put(work.get());
        if (retval) {
            fin.pop();
            play.backup();
            fin.put(work.pop());

            if (!work.isEmpty())
                work.get().turnFaceUp();

            if (fin.size() == 13) { // Possibility of the end of game
                for (int i = 0; i < 4; i++) {

                    if (play.get().getF(i).size() != 13) break;
                    if (i != 3) continue;
                    notifyListeners(play, play.getGameIndex());
                    src.game.Main.WIN();

                }
            }

            notifyListeners(play, play.getGameIndex());
            return true;
        } else return false;

    }

    /**
     * {@inheritDoc}
     */
    public boolean Finish2Work(Game play, int from, int to) {

        if (to < 0 || to > 6 || from < 0 || from > 3)
            return false;

        FinishPack fin = play.get().getF(from);
        WorkPack work = play.get().getW(to);

        if (fin.isEmpty())
            return false;

        boolean retval = work.put(fin.get());
        if (retval) {
            work.pop();
            play.backup();
            work.put(fin.pop());

            notifyListeners(play, play.getGameIndex());
            return true;
        } else return false;

    }

    /**
     * {@inheritDoc}
     */
    public boolean Work2Work(Game play, int from, int to, int which) {

        if (from < 0 || to < 0 || from > 6 || to > 6 || from == to) {
            return false;
        }


        WorkPack workFrom = play.get().getW(from);
        WorkPack workTo = play.get().getW(to);

        if (workFrom.isEmpty()) {
            return false;
        }

        // workFrom.get(INDEX)
        // INDEX must be 0-12
        // returned card MUST BE turned-up (else do-not-nothing)
        // IF I'm lucky to get a turned-up-card (only pointer, remember), throw it into...
        // pop(Card card). but to a temporary place! you never know

        Card test = workFrom.get(which);

        if (test == null || !test.isTurnedFaceUp())
            return false;

        // Store last gamesave (5th) and call backup routine
        play.storeTmp();
        play.backup();

        CardStack tmp = workFrom.pop(test);
        if (tmp == null) {
            play.undo();
            play.revertTmp();
            return false;
        }

        if (!workTo.put(tmp)) {
            play.undo();
            play.revertTmp();
            return false;
        }

        if (!workFrom.isEmpty())
            workFrom.get().turnFaceUp();

        notifyListeners(play, play.getGameIndex());
        return true;
    }

    ///////////////////////////////////////////////////////////////////////
    // Implementation of Features ----- ----- ----- ----- ----- ----- -----
    ///////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public boolean Finish2Finish(Game play, int from, int to) {

        if (from < 0 || to < 0 || from > 3 || to > 3 || from == to)
            return false;

        FinishPack finFrom = play.get().getF(from);
        FinishPack finTo = play.get().getF(to);

        if (finFrom.isEmpty())
            return false;

        if (finFrom.size() == 1 && finTo.isEmpty()) {

            play.backup();
            finTo.put(finFrom.pop());

            if (finTo.size() == 13) { // Possibility of the end of game
                for (int i = 0; i < 4; i++) {
                    if (play.get().getF(i).size() != 13) break;
                    if (i != 3) continue;
                }
            }

            notifyListeners(play, play.getGameIndex());
            return true;

        } else return false;

    }

    /**
     * {@inheritDoc}
     */
    public void CallUndo(Game play) {
        play.undo();
        notifyListeners(play, play.getGameIndex());
    }

    /**
     * {@inheritDoc}
     */
    public void SaveGame(Game play, String where) {

        String savePath = System.getProperty("user.dir");
        int occurence = savePath.indexOf("\\");
        if (occurence == -1) savePath = savePath.concat("/examples/");
        else savePath = savePath.concat("\\examples\\");

        savePath = savePath.concat(where);
        savePath = savePath.concat(".save");

        try {
            FileOutputStream fileOut = new FileOutputStream(savePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(play.get().getS());
            out.writeObject(play.get().getT());
            for (int i = 0; i < 7; i++)
                out.writeObject(play.get().getW(i));
            for (int i = 0; i < 4; i++)
                out.writeObject(play.get().getF(i));

            out.close();
            fileOut.close();

        } catch (java.io.IOException exception) {

            exception.printStackTrace();

        }

    }

    /**
     * {@inheritDoc}
     */
    public Stack<String> getSavedGames() {
        Stack<String> ret = new Stack<>();

        String loadPath = System.getProperty("user.dir");
        int occurence = loadPath.indexOf("\\");
        if (occurence == -1) loadPath = loadPath.concat("/examples");
        else loadPath = loadPath.concat("\\examples");

        Path dir = Paths.get(loadPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.save")) {
            for (Path file : stream) {
                int slashIndex = file.toString().lastIndexOf("/");
                if (slashIndex == -1) slashIndex = file.toString().lastIndexOf("\\");
                String cut = file.toString().substring(slashIndex + 1, file.toString().length() - 5);
                ret.push(cut);
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public void LoadGame(Game play, String which) {

        if (which.equals("")) return;

        String loadPath = System.getProperty("user.dir");
        int occurence = loadPath.indexOf("\\");
        if (occurence == -1) loadPath = loadPath.concat("/examples/");
        else loadPath = loadPath.concat("\\examples\\");

        loadPath = loadPath.concat(which + ".save");

        StartPack sl;
        TurnPile tl;

        WorkPack wl[] = new WorkPack[7];
        for (int i = 0; i < 4; i++) wl[i] = null;
        FinishPack fl[] = new FinishPack[4];
        for (int i = 0; i < 4; i++) wl[i] = null;

        try {
            FileInputStream fileIn = new FileInputStream(loadPath);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            sl = (StartPack) in.readObject();
            tl = (TurnPile) in.readObject();
            for (int i = 0; i < 7; i++)
                wl[i] = (WorkPack) in.readObject();
            for (int i = 0; i < 4; i++)
                fl[i] = (FinishPack) in.readObject();

            in.close();
            fileIn.close();

            play.clear();
            play.get().loadIntoGameSet(sl, tl, wl, fl);
            notifyListeners(play, play.getGameIndex());

        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        } catch (ClassNotFoundException cnfExc) {
            cnfExc.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     */
    public String Hint(Game play) {

        //System.out.println("Toi toi toi toi.");
        boolean foundHint;

        // Test 1: Work2Finish()
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 4; j++) {
                play.storeTmp();
                play.backup();
                foundHint = Work2Finish(play, i, j);
                play.undo();
                play.revertTmp();

                if (foundHint) {
                    CallUndo(play);

                    String c1 = changeLetter(play.get().getW(i).get().toString());
                    String c2;

                    if (play.get().getF(j).get() == null) {
                        c2 = "empty target pack.";
                    } else {
                        c2 = changeLetter(play.get().getF(j).get().toString());
                    }

                    notifyListeners(play, play.getGameIndex());
                    return "Move Card " + c1 + " to " + c2;

                }

            }
        }

        // Test 2: Turn2Finish()
        for (int i = 0; i < 4; i++) {
            play.storeTmp();
            play.backup();
            foundHint = Turn2Finish(play, i);
            play.undo();
            play.revertTmp();

            if (foundHint) {
                CallUndo(play);

                String c1 = changeLetter(play.get().getT().get().toString());
                String c2;

                if (play.get().getF(i).get() == null) {
                    c2 = "empty target pack.";
                } else {
                    c2 = changeLetter(play.get().getF(i).get().toString());
                }

                notifyListeners(play, play.getGameIndex());
                return "Move Card " + c1 + " to " + c2;

            }

        }

        // Test 3: Turn2Work()
        for (int i = 0; i < 7; i++) {
            play.storeTmp();
            play.backup();
            foundHint = Turn2Work(play, i);
            play.undo();
            play.revertTmp();

            if (foundHint) {
                CallUndo(play);

                String c1 = changeLetter(play.get().getT().get().toString());
                String c2;

                if (play.get().getW(i).get() == null) {
                    c2 = "empty working pack.";
                } else {
                    c2 = changeLetter(play.get().getW(i).get().toString());
                }

                notifyListeners(play, play.getGameIndex());
                return "Move Card " + c1 + " to " + c2;
            }

        }

        // Test 4: Work2Work()
        for (int i = 0; i < 7; i++) {

            for (int j = 0; j < 7; j++) {

                for (int k = 0; k < play.get().getW(i).size(); k++) {
                    play.storeTmp();
                    play.backup();
                    foundHint = Work2Work(play, i, j, k);
                    play.undo();
                    play.revertTmp();

                    if (foundHint) {
                        CallUndo(play);

                        String c1 = changeLetter(play.get().getW(i).get(k).toString());
                        String c2;

                        if (play.get().getW(j).get() == null) {
                            c2 = "empty working pack.";
                        } else {
                            c2 = changeLetter(play.get().getW(j).get().toString());
                        }

                        notifyListeners(play, play.getGameIndex());

                        if (k + 1 == play.get().getW(i).size())
                            return "Move Card " + c1 + " to " + c2;
                        else
                            return "Move stack with base " + c1 + " to " + c2;

                    }

                }

            }

        }

        // Test 5: Draw a Card.
        if (play.get().getT().size() == 0 && play.get().getS().size() != 0) {
            return "Draw a card.";
        }

        // Test 6: Reset the start pack.
        if (play.get().getS().size() == 0 && play.get().getT().size() != 0) {
            return "Click on the empty start pack.";
        }

        return "None hints available.";

    }

    ///////////////////////////////////////////////////////////////////////
    // Implementation of Direction part: "PostCards"
    // Return value: Shall I execute?
    // No -> From == ""
    // Yes -> From != "" && To == ""  (soon to be filled, need to execute!)
    ///////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public boolean fillPostCard(String add) {

        // Add coordinate, if the postcard is empty
        if (from.equals("")) {
            from = add;
            return false;
        } else return true; // Full postcard

    }

    /**
     * {@inheritDoc}
     */
    public void clearPostCard() {
        from = "";
    }

    /**
     * {@inheritDoc}
     */
    public String getPostCard() {
        return from;
    }

    ///////////////////////////////////////////////////////////////////////
    // Implementation of GUI part
    ///////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public String changeLetter(String card) {

        // ♠ ♣ ♥ ♦
        String letter;
        if (card.length() == 2)
            letter = card.substring(1, 2);
        else
            letter = card.substring(2, 3);

        switch (letter) {
            case "D":
                return card.replace("D", "♦");
            case "H":
                return card.replace("H", "♥");
            case "C":
                return card.replace("C", "♣");
            case "S":
                return card.replace("S", "♠");
            default:
                return "";
        }

    }

    /**
     * Method used by GUI part.
     *
     * @param pListener Listener to be added to {@link #aListeners}
     */
    public void addListener(GameListen pListener) {
        aListeners.add(pListener);
    }

    /**
     * Method used by GUI part.
     *
     * @param game reference to game instance
     * @param gameIndex identification of the game instance
     */
    private void notifyListeners(Game game, int gameIndex) {
        for (GameListen listener : aListeners) {
            listener.gameStateChanged(game, gameIndex);
        }
    }

}
