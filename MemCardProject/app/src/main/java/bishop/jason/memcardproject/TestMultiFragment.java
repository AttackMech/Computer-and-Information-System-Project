package bishop.jason.memcardproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Random;


/**
 * TestSideFragment class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * A simple {@link Fragment} subclass. It tests a card from the database by showing the front or
 * back of the card, along with the correct answer and 2 other randomly selected answers from other
 * cards in the database.  The user can then select one of the answers from a RadioGroup.
 *
 * Activities that contain this fragment must implement the {@link OnMultiAnswerConfirm} interface
 * to handle interaction events.
 *
 * Use the {@link TestMultiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestMultiFragment extends Fragment {
    private static final String ARG_CARD_ID = "card_id";
    private static final String ARG_Q_TYPE = "question_type";

    private int qType, cardId;

    private String question, correctAnswer, wrongAnswer1, wrongAnswer2, selectedAnswer;
    private Random random;
    private OnMultiAnswerConfirm clickListener;
    private RadioGroup rgSelect;

    public TestMultiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cardId the ID of the card used in the question.
     * @param qType the type of question for this fragment (front/back).
     * @return A new instance of fragment TestMultiFragment.
     */
    public static TestMultiFragment newInstance(int qType, int cardId) {
        TestMultiFragment fragment = new TestMultiFragment();

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

        // determine question parameters
        if (getArguments() != null) {
            qType = getArguments().getInt(ARG_Q_TYPE);
            cardId = getArguments().getInt(ARG_CARD_ID);
        }

        random = new Random();
        selectedAnswer = "";

        // get card infor from the database and store
        Cursor cursor = MemCardDbContract.getCardInfo(getContext(), Integer.toString(cardId));

        if (cursor.moveToFirst()) {
            if (qType == TestActivity.TEST_MULTI_BACK) {
                question = cursor.getString(
                        cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
                correctAnswer = cursor.getString(
                        cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK));
            } else {
                question = cursor.getString(
                        cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK));
                correctAnswer = cursor.getString(
                        cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
            }
        }

        // close open resources
        cursor.close();
        MemCardDbContract.closeDb();

        // get incorrect answers to fill in other multiple choich options
        int[] cardIds = MemCardDbContract.getCardIds(getContext());
        int wrongCardId1, wrongCardId2;

        // loop until selection does not match the correct answer
        do {
            wrongCardId1 = cardIds[random.nextInt(cardIds.length)];
        } while (wrongCardId1 == cardId);

        // loop until selection does not match correct answer nor other wrong answer
        do {
            wrongCardId2 = cardIds[random.nextInt(cardIds.length)];
        } while (wrongCardId2 == cardId || wrongCardId2 == wrongCardId1);

        // get card info for first wrong answer
        cursor = MemCardDbContract.getCardInfo(getContext(), Integer.toString(wrongCardId1));
        if (cursor.moveToFirst()) {
            if (qType == TestActivity.TEST_MULTI_BACK) {
                wrongAnswer1 = cursor.getString(cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK));
            } else {
                wrongAnswer1 = cursor.getString(cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
            }
        }
        cursor.close();

        // get card info for second wrong answer
        cursor = MemCardDbContract.getCardInfo(getContext(), Integer.toString(wrongCardId2));
        if (cursor.moveToFirst()) {
            if (qType == TestActivity.TEST_MULTI_BACK) {
                wrongAnswer2 = cursor.getString(cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK));
            } else {
                wrongAnswer2 = cursor.getString(cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
            }
        }

        // close resources
        cursor.close();
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
        View view = inflater.inflate(R.layout.fragment_test_multi, container, false);

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
        RadioButton rbChoice1 = (RadioButton) view.findViewById(R.id.radioButtonChoice1);
        RadioButton rbChoice2 = (RadioButton) view.findViewById(R.id.radioButtonChoice2);
        RadioButton rbChoice3 = (RadioButton) view.findViewById(R.id.radioButtonChoice3);

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

        // set radio group to record current selection
        rgSelect = (RadioGroup) view.findViewById(R.id.radioGroupChoices);
        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                RadioButton rbSelected = (RadioButton) radioGroup.findViewById(checkedId);
                selectedAnswer = rbSelected.getText().toString();
            }
        });

        // randomly assign answers to radio buttons
        int answerPosition = random.nextInt(3);
        switch (answerPosition) {
            case 0:
                rbChoice1.setText(correctAnswer);
                rbChoice2.setText(wrongAnswer1);
                rbChoice3.setText(wrongAnswer2);
                break;
            case 1:
                rbChoice1.setText(wrongAnswer1);
                rbChoice2.setText(correctAnswer);
                rbChoice3.setText(wrongAnswer2);
                break;
            case 2:
                rbChoice1.setText(wrongAnswer1);
                rbChoice2.setText(wrongAnswer2);
                rbChoice3.setText(correctAnswer);
                break;
        }

        return view;
    }

    /**
     * Callback method to handle button click in the fragment.
     *
     * @param view the view calling this method.
     */
    public void onButtonPressed(View view) {
        if (clickListener != null) {
            clickListener.onMultiAnswerConfirm(view);
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

        if (context instanceof OnMultiAnswerConfirm) {
            clickListener = (OnMultiAnswerConfirm) context;
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
    public interface OnMultiAnswerConfirm {
        void onMultiAnswerConfirm(View view);
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
    public String getSelectedAnswer() { return selectedAnswer; }

    /**
     * Returns the answer the user input.
     *
     * @return a String representing the user's answer.
     */
    public String getCorrectAnswer() { return correctAnswer; }
}
