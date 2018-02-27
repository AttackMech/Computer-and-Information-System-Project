package bishop.jason.memcardproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SetViewerAdapter class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class extends the CursorAdapter class and is used to populate the ListView in the set viewer
 * activity.  It inflates the views and populates them with set data from the database for display
 * to the user.
 *
 * @see CursorAdapter
 */

public class SetViewerAdapter extends CursorAdapter {
    Context context;

    /**
     * Class constructor.
     *
     * @param context the context of the activity instantiating this class.
     * @param cursor the Cursor object containing the set data from the database.
     */
    public SetViewerAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    /**
     * Inflates a new View and returns it.
     *
     * @param context the context of the activity using this class.
     * @param cursor the Cursor object containing the set data.
     * @param parent the parent tha returned view will eventually be attached to.
     * @return a view used to represent the set data.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.set_viewer_listview_row, parent, false);
    }

    /**
     * Binds set data at the given position to a particular View.
     *
     * @param view the view to bind the data to.
     * @param context the context of the activity using this class.
     * @param cursor the cursor object containing the set data.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find views to populate
        TextView tvName = (TextView) view.findViewById(R.id.textViewName);
        TextView tvAmount = (TextView) view.findViewById(R.id.textViewAmount);
        ImageView ivColour = (ImageView) view.findViewById(R.id.imageViewColour);
        TextView tvSetId = (TextView) view.findViewById(R.id.textViewSetId);
        Button bViewSet = (Button) view.findViewById(R.id.buttonViewSet);

        // get values from cursor
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_NAME));
        final int amount = cursor.getInt(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Sets.VIEW_COLUMN_AMOUNT));
        int colour = cursor.getInt(
                cursor.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_COLOUR));

        final int setId = cursor.getInt(cursor.getColumnIndexOrThrow(MemCardDbContract.Sets._ID));

        // set click action for button
        bViewSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount == 0) {
                    Toast.makeText(view.getContext(), "No cards in selected set", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewSetCards(view.getContext(), setId);
            }
        });

        // populate views with values
        tvName.setText(name);
        tvAmount.setText(Integer.toString(amount));
        ivColour.setBackgroundColor(colour);
        tvSetId.setText(Integer.toString(setId));
    }

    /**
     * Starts the card viewer activity, allowing the user to view all cards assigned to the user
     * selected sets.
     *
     * @param context the context of the activity using this class.
     * @param setId the ID of the set to view.
     */
    private void viewSetCards(Context context, int setId) {
        closeResources();

        // start card viewer activity
        Intent intent = new Intent(context, CardViewerActivity.class);
        intent.putExtra(CardViewerActivity.CARD_VIEW_TYPE, CardViewerActivity.VIEW_SET);
        intent.putExtra(CardViewerActivity.SET_TO_SHOW, setId);
        context.startActivity(intent);
    }

    /**
     * Closes open database resources to prevent memory leaks.
     */
    public void closeResources() {
        getCursor().close();
        MemCardDbContract.closeDb();
    }
}
