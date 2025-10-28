package com.team7.StemHub.service;

import com.team7.StemHub.dao.CourseRepo;
import com.team7.StemHub.model.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;

    public Course getCourseByCourseName(String courseName) {
        return courseRepo.findByCourseName(courseName)
                .orElseGet(() -> {
                    Course newCourse = new Course();
                    newCourse.setCourseId(UUID.randomUUID().toString()); // Auto-generate UUID
                    newCourse.setCourseName(courseName);
                    return courseRepo.save(newCourse);
                });
    }

    public List<Course> getAllCourses(){
        return courseRepo.findAll();
    }

    public List<Course> searchCourses(String keyword){
        return courseRepo.findByCourseNameContainingIgnoreCase(keyword);
    }
}
