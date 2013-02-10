package com.digitald4.iis.servlet;

import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.digitald4.common.servlet.ParentServlet;
import com.digitald4.iis.model.Patient;

public class PatientServlet extends ParentServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
			if(!checkLogin(request, response)) return;
			request.setAttribute("body", "/WEB-INF/jsp/patient.jsp");
			request.setAttribute("patient", Patient.getInstance(Integer.parseInt(request.getParameter("id"))));
			getLayoutPage().forward(request, response);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		try {
			if(!checkLogin(request, response)) return;
			Patient patient = Patient.getInstance(Integer.parseInt(request.getParameter("id")));
			String paramName=null;
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				paramName = paramNames.nextElement();
				if (paramName.toLowerCase().startsWith("patient.")) {
					Object attr = request.getParameter(paramName);
					patient.setPropertyValue(paramName, (String)attr);
				}
			}
			patient.save();
		} catch (Exception e) {
			throw new ServletException(e);
		}
		doGet(request,response);
	}
}