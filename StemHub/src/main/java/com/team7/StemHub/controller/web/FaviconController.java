package com.team7.StemHub.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public String favicon() {
        // Redirect to an existing icon under /images
        return "redirect:/images/Symbol.png";
    }
}

