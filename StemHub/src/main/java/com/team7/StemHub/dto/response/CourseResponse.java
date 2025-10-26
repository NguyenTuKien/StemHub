package com.team7.StemHub.dto.response;

import com.team7.StemHub.model.Course;
import lombok.Data;

@Data
public class CourseResponse {
    private String courseId;
    private String courseName;

    public CourseResponse(Course course) {
        this.courseId = courseId;
        this.courseName = courseName;
    }
}
