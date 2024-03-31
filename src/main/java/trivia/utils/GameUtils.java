package trivia.utils;

import trivia.QuestionType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class GameUtils {
    private static final String QUESTION = " Question ";

    private GameUtils() {
    }

    public static Map<QuestionType, List<String>> constructQuestions() {
        final EnumMap<QuestionType, List<String>> questions = new EnumMap<>(QuestionType.class);
        List<String> popQuestions = new ArrayList<>();
        List<String> scienceQuestions = new ArrayList<>();
        List<String> sportsQuestions = new ArrayList<>();
        List<String> rockQuestions = new ArrayList<>();

        IntStream.range(0, 50).forEach(counter -> {
            rockQuestions.add(createQuestion(QuestionType.ROCK, counter));
            sportsQuestions.add(createQuestion(QuestionType.SPORTS, counter));
            scienceQuestions.add(createQuestion(QuestionType.SCIENCE, counter));
            popQuestions.add(createQuestion(QuestionType.POP, counter));
        });

        questions.put(QuestionType.ROCK, rockQuestions);
        questions.put(QuestionType.SCIENCE, scienceQuestions);
        questions.put(QuestionType.SPORTS, sportsQuestions);
        questions.put(QuestionType.POP, popQuestions);

        return questions;
    }

    private static String createQuestion(QuestionType questionType, int counter) {
        return questionType.getValue() + QUESTION + counter;
    }
}
