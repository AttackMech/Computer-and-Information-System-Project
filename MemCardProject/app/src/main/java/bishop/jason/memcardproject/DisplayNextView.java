package bishop.jason.memcardproject;

import android.view.View;
import android.view.animation.Animation;

/**
 * DisplayNextView class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is used in conjunction with the FlipAnimation class and SwapViews class to animate
 * card flips in the card viewer activity and game piece flips in the matching game activity.
 * It utilizes the animation listener class to listen for when the FlipAnimation has ended and shows
 * the next part of the flip using the SwapViews class.
 *
 * @see android.view.animation.Animation.AnimationListener
 * @see FlipAnimation
 * @see SwapViews
 */

public class DisplayNextView implements Animation.AnimationListener {
    private boolean currentView;
    private View view1, view2;

    /**
     * Class constructor stores passed values.
     *
     * @param currentView determines the current View being animated.
     * @param view1 the first View of the animation.
     * @param view2 the second View of the animation.
     */
    public DisplayNextView(boolean currentView, View view1, View view2) {
        this.currentView = currentView;
        this.view1 = view1;
        this.view2 = view2;
    }

    /**
     * Required override that is not used.
     *
     * @param animation not used.
     */
    public void onAnimationStart(Animation animation) {}

    /**
     * Triggered when the first part of the animation ends and begins the next animation.
     *
     * @param animation the animation that triggered the listener.
     */
    public void onAnimationEnd(Animation animation) {
//        view1.post(new SwapViews(currentView, view1, view2));
        SwapViews swapper = new SwapViews(currentView, view1, view2);
        swapper.swap();
    }

    /**
     * Required override that is not used.
     *
     * @param animation not used.
     */
    public void onAnimationRepeat(Animation animation) {}
}
