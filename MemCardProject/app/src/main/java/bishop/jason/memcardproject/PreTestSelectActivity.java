package bishop.jason.memcardproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * PreTestSelectActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 09/10/2017
 *
 * This class is an activity used to display all the cards that are available for testing to the
 * user.  Users are able to select which cards they would like to test, and can begin the test by
 * hitting the appropriate button.
 */

public class PreTestSelectActivity extends AppCompatActivity {
    public static final long TWELVE_HOURS = 60 * 60 * 12;
    public static final long ONE_DAY = TWELVE_HOURS * 2;
    public static final long THREE_DAYS = ONE_DAY * 3;
    public static final long ONE_WEEK = ONE_DAY * 7;
    public static final long TWO_WEEKS = ONE_WEEK * 2;

    private PreTestAdapter adapter;

    /**
     * Creates the layout for this activity and sets up the GridView and adapter to display the
     * testable card data.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pre_test_select);

        // set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.test)));

        // populate GridView with available cards and set click listener for highlighting
        getTestableCards();
        GridView gridView = (GridView) findViewById(R.id.gridViewTestCards);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                int selectedIndex = adapter.getSelectedPositions().indexOf(position);

                if (selectedIndex > -1) {
                    adapter.getSelectedPositions().remove(selectedIndex);
                    // turn off highlighting
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    adapter.getSelectedPositions().add(position);
                    // turn on highlighting
                    view.setBackgroundColor(getApplicationContext().getResources().getColor(
                            R.color.colorHighlight));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Ends the activity when the user presses the back button.
     */
    @Override
    public void onBackPressed() {
        endActivity(null);
    }

    /**
     * Updates the adapter when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (getTestableCards()) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Sets up the 'SELECT ALL' button in the action bar as a convenient shortcut for the user.
     *
     * @param menu the menu item to attach the options to.
     * @return always true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_all_action_menu, menu);
        return true;
    }

    /**
     * Selects all cards on display when the user hits the 'SELECT ALL' button.
     *
     * @param item the menu item that was pressed.
     * @return a boolean true when the user hits the button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionSelectAll && adapter != null) {
            adapter.selectAll();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When user clicks begin test button, will begin test with selected cards or inform user that
     *  no cards have been selected.
     */
    public void beginTest(View view) {
        if (adapter != null) {
            int[] selectedItems = adapter.getSelectedItems();
            if (selectedItems.length > 0) {
                // get selected items from adapter and begin test activity
                Intent intent = new Intent(this, TestActivity.class);
                intent.putExtra(TestActivity.TEST_SELECTION, selectedItems);

                startActivity(intent);
                endActivity(null);

            } else {
                Toast.makeText(
                        getApplicationContext(), "No cards have been selected", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(), "No cards are available to test", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Ends the activity when user pushes the cancel button.
     *
     * @param view the view that triggered this method.
     */
    public void endActivity(View view) {
        if (adapter != null) {
            adapter.closeResources();
        }

        MemCardDbContract.closeDb();
        finish();
    }

    /**
     * Retrieves the list of testable cards from the database.
     * @return a boolean true if there are any cards available for testing
     */
    private boolean getTestableCards() {
        // get views
        GridView gridView = (GridView) findViewById(R.id.gridViewTestCards);
        TextView tvNoTest = (TextView) findViewById(R.id.textViewNoTestMsg);

        // access database
        Cursor testableCards = MemCardDbContract.getTestableCards(getApplicationContext());

        // show cards or message if no cards are available
        if (testableCards != null && testableCards.getCount() != 0) {
            tvNoTest.setVisibility(View.GONE);
            adapter = new PreTestAdapter(getApplicationContext(),
                    MemCardDbContract.getTestableCards(getApplicationContext()));

            gridView.setAdapter(adapter);

            return true;
        } else {
            tvNoTest.setVisibility(View.VISIBLE);

            return false;
        }
    }
}
