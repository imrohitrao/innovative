<%@ taglib uri="../tld/dd4.tld" prefix="dd4"%>
<%@ page import="java.util.Collection"%>
<%@ page import="com.digitald4.common.dao.*"%>
<%@ page import="com.digitald4.common.component.Column"%>

<article class="container_12">
	<dd4:table title="Billable" columns="<%=(Collection<Column>)request.getAttribute(\"billable_cols\")%>" data="<%=(Collection<? extends DataAccessObject>)request.getAttribute(\"billables\")%>"/>
</article>