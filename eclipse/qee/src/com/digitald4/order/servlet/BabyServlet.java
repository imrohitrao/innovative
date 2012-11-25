package com.digitald4.order.servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.digitald4.common.servlet.ParentServlet;

public class BabyServlet extends ParentServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
      		request.setAttribute("body", "/WEB-INF/jsp/baby.jsp");
      		getLayoutPage().forward(request, response);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		doGet(request,response);
	}
}