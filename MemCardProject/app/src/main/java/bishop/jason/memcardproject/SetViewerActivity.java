package bishop.jason.memcardproject;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SetViewerActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * The set viewer activity is used to handle the display of all the sets currently stored in the
 * database. The sets are presented as a series of cards in a list. Each card displays the name of
 * the set as well as the colour and the amount of cards stored in it. The user can select sets to
 * edit or delete, or a new set can be created. These functions are accessible through a set of
 * floating action buttons in the lower right corner of the screen.
 *
 */
public class SetViewerActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.DeleteDialogListener {

    private int fabAdjust, selectedSetPosition;
    private int selectedSetID;
    private Animation showFabAdd, showFabEdit, showFabDel, hideFabAdd, hideFabEdit, hideFabDel,
            rotFabOpen, rotFabClose;

    /**
     * Creates the activity by creating and populating the views, as well as setting up the fab menu
     * and action bar.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_viewer);

        // set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Card Sets");
        actionBar.setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.sets)));

        // set default values
        selectedSetPosition = -1;
        selectedSetID = -1;
        fabAdjust = 1;

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
        fab.setOnClickListener(new View.OnClickListener() {
            // collapse/expand fab menu when user taps
            @Override
            public void onClick(View view) {
                moveFabMenu(view);
            }
        });

        // set action for add fab
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewSet(view);
            }
        });

        // set action for delete fab
        fab = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedSet(view);
                onResume();
            }
        });

        // set action for edit fab
        fab = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSelectedSet(view);
            }
        });

        // populate the list
        populateListData();

        // set up click actions to highlight items for selection
        final ListView lvSets = (ListView) findViewById(R.id.listViewSets);
        lvSets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (selectedSetPosition != position) {
                    view.setBackgroundColor(getResources().getColor(R.color.colorHighlight));
                    if (selectedSetPosition != -1) {
                        View unhighlight = lvSets.getChildAt(selectedSetPosition);
                        unhighlight.setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedSetPosition = position;
                }

                TextView tvSetId = (TextView) view.findViewById(R.id.textViewSetId);
                selectedSetID = Integer.parseInt(tvSetId.getText().toString());
            }
        });
    }

    /**
     * Pressing back will end the activity and close the database resources.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ListView lvSets = (ListView) findViewById(R.id.listViewSets);
        SetViewerAdapter adapter = (SetViewerAdapter) lvSets.getAdapter();
        adapter.closeResources();
    }

    /**
     * Pressing back arrow in toolbar moves to previous activity.
     *
     * @return always true.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Override to refresh data when resumed and reset fabs.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (fabAdjust < 0) {
            moveFabMenu(null);
        }

        ListView lvSets = (ListView) findViewById(R.id.listViewSets);
        SetViewerAdapter adapter = (SetViewerAdapter) lvSets.getAdapter();
        populateListData();
        adapter.notifyDataSetChanged();
    }


    /**
     * Starts the activity to create a new set when the user hits the 'add' fab
     *
     * @param view the view that triggered this method
     */
    public void addNewSet(View view) {
        Intent intent = new Intent(this, CreateNewSetActivity.class);
        intent.putExtra(CreateNewSetActivity.CREATE_SET_NUM, -1);  // -1 to create new set
        startActivity(intent);
    }

    /**
     * Deletes the selected set from the database after user confirmation when the user hits the
     * 'delete' fab.
     *
     * @param view
     */
    private void deleteSelectedSet(View view) {
        if (selectedSetID == -1) {
            Toast.makeText(view.getContext(),"No set selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // confirm delete using dialog
        showConfirmationDialog();
    }

    /**
     * Starts the activity to edit the selected set when the user hits the 'edit' fab.
     *
     * @param view the view that triggered this method.
     */
    private void editSelectedSet(View view) {
        // start activity with selected set information for editing
        if (selectedSetID == -1) {
            Toast.makeText(view.getContext(),"No set selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CreateNewSetActivity.class);
        intent.putExtra(CreateNewSetActivity.CREATE_SET_NUM, selectedSetID);
        startActivity(intent);
    }

    /**
     * Opens and closes the fab menu to allow user to perform actions.
     *
     * @param view the view that triggered this method.
     */
    private void moveFabMenu(View view) {
        // get fab views
        FloatingActionButton fabMain = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);

        // adjust layout params so user can click of viewed fab positions
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
     * Creates the adapter an populates the ListView with items from the database.
     */
    private void populateListData() {
        // get database items
        Cursor cursorSets = MemCardDbContract.getAllSets(getApplicationContext());

        // set list view with adapter
        ListView lvSets = (ListView) findViewById(R.id.listViewSets);
        SetViewerAdapter adapter = new SetViewerAdapter(this, cursorSets);
        lvSets.setAdapter(adapter);
    }

    /**
     * Displays confirmation dialog to confirm set deletion.
     *
     * @see ConfirmDeleteDialog
     */
    public void showConfirmationDialog() {
        DialogFragment dialog = new ConfirmDeleteDialog();
        dialog.show(getFragmentManager(), "ConfirmDeleteDialog");
    }

    /**
     * Will delete selected card if user selects delete option in confirmation dialog.
     *
     * @param dialog the dialog fragment triggering this method.
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        MemCardDbContract.deleteSet(getApplicationContext(), Integer.toString(selectedSetID));
        selectedSetID = -1;

        // inform the user of deletion
        Toast.makeText(getApplicationContext(), R.string.toast_set_delete, Toast.LENGTH_SHORT).show();
        onResume();
    }

    /**
     * Will cancel card deletion when user selects cancel option in confirmation dialog.
     *
     * @param dialog the dialog fragment triggering this method.
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        selectedSetID = -1;
    }

}
