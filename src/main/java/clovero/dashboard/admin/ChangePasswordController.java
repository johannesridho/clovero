package clovero.dashboard.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
@RequestMapping("dashboard/admin")
public class ChangePasswordController {

    private final AdminService adminService;

    @Autowired
    public ChangePasswordController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(path = "/change-password", method = RequestMethod.GET)
    public String getChangePasswordPage(Model model) {
        final ChangePasswordForm form = new ChangePasswordForm();
        model.addAttribute("form", form);
        return "dashboard/admin/change-password";
    }

    @RequestMapping(path = "/change-password", method = RequestMethod.POST)
    public String changePassword(Model model, Principal principal, @ModelAttribute("form") ChangePasswordForm userForm,
                                 BindingResult bindingResult) {

        final Admin admin = adminService.find(principal.getName()).get();

        if (!adminService.isPasswordMatch(admin.getPassword(), userForm.getCurrentPassword())) {
            bindingResult.addError(new FieldError("form", "currentPassword", "Password is incorrect"));
        }

        if (!userForm.getPassword().equals(userForm.getRePassword())) {
            bindingResult.addError(new FieldError("form", "rePassword", "Password doesn't match"));
        }

        if (!bindingResult.hasErrors()) {
            adminService.changePassword(admin, userForm.getPassword());
            return "redirect:/dashboard/admin/change-password?success";
        }

        model.addAttribute("form", userForm);
        return "dashboard/admin/change-password";
    }
}
