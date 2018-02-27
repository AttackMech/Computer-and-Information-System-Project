package bishop.jason.memcardproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * CreateNewSetActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is an activity that allows the user to create or edit a set in the database.  Sets are
 * used to contain collections of cards.  The user can choose the name and colour of the set, and
 * assigns cards to the set by accessing the card creation/edit options.
 */

public class CreateNewSetActivity extends AppCompatActivity {
    public static final String CREATE_SET_NUM  = "bishop.jason.memcardproject.SET_NUM";

    private int setNum;

    /**
     * Sets the layout for the activity, populating the views and setting the action bar.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_set);

        // set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.sets)));

        // get Intent that created view and check if need to create new set or edit existing
        Intent intent = getIntent();
        setNum = intent.getIntExtra(CREATE_SET_NUM, -1);

        // set custom spinner to display colours
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSetColours);
        ColourSpinnerAdapter adapter = new ColourSpinnerAdapter(getApplicationContext());
        spinner.setAdapter(adapter);

        EditText etSetName = (EditText) findViewById(R.id.editTextSetName);

        // check for edit set and populate views with database values
        if (setNum != -1) {

            Cursor setInfo = MemCardDbContract.getSetInfo(getApplicationContext(),
                    Integer.toString(setNum));

            if (setInfo.moveToFirst()) {
                actionBar.setTitle("Edit Set: " + setInfo.getString(
                        setInfo.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_NAME)));

                etSetName.setText(setInfo.getString(
                        setInfo.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_NAME)));
                etSetName.requestFocus();
                etSetName.selectAll();

                spinner.setSelection(adapter.getColourPosition(setInfo.getInt(
                        setInfo.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_COLOUR))));
            } else { // error, inform user and exit activity
                Toast.makeText(
                        this, "Error finding Set information in database", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }

            // close resources
            setInfo.close();
            MemCardDbContract.closeDb();

        } else { // create a new set
            actionBar.setTitle("Create New Set");

            etSetName.setHint("Enter a name for the set...");
            etSetName.requestFocus();
            etSetName.selectAll();
        }
    }

    /**
     * Returns to the set viewer activity when the user hits the 'CANCEL' button.
     *
     * @param view the view that initiated this method (not used).
     */
    public void endActivity(View view) { finish(); }

    /**
     * Saves the set information the user has entered into to the database when the user clicks the
     * 'SAVE' button.
     *
     * @param view the view that initiated this method (not used).
     */
    public void saveSet(View view) {
        // get values
        EditText etSetName = (EditText) findViewById(R.id.editTextSetName);
        Spinner spinColours = (Spinner) findViewById(R.id.spinnerSetColours);
        int colour = (Integer) spinColours.getSelectedItem();

        // put into db
        saveSetToDb(etSetName.getText().toString(), colour);

        // return to previous activity
        finish();
    }

    /**
     * Saves the passed information into the database for a new or updated set.
     *
     * @param name the name of the set.
     * @param colour the colour of the set.
     */
    private void saveSetToDb(String name, int colour) {
        ContentValues values = new ContentValues();

        values.put(MemCardDbContract.Sets.COLUMN_NAME, name);
        values.put(MemCardDbContract.Sets.COLUMN_COLOUR, colour);

        // check if creating new set or editing existing and save
        if (setNum == -1) {
            MemCardDbContract.saveNewSet(getApplicationContext(), values);
            Toast.makeText(getApplicationContext(), R.string.toast_set_create, Toast.LENGTH_SHORT).show();
        } else {
            MemCardDbContract.updateSet(getApplicationContext(), values, Integer.toString(setNum));
            Toast.makeText(getApplicationContext(), R.string.toast_set_update, Toast.LENGTH_SHORT).show();
        }
    }
}
