<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form id = "form" action="chirp/user/edit.do" modelAttribute ="chirp">
	
	
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<p><em><spring:message code = "form.required.params"/></em></p>
	
	<acme:textbox code="chirp.title" path="title" required = "true"/>
	
	<acme:textarea code="chirp.description" path="description" required = "true"/>
	
	<acme:submit name="save" code="chirp.save"/>
	
	<acme:cancel url="chirp/user/list.do" code="chirp.cancel"/>

</form:form>