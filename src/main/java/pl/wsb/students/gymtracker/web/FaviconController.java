package pl.wsb.students.gymtracker.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public String redirectFavicon() {
        return "redirect:/favicon.svg";
    }
}
