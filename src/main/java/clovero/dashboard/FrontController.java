package clovero.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class FrontController {

    @RequestMapping("")
    public String index() {
        return "dashboard/index";
    }

    @RequestMapping("/login")
    public String getLoginPage(Model model, @RequestParam Optional<String> error, @RequestParam Optional<String> logout) {
        if (error.isPresent()) {
            model.addAttribute("error", error);
        } else if (logout.isPresent()) {
            model.addAttribute("logout", logout);
        }

        return "dashboard/login";
    }
}
