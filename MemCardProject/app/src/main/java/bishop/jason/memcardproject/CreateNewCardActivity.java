package bishop.jason.memcardproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * CreateNewCardActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This set is used to create a new card or edit an existing card from the database.  Users can set
 * the front and back text, as well as pick a set to put the card in.  When the user selects the
 * 'SAVE' button, the information will be saved to the database and the user will be returned to
 * the card viewing activity to see their changes.
 */

public class CreateNewCardActivity extends AppCompatActivity {
    public static final String CREATE_CARD_NUM = "bishop.jason.memcardproject.CARD_ID";

    private int cardNum;

    /**
     * Sets the layout for the activity, populating the views and setting the action bar.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_card);

        // set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(
                getApplicationContext().getResources().getColor(R.color.cards)));

        // get Intent that created view and check if need to create new card or edit existing
        Intent intent = getIntent();
        cardNum = intent.getIntExtra(CREATE_CARD_NUM, -1);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerSetSelect);
        Cursor sets = MemCardDbContract.getAllSets(getApplicationContext());
        SetNumSpinnerAdapter adapter = new SetNumSpinnerAdapter(getApplicationContext(), sets);
        spinner.setAdapter(adapter);

        EditText etFront = (EditText) findViewById(R.id.editTextCardFront);
        EditText etBack = (EditText) findViewById(R.id.editTextCardBack);

        // edit an existing card
        if (cardNum != -1) {
            actionBar.setTitle("Edit Card");

            // get card info from database
            Cursor card = MemCardDbContract.getCardInfo(
                    getApplicationContext(), Integer.toString(cardNum));

            // populate views with retrieved values.
            if (card.moveToFirst()) {
                etFront.setText(card.getString(card.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_FRONT)));
                etBack.setText(card.getString(card.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_BACK)));
                etFront.requestFocus();
                etFront.selectAll();
                spinner.setSelection(adapter.getSetPosition(card.getInt(card.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_SET_ID))));

                card.close();
            } else {  // error, leave activity
                Toast.makeText(
                        this, R.string.no_card_in_db, Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        } else { // create a new card
            actionBar.setTitle("Create New Card");
            etFront.requestFocus();
            etFront.selectAll();

            spinner.setSelection(0);
        }

        // close resources
        sets.close();
        MemCardDbContract.closeDb();
    }

    /**
     * Saves the new/edited card information that the user entered into the database when user hits
     * the 'SAVE' button.
     *
     * @param view the View that triggered this method (not used).
     */
    public void saveCard(View view) {
        // get values from views
        TextView cardFront = (TextView) findViewById(R.id.editTextCardFront);
        TextView cardBack = (TextView) findViewById(R.id.editTextCardBack);
        Spinner setID = (Spinner) findViewById(R.id.spinnerSetSelect);

        // get database values
        ContentValues values = new ContentValues();
        values.put(MemCardDbContract.Cards.COLUMN_FRONT, cardFront.getText().toString());
        values.put(MemCardDbContract.Cards.COLUMN_BACK, cardBack.getText().toString());
        values.put(MemCardDbContract.Cards.COLUMN_SET_ID, (Integer) setID.getSelectedItem());


        // check if new (or edit)
        if (cardNum < 0) {
            // put values into db
            values.put(MemCardDbContract.Cards.COLUMN_LEVEL, 1);
            values.put(
                    MemCardDbContract.Cards.COLUMN_UPDATE,MemCardDbContract.getCurrentDateTime());
            values.put(
                    MemCardDbContract.Cards.COLUMN_TEST_DATE, MemCardDbContract.getInitDateDiff());

            MemCardDbContract.saveNewCard(getApplicationContext(), values);

            // display info to user
            Toast.makeText(this, R.string.toast_card_create, Toast.LENGTH_LONG).show();
        } else {
            // else update values in db
            MemCardDbContract.updateCard(getApplicationContext(), values, Integer.toString(cardNum));

            // display info to user
            Toast.makeText(this, R.string.toast_card_update, Toast.LENGTH_LONG).show();
        }

        // return to previous activity
        finish();
    }

    /**
     * Returns to the previous activity when the user hits the 'CANCEL' button.
     *
     * @param view the view that triggered this method (not used).
     */
    public void endActivity(View view) { finish(); }
}
