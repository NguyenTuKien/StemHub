package com.team7.StemHub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "Course", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"courseName"})
})
public class Course {

    @Id
    @Column(name = "courseId", nullable = false)
    @EqualsAndHashCode.Include // QUAN TRỌNG: Chỉ định so sánh bằng ID
    private String courseId;

    @Column(name = "courseName", nullable = false, columnDefinition = "NVARCHAR(200)")
    private String courseName;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> docs;
}