package clovero.quiz;

import clovero.category.Category;
import clovero.jpa.ZonedDateTimeConverter;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quiz")
@Data
public class Quiz {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "answer")
    private String answer;

    @Column(name = "description")
    private String description;

    @Column(name = "point")
    private Long point;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "created_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    @CreatedDate
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
