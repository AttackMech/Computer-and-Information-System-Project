package bishop.jason.memcardproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MemCardSQLiteHelper class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is an extension of the SQLiteOpenHelper class.  It is used to create and access the
 * database for various functions related to managing the user's sets and cards.
 *
 * @see SQLiteOpenHelper
 */

public class MemCardSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 20;
    public static final String DATABASE_NAME = "memcards";
    private Context context;

    /**
     * The constructor for this class saves the context and instantiates the super class.
     *
     * @param context the context of the activity instantiating this class
     * @see SQLiteOpenHelper
     */
    public MemCardSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * This method creates the database, setting up the tables and creating a sample set with 6
     * sample cards.
     *
     * @param db the database used to store data.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MemCardDbContract.Sets.CREATE_TABLE);
        db.execSQL(MemCardDbContract.Cards.CREATE_TABLE);

        // create sample set
        ContentValues setValues = new ContentValues(MemCardDbContract.getSampleSet(context));
        db.insert(MemCardDbContract.Sets.TABLE_NAME, null, setValues);

        // create sample cards
        ContentValues[] cardValues = MemCardDbContract.getSampleCards();
        for (int i = 0; i < cardValues.length; i++) {
            db.insert(MemCardDbContract.Cards.TABLE_NAME, null, cardValues[i]);
        }

    }

    /**
     * This method will reset the database when the version is upgraded.
     *
     * @param db the database used to store data.
     * @param oldVersion the int representing the old version.
     * @param newVersion the int representing the new version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MemCardDbContract.Sets.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MemCardDbContract.Cards.TABLE_NAME);
        onCreate(db);
    }

    /**
     * This method will reset the database when the version is downgraded.
     *
     * @param db the database used to store data.
     * @param oldVersion the int representing the old version.
     * @param newVersion the int representing the new version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
