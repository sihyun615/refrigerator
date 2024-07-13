package com.sparta.refrigerator.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index.html";
    }
    @GetMapping("/styles.css")
    public String style() {
        return "styles.css";
    }
    @GetMapping("/scripts.js")
    public String script() {
        return "scripts.js";
    }
}
