package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.dto.response.UserResponse;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.model.enums.Category;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("")
    public String home(Model model, @RequestParam(defaultValue = "1") int page) {
        if (page < 1) page = 1;
        Page<Document> newestDocumentsPage = documentService.getNewestDocuments(page - 1);
        int totalPages = newestDocumentsPage.getTotalPages();
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
            newestDocumentsPage = documentService.getNewestDocuments(page - 1);
        }
        Page<DocumentResponse> responsePage = newestDocumentsPage.map(DocumentResponse::new);
        var topDocuments = documentService.getTopDocument();
        List<DocumentResponse> topDocumentsDTO = topDocuments.stream().map(DocumentResponse::new).toList();
        List<User> topUsers = userService.getTop10UsersOrderByDocument();
        List<UserResponse> topUsersDTO = topUsers.stream().map(UserResponse::new).toList();
        List<Category> categories = Category.getAllDisplayNames();
        model.addAttribute("newestDocumentsPage", responsePage);
        model.addAttribute("topDocuments", topDocumentsDTO);
        model.addAttribute("users", topUsersDTO);
        model.addAttribute("categories", categories);
        return "home/home";
    }

    @GetMapping("/category")
    public String category(@RequestParam String category, Model model, @RequestParam(defaultValue = "1") int page) {
        List <DocumentResponse> topDocuments = documentService.getDocumentsByCategorySortedByDownloadCount(category).stream().map(DocumentResponse::new).toList();
        int pageIndex = (page < 1) ? 0 : page - 1;
        Page<Document> documentPage = documentService.getDocumentsByCategorySortedByCreateAt(category, pageIndex);
        Page<DocumentResponse> newestDocumentsPage = documentPage.map(DocumentResponse::new);
        List <UserResponse> topUsers = userService.getTop10UsersOrderByDocument().stream().map(UserResponse::new).toList();
        List <Category> categories = Category.getAllDisplayNames();
        model.addAttribute("topDocuments", topDocuments);
        model.addAttribute("newestDocumentsPage", newestDocumentsPage);
        model.addAttribute("users", topUsers);
        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
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
