package bishop.jason.memcardproject;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * SwapViews class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is used to change views during the flip animation for cards in the card viewer
 * activity and game pieces in the matching game activity.  It takes the views and begins the
 * flipping animation for the second item.
 */

public class SwapViews {
    private boolean isFirst;
    private View view1, view2;

    /**
     * Class constructor stores the past items.
     *
     * @param isFirst a boolean value to determine which view to show.
     * @param view1 the fist view in the total animation.
     * @param view2 the second view in the total animation.
     */
    public SwapViews(boolean isFirst, View view1, View view2) {
        this.isFirst = isFirst;
        this.view1 = view1;
        this.view2 = view2;
    }

    /**
     * Runs the animation to flip the second view in, simulating a flip of the card.
     */
    public void swap() {
        // find the center of the view
        float centerX = (view1.getLeft() + view1.getRight()) / 2;
        float centerY = (view1.getTop() + view1.getBottom()) / 2;

        FlipAnimation animation;

        // set values for views
        if (isFirst) {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
            view2.requestFocus();

            animation = new FlipAnimation(-90, 0, centerX, centerY);
        } else {
            view2.setVisibility(View.GONE);
            view1.setVisibility(View.VISIBLE);
            view1.requestFocus();

            animation = new FlipAnimation(90, 0, centerX, centerY);
        }

        // set animation values
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator());

        // decide which animation to run
        if (isFirst
                ) {
            view2.startAnimation(animation);
        } else {
            view1.startAnimation(animation);
        }
    }
}
