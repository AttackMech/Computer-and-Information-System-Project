package bishop.jason.memcardproject;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * FlipAnimation class
 * @author Jason Bishop
 * @version 1.0
 * created 10/09/2017
 *
 * This class extends the Animation class to create a flipping animation used for cards and game
 * pieces in the application.
 */

public class FlipAnimation extends Animation {
    private final float fromDegrees, toDegrees, centerX, centerY;
    private Camera camera;

    /**
     * Class constructor saves passed values
     * @param fromDegrees the degree value from which the animation should begin.
     * @param toDegrees the degree value at which the animation will end.
     * @param centerX the center of the View being animated.
     * @param centerY the center of the View being animated.
     */
    public FlipAnimation(float fromDegrees, float toDegrees, float centerX, float centerY) {
        this.fromDegrees = fromDegrees;
        this.toDegrees = toDegrees;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    /**
     * Initializes the animation and creates a new Camera object
     *
     * @param width the width of the animated View.
     * @param height the height of the animated View.
     * @param parentWidth the width of the parent View.
     * @param parentHeight the height of the parent View.
     */
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        camera = new Camera();
    }

    /**
     * Applies the transformation to to the View to create the animation.
     * @param interpolatedTime the interpolated time used for the length of the animation.
     * @param t the transformation to apply for the animation.
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees = fromDegrees + ((toDegrees - fromDegrees) * interpolatedTime);
        Matrix matrix = t.getMatrix();

        camera.save();
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
