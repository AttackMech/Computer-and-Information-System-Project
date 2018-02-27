package bishop.jason.memcardproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ResultsAdapter class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is an extension of the base adapter class and is used to populate a ListView showing
 * the results of the test the user has completed.  It inflates the view and populates it with data
 * showing the results of the test, as well as the dates for future testing.
 *
 * @see BaseAdapter
 */

public class ResultsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private TestResultItem[] testResults;

    /**
     * Class constructor creates inflater and stores array of test result data.
     *
     * @param context the context of the activity using this class.
     * @param testResults the array of test results.
     */
    public ResultsAdapter(Context context, TestResultItem[] testResults) {
        inflater = LayoutInflater.from(context);
        this.testResults = testResults;
    }

    /**
     * Returns the number of test result items in the adapter.
     *
     * @return an int value for the number of result items.
     */
    @Override
    public int getCount() {
        return testResults.length;
    }

    /**
     * Required method to override, not used.
     *
     * @param position the position of the requested item.
     * @return an int for the ID of the item in the adapter.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Required method to override, not used.
     *
     * @param position the position of the requested item.
     * @return an Object representing the requested item.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Returns a view populated with the appropriate test result data.
     *
     * @param position the position of the view in the list
     * @param view the view to inflate and return.
     * @param viewGroup the parent the returned view will eventually be attached to.
     * @return a View containing the test result data.
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.results_view_item, null);

        // original test info
        TextView tvQuestion = (TextView) view.findViewById(R.id.textViewQuestion);
        TextView tvAnswer = (TextView) view.findViewById(R.id.textViewAnswer);

        // results of question
        ImageView ivResult = (ImageView) view.findViewById(R.id.imageViewQuestionResult);
        TextView tvResult = (TextView) view.findViewById(R.id.textViewQuestionResult);
        TextView tvCorrectLabel = (TextView) view.findViewById(R.id.textViewCorrectLabel);
        TextView tvCorrectAnswer = (TextView) view.findViewById(R.id.textViewCorrectAnswer);

        // next test date for card
        TextView tvNextDate = (TextView) view.findViewById(R.id.textViewNextTest);

        // card level information
        TextView tvLevelUp = (TextView) view.findViewById(R.id.textViewLevelUp);
        TextView tvFromLevel = (TextView) view.findViewById(R.id.textViewLevelFrom);
        TextView tvToLevel = (TextView) view.findViewById(R.id.textViewLevelTo);
        ImageView ivArrow = (ImageView) view.findViewById(R.id.imageViewLevelArrow);

        // populate views with relevant information
        tvQuestion.setText(testResults[position].question);
        tvAnswer.setText(testResults[position].testAnswer);
        tvToLevel.setText(Integer.toString(testResults[position].originalLevel));
        tvNextDate.setText(testResults[position].nextTest);

        if (testResults[position].correct) {
            ivResult.setImageResource(R.drawable.ok_checkmark);
            tvResult.setText(R.string.correct_result);
            tvCorrectLabel.setVisibility(View.INVISIBLE);
            tvCorrectAnswer.setVisibility(View.INVISIBLE);

            tvLevelUp.setText(R.string.level_up);
            tvFromLevel.setText(Integer.toString(testResults[position].originalLevel - 1));
            ivArrow.setImageResource(R.drawable.up_arrow);
        } else {
            ivResult.setImageResource(R.drawable.wrong_x);
            tvResult.setText(R.string.incorrect_result);
            tvCorrectLabel.setVisibility(View.VISIBLE);
            tvCorrectAnswer.setVisibility(View.VISIBLE);
            tvCorrectAnswer.setText(testResults[position].correctAnswer);

            tvLevelUp.setText(R.string.level_same);
            tvFromLevel.setText(Integer.toString(testResults[position].originalLevel));
            ivArrow.setImageResource(R.drawable.arrow_to_right);
        }

        return view;
    }
}
