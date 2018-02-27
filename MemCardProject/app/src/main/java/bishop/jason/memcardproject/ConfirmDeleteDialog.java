package bishop.jason.memcardproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * ConfirmDeleteDialog class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is an extension of the DialogFragment class and used to confirm when the user wants to
 * delete a card or set from the database.
 *
 * @see android.app.DialogFragment
 */

public class ConfirmDeleteDialog extends DialogFragment {
    /**
     * The interface required to be implemented when this class is used, to handle the user
     * selection.
     */
    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    DeleteDialogListener listener;

    /**
     * Sets the listener object when the dialog is attached to the calling activity and checks that
     * the interface is implemented.
     *
     * @param context the context of the activity instantiating this class.
     * @throws ClassCastException if interface is not implemented.
     */
    @Override
    public void onAttach(Context context) throws ClassCastException {
        super.onAttach(context);
        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement DeleteDialogListener");
        }
    }

    /**
     * Creates the dialog to show to the user.
     *
     * @param savedInstanceState not used.
     * @return the Dialog to be shown to the user.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_del)
                .setPositiveButton(R.string.del_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(ConfirmDeleteDialog.this);
                    }
                }).setNegativeButton(R.string.del_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogNegativeClick(ConfirmDeleteDialog.this);
                    }
        });

        return builder.create();
    }
}
