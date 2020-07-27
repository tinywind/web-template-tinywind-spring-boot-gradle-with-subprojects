<%@ tag pageEncoding="UTF-8" %>

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

<!-- external library common -->

<!-- external library depend -->
<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.css"/>" rel="stylesheet"/>

<c:choose>
    <c:when test="${debugging}">
        <link href="<c:url value="/resources/less/app.less?version=${version}"/>" rel="stylesheet/less"/>
        <script src="<c:url value="/webjars/less.js/2.7.3/dist/less.min.js"/>"></script>
    </c:when>
    <c:otherwise>
        <link href="<c:url value="/resources/compiled/${version}.min.css?version=${version}"/>" rel="stylesheet"/>
    </c:otherwise>
</c:choose>