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

<tags:scripts/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=440, initial-scale=0.8"/>
    <title>TBS</title>
    <tags:favicon/>
    <tags:css/>
</head>
<body>

<jsp:doBody/>

<div id="scripts">
    <tags:js/>
    <tags:alerts/>
    <tags:scripts method="pop"/>
</div>

</body>
</html>