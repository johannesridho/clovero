package clovero.dashboard.quizsolver;

import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import clovero.quizsolver.QuizSolver;
import clovero.quizsolver.QuizSolverRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class QuizSolverDashboardService {

    QuizSolverRepository quizSolverRepository;

    public DatatablesResponse findWithDatatablesCriteria(DatatablesCriteria criteria) {
        final Page<QuizSolver> quizSolvers = quizSolverRepository.findAll(
                new QuizSolverDatatablesSpecification(criteria),
                criteria.getPageRequest());

        return new DatatablesResponse(
                quizSolvers.getContent(),
                quizSolvers.getTotalElements(),
                quizSolverRepository.count(),
                criteria.getDraw());
    }

}
