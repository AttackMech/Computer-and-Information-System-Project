package bishop.jason.memcardproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Class MainActivity
 * @author Jason Bishop 3042012
 * @version 1.0
 * created on 10/09/2017
 *
 * This class is the activity for  main menu of the application, allowing users to navigate to
 * different areas of the application using the buttons provided on screen.  Also included is a
 * button to reset the sample cards in the database, used for debugging/testing purposes only.
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Sets the layout for this activity.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Launches the set viewing activity.
     *
     * @param view the view that triggered this method (not used).
     */
    public void viewSets(View view) {
        Intent intent = new Intent(this, SetViewerActivity.class);
        startActivity(intent);
    }

    /**
     * Launches the card viewing activity.
     *
     * @param view the view that triggered this method (not used).
     */
    public void viewAllCards(View view) {
        Intent intent = new Intent(this, CardViewerActivity.class);
        // inform the activity to view all cards in the database
        intent.putExtra(CardViewerActivity.CARD_VIEW_TYPE, CardViewerActivity.VIEW_ALL);
        startActivity(intent);
    }

    /**
     * Launches the pre-test activity, allowing users to test themselves with their saved cards.
     *
     * @param view the view that triggered this method (not used).
     */
    public void takeATest(View view) {
        Intent intent = new Intent(this, PreTestSelectActivity.class);
        startActivity(intent);
    }

    /**
     * Launches the matching game activity.
     *
     * @param view the view that triggered this method (not used)
     */
    public void playAGame(View view) {
        Intent intent = new Intent(this, MatchingGameActivity.class);
        startActivity(intent);
    }

    /**
     * Resets the sample cards and sets in the database.
     *
     * @param view the view that triggered this method (not used).
     */
    public void resetSampleCards(View view) {
        MemCardDbContract.resetSample(getApplicationContext());
        Toast.makeText(getApplicationContext(), "Sample cards reset", Toast.LENGTH_SHORT).show();
    }

    /**
     * to do
     *
     *
     * items:
     *     how to view cards in no set? - don't care
     *     test ready icon - not working on pre-test select for unknown reason
     *         even on test cards - trying clean/rebuild (didn't work)
     *     check to make sure onCreate for DBhelper not recreating cards?
     *     clean up db? - find db leaks and close resources
     *         esp noticed with test frags
     *
     * comments
     *
     * getTestableCards
     *     self
     *     user
     *     trials
     *
     * update proposal
     *
     * write up final report
     *
     */

}
