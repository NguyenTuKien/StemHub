package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.dto.response.UserResponse;
import com.team7.StemHub.model.User;
import com.team7.StemHub.model.enums.Category;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomePageController {
    private final DocumentService documentService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        var topDocuments = documentService.getTopDocument();
        List<DocumentResponse> topDocumentsDTO = topDocuments.stream().map(DocumentResponse::new).toList();
        var newestDocuments = documentService.getNewestDocuments();
        List<DocumentResponse> newestDocumentsDTO = newestDocuments.stream().map(DocumentResponse::new).toList();
        List<User> topUsers = userService.getTop10UsersOrderByDocument();
        List<UserResponse> topUsersDTO = topUsers.stream().map(UserResponse::new).toList();
        List<String> categories = Category.getAllDisplayNames();
        model.addAttribute("topDocuments", topDocumentsDTO);
        model.addAttribute("newestDocuments", newestDocumentsDTO);
        model.addAttribute("users", topUsersDTO);
        model.addAttribute("categories", categories);
        return "home/home";
    }

    @GetMapping("/category")
    public String category(@RequestParam String category, Model model) {
        List <DocumentResponse> topDocuments = documentService.getDocumentsByCategorySortedByDownloadCount(category).stream().map(DocumentResponse::new).toList();
        List <DocumentResponse> newestDocuments = documentService.getDocumentsByCategorySortedByCreateAt(category).stream().map(DocumentResponse::new).toList();
        List <UserResponse> topUsers = userService.getTop10UsersOrderByDocument().stream().map(UserResponse::new).toList();
        List <String> categories = Category.getAllDisplayNames();
        model.addAttribute("topDocuments", topDocuments);
        model.addAttribute("newestDocuments", newestDocuments);
        model.addAttribute("users", topUsers);
        model.addAttribute("categories", categories);
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
