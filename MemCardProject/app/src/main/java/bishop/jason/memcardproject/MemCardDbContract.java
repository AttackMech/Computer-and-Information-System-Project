package bishop.jason.memcardproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * MemCardDbContract class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is the main interface to the database for all other classes in the application.  It
 * contains the inner classes Sets and Cards to hold the information for those tables in the
 * databse.  The class also contains various methods to access information from the database,
 * selecting the appropriate data needed.
 *
 * @see Sets
 * @see Cards
 */

public final class MemCardDbContract {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * Empty private constructor so the class cannot be instantiated
     */
    private MemCardDbContract() {}
        private static SQLiteDatabase db;

    /**
     * This class extends the BaseColumns class and is the model for the sets table in the database.
     * There are also several SQL statements that allow various types of access to this table.
     *
     * @see BaseColumns
     */
    public static class Sets implements BaseColumns {
        // table name and columns
        public static final String TABLE_NAME = "sets";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COLOUR = "colour";
        public static final String VIEW_COLUMN_AMOUNT = "set_amount";

        // create table statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_COLOUR + " INTEGER" + ")";

        // select a particular set
        public static final String SELECT_SET = "SELECT * FROM " + TABLE_NAME + " WHERE " + _ID +
                " LIKE ?";

        // select all sets and cards in each set
        public static String SELECT_ALL_SETS = "SELECT " + TABLE_NAME + "." + _ID + "," +
                TABLE_NAME + "." + COLUMN_NAME + "," + TABLE_NAME + "." + COLUMN_COLOUR +
                ",COUNT(" + Cards.TABLE_NAME + "." + Cards.COLUMN_SET_ID + ") AS " +
                VIEW_COLUMN_AMOUNT +
                " FROM " + TABLE_NAME +
                " LEFT JOIN " + Cards.TABLE_NAME + " ON " + TABLE_NAME + "." + _ID + "=" +
                Cards.COLUMN_SET_ID +
                " GROUP BY " +
                TABLE_NAME + "." + _ID +
                " ORDER BY " + TABLE_NAME + "." + _ID;

        // select set with a given name
        public static final String SELECT_SET_NAME = "SELECT " + COLUMN_NAME +
                " FROM " + TABLE_NAME +
                " WHERE " + _ID + " LIKE ?;";
    }

    /**
     * This class extends the BaseColumns class and is the model for the cards table in the
     * database. There are also several SQL statements that allow various types of access to this
     * table.
     *
     * @see BaseColumns
     */
    public class Cards implements BaseColumns {
        // table name and columns
        public static final String TABLE_NAME = "cards";
        public static final String COLUMN_FRONT = "front";
        public static final String COLUMN_BACK = "back";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_SET_ID = "in_set";
        public static final String COLUMN_UPDATE = "date_updated";
        public static final String COLUMN_TEST_DATE = "test_date";
        public static final String VIEW_COLUMN_SET_ID = "set_id";
        public static final String VIEW_COLUMN_SET_COLOUR = "set_colour";
        public static final String VIEW_COLUMN_SET_NAME = "set_name";

        // create table statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FRONT + " TEXT, " +
                COLUMN_BACK + " TEXT, " +
                COLUMN_LEVEL + " INTEGER, " +
                COLUMN_UPDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_TEST_DATE + " DATETIME, " +
                COLUMN_SET_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_SET_ID + ") REFERENCES " +
                Sets.TABLE_NAME + "(" + Sets._ID + ")" + ")";

        // select a card with associated set info
        public static final String SELECT_CARD = "SELECT * ," +
                Sets.TABLE_NAME + "." + Sets.COLUMN_NAME + " AS " + VIEW_COLUMN_SET_NAME +
                " FROM " + TABLE_NAME +
                " LEFT JOIN " + Sets.TABLE_NAME + " ON " + TABLE_NAME + "." + COLUMN_SET_ID +
                "=" + Sets.TABLE_NAME + "." + Sets._ID +
                " WHERE " + TABLE_NAME + "." + _ID + " LIKE ?";

        // select all cards from the database
        public static final String SELECT_ALL_CARDS = "SELECT " + TABLE_NAME + "." + _ID + ", " +
                TABLE_NAME + "." + COLUMN_FRONT + ", " + TABLE_NAME + "." + COLUMN_BACK + ", " +
                TABLE_NAME + "." + COLUMN_LEVEL + ", " + TABLE_NAME + "." + COLUMN_SET_ID + ", " +
                TABLE_NAME + "." + COLUMN_UPDATE + ", " + TABLE_NAME + "." + COLUMN_TEST_DATE +
                ", " +
                Sets.TABLE_NAME + "." + Sets._ID + " AS " + VIEW_COLUMN_SET_ID + ", " +
                Sets.TABLE_NAME + "." + Sets.COLUMN_NAME + " AS " + VIEW_COLUMN_SET_NAME + ", " +
                Sets.TABLE_NAME + "." + Sets.COLUMN_COLOUR + " AS " + VIEW_COLUMN_SET_COLOUR +
                " FROM " + TABLE_NAME + " LEFT JOIN " + Sets.TABLE_NAME + " ON " + TABLE_NAME +
                "." + COLUMN_SET_ID + "=" + Sets.TABLE_NAME + "." + Sets._ID + " ORDER BY " +
                TABLE_NAME + "." + _ID;

        // select all card IDs in the database
        public static final String SELECT_ALL_CARD_IDS = "SELECT " + _ID + " FROM " + TABLE_NAME;

        // select all cards in a particular set
        public static final String SELECT_CARDS_IN_SET = "SELECT " + TABLE_NAME + "." + _ID + ", " +
                TABLE_NAME + "." + COLUMN_FRONT + ", " + TABLE_NAME + "." + COLUMN_BACK + ", " +
                TABLE_NAME + "." + COLUMN_LEVEL + ", " + TABLE_NAME + "." + COLUMN_SET_ID + ", " +
                TABLE_NAME + "." + COLUMN_UPDATE + ", " + TABLE_NAME + "." + COLUMN_TEST_DATE +
                ", " +
                Sets.TABLE_NAME + "." + Sets._ID + " AS " + VIEW_COLUMN_SET_ID + ", " +
                Sets.TABLE_NAME + "." + Sets.COLUMN_NAME + " AS " + VIEW_COLUMN_SET_NAME + ", " +
                Sets.TABLE_NAME + "." + Sets.COLUMN_COLOUR + " AS " + VIEW_COLUMN_SET_COLOUR +
                " FROM " + TABLE_NAME +
                " LEFT JOIN " + Sets.TABLE_NAME + " ON " + TABLE_NAME + "." + COLUMN_SET_ID +
                "=" + Sets.TABLE_NAME + "." + Sets._ID +
                " WHERE " + COLUMN_SET_ID + " LIKE ? " +
                " ORDER BY " + TABLE_NAME + "." + _ID;

        // select all cards with associated test/update data
        private static final String SELECT_CARD_TEST_DATA = "SELECT " + _ID + ", " + COLUMN_UPDATE +
                ", " + COLUMN_TEST_DATE + ", " +
                "(strftime('%s', " + COLUMN_TEST_DATE + ") - " +
                    "strftime('%s', " + COLUMN_UPDATE + ")) AS dateDiff " +
                "FROM "+ TABLE_NAME + " WHERE " +
                _ID + " LIKE ? AND " +
                "((" + COLUMN_LEVEL + " = 1 AND dateDiff < " +
                    PreTestSelectActivity.TWELVE_HOURS + ") OR " +
                "(" + COLUMN_LEVEL + " = 2 AND dateDiff < " +
                    PreTestSelectActivity.ONE_DAY + ") OR " +
                "(" + COLUMN_LEVEL + " = 3 AND dateDiff < " +
                    PreTestSelectActivity.THREE_DAYS + ") OR " +
                "(" + COLUMN_LEVEL + " = 4 AND dateDiff < " +

                    PreTestSelectActivity.ONE_WEEK + ") OR " +
                "(" + COLUMN_LEVEL + " = 5 AND dateDiff < " + PreTestSelectActivity.TWO_WEEKS + "))";

        // select all cards that are ready to test in the database
        private static final String SELECT_TESTABLE_CARDS = "SELECT " + TABLE_NAME + ".*, " +
                Sets.TABLE_NAME + "." + Sets.COLUMN_COLOUR + ", " +
                Sets.TABLE_NAME + "." + Sets.COLUMN_NAME + ", " +
                "(strftime('%s', " + COLUMN_TEST_DATE + ") - " +
                    "strftime('%s', " + COLUMN_UPDATE + ")) AS dateDiff " +
                "FROM "+ TABLE_NAME +
                " LEFT JOIN " + Sets.TABLE_NAME + " ON " + TABLE_NAME + "." + COLUMN_SET_ID +
                "=" + Sets.TABLE_NAME + "." + Sets._ID +
                " WHERE " +
                "(" + COLUMN_LEVEL + " = 1 AND dateDiff < " +
                    PreTestSelectActivity.TWELVE_HOURS + ") OR " +
                "(" + COLUMN_LEVEL + " = 2 AND dateDiff < " +
                    PreTestSelectActivity.ONE_DAY + ") OR " +
                "(" + COLUMN_LEVEL + " = 3 AND dateDiff < " +
                    PreTestSelectActivity.THREE_DAYS + ") OR " +
                "(" + COLUMN_LEVEL + " = 4 AND dateDiff < " +
                    PreTestSelectActivity.ONE_WEEK + ") OR " +
                "(" + COLUMN_LEVEL + " = 5 AND dateDiff < " + PreTestSelectActivity.TWO_WEEKS + ")";

        // select all cards with data for use in the matching game
        private static final String SELECT_GAME_CARDS = "SELECT " + _ID + ", " + COLUMN_FRONT +
                ", " + COLUMN_BACK +
                " FROM " + TABLE_NAME;
    }

    /**
     * Saves a newly created set in the database with the passed values.
     *
     * @param context used to access the database.
     * @param values the values to save in the database.
     * @return a long that indicates the row of the saved data.
     */
    public static long saveNewSet(Context context, ContentValues values) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        long rowId = db.insert(Sets.TABLE_NAME, null, values);

        db.close();

        return rowId;
    }

    /**
     * Saves a newly created card in the database with the passed values.
     *
     * @param context used to access the database.
     * @param values the values to save in the database.
     * @return a long that indicates the row of the saved data.
     */
    public static long saveNewCard(Context context, ContentValues values) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        long rowId = db.insert(Cards.TABLE_NAME, null, values);

        db.close();

        return rowId;
    }

    /**
     * Updates an existing set in the database using the passed values.
     *
     * @param context used to access the database.
     * @param values the values to save in the database.
     * @param setNum the set to update.
     * @return a long that indicates the row of the saved data.
     */
    public static long updateSet(Context context, ContentValues values, String setNum) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        String clause = Sets._ID + " LIKE ?";
        String[] args = {setNum};

        long rowId = db.update(Sets.TABLE_NAME, values, clause, args);
        db.close();

        return rowId;
    }

    /**
     * Updates an existing card in the database using the passed values.
     *
     * @param context used to access the database.
     * @param values the values to save in the database.
     * @param cardNum the set to update.
     * @return a long that indicates the row of the saved data.
     */
    public static long updateCard(Context context, ContentValues values, String cardNum) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        String clause = Cards._ID + " LIKE ?";
        String[] args = {cardNum};

        long rowId = db.update(Cards.TABLE_NAME, values, clause, args);
        db.close();

        return rowId;
    }

    /**
     * Deletes the specified set in the database.  Will also remove all cards from the deleted set.
     *
     * @param context used to access the database
     * @param setNum the set to delete.
     * @return a long representing the deleted row.
     */
    public static long deleteSet(Context context, String setNum) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        String clause = Sets._ID + " LIKE ?";
        String[] args = {setNum};

        long rowId = db.delete(Sets.TABLE_NAME, clause, args);

        // find cards in set and change to no set
        Cursor cursorCards = getCardsInSet(context, setNum);

        if (cursorCards.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(Cards.COLUMN_SET_ID, 0);
            clause = Cards.COLUMN_SET_ID + " LIKE ?";

            do {
                String cardId = Integer.toString(cursorCards.getInt(
                        cursorCards.getColumnIndexOrThrow(Cards._ID)));
                args = new String[1];
                args[0] = Integer.toString(cursorCards.getInt(
                        cursorCards.getColumnIndexOrThrow(Cards._ID)));

                db.update(Cards.TABLE_NAME, values, clause, args);
            } while (cursorCards.moveToNext());
        }

        cursorCards.close();
        db.close();

        return rowId;
    }

    /**
     * Deletes the specified card in the database.
     *
     * @param context used to access the database.
     * @param cardNum the card to delete.
     * @return a long representing the deleted row.
     */
    public static long deleteCard(Context context, String cardNum) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        String clause = Cards._ID + " LIKE ?";
        String[] args = {cardNum};

        long rowId = db.delete(Cards.TABLE_NAME, clause, args);
        db.close();

        return rowId;
    }

    /**
     * Retrieve the name of a given set.
     *
     * @param context used to access the database.
     * @param setNum the set to get the name from.
     * @return a String value for the name of the set.
     */
    public static String getSetName(Context context, String setNum) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        String[] args = {setNum};
        String setName = "";

        Cursor cursor = db.rawQuery(Sets.SELECT_SET_NAME, args);
        if (cursor.moveToFirst()) {
            setName = cursor.getString(cursor.getColumnIndexOrThrow(Sets.COLUMN_NAME));
        }

        cursor.close();

        return setName;
    }

    /**
     * Retrieve all sets stored in the database.
     *
     * @param context used to access the database.
     * @return a Cursor object containing all the set data.
     */
    public static Cursor getAllSets(Context context) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Sets.SELECT_ALL_SETS, null);

        return cursor;
    }

    /**
     * Retrieve all sets stored in the database.
     *
     * @param context used to access the database.
     * @return a Cursor object containing all the set data.
     */
    public static Cursor getAllCards(Context context) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Cards.SELECT_ALL_CARDS, null);

        return cursor;
    }

    /**
     * Retrieve all data for a particular set.
     *
     * @param context used to access the database.
     * @param setNum the set to retrieve.
     * @return a Cursor object containing the set data.
     */
    public static Cursor getSetInfo(Context context, String setNum) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Sets.SELECT_SET + setNum, null);

        return cursor;
    }

    /**
     * Retrieve all data for a particular card.
     *
     * @param context used to access the database.
     * @param cardNum the card to retrieve.
     * @return a Cursor object containing the card data.
     */
    public static Cursor getCardInfo(Context context, String cardNum) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        String[] args = {cardNum};

        Cursor cursor = db.rawQuery(Cards.SELECT_CARD, args);

        return cursor;
    }

    /**
     * Retrieve all cards associated with a particular set.
     *
     * @param context used to access the database.
     * @param setNum the set of cards.
     * @return a Cursor object containing card information for the set.
     */
    public static Cursor getCardsInSet(Context context, String setNum) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        String[] args = {setNum};

        Cursor cursor = db.rawQuery(Cards.SELECT_CARDS_IN_SET, args);

        return cursor;
    }

    /**
     * Retrieve all testable cards in the database.
     *
     * @param context used to access the database.
     * @return a Cursor object containing the card data.
     */
    public static Cursor getTestableCards(Context context) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Cards.SELECT_TESTABLE_CARDS, null);
        return cursor;
    }

    /**
     * Determines whether or not a particular card from the database is ready for testing.
     *
     * @param context used to access the database.
     * @param cardId the ID of the card to check.
     * @return a boolean value that is true if the card is ready to test.
     */
    public static boolean isCardTestable(Context context, int cardId) {
        SQLiteDatabase newDb = new MemCardSQLiteHelper(context).getReadableDatabase();
        String[] args = {Integer.toString(cardId)};
        Cursor cursor = newDb.rawQuery(Cards.SELECT_CARD_TEST_DATA, args);
        boolean testable = false;

        if (cursor.moveToFirst()) {
            testable = true;
        }

        cursor.close();
        newDb.close();

        return testable;
    }

    /**
     * Changes the value for the level column of a particular card in the database.
     *
     * @param context used to access the database.
     * @param cardId the ID of the card to update.
     * @param newLevel the new level of the card.
     */
    public static void setNewCardLevel(Context context, int cardId, int newLevel) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        String clause = Cards._ID + " LIKE ?";
        String[] args = { Integer.toString(cardId) };

        ContentValues contentValues = new ContentValues();

        contentValues.put(Cards.COLUMN_LEVEL, newLevel);
        contentValues.put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        contentValues.put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(newLevel));

        db.update(Cards.TABLE_NAME, contentValues, clause, args);

        closeDb();
    }

    /**
     * Returns an array of all card IDs stored in the database.
     *
     * @param context used to access the database.
     * @return an integer array containing the card IDs.
     */
    public static int[] getCardIds(Context context) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Cards.SELECT_ALL_CARD_IDS, null);

        // put all ids into array
        int[] allIds = null;
        if (cursor.moveToFirst()) {
            allIds = new int[cursor.getCount()];
            int position = 0;
            do {
                allIds[position++] = cursor.getInt(cursor.getColumnIndexOrThrow(Cards._ID));
            } while (cursor.moveToNext());
        }

        // close resources
        cursor.close();
        closeDb();

        return allIds;
    }

    /**
     * Get card data for use in the matching game.
     *
     * @param context used to access the database.
     * @return a Cursor object containing the card data.
     */
    public static Cursor getGameCards(Context context) {
        db = new MemCardSQLiteHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Cards.SELECT_GAME_CARDS, null);

        return cursor;
    }

    /**
     * Creates a set of content values representing a sample set in the database.
     *
     * @param context used to access the database.
     * @return a ContentValues object containing the set information.
     */
    public static ContentValues getSampleSet(Context context) {
        ContentValues values = new ContentValues();
        values.put(Sets.COLUMN_NAME, "Sample Set");
        values.put(Sets.COLUMN_COLOUR, ContextCompat.getColor(context, R.color.colorAccent));
        return values;
    }

    /**
     * Creates a set of content values for 6 sample cards in the database.
     *
     * @return a ContentValues object containing the card information.
     */
    public static ContentValues[] getSampleCards() {
        ContentValues[] values = new ContentValues[6];
        // card 1
        values[0] = new ContentValues();
        values[0].put(Cards.COLUMN_FRONT, "1");
        values[0].put(Cards.COLUMN_BACK, "one");
        values[0].put(Cards.COLUMN_SET_ID, 1);
        values[0].put(Cards.COLUMN_LEVEL, 1);
        values[0].put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        values[0].put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(1));

        // card 2
        values[1] = new ContentValues();
        values[1].put(Cards.COLUMN_FRONT, "2");
        values[1].put(Cards.COLUMN_BACK, "two");
        values[1].put(Cards.COLUMN_SET_ID, 1);
        values[1].put(Cards.COLUMN_LEVEL, 2);
        values[1].put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        values[1].put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(1));

        // card 3
        values[2] = new ContentValues();
        values[2].put(Cards.COLUMN_FRONT, "3");
        values[2].put(Cards.COLUMN_BACK, "three");
        values[2].put(Cards.COLUMN_SET_ID, 1);
        values[2].put(Cards.COLUMN_LEVEL, 3);
        values[2].put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        values[2].put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(2));

        // card 4
        values[3] = new ContentValues();
        values[3].put(Cards.COLUMN_FRONT, "4");
        values[3].put(Cards.COLUMN_BACK, "four");
        values[3].put(Cards.COLUMN_SET_ID, 1);
        values[3].put(Cards.COLUMN_LEVEL, 4);
        values[3].put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        values[3].put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(4));

        // card 5
        values[4] = new ContentValues();
        values[4].put(Cards.COLUMN_FRONT, "5");
        values[4].put(Cards.COLUMN_BACK, "five");
        values[4].put(Cards.COLUMN_SET_ID, 1);
        values[4].put(Cards.COLUMN_LEVEL, 5);
        values[4].put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        values[4].put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(5));

        // card 6
        values[5] = new ContentValues();
        values[5].put(Cards.COLUMN_FRONT, "6");
        values[5].put(Cards.COLUMN_BACK, "six");
        values[5].put(Cards.COLUMN_SET_ID, 1);
        values[5].put(Cards.COLUMN_LEVEL, 6);
        values[5].put(Cards.COLUMN_UPDATE, getCurrentDateTime());
        values[5].put(Cards.COLUMN_TEST_DATE, getLevelDateDiff(6));

        return values;
    }

    /**
     * Resets the sample cars and set in the database.
     *
     * @param context used to access the database.
     */
    public static void resetSample(Context context) {
        db = new MemCardSQLiteHelper(context).getWritableDatabase();

        // check if set exists and replace if needed
        String clause = Sets._ID + " LIKE ?";
        String[] args = { "1" };

        ContentValues setValues = getSampleSet(context);
        Cursor cursor = getSetInfo(context, args[0]);
        if (cursor.getCount() == 0) {
            setValues.put(Sets._ID, "1");
            db.insert(Sets.TABLE_NAME, null, setValues);
        } else {
            if (cursor.moveToFirst()) {
                args = new String[]{"1"};
                db.update(Sets.TABLE_NAME, setValues, clause, args);
            }
        }
        cursor.close();

        // check if sets exist and create/update as needed
        ContentValues[] cardValues = getSampleCards();
        clause = Cards._ID + " LIKE ?";

        for (int i = 0; i < cardValues.length; i++) {
            cursor = getCardInfo(context, Integer.toString(i + 1));
            if (cursor.getCount() == 0) {
                cardValues[i].put(Cards._ID, (i + 1));
                db.insert(Cards.TABLE_NAME, null, cardValues[i]);
            } else  {
                if (cursor.moveToFirst()) {
                    args = new String[]{Integer.toString(i + 1)};
                    db.update(Cards.TABLE_NAME, cardValues[i], clause, args);
                }
            }
        }

        // close resources
        cursor.close();
        closeDb();
    }

    /**
     * Creates a String for the current date and time in the appropriate format.
     *
     * @return a String representing the current date and time.
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Creates a String for the initial date required for testing a card, which is 12 hours after
     * the current time.
     *
     * @return a String representing the future date.
     */
    public static String getInitDateDiff() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);

        return dateFormat.format(calendar.getTime());
    }

    /**
     * Creates a String for the date difference between the current time and the date required for
     * testing a particular card level.
     *
     * @param level the level of the card.
     * @return a String representing the future date.
     */
    public static String getLevelDateDiff(int level) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        switch (level) {
            case 1:
                calendar.add(Calendar.HOUR, 12);
                break;
            case 2:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, 3);
                break;
            case 4:
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
                break;
            case 5:
                calendar.add(Calendar.WEEK_OF_MONTH, 2);
                break;
        }

        return dateFormat.format(calendar.getTime());
    }

    /**
     * Closes the database to prevent memory leaks.
     */
    public static void closeDb() {
        db.close();
    }

} // end of class
