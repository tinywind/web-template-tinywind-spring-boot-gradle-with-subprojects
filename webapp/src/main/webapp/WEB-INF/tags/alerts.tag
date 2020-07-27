<%@ tag pageEncoding="utf-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%--@elvariable id="debugging" type="java.lang.Boolean"--%>
<%--@elvariable id="version" type="java.lang.String"--%>
<%--@elvariable id="contextPath" type="java.lang.String"--%>
<%--@elvariable id="g" type="org.tinywind.server.config.RequestGlobal"--%>
<%--@elvariable id="message" type="org.tinywind.server.config.RequestMessage"--%>
<%--@elvariable id="user" type="org.tinywind.server.model.User"--%>

<script>
    <c:forEach var="message" items="${g.popAlerts()}">
    alert("${g.escapeQuote(message)}");
    </c:forEach>
</script>