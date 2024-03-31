package trivia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

// REFACTOR ME
public class GameBetter implements IGame {
    private static final Logger logger = Logger.getLogger(GameBetter.class.getSimpleName());
    private static final String QUESTION = " Question ";
    private final List<String> players = new ArrayList<>();
    private final int[] places = new int[6];
    private final int[] purses = new int[6];
    private final boolean[] inPenaltyBox = new boolean[6];

    private final List<String> popQuestions = new LinkedList<>();
    private final List<String> scienceQuestions = new LinkedList<>();
    private final List<String> sportsQuestions = new LinkedList<>();
    private final List<String> rockQuestions = new LinkedList<>();

    private int currentPlayer = 0;
    private boolean isGettingOutOfPenaltyBox;

    public GameBetter() {
        IntStream.range(0, 50).forEach(counter -> {
            rockQuestions.add(createQuestion(QuestionType.ROCK, counter));
            sportsQuestions.add(createQuestion(QuestionType.SPORTS, counter));
            scienceQuestions.add(createQuestion(QuestionType.SCIENCE, counter));
            popQuestions.add(createQuestion(QuestionType.POP, counter));
        });
    }

    private String createQuestion(QuestionType questionType, int counter) {
        return questionType.getValue() + QUESTION + counter;
    }

    @Override
    public boolean add(String playerName) {
        players.add(playerName);
        int playersNumber = getPlayersNumber();
        resetScore(playersNumber);

        logger.log(Level.INFO, "{0} was added", playerName);
        logger.log(Level.INFO, "They are player number {0}", playersNumber);
        return true;
    }

    private void resetScore(int numberOfPlayers) {
        places[numberOfPlayers] = 0;
        purses[numberOfPlayers] = 0;
        inPenaltyBox[numberOfPlayers] = false;
    }

    private int getPlayersNumber() {
        return players.size();
    }

    @Override
    public void roll(int roll) {
        String activePlayer = players.get(currentPlayer);
        logger.log(Level.INFO, "{0} is the current player", activePlayer);
        logger.log(Level.INFO, "They have rolled a {0}", roll);

        if (inPenaltyBox[currentPlayer]) {
            if (roll % 2 != 0) {
                isGettingOutOfPenaltyBox = true;

                logger.log(Level.INFO, "{0} is getting out of the penalty box", activePlayer);
                computePlayerMove(roll, activePlayer);
            } else {
                logger.log(Level.INFO, "{0} is not getting out of the penalty box", activePlayer);
                isGettingOutOfPenaltyBox = false;
            }

        } else {
            computePlayerMove(roll, activePlayer);
        }

    }

    private void computePlayerMove(int roll, String activePlayer) {
        places[currentPlayer] += roll;
        if (places[currentPlayer] > 11) {
            places[currentPlayer] -= 12;
        }

        logger.log(Level.INFO, () -> String.format("%s's new location is %s", activePlayer, places[currentPlayer]));
        logger.log(Level.INFO, "The category is {0}", currentCategory());
        askQuestion();
    }

    private void askQuestion() {
        switch (currentCategory()) {
            case POP -> {
                String popQuestionRemoved = popQuestions.remove(0);
                logger.log(Level.INFO, popQuestionRemoved);
            }
            case SCIENCE -> {
                String scienceQuestionRemoved = scienceQuestions.remove(0);
                logger.log(Level.INFO, scienceQuestionRemoved);
            }
            case SPORTS -> {
                String sportsQuestionsRemoved = sportsQuestions.remove(0);
                logger.log(Level.INFO, sportsQuestionsRemoved);
            }
            case ROCK -> {
                String rockQuestionsRemoved = rockQuestions.remove(0);
                logger.log(Level.INFO, rockQuestionsRemoved);
            }
        }
    }


    private QuestionType currentCategory() {
        return switch (places[currentPlayer]) {
            case 0, 4, 8 -> QuestionType.POP;
            case 1, 5, 9 -> QuestionType.SCIENCE;
            case 2, 6, 10 -> QuestionType.SPORTS;
            default -> QuestionType.ROCK;
        };
    }

    @Override
    public boolean wasCorrectlyAnswered() {
        if (checkIfPlayerIsInPenaltyBox()) {
            return computeCorrectAnswer();
        }

        changeActivePlayer();
        return true;
    }

    private boolean checkIfPlayerIsInPenaltyBox() {
        return !inPenaltyBox[currentPlayer] || (inPenaltyBox[currentPlayer] && isGettingOutOfPenaltyBox);
    }

    private void changeActivePlayer() {
        currentPlayer++;
        if (currentPlayer == players.size()) currentPlayer = 0;
    }

    private boolean computeCorrectAnswer() {
        logger.log(Level.INFO, ("Answer was correct!!!!"));
        int currentPlayerCoins = purses[currentPlayer]++;
        logger.log(Level.INFO, () -> String.format("%s now has %s Gold Coins.", players.get(currentPlayer), currentPlayerCoins));

        boolean winner = didPlayerWin();
        changeActivePlayer();

        return winner;
    }

    @Override
    public boolean wrongAnswer() {
        logger.log(Level.INFO, ("Question was incorrectly answered"));
        logger.log(Level.INFO,  "{0} was sent to the penalty box", players.get(currentPlayer));
        inPenaltyBox[currentPlayer] = true;

        changeActivePlayer();
        return true;
    }


    private boolean didPlayerWin() {
        return purses[currentPlayer] != 6;
    }
}
