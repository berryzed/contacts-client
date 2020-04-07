package com.daou.contacts.client.config;

import com.daou.contacts.client.domain.User;
import com.daou.contacts.client.dto.TokenResponse;
import com.daou.contacts.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

	@Autowired
	private UserService userService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${app.api.url}")
	private String apiUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException, ServletException {
		User loggedUser = userService.getLoggedUser();
		log.info("loggedUser {}", loggedUser.getId());
		log.info("accessToken {}", loggedUser.getAccessToken());

		ResponseEntity<TokenResponse> response = restTemplate.exchange(apiUrl + "/token/{userId}", HttpMethod.POST, null, TokenResponse.class, loggedUser.getId());
		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("토큰 정보 확인 불가");
		}

		loggedUser.setAccessToken(response.getBody().getAccessToken());
		userService.save(loggedUser);

		Authentication authentication =
				new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(),
						loggedUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		res.sendRedirect(req.getContextPath());
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
		log.info("onAuthenticationFailure");
		String username = req.getParameter("username");

		req.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", authException);
		req.setAttribute("SPRING_SECURITY_LAST_USERNAME", username);
		req.setAttribute("username", username);
		req.setAttribute("error", getErrorMessage(authException));
		req.getRequestDispatcher(SecurityConfig.LOGIN_URL).forward(req, res);
	}

	/**
	 * Spring Security 가 반환하는 에러 메시지 얻어옴
	 *
	 * @return 에러메시지
	 */
	private String getErrorMessage(AuthenticationException exception) {

		String error;
		if (exception instanceof InternalAuthenticationServiceException) {
			error = "존재하지 않는 계정입니다.";
		} else if (exception instanceof BadCredentialsException) {
			error = "아이디 또는 비밀번호를 확인해주시기 바랍니다.";
		} else if (exception instanceof LockedException) {
			error = "해당 계정은 관리자에 의해 잠겨있는 계정입니다.";
		} else if (exception instanceof DisabledException) {
			error = "로그인 반복 실패 또는 관리자에 의해 잠겨있는 계정입니다.\n관리자에게 문의하세요.";
		} else {
			error = "알 수 없는 에러가 발생하였습니다.\n관리자에게 문의하세요.";
		}

		return error;
	}
}
