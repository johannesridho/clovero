package clovero.dashboard.category;

import clovero.category.Category;
import clovero.category.CategoryRepository;
import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("dashboard/category")
public class CategoryDashboardController {

    private final CategoryDashboardService categoryDashboardService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryDashboardController(CategoryDashboardService categoryDashboardService, CategoryRepository categoryRepository) {
        this.categoryDashboardService = categoryDashboardService;
        this.categoryRepository = categoryRepository;
    }

    @RequestMapping(path = "")
    public String index() {
        return "dashboard/category/index";
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse list(HttpServletRequest request) {
        final DatatablesCriteria criteria = DatatablesCriteria.getFromRequest(request);

        return categoryDashboardService.findWithDatatablesCriteria(criteria);
    }

    @RequestMapping(path = "/create", method = RequestMethod.GET)
    public String getCreatePage(Model model) {
        model.addAttribute("form", new CategoryForm());
        setCreateAttributeViewModel(model);
        return "dashboard/category/form";
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("form") CategoryForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setCreateAttributeViewModel(model);
            return "dashboard/category/form";
        }

        categoryDashboardService.save(form);
        return "redirect:/dashboard/category";
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.GET)
    public String getUpdatePage(@PathVariable Integer id, Model model) {
        final Optional<Category> category = categoryDashboardService.find(id);

        if (!category.isPresent()) {
            return "redirect:/dashboard/category";
        }

        model.addAttribute("form", new CategoryForm(category.get()));
        setUpdateAttributeViewModel(model, id);
        return "dashboard/category/form";
    }

    @RequestMapping(path = "/update/{id}", method = RequestMethod.POST)
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("form") CategoryForm form,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            setUpdateAttributeViewModel(model, id);
            return "dashboard/category/form";
        }

        form.setId(Optional.ofNullable(id));
        categoryDashboardService.save(form);
        return "redirect:/dashboard/category";
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Integer id) {
        categoryDashboardService.delete(id);
        return "redirect:/dashboard/category";
    }

    private void setCreateAttributeViewModel(Model model) {
        setAttributeViewModel(model, "Create New Category", "/dashboard/category/create", "Create");
    }

    private void setUpdateAttributeViewModel(Model model, Integer id) {
        setAttributeViewModel(model, "Update Category", "/dashboard/category/update/" + id, "Update");
    }

    private void setAttributeViewModel(Model model, String pageHeader, String formAction, String buttonValue) {
        model.addAttribute("pageHeader", pageHeader);
        model.addAttribute("formAction", formAction);
        model.addAttribute("buttonValue", buttonValue);
        model.addAttribute("categoryList", categoryRepository.findAll());
    }
}
