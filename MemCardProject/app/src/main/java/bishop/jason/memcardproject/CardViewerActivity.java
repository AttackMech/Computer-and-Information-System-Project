package bishop.jason.memcardproject;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Class CardViewerActivity
 * @author Jason Bishop 3042012
 * @version 1.0
 * created on 10/09/2017
 *
 * This class is an activity that allows the user to view cards they have stored in the database.
 * Either all cards are shown, or only those cards pertaining to a set, depending on the passed
 * parameters from the intent that started the activity.
 *
 * Users can scroll through a grid of cards.  Tapping a particular card will flip the view to the
 * back of the card, and highlight it for selection.  Users can also use the fab menu to delete a
 * selected card, or start an activity to edit a selected card, or add a new card.
 */

public class CardViewerActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.DeleteDialogListener {

    public static final String CARD_VIEW_TYPE = "bishop.jason.memcardproject.VIEW_TYPE";
    public static final String SET_TO_SHOW = "bishop.jason.memcardproject.SHOW_SET";
    public static final int VIEW_ALL = 0;
    public static final int VIEW_SET = 1;

    private int fabAdjust, displayType, setToShow, selectedCard;
    private Animation showFabAdd, showFabEdit, showFabDel, hideFabAdd, hideFabEdit, hideFabDel,
            rotFabOpen, rotFabClose;

    /**
     * Sets the layout for the activity, including customized action bar and fab menu.  It also
     * creates and loads the adapter used to display the cards in a RecyclerView.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        // set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.cards)));

        // get intent to determine which cards need to be shown and if multiple cards selectable
        Intent intent = getIntent();
        displayType = intent.getIntExtra(CARD_VIEW_TYPE, VIEW_ALL);

        if (displayType == VIEW_SET) {
            setToShow = intent.getIntExtra(SET_TO_SHOW, 0);
            if (setToShow == 0) {
                Toast.makeText(getApplicationContext(), R.string.no_set_in_db,
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // get set name from database
                actionBar.setTitle("Set: " + MemCardDbContract.getSetName(getApplicationContext(),
                        Integer.toString(setToShow)));
            }
        }

        // get animations for fabs
        showFabAdd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_fab_in);
        hideFabAdd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_fab_out);
        showFabEdit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edit_fab_in);
        hideFabEdit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edit_fab_out);
        showFabDel = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.del_fab_in);
        hideFabDel = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.del_fab_out);
        rotFabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rot_open);
        rotFabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rot_close);

        // set action for main floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fabAdjust = 1;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveFabMenu(view);
            }
        });

        // set action for add fab
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCard(view);
            }
        });

        // set action for delete fag
        fab = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedCard(view);
                onResume();
            }
        });

        // set action for edit fab
        fab = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSelectedCard(view);
                onResume();
            }
        });

        // get card info from database and populate custom RecyclerView
        List<Card> cards = getCardList();
        CardViewerAdapter adapter = new CardViewerAdapter(getApplicationContext(), cards);
        RecyclerView allCards = (RecyclerView) findViewById(R.id.recycleViewCards);
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                getApplicationContext(), calculateColumns(getApplicationContext()));

        allCards.setHasFixedSize(true);
        allCards.setLayoutManager(layoutManager);
        allCards.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        selectedCard = -1;  // no card selected

        // show header if cards ready to test
        ImageView ivTestable = (ImageView) findViewById(R.id.imageViewTestHeadIcon);
        TextView tvTestable = (TextView) findViewById(R.id.textViewTestHeadMessage);
        Cursor cursor = MemCardDbContract.getTestableCards(getApplicationContext());

        // inform user if any cards are ready to be tested
        if (cursor.getCount() > 0) {
            ivTestable.setVisibility(View.VISIBLE);
            tvTestable.setVisibility(View.VISIBLE);
        } else {
            ivTestable.setVisibility(View.INVISIBLE);
            tvTestable.setVisibility(View.INVISIBLE);
        }

        // close resources
        cursor.close();
        MemCardDbContract.closeDb();
    }

    /**
     * Pressing back arrow in toolbar moves to previous activity.
     *
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Resuming activity refreshes list data.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // reset fab menu
        if (fabAdjust < 0) {
            moveFabMenu(null);
        }

        // repopulate RecyclerView with data from the database
        RecyclerView rvAllCards = (RecyclerView) findViewById(R.id.recycleViewCards);
        CardViewerAdapter adapter = new CardViewerAdapter(getApplicationContext(), getCardList());
        rvAllCards.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Starts activity to create a new card.
     *
     * @param view the View that initiated this method
     */
    public void addNewCard(View view) {
        Intent intent = new Intent(this, CreateNewCardActivity.class);
        intent.putExtra(CreateNewCardActivity.CREATE_CARD_NUM, -1);  // -1 to create new set
//        intent.putExtra(CreateNewCardActivity.CARD_IN_SET, setToShow);
        startActivity(intent);
    }

    /**
     * Starts new activity to edit the selected card information if user has selected one.
     *
     * @param view the View that initiated this method.
     */
    private void editSelectedCard(View view) {
        RecyclerView rvCards = (RecyclerView) findViewById(R.id.recycleViewCards);
        CardViewerAdapter adapter = (CardViewerAdapter) rvCards.getAdapter();

        // check for card selection
        if (adapter.getSelectedCardID() == -1) {
            Toast.makeText(view.getContext(), "No card selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // start edit activity passing card information through intent
        Intent intent = new Intent(getApplicationContext(), CreateNewCardActivity.class);
        intent.putExtra(CreateNewCardActivity.CREATE_CARD_NUM, adapter.getSelectedCardID());
//        intent.putExtra(CreateNewCardActivity.CARD_IN_SET, adapter.getSelectedSetValue());
        startActivity(intent);
    }

    /**
     * Shows a dialog to confirm deletion of card if user has selected one.
     *
     * @param view the View that initiated this method.
     */
    private void deleteSelectedCard(View view) {
        RecyclerView rvCards = (RecyclerView) findViewById(R.id.recycleViewCards);
        CardViewerAdapter adapter = (CardViewerAdapter) rvCards.getAdapter();
        selectedCard = adapter.getSelectedCardID();

        // check for card selection
        if (selectedCard == -1) {
            Toast.makeText(view.getContext(), "No card selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // confirm delete with dialog
        showConfirmationDialog();
    }

    /**
     * Displays confirmation dialog to confirm card deletion.
     *
     * @see ConfirmDeleteDialog
     */
    public void showConfirmationDialog() {
        DialogFragment dialog = new ConfirmDeleteDialog();
        dialog.show(getFragmentManager(), "ConfirmDeleteDialog");
    }

    /**
     * Will delete selected card if user selects delete option in confirmation dialog.
     * Required interface for ConfirmDeleteDialog.
     *
     * @param dialog the DialogFragment that initiated this method.
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        MemCardDbContract.deleteCard(getApplicationContext(), Integer.toString(selectedCard));
        selectedCard = -1;

        // inform the user of deletion
        Toast.makeText(
                getApplicationContext(), R.string.toast_card_delete, Toast.LENGTH_SHORT).show();

        onResume();
    }

    /**
     * Will cancel card deletion when user selects cancel option in confirmation dialog.
     * Required interface for ConfrimDeleteDialog.
     *
     * @param dialog the DialogFragment that initiated this method.
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        selectedCard = -1;
    }


    /**
     * Opens and closes the fab menu to allow user to perform actions.
     *
     * @param view the View that initiated this method.
     */
    private void moveFabMenu(@Nullable View view) {
        // get fabs
        FloatingActionButton fabMain = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);

        // adjust fabs so that user can click on same position that fabs appear on screen
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabAdd.getLayoutParams();
        layoutParams.rightMargin += (int) (fabAdd.getWidth() * 1.5 * fabAdjust);
        fabAdd.setLayoutParams(layoutParams);

        layoutParams = (FrameLayout.LayoutParams) fabEdit.getLayoutParams();
        layoutParams.rightMargin += (fabEdit.getWidth() * 3 * fabAdjust);
        fabEdit.setLayoutParams(layoutParams);

        layoutParams = (FrameLayout.LayoutParams) fabDelete.getLayoutParams();
        layoutParams.rightMargin += (int) (fabDelete.getWidth() * 4.5 * fabAdjust);
        fabDelete.setLayoutParams(layoutParams);

        // set fab properties and animations
        if (fabAdjust > 0) {
            fabMain.startAnimation(rotFabOpen);
            fabAdd.startAnimation(showFabAdd);
            fabEdit.startAnimation(showFabEdit);
            fabDelete.startAnimation(showFabDel);

            fabAdd.setClickable(true);
            fabEdit.setClickable(true);
            fabDelete.setClickable(true);
        } else {
            fabAdd.setClickable(false);
            fabEdit.setClickable(false);
            fabDelete.setClickable(false);

            fabMain.startAnimation(rotFabClose);
            fabAdd.startAnimation(hideFabAdd);
            fabEdit.startAnimation(hideFabEdit);
            fabDelete.startAnimation(hideFabDel);
        }

        fabAdjust *= -1;
    }

    /**
     * Calculates the maximum number of columns that will fit on the device screen for use in the
     * RecyclerView that displays the cards on screen.
     *
     * @param context the Context of the activity needing to display the columns.
     * @return an int for the number of columns that can fit on screen.
     */
    public static int calculateColumns(Context context) {
        // get values for display and element sizes
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float cardWidth = context.getResources().getDimension(R.dimen.card_width);
        float paddingWidth = context.getResources().getDimension(R.dimen.card_padding);

        // column calculation
        int colNumber = (int) (dpWidth / ((cardWidth + paddingWidth *2) / displayMetrics.density));

        if (colNumber < 1) {
            return 1;
        }

        return colNumber;
    }

    /**
     * Creates and returns a list of cards based on the intent that started the activity.
     *
     * @return a List of Card objects populated with data from the database.
     */
    private List<Card> getCardList() {
        Cursor cursorCards;
        List<Card> cards = new ArrayList<>();

        // get info from database
        if (displayType == VIEW_ALL) {
            cursorCards = MemCardDbContract.getAllCards(getApplicationContext());
        } else {
            cursorCards = MemCardDbContract.getCardsInSet(
                    getApplicationContext(), Integer.toString(setToShow));
        }

        // populate list with Card objects
        if (cursorCards.moveToFirst()) {
            do {
                Card card = new Card();

                card.setBack(cursorCards.getString(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_BACK)));
                card.setFront(cursorCards.getString(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_FRONT)));
                card.setId(cursorCards.getInt(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards._ID)));
                card.setSetNum(cursorCards.getInt(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_SET_ID)));
                card.setSetName(cursorCards.getString(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.VIEW_COLUMN_SET_NAME)));
                card.setSetColour(cursorCards.getInt(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.VIEW_COLUMN_SET_COLOUR)));
                card.setLevel(cursorCards.getInt(cursorCards.getColumnIndexOrThrow(
                        MemCardDbContract.Cards.COLUMN_LEVEL)));
                card.setTestable(MemCardDbContract.isCardTestable(
                        getApplicationContext(), card.getId()));

                cards.add(card);
            } while (cursorCards.moveToNext());
        }

        // close resources
        cursorCards.close();
        MemCardDbContract.closeDb();

        return cards;
    }
}
