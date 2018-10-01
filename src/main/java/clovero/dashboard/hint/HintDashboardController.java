package clovero.dashboard.hint;

import clovero.dashboard.quiz.QuizDashboardService;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import clovero.hint.Hint;
import clovero.hint.HintRepository;
import clovero.hint.HintType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("dashboard/hint")
public class HintDashboardController {

    private final HintDashboardService hintDashboardService;
    private final QuizDashboardService quizDashboardService;
    private final HintRepository hintRepository;

    @Autowired
    public HintDashboardController(HintDashboardService hintDashboardService, QuizDashboardService quizDashboardService, HintRepository hintRepository) {
        this.hintDashboardService = hintDashboardService;
        this.quizDashboardService = quizDashboardService;
        this.hintRepository = hintRepository;
    }

    @RequestMapping(path = "")
    public String index() {
        return "dashboard/hint/index";
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse list(HttpServletRequest request) {
        final DatatablesCriteria criteria = DatatablesCriteria.getFromRequest(request);

        return hintDashboardService.findWithDatatablesCriteria(criteria);
    }

    @RequestMapping(path = "/create", method = RequestMethod.GET)
    public String getCreatePage(Model model) {
        model.addAttribute("form", new HintForm());
        setCreateAttributeViewModel(model);
        return "dashboard/hint/form";
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("form") HintForm form, BindingResult bindingResult, Model model) {
        if (!quizDashboardService.find(form.getQuizId()).isPresent()) {
            bindingResult.addError(new FieldError("form", "quizId",
                    "quiz with id " + form.getQuizId() + " is not exist"));
        }

        if (bindingResult.hasErrors()) {
            setCreateAttributeViewModel(model);
            return "dashboard/hint/form";
        }

        hintDashboardService.save(form);
        return "redirect:/dashboard/hint";
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.GET)
    public String getUpdatePage(@PathVariable Integer id, Model model) {
        final Optional<Hint> hint = hintDashboardService.find(id);

        if (!hint.isPresent()) {
            return "redirect:/dashboard/hint";
        }

        model.addAttribute("form", new HintForm(hint.get()));
        setUpdateAttributeViewModel(model, id);
        return "dashboard/hint/form";
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.POST)
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("form") HintForm form,
                         BindingResult bindingResult, Model model) {
        if (!quizDashboardService.find(form.getQuizId()).isPresent()) {
            bindingResult.addError(new FieldError("form", "quizId",
                    "quiz with id " + form.getQuizId() + " is not exist"));
        }

        if (bindingResult.hasErrors()) {
            setUpdateAttributeViewModel(model, id);
            return "dashboard/hint/form";
        }

        form.setId(Optional.ofNullable(id));
        hintDashboardService.save(form);
        return "redirect:/dashboard/hint";
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Integer id) {
        hintDashboardService.delete(id);
        return "redirect:/dashboard/hint";
    }

    private void setCreateAttributeViewModel(Model model) {
        setAttributeViewModel(model, "Create new hint", "/dashboard/hint/create", "Create");
    }

    private void setUpdateAttributeViewModel(Model model, Integer id) {
        setAttributeViewModel(model, "Update hint", "/dashboard/hint/update/" + id, "Update");
    }

    private void setAttributeViewModel(Model model, String pageHeader, String formAction, String buttonValue) {
        model.addAttribute("pageHeader", pageHeader);
        model.addAttribute("formAction", formAction);
        model.addAttribute("buttonValue", buttonValue);
        model.addAttribute("hintList", hintRepository.findAll());
        model.addAttribute("hintTypes", HintType.values());
    }
}
