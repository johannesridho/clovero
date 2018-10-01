package clovero.hint;

import clovero.jpa.ZonedDateTimeConverter;
import clovero.quiz.Quiz;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "hint")
@Data
public class Hint {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private Quiz quiz;

    @Column(name = "number")
    private Integer number;

    @Column(name = "type")
    @Convert(converter = HintTypeConverter.class)
    private HintType type;

    @Column(name = "description")
    private String description;

    @Column(name = "original_image_url")
    private String originalImageUrl;

    @Column(name = "preview_image_url")
    private String previewImageUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "audio_duration")
    private Integer audioDuration;

    @Column(name = "created_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    @CreatedDate
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
