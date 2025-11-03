package com.team7.StemHub.config;

import com.team7.StemHub.dao.CourseRepo;
import com.team7.StemHub.model.Course;
import com.team7.StemHub.service.CourseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class InitDatabase {
    @Bean
    @Order(1)
    CommandLineRunner initCourse(CourseRepo courseRepo) {
        return args -> {
            if (courseRepo.count() == 0) {
                courseRepo.saveAll(List.of(
                        Course.builder().courseId("######").courseName("Other").otherName("").build(),
                        Course.builder().courseId("INT1319").courseName("Hệ điều hành").otherName("OS, Operation System, He dieu hanh, HĐH").build(),
                        Course.builder().courseId("INT1313").courseName("Cơ sở dữ liệu").otherName("Database, DB,Co so du lieu, CSDL").build(),
                        Course.builder().courseId("INT13162").courseName("Lập trình với Python").otherName("Lap trinh voi Python, LTVP").build(),
                        Course.builder().courseId("INT1332").courseName("Lập trình hướng đối tượng").otherName("Lap trinh huong doi tuong, OOP, Java, LTHDT").build(),
                        Course.builder().courseId("INT1336").courseName("Mạng máy tính").otherName("Mang may tinh, Computer Network, Network, MMT").build(),
                        Course.builder().courseId("INT1306").courseName("Cấu trúc dữ liệu và giải thuật").otherName("Cau truc du lieu va giai thuat, CTDL&GT, Data Structure and Algorithm, DSA").build(),
                        Course.builder().courseId("INT13145").courseName("Kiến trúc máy tính").otherName("Kien truc may tinh, KTMT, Computer Architecture").build(),
                        Course.builder().courseId("INT1359").courseName("Toán rời rạc 2").otherName("Toan roi rac 2, Ly thuyet do thi, Lý thuyết đồ thị, TTR2, Disjointed Math 2").build(),
                        Course.builder().courseId("INT1358").courseName("Toán rời rạc 1").otherName("Toan roi rac 1, TTR1, Disjointed Math 1").build(),
                        Course.builder().courseId("INT1339").courseName("Ngôn ngữ lập trình C++").otherName("Ngon ngu lap trinh C++, Code C++").build(),
                        Course.builder().courseId("INT1155").courseName("Tin học cơ sở 2").otherName("Tin hoc co so 2, Ngon ngu lap trinh C, Ngôn ngữ lập trình C, Code C").build()
                ));
            }
        };
    }
}
