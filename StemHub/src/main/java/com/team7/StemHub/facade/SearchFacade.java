package com.team7.StemHub.facade;

import com.team7.StemHub.model.Course;
import com.team7.StemHub.model.Document;
import com.team7.StemHub.model.User;
import com.team7.StemHub.service.CourseService;
import com.team7.StemHub.service.DocumentService;
import com.team7.StemHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchFacade {
    private final DocumentService documentService;
    private final CourseService courseService;
    private final UserService userService;

    public Set<Document> searchDocument(String keyword) {
        Set<Document> documents = documentService.searchDocuments(keyword);
        Set<Course> courses = courseService.searchCourses(keyword);
        for (Course course : courses) {
            documents.addAll(documentService.getDocumentsByCourse(course));
        }
        return documents;
    }

    public Set<User> searchUser(String keyword) {
        return userService.searchUsers(keyword);
    }

    public Set<Course> searchCourse(String keyword) {
        return courseService.searchCourses(keyword);
    }
}
