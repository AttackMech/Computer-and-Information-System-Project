package bishop.jason.memcardproject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * SetNumSpinnerAdapter class
 * @author Jason Bishop 30420212
 * @version 1.0
 * created 10/09/2017
 *
 * This class is an extension of the BaseAdapter class and used to populate a spinner showing the
 * sets available in the database that a user created card can be assigned to.
 *
 * @see BaseAdapter
 */

public class SetNumSpinnerAdapter extends BaseAdapter {
    private int[] setColours, setIds;
    private String[] setNames;
    private LayoutInflater inflater;

    /**
     * Class constructor creates inflater and stores set information in arrays.
     * @param context
     * @param setItems
     */
    public SetNumSpinnerAdapter(Context context, Cursor setItems) {
        inflater = (LayoutInflater.from(context));
        if (setItems.moveToFirst()) {
            setNames = new String[setItems.getCount()];
            setIds = new int[setItems.getCount()];
            setColours = new int[setItems.getCount()];
            int cursorPosition = 0;
            do {
                setNames[cursorPosition] = setItems.getString(
                        setItems.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_NAME));
                setIds[cursorPosition] = setItems.getInt(
                        setItems.getColumnIndexOrThrow(MemCardDbContract.Sets._ID));
                setColours[cursorPosition] = setItems.getInt(
                        setItems.getColumnIndexOrThrow(MemCardDbContract.Sets.COLUMN_COLOUR));

                cursorPosition++;
            } while (setItems.moveToNext());

            // close resources
            setItems.close();
            MemCardDbContract.closeDb();
        }
    }

    /**
     * Returns the total number of items available in the adapter.  There is always 1 because there
     * is an option for 'None.'
     * @return an int representing the number of items available.
     */
    @Override
    public int getCount() {
        if (setNames == null) {
            return 1;
        }
        return setNames.length + 1; }

    /**
     * Returns the ID of the set at the requested position.
     *
     * @param position the positiong of the requested set.
     * @return the int representing the set ID.
     */
    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return 0;
        } else {
            return setIds[position - 1];
        }
    }

    /**
     * Required method to override, not used.
     * @param position the position of the requested item.
     * @return an int representing the ID of the requested item
     */
    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // inflate view
        view = inflater.inflate(R.layout.set_spinner_items, null);

        // get views to populate
        ImageView icon = (ImageView) view.findViewById(R.id.imageViewSetColour);
        TextView setName = (TextView) view.findViewById(R.id.textViewSetName);
        TextView setId = (TextView) view.findViewById(R.id.textViewSetNum);

        // set the first position to none and populate the rest with set data
        if (position == 0) {
            icon.setBackgroundColor(Color.WHITE);
            setName.setText("None");
            setId.setText("0");
        } else {
            icon.setBackgroundColor(setColours[position - 1]);
            setName.setText(setNames[position - 1]);
            setId.setText(Integer.toString(setIds[position - 1]));
        }

        return view;
    }

    /**
     * Returns the position in the spinner of the passed set number.
     *
     * @param setNum the ID of the requested set.
     * @return an int representing the position in the spinner of the requested set.
     */
    public int getSetPosition(int setNum) {
       if (setIds != null) {
           for (int i = 0; i < setIds.length; i++) {
               if (setNum == setIds[i]) {
                   return i + 1;
               }
           }
       }
        return 0;
    }
}
