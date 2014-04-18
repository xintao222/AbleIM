package com.ablesky.im.controller.redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.ablesky.im.util.AbleskyImConfig;

public class FaultLinkRedirectController extends MultiActionController {
	public static final Log log = LogFactory.getLog(FaultLinkRedirectController.class);

	public ModelAndView resolveCanNotFind(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("resolveCanNotFind-404, Referer=" + request.getHeader("Referer"));
		response.sendRedirect(AbleskyImConfig.HTTP_SERVER + "faultLinkRedirect.do?action=resolveInternalError");

		return null;
	}

	public ModelAndView resolveInternalError(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("resolveInternalError-500, Referer=" + request.getHeader("Referer"));
		response.sendRedirect(AbleskyImConfig.HTTP_SERVER + "faultLinkRedirect.do?action=resolveCanNotFind");

		return null;
	}
}
