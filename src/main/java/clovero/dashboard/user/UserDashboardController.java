package clovero.dashboard.user;

import clovero.datatables.DatatablesCriteria;
import clovero.datatables.DatatablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("dashboard/user")
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    @Autowired
    public UserDashboardController(UserDashboardService userDashboardService) {
        this.userDashboardService = userDashboardService;
    }

    @RequestMapping(path = "")
    public String index() {
        return "dashboard/user/index";
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse list(HttpServletRequest request) {
        final DatatablesCriteria criteria = DatatablesCriteria.getFromRequest(request);

        return userDashboardService.findWithDatatablesCriteria(criteria);
    }

}
