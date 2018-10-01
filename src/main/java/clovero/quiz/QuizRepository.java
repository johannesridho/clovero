package clovero.quiz;

import clovero.jpa.Jpa8Repository;

import java.util.List;

public interface QuizRepository extends Jpa8Repository<Quiz, Integer> {
    List<Quiz> findByCategoryIdOrderByPointAsc(Integer categoryId);
}
