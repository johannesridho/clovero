package clovero.user;

import clovero.category.Category;
import clovero.jpa.ZonedDateTimeConverter;
import clovero.quiz.Quiz;
import clovero.state.State;
import clovero.state.StateConverter;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String lineId;

    private String name;

    @Convert(converter = StateConverter.class)
    private State state;

    private Long point;

    @ManyToOne
    @JoinColumn(name = "current_category_id", referencedColumnName = "id")
    private Category currentCategory;

    @ManyToOne
    @JoinColumn(name = "current_quiz_id", referencedColumnName = "id")
    private Quiz currentQuiz;

    private Integer currentHintNumber;
    private String userCurrentAnswer;
    private Integer currentQuizWrongAnswerCount;
    private Long currentQuizRemainingPoint;

    private Long currentRoundMaxPoint;
    private Long currentRoundPoint;
    private Integer currentRoundTotalQuizSolved;

    @Convert(converter = ZonedDateTimeConverter.class)
    @CreatedDate
    private ZonedDateTime createdAt;

    @Convert(converter = ZonedDateTimeConverter.class)
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
