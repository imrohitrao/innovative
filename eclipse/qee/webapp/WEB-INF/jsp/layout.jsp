<%@ taglib uri="../tld/c.tld" prefix="c"%>
<%@ page import="com.digitald4.pm.*"%>
<%@ page import="com.digitald4.pm.servlet.*"%>
<%@ page import="com.digitald4.util.*"%>
<%@ page import="java.sql.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

	<head>

		<%@ include file="/WEB-INF/jsp/include/head.jsp" %>

	</head>

	<body onload="readCookieSetInput('login','u');">
		<div id="container">
			<%@ include file="/WEB-INF/jsp/include/containerheader.jsp" %>
			<div id="content">

				<div id="right_panel">
					<c:import url="${body}" />
				</div>

			</div>
		</div> <%//container%>
		<div id="footer">
			<%@ include file="/WEB-INF/jsp/include/footer.jsp" %>
		</div> <%//footer%>
	</body>
</html>
