package clovero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    private final LineService lineService;

    @Autowired
    public IndexController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/")
    public String welcome(@RequestParam(required = false) String request) {
        if (request == null) {
            return "This is Clovero Bot Backend";
        }
        return lineService.handleTextMessageEvent(request);
    }
}
