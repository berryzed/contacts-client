/*
 * Created by Kyungrim Choi (rimi@userinsight.co.kr).
 * Copyright (c) 2019 Userinsight Co. <http://www.userinsight.co.kr>
 * All rights reserved.
 */

package com.daou.contacts.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class ControllerNameInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
						   Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

		String controllerName = "";
		String methodName = "";

		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			controllerName = handlerMethod.getBeanType().getSimpleName().replace("Controller", "");
			methodName = handlerMethod.getMethod().getName();
		}

		String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
		request.setAttribute("queryString", queryString);
		request.setAttribute("controllerName", controllerName);
		request.setAttribute("methodName", methodName);
	}
}
