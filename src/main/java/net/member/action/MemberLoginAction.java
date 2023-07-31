package net.member.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemberLoginAction implements Action {
	
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("id")) {
					id = cookie.getValue();
				}
			}
		}

		request.setAttribute("id", id);
		ActionForward forward = new ActionForward();
		forward.setRedirect(false); // 주소 변경없이 jsp페이지의 내용을 보여줍니다.
		forward.setPath("member/loginForm.jsp");
		return forward;
	}
}
