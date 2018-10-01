package clovero.dashboard.hint;

import clovero.hint.Hint;
import clovero.hint.HintType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Data
@NoArgsConstructor
public class HintForm {

    private Optional<Integer> id = Optional.empty();

    @NotNull
    private Integer quizId;

    @NotNull
    private Integer number;

    @NotNull
    private HintType type;

    @NotEmpty
    private String description;

    private String originalImageUrl;

    private String previewImageUrl;

    private String audioUrl;

    private Integer audioDuration;

    public HintForm(Hint hint) {
        this.quizId = hint.getQuiz().getId();
        this.number = hint.getNumber();
        this.type = hint.getType();
        this.description = hint.getDescription();
        this.originalImageUrl = hint.getOriginalImageUrl();
        this.previewImageUrl = hint.getPreviewImageUrl();
        this.audioUrl = hint.getAudioUrl();
        this.audioDuration = hint.getAudioDuration();
    }
}
