package clovero.dashboard.quizsolver;

import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static lombok.AccessLevel.PRIVATE;

@Controller
@RequestMapping("dashboard/quiz-solver")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class QuizSolverDashboardController {

    QuizSolverDashboardService quizSolverDashboardService;

    @RequestMapping(path = "")
    public String index() {
        return "dashboard/quiz-solver/index";
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse list(HttpServletRequest request) {
        final DatatablesCriteria criteria = DatatablesCriteria.getFromRequest(request);

        return quizSolverDashboardService.findWithDatatablesCriteria(criteria);
    }

}

