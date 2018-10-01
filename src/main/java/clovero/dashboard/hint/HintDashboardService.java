package clovero.dashboard.hint;

import clovero.NotFoundException;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import clovero.hint.Hint;
import clovero.hint.HintRepository;
import clovero.quiz.Quiz;
import clovero.quiz.QuizRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class HintDashboardService {

    HintRepository hintRepository;
    QuizRepository quizRepository;

    public DatatablesResponse findWithDatatablesCriteria(DatatablesCriteria criteria) {
        final Page<Hint> hints = hintRepository.findAll(
                new HintDatatablesSpecification(criteria),
                criteria.getPageRequest());

        return new DatatablesResponse(
                hints.getContent(),
                hints.getTotalElements(),
                hintRepository.count(),
                criteria.getDraw());
    }

    public Optional<Hint> find(Integer id) {
        return hintRepository.findOne(id);
    }

    public Hint save(HintForm form) {
        final Hint hint;
        if (form.getId().isPresent()) {
            hint = hintRepository.findOne(form.getId().get()).orElse(new Hint());
        } else {
            hint = new Hint();
        }

        final Quiz quiz = quizRepository.findOne(form.getQuizId())
                .orElseThrow(() -> new NotFoundException(Quiz.class, form.getQuizId().toString()));

        hint.setQuiz(quiz);
        hint.setNumber(form.getNumber());
        hint.setType(form.getType());
        hint.setDescription(form.getDescription());
        hint.setOriginalImageUrl(form.getOriginalImageUrl());
        hint.setPreviewImageUrl(form.getPreviewImageUrl());
        hint.setAudioUrl(form.getAudioUrl());
        hint.setAudioDuration(form.getAudioDuration());

        return hintRepository.save(hint);
    }

    public void delete(Integer id) {
        hintRepository.delete(id);
    }
}
