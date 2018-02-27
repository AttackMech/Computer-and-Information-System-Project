package bishop.jason.memcardproject;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ColourSpinnerAdapter class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * An adapter extended from the BaseAdapter class used to populate a spinner of colours used when
 * the user is creating or editing a set.
 *
 * @see android.widget.BaseAdapter
 */

public class ColourSpinnerAdapter extends BaseAdapter {
    private int[] colourValues;
    private String[] colourNames;
    private LayoutInflater inflater;

    /**
     * The constructor for this class sets up the arrays for colours and names.
     *
     * @param context the context of the activity using this class.
     */
    public ColourSpinnerAdapter(Context context) {
        colourNames = context.getResources().getStringArray(R.array.setColourNames);
        colourValues = new int[] {ContextCompat.getColor(context,R.color.setBlack),
                ContextCompat.getColor(context,R.color.setBlue),
                ContextCompat.getColor(context,R.color.setBrown),
                ContextCompat.getColor(context,R.color.setCyan),
                ContextCompat.getColor(context,R.color.setGreen),
                ContextCompat.getColor(context,R.color.setOrange),
                ContextCompat.getColor(context,R.color.setPink),
                ContextCompat.getColor(context,R.color.setRed),
                ContextCompat.getColor(context,R.color.setViolet)};
        inflater = (LayoutInflater.from(context));
    }

    /**
     * Returns the total number of colours available.
     *
     * @return an int representing the total number of colours.
     */
    @Override
    public int getCount() {
        return colourNames.length;
    }

    /**
     * Returns an object representing the colour value at the specified position.
     *
     * @param position the position of the colour in the spinner list.
     * @return an int object that represents the colour value.
     */
    @Override
    public Object getItem(int position) { return colourValues[position]; }

    /**
     * A method required to be overidden but not used.
     *
     * @param position int value.
     * @return a long value.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Returns the View to be used when representing the item in the spinner list.
     *
     * @param position the position of the represented item.
     * @param view the View used to represent the colour value.
     * @param viewGroup the parent that the view will be attached to.
     * @return a view containing the colour information for the spinner.
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // inflate and get views to store info
        view = inflater.inflate(R.layout.colour_spinner_items, null);
        ImageView icon = (ImageView) view.findViewById(R.id.textViewColourIcon);
        TextView colorName = (TextView) view.findViewById(R.id.textViewColourName);

        // set values
        icon.setBackgroundColor(colourValues[position]);
        colorName.setText(colourNames[position]);

        return view;
    }

    /**
     * Returns the position in the spinner of the passed colour value.
     *
     * @param colour the colour requested.
     * @return an int representing the position of the colour in the list.
     */
    public int getColourPosition(int colour) {
        for (int i = 0; i < colourValues.length; i++) {
            if (colour == colourValues[i])
                return i;
        }
        return 0;
    }
}

