package bishop.jason.memcardproject;

/**
 * Card class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is used to represent a card that the user has created and stored in the database.  It
 * contains several variables to represent the columns of the database, as well as other
 * associated data.
 */

public class Card {
    private String front, back, setName, date, test;
    private int id, setNum, setColour, level;
    private boolean testable;

    /**
     * Empty constructor.
     */
    public Card() {}

    /**
     * Returns the ID of the card.
     *
     * @return an int that represents the card ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the text for the front of the card.
     *
     * @return a String representing the text on the front of the card.
     */
    public String getFront() {
        return front;
    }

    /**
     * Returns the text for the back of the card.
     *
     * @return a String representing the text on the back of the card.
     */
    public String getBack() {
        return back;
    }

    /**
     * Returns the set number associated with this card.
     *
     * @return and int representing the set ID in the database.
     */
    public int getSetNum() {
        return setNum;
    }

    /**
     * Returns the colour value of the set associated with this card.
     *
     * @return an int representing the colour of the set.
     */
    public int getSetColour() {
        return setColour;
    }

    /**
     * Returns the name of the set associated with this card.
     *
     * @return a String representing the name of the set.
     */
    public String getSetName() {
        return setName;
    }

    /**
     * Returns the level of the card.
     *
     * @return an int representing the level the user has reached with this card through testing.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the date that this card was last updated.
     *
     * @return a String that represents the date.
     */
    public String getDate() { return date; }

    /**
     * Returns the date that this card is available to be tested.
     *
     * @return a String that represents the test date.
     */
    public String getTest() { return test; }

    /**
     * Returns a value that determines if the card will be available for testing.
     *
     * @return a boolean value that is true if the card can be tested.
     */
    public boolean isTestable() { return testable; }


    /**
     * Sets the ID of the card.
     *
     * @param id the ID of the card.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the the text representing the front of the card.
     *
     * @param front the String for the front of the card.
     */
    public void setFront(String front) {
        this.front = front;
    }

    /**
     * Sets the the text representing the back of the card.
     *
     * @param back the String for the back of the card.
     */
    public void setBack(String back) {
        this.back = back;
    }

    /**
     * Sets the set ID for the card.
     *
     * @param setNum the ID of the set associated with the card
     */
    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }

    /**
     * Sets the colour of the set for the card.
     *
     * @param setColour an int that represents the colour of the set.
     */
    public void setSetColour(int setColour) {
        this.setColour = setColour;
    }

    /**
     * Sets the name of the set associated with the card.
     *
     * @param setName a String that represents the name of the set.
     */
    public void setSetName(String setName) {
        this.setName = setName;
    }

    /**
     * Sets the user level of the card.
     *
     * @param level an int representing the card level.
     */
    public void setLevel(int level) { this.level = level; }

    /**
     * Sets the date that the card was last updated.
     *
     * @param date a String representing the date.
     */
    public void setDate(String date) { this.date = date; }

    /**
     * Sets the date that card can next be tested.
     *
     * @param test a String representing the date.
     */
    public void setTest(String test) { this.test = test; }

    /**
     * Sets the value of the card testability.
     *
     * @param testable a boolean representing the testability.
     */
    public void setTestable(boolean testable) { this.testable = testable; }
}