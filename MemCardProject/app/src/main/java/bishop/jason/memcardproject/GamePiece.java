package bishop.jason.memcardproject;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * GamePiece class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is used to represent a game piece in the matching game activity.  It stores the
 * associated card data, flips the pieces over, and checks for matches between different pieces.
 */

public class GamePiece {
    public int slot;
    public boolean showFront, isFlipped, isMatched;
    public Card card;
    public TextView tvFront;
    public ImageView ivBack;

    /**
     * Class constructor saves passed values.
     * @param slot the position of this piece on the game board.
     * @param card the Card object containing card data.
     * @param showFront determines if the front or back of the card is shown.
     * @param textView the textView used to display the card info.
     * @param imageView the imageView for the back of the piece.
     */
    public GamePiece(int slot, Card card, boolean showFront, TextView textView, ImageView imageView) {
        this.slot = slot;
        this.card = card;
        this.showFront = showFront;
        tvFront = textView;
        ivBack = imageView;
        isFlipped = false;
        isMatched = false;

        // set text for appropriate side
        if (showFront) {
            tvFront.setText(card.getFront());
        } else {
            tvFront.setText(card.getBack());
        }
    }

    /**
     * Flips the card using the FlipAnimation class
     *
     * @see FlipAnimation
     */
    public void flip() {
        // get center of view
        float centerX = (ivBack.getLeft() + ivBack.getRight()) / 2;
        float centerY = (ivBack.getTop() + ivBack.getBottom()) / 2;

        // create animation and set values
        FlipAnimation animation;
        if (!isFlipped) {
            animation = new FlipAnimation(0, 90, centerX, centerY);
        } else {
            animation = new FlipAnimation(0, -90, centerX, centerY);
        }
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new DisplayNextView(!isFlipped, ivBack, tvFront));

        // begin appropirate animation
        if (!isFlipped) {
            ivBack.startAnimation(animation);
        } else {
            tvFront.startAnimation(animation);
        }

        isFlipped = !isFlipped;
    }

    /**
     * Returns the value representing if the card is flipped over.
     *
     * @return a boolean representing the flip status.
     */
    public boolean isFlipped() { return isFlipped; }

    /**
     * Returns a value representing if this piece has been matched to another.
     *
     * @return a boolean representing the match status.
     */
    public boolean isMatched() { return isMatched; }

    /**
     * Checks to see if this piece is the match for the passed piece.
     *
     * @param gp the piece to check for a match.
     * @return a boolean that is true if there is a match.
     */
    public boolean matches(GamePiece gp) {
        // when showing front, check for match if other card is showing back
        if (this.showFront) {

            if (!gp.showFront && gp.card.getFront().equalsIgnoreCase(this.card.getFront())) {
                return true;
            }
        // when showing front, check for match if other card is showing back
        } else {

            if (gp.showFront && gp.card.getBack().equalsIgnoreCase(this.card.getBack())) {
                return true;
            }
        }
        // no match
        return false;
    }

    /**
     * Updates the match status of this piece and removes the click listener.
     */
    public void matched() {
        ivBack.setOnClickListener(null);
        isMatched = true;
    }

    /**
     * Sets the onClickListener to the ImageView of this piece so that the user can flip it over.
     *
     * @param listener the onClickListener to apply.
     * @see android.view.View.OnClickListener
     */
    public void setOnClickListener(View.OnClickListener listener) {
        ivBack.setOnClickListener(listener);
    }
}
