package bishop.jason.memcardproject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * PreTestAdapter class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is an extension of the CursorAdapter class. It is used to create and populate views
 * for use in the PreTestSelect activity.  Views are inflated and populated with card data for any
 * cards in the database that are ready to test as of the current date and time.
 *
 * @see CursorAdapter
 */

public class PreTestAdapter extends CursorAdapter {
    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";

    private List<Integer> selectedPositions = new ArrayList<>();
    private SimpleDateFormat full, date, time;

    /**
     * Class constructor takes the context of the calling activity and a cursor object with all the
     * card data.
     *
     * @param context the context of the activity instantiating this class.
     * @param cursor a Cursor object containing the card data.
     */
    public PreTestAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        full = new SimpleDateFormat(MemCardDbContract.DATE_FORMAT, Locale.getDefault());
        date = new SimpleDateFormat(DATE, Locale.getDefault());
        time = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    }

    /**
     * Inflates new view for each item visible in the GridView.
     *
     * @param context the context of the activity using this adapter.
     * @param cursor a cursor object containing the data to display.
     * @param parent the ViewGroup the returned view will be attached to.
     * @return a View object representing a testable card.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pre_test_gridview_item, parent, false);
    }

    /**
     * Binds data to each passed View from the GridView.
     *
     * @param view the View to bind the data to.
     * @param context the context of the activity using this adapter.
     * @param cursor the cursor object containing the data to display.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // find views to populate
        TextView tvId = (TextView) view.findViewById(R.id.textViewCardId);
        TextView tvFront = (TextView) view.findViewById(R.id.textViewCardFront);
        TextView tvSetName = (TextView) view.findViewById(R.id.textViewCardSet);
        ImageView ivSetColour = (ImageView) view.findViewById(R.id.imageViewCardSetColourFront);
        TextView tvUpdate = (TextView) view.findViewById(R.id.textViewUpdate);
        TextView tvTestDate = (TextView) view.findViewById(R.id.textViewTestDate);
        TextView tvTestTime = (TextView) view.findViewById(R.id.textViewTestTime);
        ImageView ivTestIcon = (ImageView) view.findViewById(R.id.imageViewTestReady);

        // get values from cursor
        int cardId = cursor.getInt(cursor.getColumnIndexOrThrow(MemCardDbContract.Cards._ID));
        String front = cursor.getString(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT));
        String setName = cursor.getString(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_NAME));
        int setColor = cursor.getInt(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_COLOUR));
        String update = cursor.getString(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_UPDATE));
        String testDate = cursor.getString(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_TEST_DATE));
        String testTime = cursor.getString(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_TEST_DATE));

        // populate views with retrieved values
        tvFront.setText(front);
        tvId.setText(Integer.toString(cardId));
        ivSetColour.setBackgroundColor(setColor);
        ivTestIcon.setVisibility(View.VISIBLE);

        // parse dates and catch exceptions
        try {
            Date dateHolder = date.parse(update);
            tvUpdate.setText(date.format(dateHolder));
            dateHolder = date.parse(testDate);
            tvTestDate.setText(date.format(dateHolder));
            dateHolder = full.parse(testTime);
            tvTestTime.setText(time.format(dateHolder));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // set the name of the set or set to NONE
        if (setName != null) {
            tvSetName.setText("Set: " + setName);
        } else {
            tvSetName.setText("Set: None");
        }

        // set highlighting if needed
        if (selectedPositions.contains(cursor.getPosition())) {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorHighlight));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * Required method to override, but simply returns the super value.
     *
     * @param position the position of the requested item.
     * @return an Object representing the requested item.
     */
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Required method to override, but simply returns the super value.
     * @param position the position of the requested view.
     * @param convertView the view to convert if needed.
     * @param parent the parent that the returned view will eventually be attached to.
     * @return a View object for the requested position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    /**
     * Selects all the cards available in the adapter, or deselects if all are selected.
     */
    public void selectAll() {
        if (selectedPositions.size() == getCount()) {
            selectedPositions.clear();
        } else {
            selectedPositions.clear();
            for (int i = 0; i < getCount(); i++) {
                selectedPositions.add(i);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * Returns the IDs of all cards that have been selected.
     *
     * @return an integer array containing all of the selected card IDs.
     */
    public int[] getSelectedItems() {
        int[] selectedItems = new int[selectedPositions.size()];

        for (int i = 0; i < selectedPositions.size(); i++) {
            View view = getView(i, null, null);
            TextView tvId = (TextView) view.findViewById(R.id.textViewCardId);
            selectedItems[i] = Integer.parseInt(tvId.getText().toString());
        }
        return  selectedItems;
    }


    /**
     * Returns a list of the positions of selected cards for the test.
     *
     * @return a List item containing the selected cards IDs.
     */
    public List<Integer> getSelectedPositions() { return selectedPositions; }

    /**
     * Closes the database resources used in this adapter.
     */
    public void closeResources() {
        getCursor().close();
        MemCardDbContract.closeDb();
    }
}
