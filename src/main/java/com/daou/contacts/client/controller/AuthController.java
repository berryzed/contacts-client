package com.daou.contacts.client.controller;

import com.daou.contacts.client.domain.User;
import com.daou.contacts.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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
