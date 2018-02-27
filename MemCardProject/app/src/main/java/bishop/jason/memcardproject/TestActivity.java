package bishop.jason.memcardproject;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TestActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is the activity used to run the tests on cards the user selects.  It shows various
 * fragments, starting with a timer fragment to prepare the user for the test.  Subsequent fragments
 * are randomly chosen from one of two types: a multiple choice type question, and a written answer
 * type question.  Additionally, each type of fragment is randomly selected to test either the front
 * or the back of the card.  When the test is complete, the results are passed to another activity
 * to display to the user.
 *
 */

public class TestActivity extends AppCompatActivity
    implements StartTestTimerFragment.OnTimerEnded,
        TestSideFragment.OnSideAnswerConfirm,
        TestMultiFragment.OnMultiAnswerConfirm,
        ConfirmTestCancelDialog.TestCancelDialogListener {

    public static final String TEST_SELECTION  = "bishop.jason.memcardproject.TEST_SELECT";
    public static final String FRAG_TAG = "TestFragment";
    public static final int TEST_BACK = 0;
    public static final int TEST_FRONT = 1;
    public static final int TEST_MULTI_FRONT = 2;
    public static final int TEST_MULTI_BACK = 3;

    private int currentFragment;
    private int[] testSelection, testQTypes;
    private String[] testQuestions, testAnswers, correctAnswers;
    private TextView tvTestProgress;
    private ProgressBar pbTestProgress;

    /**
     * Creates the layout for the test and begins the test by showing the timer fragment to prepare
     * the user.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        // set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.test)));

        // prepare layout
        currentFragment = 0;
        tvTestProgress = (TextView) findViewById(R.id.textViewTestProgress);
        tvTestProgress.setText("Preparing test...");
        pbTestProgress = (ProgressBar) findViewById(R.id.progressBarTest);
        pbTestProgress.setProgress(0);

        // get info for cards to use in test
        Intent intent = getIntent();
        testSelection = intent.getIntArrayExtra(TEST_SELECTION);
        randomizeTestOrder();

        // prepare Q&A arrays
        testQuestions = new String[testSelection.length];
        testAnswers = new String[testSelection.length];
        correctAnswers = new String[testSelection.length];

        // get list of fragments for getTestableCards
        testQTypes = getTestQuestionTypes();

        // display timer fragment to prepare for test
        StartTestTimerFragment startFragment = new StartTestTimerFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.test_frag_slide_out);
        transaction.add(R.id.frameLayoutTestFragment, startFragment.newInstance(), "Timer");
        transaction.commit();
    }

    /**
     * Creates an array of different randomly select question types.
     *
     * @return an int array to represent the different question types.
     */
    private int[] getTestQuestionTypes() {
        // create array of question types
        int[] qList = new int[testSelection.length];


        // add random question types to array
        Random random = new Random();
        int qType;
        for (int i = 0; i < testSelection.length; i++) {
            qType = random.nextInt(3);
            qList[i] = qType;
        }

        return qList;
    }

    /**
     * Randomizes the order of the cards that will be tested.
     */
    private void randomizeTestOrder() {
        List<Integer> tempList = new ArrayList<>();

        // add integers to list
        for (int i = 0; i < testSelection.length; i++) {
            tempList.add(testSelection[i]);
        }

        // randomly select integers from list and place back in array
        Random random = new Random();
        for (int i = 0; i < testSelection.length; i++) {
            testSelection[i] = tempList.remove(random.nextInt(tempList.size()));
        }
    }

    /**
     * Shows the next fragment in the testing sequence (ie. the next question).
     */
    private void showNextFragment() {
        TestSideFragment sideFragment;
        TestMultiFragment multiFragment;

        // get results from fragment before replacing
        if (currentFragment > 0) {
            int currentFragmentType = testQTypes[currentFragment - 1];

            switch (currentFragmentType) {
                case TEST_BACK:
                case TEST_FRONT:
                    sideFragment = (TestSideFragment) getSupportFragmentManager().findFragmentByTag(
                            FRAG_TAG);
                    // get from sideFrag
                    testQuestions[currentFragment - 1] = sideFragment.getQuestion();
                    testAnswers[currentFragment - 1] = sideFragment.getAnswer();
                    correctAnswers[currentFragment - 1] = sideFragment.getCorrectAnswer();

                    break;

                case TEST_MULTI_BACK:
                case TEST_MULTI_FRONT:
                    multiFragment = (TestMultiFragment) getSupportFragmentManager().
                            findFragmentByTag(FRAG_TAG);
                    // get from multiFrag
                    testQuestions[currentFragment - 1] = multiFragment.getQuestion();
                    testAnswers[currentFragment - 1] = multiFragment.getSelectedAnswer();
                    correctAnswers[currentFragment - 1] = multiFragment.getCorrectAnswer();

                    break;
            }
        }

        // check to see if test finished
        if (currentFragment >= testQTypes.length) {
            // go to results
            Intent intent = new Intent(getApplicationContext(), TestResultsActivity.class);
            intent.putExtra(TestResultsActivity.RESULT_QUESTIONS, testQuestions);
            intent.putExtra(TestResultsActivity.RESULT_ANSWERS, testAnswers);
            intent.putExtra(TestResultsActivity.CORRECT_ANSWERS, correctAnswers);
            intent.putExtra(TestResultsActivity.CARD_IDS, testSelection);
            startActivity(intent);
            finish();
            return;
        }

        // display progress of test
        tvTestProgress.setText((currentFragment + 1) + " / " + testSelection.length);
        int progress = (int) ((currentFragment + 1) / (float) testAnswers.length * 100);
        pbTestProgress.setProgress(progress);

        // get next fragment and layout and pass question info if needed
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.test_frag_slide_in, R.anim.test_frag_slide_out);
        transaction.remove(getSupportFragmentManager()
                .findFragmentById(R.id.frameLayoutTestFragment));

        // display the next fragment
        int nextFragmentType = testQTypes[currentFragment];
        switch (nextFragmentType) {
            case TEST_BACK:
            case TEST_FRONT:
                sideFragment = new TestSideFragment();
                transaction.add(R.id.frameLayoutTestFragment,
                        sideFragment.newInstance(nextFragmentType, testSelection[currentFragment]),
                        FRAG_TAG);
                break;
            case TEST_MULTI_FRONT:
            case TEST_MULTI_BACK:
                multiFragment = new TestMultiFragment();
                transaction.add(R.id.frameLayoutTestFragment,
                        multiFragment.newInstance(nextFragmentType, testSelection[currentFragment]),
                        FRAG_TAG);
                break;
        }

        transaction.commit();
        currentFragment++;
    }

    /**
     * An interface method required for the timer fragment used to show the first question of the
     * test after the timer has ended.
     */
    @Override
    public void onTimerEnded() {
        showNextFragment();
    }

    /**
     * An interface method required for the test side fragment that will show the next question when
     * the user pushes the 'NEXT' button.
     *
     * @param view the view that triggered this method.
     */
    @Override
    public void onSideAnswerConfirm(View view) {
        showNextFragment();
    }

    /**
     * An interface method required for the multiple choice fragment that will show the next
     * question when the user pushes the 'NEXT' button.
     *
     * @param view the view that triggered this method.
     */
    @Override
    public void onMultiAnswerConfirm(View view) {
        showNextFragment();
    }

    /**
     * Displays confirmation dialog to confirm leaving the test.
     */
    public void showConfirmationDialog() {
        DialogFragment dialog = new ConfirmTestCancelDialog();
        dialog.show(getFragmentManager(), "ConfirmTestCancelDialog");
    }

    /**
     * Ends the activity when the user chooses to confirm leaving the test.
     *
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) { finish(); }

    /**
     * Returns to the testing activity if the user cancels leaving the test.
     *
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {}

    /**
     * Shows the confirmation dialog when the user presses the back button.
     */
    @Override
    public void onBackPressed() {
        showConfirmationDialog();
    }

    /**
     * Pressing back arrow in toolbar cancels test and moves to previous activity
     * @return always true.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
