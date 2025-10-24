package com.team7.StemHub.controller.web;

import com.team7.StemHub.model.User;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomePageController {
    private final DocumentService documentService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        var topDocuments = documentService.getTopDocument();
        var newestDocuments = documentService.getNewestDocuments();
        List<User> topUsers = userService.getAllUsersOrderByDocument();

        model.addAttribute("topDocuments", topDocuments);
        model.addAttribute("newestDocuments", newestDocuments);
        model.addAttribute("users", topUsers);

        return "home/home";
    }

    @GetMapping("/about")
    public String about() {
        return "home/about";
    }

    @GetMapping("/search/{keyword}")
    public String search(Model model) {
        return "home/search";
    }
}
