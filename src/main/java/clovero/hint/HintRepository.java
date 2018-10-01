package clovero.hint;

import clovero.jpa.Jpa8Repository;
import clovero.quiz.Quiz;

import java.util.List;
import java.util.Optional;

public interface HintRepository extends Jpa8Repository<Hint, Integer> {
    List<Hint> findByQuiz(Quiz quiz);
    Optional<Hint> findByQuizAndNumber(Quiz quiz, Integer number);
}
