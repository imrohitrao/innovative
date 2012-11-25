package com.digitald4.common.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.digitald4.common.model.User;
   
public class LoginServlet extends ParentServlet
{
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
{
		HttpSession session = request.getSession(true);
		User user = (User)session.getAttribute("user");
		if(user != null && user.getId() > 0){
      		response.sendRedirect("home");
			return;
		}
		if(request.getParameter("u") != null && request.getParameter("key") != null)
			doPost(request,response);
		else
			forward2Jsp(request, response);
   }
	protected void forward2Jsp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession(true);
		User user = (User)session.getAttribute("user");
		if(user == null){
			user = new User();
			session.setAttribute("user",user);
		}
		request.setAttribute("body", "/WEB-INF/jsp/login.jsp");
   	   getLayoutPage().forward(request, response);
	}
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException
   {
      HttpSession session = request.getSession();
      String username = request.getParameter("u");
      if (username == null || username.length() == 0) {
         request.setAttribute("error", "Username is required.");
         forward2Jsp(request, response);
         return;
      }
      String passwd = request.getParameter("key");
      if (passwd == null) passwd = "";

      try {
			User user = User.getInstance(username,passwd);
			if(user!=null){
				session.setAttribute("user", user);
			}
			else{
				request.setAttribute("error", "Login incorrect");
         		forward2Jsp(request, response);
         		return;
			}
      } catch (Exception e) {
         throw new ServletException(e);
      }
	String redirect = (String)session.getAttribute("redirect");
	if(redirect == null)
		redirect = "account";
	else
		session.removeAttribute("redirect");
      response.sendRedirect(redirect);
   }
}