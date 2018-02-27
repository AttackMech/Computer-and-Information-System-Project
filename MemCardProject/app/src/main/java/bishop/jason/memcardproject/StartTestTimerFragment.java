package bishop.jason.memcardproject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * StartTestTimerFragment class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * A simple {@link Fragment} subclass.  It shows a timer to prepare the user for the test.
 *
 * Activities that contain this fragment must implement the {@link OnTimerEnded} interface to handle
 * interaction events.
 * Use the {@link StartTestTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartTestTimerFragment extends Fragment {
    private static final int COUNTDOWN_TIME = 3000;

    private OnTimerEnded timerEnded;
    private CountDownTimer timer;

    public StartTestTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @return A new instance of fragment StartTestTimerFragment.
     */
    public static StartTestTimerFragment newInstance() {
        StartTestTimerFragment fragment = new StartTestTimerFragment();
        return fragment;
    }

    /**
     * Called when the fragment is created in the parent activity.
     *
     * @param savedInstanceState not used.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    /**
     * Creates and returns the view in this fragment showing a countdown timer.
     *
     * @param inflater LayoutInflater object to inflate the layout.
     * @param container the container View in the parent activity.
     * @param savedInstanceState not used.
     * @return a View showing the countdown timer.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_start_test_timer, container, false);

        // find views to display timer info
        final TextView tvTimer = (TextView) view.findViewById(R.id.textViewCountdown);
        final ProgressBar pbTimer = (ProgressBar) view.findViewById(R.id.progressBarCountdown);
        tvTimer.setText(R.string.timer_ready);

        // start the countdown timer
        timer = new CountDownTimer(COUNTDOWN_TIME, 250) {

            public void onTick(long millisUntilFinished) {

                if (millisUntilFinished <= 1750.0 && millisUntilFinished > 500.0) {
                    tvTimer.setText(R.string.timer_set);
                } else if (millisUntilFinished <= 500.0) {
                    tvTimer.setText(R.string.timer_go);
                }
            }

            // update listener to let activity know fragment is finished
            public void onFinish() { timerEnded.onTimerEnded(); }

        }.start();

        // animate the progress circle on screen
        ObjectAnimator anim = ObjectAnimator.ofInt(pbTimer, "progress", 0, 100);
        anim.setDuration(2500);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();

        // return the inflated view
        return view;
    }

    /**
     * Callback method used by the parent activity.
     */
    public void onTimerEnded() {
        if (timerEnded != null) {
            timerEnded.onTimerEnded();
        }
    }

    /**
     * Checks that the class using this fragment has implemented the required interface.
     *
     * @param context the context of the activity instantiating this class.
     * @throws RuntimeException when the interface has not been implemented.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimerEnded) {
            timerEnded = (OnTimerEnded) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimerEnded");
        }
    }

    /**
     * Called when the fragment is detached from the parent activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
        timerEnded = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnTimerEnded {
        void onTimerEnded();
    }
}
