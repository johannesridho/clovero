package clovero.dashboard.quiz;

import clovero.dashboard.category.CategoryDashboardService;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import clovero.quiz.Quiz;
import clovero.quiz.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("dashboard/quiz")
public class QuizDashboardController {

    private final QuizDashboardService quizDashboardService;
    private final CategoryDashboardService categoryDashboardService;
    private final QuizRepository quizRepository;

    @Autowired
    public QuizDashboardController(QuizDashboardService quizDashboardService, CategoryDashboardService categoryDashboardService, QuizRepository quizRepository) {
        this.quizDashboardService = quizDashboardService;
        this.categoryDashboardService = categoryDashboardService;
        this.quizRepository = quizRepository;
    }

    @RequestMapping(path = "")
    public String index() {
        return "dashboard/quiz/index";
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse list(HttpServletRequest request) {
        final DatatablesCriteria criteria = DatatablesCriteria.getFromRequest(request);

        return quizDashboardService.findWithDatatablesCriteria(criteria);
    }

    @RequestMapping(path = "/create", method = RequestMethod.GET)
    public String getCreatePage(Model model) {
        model.addAttribute("form", new QuizForm());
        setCreateAttributeViewModel(model);
        return "dashboard/quiz/form";
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("form") QuizForm form, BindingResult bindingResult, Model model) {
        if (!categoryDashboardService.find(form.getCategoryId()).isPresent()) {
            bindingResult.addError(new FieldError("form", "categoryId",
                    "category with id " + form.getCategoryId() + " is not exist"));
        }

        final Pattern pattern = Pattern.compile("[^A-Za-z0-9\\-\\s]");
        if (pattern.matcher(form.getAnswer()).find()) {
            bindingResult.addError(new FieldError("form", "answer",
                    "only letter, digit, space, and dash characters are allowed"));
        }

        if (bindingResult.hasErrors()) {
            setCreateAttributeViewModel(model);
            return "dashboard/quiz/form";
        }

        quizDashboardService.save(form);
        return "redirect:/dashboard/quiz";
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.GET)
    public String getUpdatePage(@PathVariable Integer id, Model model) {
        final Optional<Quiz> quiz = quizDashboardService.find(id);

        if (!quiz.isPresent()) {
            return "redirect:/dashboard/quiz";
        }

        model.addAttribute("form", new QuizForm(quiz.get()));
        setUpdateAttributeViewModel(model, id);
        return "dashboard/quiz/form";
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.POST)
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("form") QuizForm form,
                         BindingResult bindingResult, Model model) {

        if (!categoryDashboardService.find(form.getCategoryId()).isPresent()) {
            bindingResult.addError(new FieldError("form", "categoryId",
                    "category with id " + form.getCategoryId() + " is not exist"));
        }

        if (bindingResult.hasErrors()) {
            setUpdateAttributeViewModel(model, id);
            return "dashboard/quiz/form";
        }

        form.setId(Optional.ofNullable(id));
        quizDashboardService.save(form);
        return "redirect:/dashboard/quiz";
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Integer id) {
        quizDashboardService.delete(id);
        return "redirect:/dashboard/quiz";
    }

    private void setCreateAttributeViewModel(Model model) {
        setAttributeViewModel(model, "Create new quiz", "/dashboard/quiz/create", "Create");
    }

    private void setUpdateAttributeViewModel(Model model, Integer id) {
        setAttributeViewModel(model, "Update quiz", "/dashboard/quiz/update/" + id, "Update");
    }

    private void setAttributeViewModel(Model model, String pageHeader, String formAction, String buttonValue) {
        model.addAttribute("pageHeader", pageHeader);
        model.addAttribute("formAction", formAction);
        model.addAttribute("buttonValue", buttonValue);
        model.addAttribute("quizList", quizRepository.findAll());
    }
}
