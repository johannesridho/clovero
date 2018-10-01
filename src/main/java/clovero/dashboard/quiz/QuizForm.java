package clovero.dashboard.quiz;

import clovero.quiz.Quiz;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data
@NoArgsConstructor
public class QuizForm {

    private Optional<Integer> id = Optional.empty();

    @NotNull
    private Integer categoryId;

    @NotEmpty
    private String answer;

    private String description;

    @NotNull
    private Long point;

    public QuizForm(Quiz quiz) {
        this.answer = quiz.getAnswer();
        this.description = quiz.getDescription();
        this.point = quiz.getPoint();
        this.categoryId = quiz.getCategory().getId();
    }
}
