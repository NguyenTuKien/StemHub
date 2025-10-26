package com.team7.StemHub.controller.web;

import com.team7.StemHub.dto.request.SignupRequest;
import com.team7.StemHub.model.User;
import com.team7.StemHub.model.enums.Role;
import com.team7.StemHub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthPageController {
    private final AuthService authService;

    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) String error,
                        @RequestParam(name = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật kh��u không đúng.");
        }
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        }
        return "auth/enter";
    }

    @GetMapping({"/signup", "/register"})
    public String showSignupForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute SignupRequest dto, Model model) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            model.addAttribute("username", dto.getUsername());
            model.addAttribute("email", dto.getEmail());
            model.addAttribute("fullname", dto.getFullname());
            return "auth/register";
        }

        if (authService.findByUsername(dto.getUsername()) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại");
            model.addAttribute("email", dto.getEmail());
            model.addAttribute("fullname", dto.getFullname());
            return "auth/register";
        }

        if(authService.findByEmail(dto.getEmail()) != null){
            model.addAttribute("error", "Email đã tồn tại");
            model.addAttribute("username", dto.getUsername());
            model.addAttribute("fullname", dto.getFullname());
            return "auth/register";
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .fullname(dto.getFullname())
                .password(dto.getPassword())
                .role(Role.USER)
                .build();

        authService.register(user);

        model.addAttribute("message", "Đăng ký thành công. Vui lòng đăng nhập.");
        return "auth/enter"; // Render login page with success message
    }
}