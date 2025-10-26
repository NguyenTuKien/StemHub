package com.team7.StemHub.model.enums;

public enum Category {
    PRESENTATION ("Bài thuyết trình"),
    ESSAY  ("Bài luận"),
    RESEARCH_PAPER ("Bài nghiên cứu"),
    REPORT ("Báo cáo"),
    DOCUMENT ("Tài liệu học tập"),
    OTHERS ("Khác");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}
