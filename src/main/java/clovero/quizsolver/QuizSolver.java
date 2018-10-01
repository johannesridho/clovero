package clovero.quizsolver;

import clovero.jpa.ZonedDateTimeConverter;
import clovero.quiz.Quiz;
import clovero.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quiz_solver")
@Data
public class QuizSolver {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private Quiz quiz;

    @Column(name = "created_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    @CreatedDate
    private ZonedDateTime createdAt;

}
