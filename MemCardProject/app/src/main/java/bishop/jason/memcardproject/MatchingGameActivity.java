package bishop.jason.memcardproject;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MatchingGameActivity class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is used to run the matching game available from the main activity. It displays a
 * series of 16 game pieces in a 4x4 grid.  Cards are randomly selected from the database and used
 * to populate each card.  Users can tap each piece to flip it over.  If the text for one piece
 * matches the second (as the back and front of a card from the database) a match is made and the
 * user continues until all matches are made.
 */

public class MatchingGameActivity extends AppCompatActivity {
    private static final int MAX_PIECES = 16;
    private static final int FLIP_TIME = 1500;

    private ConstraintLayout gameBase;
    private Card[] gameCards;
    private GamePiece[] gamePieces;
    private Random random;
    private int matchCount = 0, flipPiece1 = -1, flipPiece2 = -1;
    private boolean waitingForFlip;

    /**
     * When the activity is created, the layout is inflated and presented along with the action bar.
     * The game is then populated with card data from the database, putting card information in
     * random positions on the game board.
     *
     * @param savedInstanceState not used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_matching_game);

        // set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Matching Game");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.game)));

        waitingForFlip = false;

        // get all card necessary card data
        Cursor cardData = MemCardDbContract.getGameCards(getApplicationContext());
        gameCards = new Card[MAX_PIECES / 2];
        gamePieces = new GamePiece[MAX_PIECES];
        random = new Random();
        Card card;
        int cardIdSelect;

        // if there are enough cards to randomly select, randomly select cards from the database
        if (gameCards.length > 0 && gameCards.length > MAX_PIECES / 2) {
            List<Integer> selectedIds = new ArrayList<>();

            // randomly select cards to put in slots
            for (int i = 0; i < gameCards.length; i++) {

                do {
                    cardIdSelect = random.nextInt(gameCards.length);
                } while (selectedIds.contains(cardIdSelect));

                selectedIds.add(cardIdSelect);
                cardData.moveToPosition(cardIdSelect);

                card = new Card();
                card.setId(cardData.getInt(
                        cardData.getColumnIndexOrThrow(MemCardDbContract.Cards._ID)));
                card.setFront(cardData.getString(
                        cardData.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT)));
                card.setBack(cardData.getString(
                        cardData.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK)));

                gameCards[i] = card;
            }
        // if there are less cards than pieces, select all cards and randomly select the rest
        } else if (gameCards.length > 0 && gameCards.length <= MAX_PIECES / 2) {

            for (int i = 0; i < gameCards.length; i++) {

                if (i < cardData.getCount()) {
                    cardData.moveToPosition(i);
                } else {
                    cardIdSelect = random.nextInt(cardData.getCount());
                    cardData.moveToPosition(cardIdSelect);
                }

                card = new Card();
                card.setId(cardData.getInt(
                        cardData.getColumnIndexOrThrow(MemCardDbContract.Cards._ID)));
                card.setFront(cardData.getString(
                        cardData.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_FRONT)));
                card.setBack(cardData.getString(
                        cardData.getColumnIndexOrThrow(MemCardDbContract.Cards.COLUMN_BACK)));

                gameCards[i] = card;
            }
        // if there are no cards, the game cannot run
        } else {
            Toast.makeText(getApplicationContext(), "Unable to load card data", Toast.LENGTH_SHORT)
                    .show();

            finish();
        }

        cardData.close();
        MemCardDbContract.closeDb();

        // populate layouts with card data
        gameBase = (ConstraintLayout) findViewById(R.id.constraintLayoutGame);
        int gamePieceSlot;
        List<Integer> selectedPieces = new ArrayList<>();
        for (int i = 0; i < MAX_PIECES; i++) {

            do {
                gamePieceSlot = random.nextInt(MAX_PIECES);
            } while (selectedPieces.contains(gamePieceSlot));

            selectedPieces.add(gamePieceSlot);

            LinearLayout gameBaseChildAt = (LinearLayout) gameBase.getChildAt(i);
            TextView tvGamePieceFront =
                    (TextView) gameBaseChildAt.findViewById(R.id.textViewGameCardFront);
            ImageView ivGamePieceBack =
                    (ImageView) gameBaseChildAt.findViewById(R.id.imageViewGameCardBack);

            final GamePiece gamePiece;
            if (gamePieceSlot % 2 == 0) {
                 gamePiece = new GamePiece(
                         i, gameCards[gamePieceSlot / 2], true, tvGamePieceFront, ivGamePieceBack);
            } else {
                gamePiece = new GamePiece(
                        i, gameCards[gamePieceSlot / 2], false, tvGamePieceFront, ivGamePieceBack);
            }

            // set click listeners for each piece, allowing user to flip and check for a match
            gamePiece.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 if (!gamePiece.isFlipped() && !waitingForFlip &&
                                                         !gamePiece.isMatched()) {
                                                     gamePiece.flip();
                                                     checkFlip(gamePiece.slot);
                                                 }
                                             }
                                         });
            gamePieces[gamePieceSlot] = gamePiece;
        }
    }

    /**
     * Runs each time the user flips a piece.  For the first card flipped, only the position is
     * saved and the game waits for the user to flip a second piece.  Once the second piece is
     * flipped, the user is prevented from flipping for a few seconds and the game will inform them
     * if a match is made.  When a match is made, the cards stay flipped, otherwise the cards are
     * flippec back over.
     *
     * @param slot the position of the card flipped
     */
    private void checkFlip(int slot) {
        // determine which piece flipped and store location
        if (flipPiece1 == -1) {  // first card flipped
            for (int i = 0; i < MAX_PIECES; i++) {
                if (gamePieces[i].slot == slot) {
                    flipPiece1 = i;
                    break;
                }
            }

            return;
        // second card flipped
        } else {
            for (int i = 0; i < MAX_PIECES; i++) {
                if (gamePieces[i].slot == slot) {
                    flipPiece2 = i;
                    break;
                }
            }
        }

        // stop the user from flipping and check for a match
        waitingForFlip = true;
        final TextView tvMatch = (TextView) findViewById(R.id.textViewMatch);
        tvMatch.setVisibility(View.VISIBLE);

        // start timer
        new CountDownTimer(FLIP_TIME, 100) {
            boolean showText = false;
            public void onTick(long millisUntilFinished) {
                // determine when to show appropriate text
                if (millisUntilFinished < FLIP_TIME / 2 && !showText) {
                    // pieces match
                    if (gamePieces[flipPiece1].matches(gamePieces[flipPiece2])) {
                        gamePieces[flipPiece1].matched();
                        gamePieces[flipPiece2].matched();
                        tvMatch.setText(R.string.match);
                    } else {  // no match
                        tvMatch.setText(R.string.no_match);
                    }
                    showText = true;
                }
            }

            // allow the user to select again or end the game
            public void onFinish() {
                // flip cards back over if not matched
                if (!gamePieces[flipPiece1].matches(gamePieces[flipPiece2])) {
                    gamePieces[flipPiece1].flip();
                    gamePieces[flipPiece2].flip();
                } else {
                    matchCount++;
                }

                // determine if all matches have been made
                if (matchCount == MAX_PIECES / 2) {
                    endGame();
                }

                // reset values to wait for next pair flip
                tvMatch.setVisibility(View.INVISIBLE);
                tvMatch.setText("");
                flipPiece1 = -1;
                flipPiece2 = -1;
                waitingForFlip = false;
            }
        }.start();
    }

    /**
     * When the user has made all matches, this method will end the game.  It shows a message to
     * the user indicating win status and then ends the activity.
     */
    private void endGame(){
        // show message
        final TextView tvWinner = (TextView) findViewById(R.id.textViewWinner);
        tvWinner.setVisibility(View.VISIBLE);

        // timer to show message and end game
        new CountDownTimer(FLIP_TIME, 100) {
            public void onTick(long millisUntilFinished) { }

            // end activity
            public void onFinish() {
                finish();
            }
        }.start();
    }
}
