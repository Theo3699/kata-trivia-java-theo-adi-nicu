package trivia;

import trivia.utils.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameBetter implements IGame {
    private static final Logger logger = Logger.getLogger(GameBetter.class.getSimpleName());
    private final List<String> players = new ArrayList<>();
    private final int[] places = new int[6];
    private final int[] purses = new int[6];
    private final boolean[] inPenaltyBox = new boolean[6];
    private final Map<QuestionType, List<String>> questions;
    private int currentPlayer = 0;

    public GameBetter() {
        questions = GameUtils.constructQuestions();
    }

    @Override
    public boolean add(String playerName) {
        players.add(playerName);
        int playersNumber = getPlayersNumber();
        if (playersNumber > 5) {
            return false;
        }
        resetScore(playersNumber - 1);

        logger.log(Level.INFO, "{0} was added", playerName);
        logger.log(Level.INFO, "They are player number {0}", playersNumber);
        return true;
    }

    private void resetScore(int lastPlayerIndex) {
        places[lastPlayerIndex] = 0;
        purses[lastPlayerIndex] = 0;
        inPenaltyBox[lastPlayerIndex] = false;
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
                inPenaltyBox[currentPlayer] = false;

                logger.log(Level.INFO, "{0} is getting out of the penalty box", activePlayer);
                computePlayerMove(roll, activePlayer);
            } else {
                logger.log(Level.INFO, "{0} is not getting out of the penalty box", activePlayer);
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
        logger.log(Level.INFO, "The category is {0}", currentCategory().getValue());
        askQuestion();
    }

    private void askQuestion() {
        switch (currentCategory()) {
            case POP -> {
                String popQuestionRemoved = questions.get(QuestionType.POP).remove(0);
                logger.log(Level.INFO, popQuestionRemoved);
            }
            case SCIENCE -> {
                String scienceQuestionRemoved = questions.get(QuestionType.SCIENCE).remove(0);
                logger.log(Level.INFO, scienceQuestionRemoved);
            }
            case SPORTS -> {
                String sportsQuestionsRemoved = questions.get(QuestionType.SPORTS).remove(0);
                logger.log(Level.INFO, sportsQuestionsRemoved);
            }
            case ROCK -> {
                String rockQuestionsRemoved = questions.get(QuestionType.ROCK).remove(0);
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
        if (!inPenaltyBox[currentPlayer]) {
            return computeCorrectAnswer();
        }
        changeActivePlayer();
        return true;
    }

    private void changeActivePlayer() {
        currentPlayer++;
        if (currentPlayer == players.size()) {
            currentPlayer = 0;
        }
    }

    private boolean computeCorrectAnswer() {
        logger.log(Level.INFO, ("Answer was correct!!!!"));
        int currentPlayerCoins = ++purses[currentPlayer];
        logger.log(Level.INFO, () -> String.format("%s now has %s Gold Coins.", players.get(currentPlayer), currentPlayerCoins));

        boolean doesGameContinue = isGameStateNonFinal();
        changeActivePlayer();

        return doesGameContinue;
    }

    @Override
    public boolean wrongAnswer() {
        logger.log(Level.INFO, "Question was incorrectly answered");
        logger.log(Level.INFO, "{0} was sent to the penalty box", players.get(currentPlayer));
        inPenaltyBox[currentPlayer] = true;

        changeActivePlayer();
        return true;
    }


    private boolean isGameStateNonFinal() {
        return purses[currentPlayer] != 6;
    }
}
