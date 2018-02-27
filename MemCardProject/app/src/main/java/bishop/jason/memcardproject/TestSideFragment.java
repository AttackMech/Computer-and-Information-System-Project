package bishop.jason.memcardproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * TestSideFragment class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * A simple {@link Fragment} subclass. It tests a card from the database by either showing the front
 * or back of a card, asking the user to input the matching term from the other side.
 *
 * Activities that contain this fragment must implement the {@link OnSideAnswerConfirm} interface to
 * handle interaction events.
 *
 * Use the {@link TestSideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestSideFragment extends Fragment {
    private static final String ARG_Q_TYPE = "question_type";
    private static final String ARG_CARD_ID = "card_id";

    private int qType, cardId;
    private String question, correctAnswer;
    private EditText etAnswer;
    private OnSideAnswerConfirm clickListener;

    public TestSideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qType an int representing the question type (front/back).
     * @param cardId the ID of the card to test.
     * @return A new instance of fragment TestSideFragment.
     */
    public static TestSideFragment newInstance(int qType, int cardId) {
        TestSideFragment fragment = new TestSideFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_Q_TYPE, qType);
        args.putInt(ARG_CARD_ID, cardId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when the fragment is created in the parent activity, it retrieves the card info from
     * the database.
     *
     * @param savedInstanceState not used.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get arguments to determine question info
        if (getArguments() != null) {
            qType = getArguments().getInt(ARG_Q_TYPE);
            cardId = getArguments().getInt(ARG_CARD_ID);
        }

        // get card info from database
        Cursor cardInfo = MemCardDbContract.getCardInfo(getContext(), Integer.toString(cardId));

        if (cardInfo.moveToFirst()) {
            if (qType == TestActivity.TEST_BACK) {
                question = cardInfo.getString(
                        cardInfo.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
                correctAnswer = cardInfo.getString(
                        cardInfo.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK));
            } else {
                question = cardInfo.getString(
                        cardInfo.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK));
                correctAnswer = cardInfo.getString(
                        cardInfo.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
            }
        }

        // close resources
        cardInfo.close();
        MemCardDbContract.closeDb();
    }

    /**
     * Called when the view is created in the parent activity it inflates and populates the views
     * with question data.
     *
     * @param inflater LayoutInflater object to inflate the layout.
     * @param container the container View in the parent activity.
     * @param savedInstanceState not used.
     * @return a View populated with question data.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_test_side, container, false);

        // set button click listener
        Button imNext = (Button) view.findViewById(R.id.buttonNextQuestion);
        imNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(view);
            }
        });

        // set layout views to represent test question
        CardView cvFront = (CardView) view.findViewById(R.id.cardViewFront);
        CardView cvBack = (CardView) view.findViewById(R.id.cardViewBack);
        etAnswer = (EditText) view.findViewById(R.id.editTextAnswer);
        etAnswer.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    onButtonPressed(view);
                    return true;
                }
                return false;
            }
        });

        // populate with front or back data
        TextView tvQuestion;
        if (qType == TestActivity.TEST_BACK) {
            cvFront.setVisibility(View.VISIBLE);
            cvBack.setVisibility(View.INVISIBLE);
            tvQuestion = (TextView) cvFront.findViewById(R.id.textViewCardFront);
            TextView tvSetName = (TextView) cvFront.findViewById(R.id.textViewCardSet);
            tvSetName.setVisibility(View.INVISIBLE);
        } else {
            cvFront.setVisibility(View.INVISIBLE);
            cvBack.setVisibility(View.VISIBLE);
            tvQuestion = (TextView) cvBack.findViewById(R.id.textViewCardBack);
            ImageView ivProgress = (ImageView) cvBack.findViewById(R.id.imageViewCardProgress);
            ivProgress.setVisibility(View.INVISIBLE);
            TextView tvProgress = (TextView) cvBack.findViewById(R.id.textViewProgress);
            tvProgress.setVisibility(View.INVISIBLE);
        }

        tvQuestion.setText(question);

        // prepare for answer input
        etAnswer.setHint("Input answer...");
        etAnswer.selectAll();
        etAnswer.requestFocus();

        return view;
    }

    /**
     * Callback method to handle button click in the fragment.
     *
     * @param view the view calling this method.
     */
    public void onButtonPressed(View view) {
        if (clickListener != null) {
            clickListener.onSideAnswerConfirm(view);
        }
    }

    /**
     * Checks that the class using this fragment has implemented the required interface.
     *
     * @param context the context of the instantiating activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSideAnswerConfirm) {
            clickListener = (OnSideAnswerConfirm) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSideAnswerConfirm");
        }
    }

    /**
     * Called when the fragment is detached from the parent activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSideAnswerConfirm {
        void onSideAnswerConfirm(View view);
    }

    /**
     * Returns the question text for this fragment.
     *
     * @return a String representing the question.
     */
    public String getQuestion() { return question; }

    /**
     * Returns the correct answer text for this fragment.
     *
     * @return a String representing the correct answer.
     */
    public String getCorrectAnswer() { return correctAnswer; }

    /**
     * Returns the answer the user input.
     *
     * @return a String representing the user's answer.
     */
    public String getAnswer() { return etAnswer.getText().toString(); }
}
