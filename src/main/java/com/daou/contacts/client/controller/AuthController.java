package com.daou.contacts.client.controller;

import com.daou.contacts.client.domain.CGroup;
import com.daou.contacts.client.domain.User;
import com.daou.contacts.client.service.CGroupService;
import com.daou.contacts.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
    private CGroupService cGroupService;

	@Value("${app.api.url}")
	private String apiUrl;

    @GetMapping("/")
    public String index() {
        if (userService.isUserLogged()) {
            return "redirect:/contacts/list";
        } else {
            return "redirect:/login";
        }
    }

    @RequestMapping("/login")
    public String login() {
        log.info("login");
        if (userService.isUserLogged()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/join")
    public String join(Model model) {
        if (userService.isUserLogged()) {
            return "redirect:/";
        }

        User user = new User();
        model.addAttribute("user", user);
        return "join";
    }

    @PostMapping("/join")
    public String joinProcess(Model model, @Valid @ModelAttribute("user") User user, BindingResult result,
                              RedirectAttributes redirectAttr) {
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.passwordConfirm", "비밀번호를 확인해 주세요.");
        }

        if (!result.hasErrors()) {
            try {
                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                userService.save(user);
                return "redirect:/login";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("username", "error.username", "이미 동일한 아이디가 사용 중 입니다.");
            }
        }

        return "join";
    }
}
