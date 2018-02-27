package bishop.jason.memcardproject;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * CardViewerAdapter class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This is a custom adapter class that extends the RecyclerView Adapter.  It is used to populate the
 * RecyclerView in the CardViewerActivity.  It creates and binds views with information from the
 * database.  Contains an inner ViewHolder class to represent the card data.
 *
 * @see android.support.v7.widget.RecyclerView.Adapter
 * @see CardViewHolder
 */

public class CardViewerAdapter extends RecyclerView.Adapter<CardViewerAdapter.CardViewHolder> {
    private Context context;
    private List<Card> cards;
    private int selectedItemPosition, selectedItemId;
    private View selectedItemHolder;

    /**
     * Constructor for this class.
     *
     * @param context the context of the Activity using this adapter.
     * @param cards a list of Card objects to be represented.
     */
    public CardViewerAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
        selectedItemPosition = RecyclerView.NO_POSITION;
        selectedItemId = -1;
    }

    /**
     * Binds the ViewHolder with information from the list of cards.
     *
     * @param holder the ViewHolder that displays card information.
     * @param position the position of the ViewHolder in the RecyclerView list.
     */
    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        // set card information in the holder
        Card card = cards.get(position);
        holder.cardId.setText(Integer.toString(card.getId()));
        holder.front.setText(card.getFront());
        holder.back.setText(card.getBack());
        holder.setColourFront.setBackgroundColor(card.getSetColour());
        holder.setColourBack.setBackgroundColor(card.getSetColour());
        holder.levelNum = card.getLevel();
        holder.level.setImageResource(getLevelImage(holder.levelNum));

        // show the "Complete" text if the card has reached maximum level
        if (holder.levelNum >= 6) {
            holder.levelComplete.setVisibility(View.VISIBLE);
        }

        // show the testable icon if card is testable
        if (card.isTestable()) {
            holder.testIcon.setVisibility(View.VISIBLE);
        }

        // set the set information of the card
        if (card.getSetNum() > 0) {
            holder.set.setText("Set: " + card.getSetName());
        } else {
            holder.set.setVisibility(View.INVISIBLE);
            holder.setColourFront.setVisibility(View.INVISIBLE);
            holder.setColourBack.setVisibility(View.INVISIBLE);
        }

        // hide the back of the card
        holder.cardBack.setVisibility(View.GONE);

        // set the click action for the front of the card
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return;
                }

                // unhighlight card to show it is deselected
                if (selectedItemPosition != RecyclerView.NO_POSITION) {
                    selectedItemHolder.setBackgroundColor(Color.TRANSPARENT);
                }

                // highlight item and store information of selected card
                selectedItemPosition = holder.getAdapterPosition();
                selectedItemHolder = holder.cardHolder;
                selectedItemId = holder.getCardId();
                holder.cardHolder.setBackgroundColor(
                        context.getResources().getColor(R.color.colorHighlight));

                // animate the card flip
                holder.flipCard();
            }
        };

        holder.cardFront.setOnClickListener(clickListener);
        holder.cardBack.setOnClickListener(clickListener);
    }

    /**
     * Creates the ViewHolder to be used when displaying the cards by inflating a View.
     *
     * @param parent the ViewGroup that the resulting holder will be attached to.
     * @param viewType the view type.
     * @return a ViewHolder representing a card.
     */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_holder, parent, false);

        return new CardViewHolder(itemView);
    }

    /**
     * Returns the number of items to be represented.
     *
     * @return an int representing the total number of items.
     */
    @Override
    public int getItemCount() {
        return cards.size();
    }


    /**
     * Returns the resource ID for the image associated with the passed level value.
     *
     * @param level the level of the card.
     * @return an int representing the ID for the image used.
     */
    private int getLevelImage(int level) {
//        Log.v(TAG, "getting image for level " + level);
        switch (level) {
            case 1:
                return R.drawable.level1;
            case 2:
                return R.drawable.level2;
            case 3:
                return R.drawable.level3;
            case 4:
                return R.drawable.level4;
            case 5:
                return R.drawable.level5;
            case 6:
                return R.drawable.level6;
            default:
                return R.drawable.level1;
        }
    }

    /**
     * Returns the ID of the currently selected item.
     *
     * @return an int representing the selected ID.
     */
    public int getSelectedCardID() {
        return selectedItemId;
    }

    /**
     * CardViewHolder class (inner class)
     * @author Jason Bishop
     * @version 1.0
     * created 10/09/2017
     *
     * This class is used to hold the view when representing the card data in the RecyclerView of
     * the associated activity.  It holds card data and animates the flipping of the card.
     *
     * @see android.support.v7.widget.RecyclerView.ViewHolder
     */
    public class CardViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout cardHolder;
        private CardView cardFront, cardBack;
        private TextView front, back, set, cardId, levelComplete;
        private ImageView setColourFront, setColourBack, level, testIcon;
        private int levelNum;
        private boolean isFlipped;

        /**
         * The constructor for this class.
         *
         * @param view the View used to represent a particular card.
         */
        public CardViewHolder(View view) {
            super(view);

            // get various views to display data to user
            cardHolder = (FrameLayout) view.findViewById(R.id.frameLayoutCard);
            cardId = (TextView) view.findViewById(R.id.textViewCardId);
            front = (TextView) view.findViewById(R.id.textViewCardFront);
            back = (TextView) view.findViewById(R.id.textViewCardBack);
            set = (TextView) view.findViewById(R.id.textViewCardSet);
            setColourFront = (ImageView) view.findViewById(R.id.imageViewCardSetColourFront);
            setColourBack = (ImageView) view.findViewById(R.id.imageViewCardSetColourBack);
            level = (ImageView) view.findViewById(R.id.imageViewCardProgress);
            levelComplete = (TextView) view.findViewById(R.id.textViewComplete);
            cardFront = (CardView) view.findViewById(R.id.cardViewFront);
            cardBack = (CardView) view.findViewById(R.id.cardViewBack);
            testIcon = (ImageView) view.findViewById(R.id.imageViewTestReady);

            // set default values
            levelNum = 0;
            isFlipped = false;
        }

        /**
         * Returns the ID of the card for this item.
         *
         * @return an int representing the card ID.
         */
        private int getCardId() { return Integer.parseInt(cardId.getText().toString()); }

        /**
         * Animates the flip between the front and back of the card views.
         */
        private void flipCard() {
            // get the center point of the View
            float centerX = (cardFront.getLeft() + cardFront.getRight()) / 2;
            float centerY = (cardFront.getTop() + cardFront.getBottom()) / 2;

            // create animation and set values
            FlipAnimation animation;
            if (!isFlipped) {
                animation = new FlipAnimation(0, 90, centerX, centerY);
            } else {
                animation = new FlipAnimation(0, -90, centerX, centerY);
            }

            animation.setDuration(200);
            animation.setFillAfter(true);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setAnimationListener(new DisplayNextView(!isFlipped, cardFront, cardBack));

            // determine flip direction
            if (!isFlipped) {
                cardFront.startAnimation(animation);
            } else {
                cardBack.startAnimation(animation);
            }
            isFlipped = !isFlipped;
        }
    }
}
