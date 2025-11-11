package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.response.CourseResponse;
import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.dto.response.UserResponse;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.service.CourseService;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final DocumentService documentService;
    private final UserService userService;
    private final CourseService courseService;

    @RequestMapping("/search")
    public String search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int userPage,
            @RequestParam(defaultValue = "1") int documentPage,
            Model model
    ) {
        Set<CourseResponse> courses = courseService.searchCourses(keyword)
                .stream()
                .map(CourseResponse::new)
                .collect(toSet());
        int documentPageIndex = (documentPage < 1) ? 0 : documentPage - 1;
        Page< Document> documentResultPage = documentService.searchDocuments(keyword, documentPageIndex);
        Page<DocumentResponse> documentResponsePage = documentResultPage.map(DocumentResponse::new);
        int userPageIndex = (userPage < 1) ? 0 : userPage - 1;
        Page<User> userResultPage = userService.searchUsers(keyword, userPageIndex);
        Page<UserResponse> userResponsePage = userResultPage.map(UserResponse::new);
        model.addAttribute("courses", courses);
        model.addAttribute("documents", documentResponsePage);
        model.addAttribute("users", userResponsePage);
        model.addAttribute("keyword", keyword);
        return "home/search";
    }
}
