// ----- ----- ----- ----- -----
// IJA Project - Solitaire
// Authors:
//   Andrej Hučko (xhucko01)
//   Do Long Thanh (xdolon00)
// May 2017
// ----- ----- ----- ----- -----

package src.board;

import src.cards.*;

import java.util.Collections;
import java.util.Stack;

/**
 * Class implementing basic handling methods with game data.
 * Operates at lower level than class Game.
 * Handles individual card decks and stacks.
 * @author Andrej Hučko
 */
public class GameSet {

    private StartPack s;
    private TurnPile t;
    private FinishPack[] f;
    private WorkPack[] w;

    /**
     * Generates shuffled set of cards on their place.
     * Used only once, at the beginning of the game.
     */
    public GameSet() {

        // Initialise here:
        //  1x StartPack
        //  1x TurnPile
        //  4x FinishPack
        //  7x WorkPack

        Stack<Card> DefaultPile = new Stack<>();
        for (int i = 1; i <= 13; i++) DefaultPile.push(new GameCard(Card.Color.CLUBS, i));
        for (int i = 1; i <= 13; i++) DefaultPile.push(new GameCard(Card.Color.DIAMONDS, i));
        for (int i = 1; i <= 13; i++) DefaultPile.push(new GameCard(Card.Color.HEARTS, i));
        for (int i = 1; i <= 13; i++) DefaultPile.push(new GameCard(Card.Color.SPADES, i));
        Collections.shuffle(DefaultPile);

        t = new TurnPile();
        s = new StartPack();
        for (int i = 0; i < 24; i++) {
            s.put(DefaultPile.pop());
        }

        f = new FinishPack[4];
        for (int i = 0; i < 4; i++) {
            f[i] = new FinishPack();
        }

        w = new WorkPack[7];
        for (int i = 0; i < 7; i++) {
            w[i] = new WorkPack();
            for (int j = 0; j < i + 1; j++) {
                w[i].generate(DefaultPile.pop());
            }
            w[i].get().turnFaceUp();
        }

    }

    /**
     * Factory constructor, used only for deep copying.
     * @param modifier value to differ from original constructor.
     */
    private GameSet(boolean modifier) {

        s = new StartPack();
        t = new TurnPile();

        f = new FinishPack[4];
        for (int i = 0; i < 4; i++)
            f[i] = new FinishPack();

        w = new WorkPack[7];
        for (int i = 0; i < 7; i++)
            w[i] = new WorkPack();

    }

    /**
     * @return Deep copy of the GameSet.
     */
    GameSet copy() {

        GameSet RetCopy = new GameSet(true);

        for (int i = 0; i < this.s.size(); i++) {

            Card ref = this.s.get(i);
            Card ccard = new GameCard(ref.color(), ref.value());
            if (ref.isTurnedFaceUp())
                ccard.turnFaceUp();

            RetCopy.s.put(ccard);
        }

        for (int i = 0; i < this.t.size(); i++) {

            Card ref = this.t.get(i);
            Card ccard = new GameCard(ref.color(), ref.value());
            if (ref.isTurnedFaceUp())
                ccard.turnFaceUp();

            RetCopy.t.put(ccard);
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < this.w[i].size(); j++) {

                Card ref = this.w[i].get(j);
                Card ccard = new GameCard(ref.color(), ref.value());
                if (ref.isTurnedFaceUp())
                    ccard.turnFaceUp();

                RetCopy.w[i].generate(ccard);

            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.f[i].size(); j++) {

                Card ref = this.f[i].get(j);
                Card ccard = new GameCard(ref.color(), ref.value());
                if (ref.isTurnedFaceUp())
                    ccard.turnFaceUp();

                RetCopy.f[i].put(ccard);

            }
        }

        return RetCopy;

    }

    // ----- ----- ----- ----- ----- ----- -----

    /**
     * Factory method used in Load Game sequence.
     * Stores game data into the instance.
     *
     * @param sl loaded start pack
     * @param tl loaded turn pack
     * @param wl loaded array of working packs
     * @param fl loaded array of finish packs
     */
    public void loadIntoGameSet(StartPack sl, TurnPile tl, WorkPack[] wl, FinishPack[] fl) {

        s = sl;
        t = tl;
        w = wl;
        f = fl;

    }

    // ----- ----- ----- ----- ----- ----- -----

    /**
     * Getter method.
     *
     * @return instance start pack
     */
    public StartPack getS() {
        return s;
    }

    /**
     * Getter method.
     *
     * @return instance turn pack
     */
    public TurnPile getT() {
        return t;
    }

    /**
     * Getter method.
     *
     * @param index working pack identifier, accepted values 0 - 6
     * @return given instance working pack
     */
    public FinishPack getF(int index) {
        return f[index];
    }

    /**
     * Getter method.
     *
     * @param index finish pack identifier, accepted values 0 - 3
     * @return given instance finish pack
     */
    public WorkPack getW(int index) {
        return w[index];
    }

}
