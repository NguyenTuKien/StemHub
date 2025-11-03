package com.team7.StemHub.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Category {
    PRESENTATION ("Bài thuyết trình"),
    ESSAY  ("Bài luận"),
    RESEARCH_PAPER ("Bài nghiên cứu"),
    REPORT ("Báo cáo"),
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

    public static List<String> getAllDisplayNames() {
        return Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());
    }

}
