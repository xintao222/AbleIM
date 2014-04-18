package com.ablesky.im.controller.redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.ablesky.im.util.AbleskyImConfig;

public class IndexController extends AbstractController {

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.sendRedirect(AbleskyImConfig.HTTP_SERVER + "login.do");
		return null;
	}

}
