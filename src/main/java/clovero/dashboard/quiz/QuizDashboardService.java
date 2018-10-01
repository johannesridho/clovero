package clovero.dashboard.quiz;

import clovero.NotFoundException;
import clovero.category.Category;
import clovero.category.CategoryRepository;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import clovero.quiz.Quiz;
import clovero.quiz.QuizRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class QuizDashboardService {

    QuizRepository quizRepository;
    CategoryRepository categoryRepository;

    public DatatablesResponse findWithDatatablesCriteria(DatatablesCriteria criteria) {
        final Page<Quiz> quizzes = quizRepository.findAll(
                new QuizDatatablesSpecification(criteria),
                criteria.getPageRequest());

        return new DatatablesResponse(
                quizzes.getContent(),
                quizzes.getTotalElements(),
                quizRepository.count(),
                criteria.getDraw());
    }

    public Optional<Quiz> find(Integer id) {
        return quizRepository.findOne(id);
    }

    public Quiz save(QuizForm form) {
        final Quiz quiz;
        if (form.getId().isPresent()) {
            quiz = quizRepository.findOne(form.getId().get()).orElse(new Quiz());
        } else {
            quiz = new Quiz();
        }

        final Category category = categoryRepository.findOne(form.getCategoryId())
                .orElseThrow(() -> new NotFoundException(Category.class, form.getCategoryId().toString()));

        quiz.setAnswer(form.getAnswer().replace(" ", "-").toLowerCase(Locale.ENGLISH));
        quiz.setDescription(form.getDescription());
        quiz.setPoint(form.getPoint());
        quiz.setCategory(category);

        return quizRepository.save(quiz);
    }

    public void delete(Integer id) {
        quizRepository.delete(id);
    }
}
