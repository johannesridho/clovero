package clovero.quizsolver;

import clovero.user.User;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class QuizSolverService {

    QuizSolverRepository quizSolverRepository;

    public void addQuizSolver(User user) {
        final QuizSolver quizSolver = new QuizSolver();
        quizSolver.setUser(user);
        quizSolver.setQuiz(user.getCurrentQuiz());
        quizSolverRepository.save(quizSolver);
    }

}
