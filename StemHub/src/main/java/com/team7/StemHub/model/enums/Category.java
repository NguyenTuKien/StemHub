package com.team7.StemHub.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Category {
    LESSON ("Bài giảng"),
    PROJECT ("Dự án"),
    REPORT ("Báo cáo"),
    RESEARCH ("Nghiên cứu"),
    DOCUMENT ("Tài liệu học tập"),
    EXAM ("Đề thi/Đề kiểm tra"),
    OTHERS ("Khác");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    Category(String displayName) {
        this.displayName = displayName;
    }

    public static List<Category> getAllDisplayNames() {
        return Arrays.stream(Category.values())
                .collect(Collectors.toList());
    }

}
