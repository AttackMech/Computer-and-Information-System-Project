package bishop.jason.memcardproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * TestResultsActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This activity is used to show the results of a test to the user.  It takes the results of each
 * question and displays a list showing if each answer was correct or not, displaying the correct
 * answer if the user was incorrect.  The user will also be shown if they have increased the level
 * of the card, and the next available testing date for the card.
 */

public class TestResultsActivity extends AppCompatActivity {
    public static final String RESULT_QUESTIONS = "bishop.jason.memcardproject.QUESTIONS";
    public static final String RESULT_ANSWERS = "bishop.jason.memcardproject.ANSWERS";
    public static final String CORRECT_ANSWERS = "bishop.jason.memcardproject.CORRECT";
    public static final String CARD_IDS = "bishop.jason.memcardproject.CARDS";

    private TestResultItem[] testResults;
    private String[] resultQuestions, resultAnswers, correctAnswers;
    private int[] questionCards;

    /**
     * Sets up the view for the activity and populates the list of result items with data passed
     * into the activity from the testing activity.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_results);

        // set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Test Results");
        actionBar.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.test)));

        // get test information from the previous activity
        Intent intent = getIntent();
        resultQuestions = intent.getStringArrayExtra(RESULT_QUESTIONS);
        resultAnswers = intent.getStringArrayExtra(RESULT_ANSWERS);
        correctAnswers = intent.getStringArrayExtra(CORRECT_ANSWERS);
        questionCards = intent.getIntArrayExtra(CARD_IDS);

        // calculate results and update database
        getResults();

        // populate adapter and set for listview
        ListView lvResults = (ListView) findViewById(R.id.listViewResults);
        ListAdapter adapter = new ResultsAdapter(getApplicationContext(), testResults);
        lvResults.setAdapter(adapter);
    }

    /**
     * Determines the results of the test by comparing user answers with the correct answers,
     * storing the results in an array of TestResult items.  It will also update the database with
     * the results of the test.
     */
    private void getResults() {
        testResults = new TestResultItem[resultQuestions.length];

        Cursor cursor;
        boolean correct;
        int level;
        String testDate;

        for (int i = 0; i < resultQuestions.length; i++) {
            // get questioned card from the database
            cursor = MemCardDbContract.getCardInfo(getApplicationContext(),
                    Integer.toString(questionCards[i]));

            if (cursor.moveToFirst()) {
                level = cursor.getInt(cursor.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_LEVEL));

                // check results
                if (resultAnswers[i].equalsIgnoreCase(correctAnswers[i])) {
                    correct = true;
                    level++;
                    testDate = MemCardDbContract.getLevelDateDiff(level);
                } else {
                    correct = false;
                    testDate = MemCardDbContract.getLevelDateDiff(level);
                }

                // store results in array
                testResults[i] = new TestResultItem(resultQuestions[i], resultAnswers[i],
                        correctAnswers[i], testDate, correct, level);

                // update card in database
                MemCardDbContract.setNewCardLevel(getApplicationContext(), questionCards[i],
                        level);
            }

            if (i == resultQuestions.length - 1) {
                cursor.close();
                MemCardDbContract.closeDb();
            }
        } // end for
    }

    /**
     * Ends the activity when the user presses the 'DONE' button.
     *
     * @param view the view that called this method.
     */
    public void donePressed(View view) {
        finish();
    }
}
