package clovero.quizsolver;

import clovero.jpa.Jpa8Repository;

import java.util.Optional;

public interface QuizSolverRepository extends Jpa8Repository<QuizSolver, Integer> {
    Optional<QuizSolver> findByUserIdAndQuizId(Integer categoryId, Integer quizId);
}
