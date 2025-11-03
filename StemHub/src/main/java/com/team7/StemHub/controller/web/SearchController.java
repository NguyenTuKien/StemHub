package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.response.CourseResponse;
import com.team7.StemHub.dto.response.DocumentResponse;
import com.team7.StemHub.dto.response.UserResponse;
import com.team7.StemHub.facade.SearchFacade;
import com.team7.StemHub.service.CourseService;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchFacade searchFacade;

    @RequestMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        Set<CourseResponse> courses = searchFacade.searchCourse(keyword).stream().map(CourseResponse::new).collect(toSet());
        Set<UserResponse> users = searchFacade.searchUser(keyword).stream().map(UserResponse::new).collect(toSet());
        Set<DocumentResponse> documents = searchFacade.searchDocument(keyword).stream().map(DocumentResponse::new).collect(toSet());
        model.addAttribute("courses", courses);
        model.addAttribute("users", users);
        model.addAttribute("documents", documents);
        return "home/search";
    }
}
