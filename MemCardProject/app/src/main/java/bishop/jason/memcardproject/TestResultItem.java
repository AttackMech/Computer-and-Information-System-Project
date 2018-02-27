package bishop.jason.memcardproject;

/**
 * TestResultItem class
 * @author Jason Bishop 3042012
 * @version 1.0
 * created 10/09/2017
 *
 * This class is used to represent a test result item.  It is used as a holder to store the
 * information from the results of the test for each card tested.  All variables are publicly
 * accessible for ease of access.
 */

public class TestResultItem {
    public String question, testAnswer, correctAnswer, nextTest;
    public boolean correct;
    public int originalLevel;

    /**
     * Constructor takes passed values and stores them in variables.
     *
     * @param question the String for the tested part of the question.
     * @param testAnswer the String for the answer the user gave on the test.
     * @param correctAnswer the String for the correct answer to compare with the testAnswer.
     * @param nextTest the date for the next test available for the questioned card.
     * @param correct a boolean representing if the user answered the question correctly.
     * @param originalLevel the original test level of the card that was questioned.
     */
    public TestResultItem(String question, String testAnswer, String correctAnswer, String nextTest,
                          boolean correct, int originalLevel) {
        this.question = question;
        this.testAnswer = testAnswer;
        this.correctAnswer = correctAnswer;
        this.nextTest = nextTest;
        this.correct = correct;
        this.originalLevel = originalLevel;
    }
}
