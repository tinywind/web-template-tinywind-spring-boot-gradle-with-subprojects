<%@ page contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR" %>

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

<tags:layout>
    <h3>로그인</h3>
    <form:form id="login-form" modelAttribute="form" cssClass="-json-submit" action="${contextPath}/api/auth/login" data-done="after">
        <div class="field">
            <label>아이디</label>
            <form:input autocomplete="false" path="loginId" placeholder="아이디"/>
        </div>
        <div class="field">
            <label>비밀번호</label>
            <form:password autocomplete="false" path="password" placeholder="비밀번호"/>
        </div>
        <div class="ui toggle checkbox">
            <input type="checkbox" id="remember">
            <label for="remember">로그인저장</label>
        </div>
        <button type="submit" class="ui basic button brand">LOGIN</button>
    </form:form>
    <tags:scripts>
        <script>
            const STORAGE_KEY = 'loginForm';
            const form = $('#login-form');
            const remember = $('#remember');

            function after() {
                localStorage.setItem(STORAGE_KEY, remember.prop('checked') ? JSON.stringify(form.formDataObject()) : '');
                location.href = contextPath + '/main';
            }

            $(document).ready(function () {
                const storedValues = localStorage.getItem(STORAGE_KEY);
                if (!storedValues) return;

                const values = JSON.parse(storedValues);
                if (!values) return;

                remember.prop('checked', true);

                const inputs = form.find('[name]');
                for (let key in values) {
                    if (values.hasOwnProperty(key)) {
                        inputs.filter(function () {
                            return $(this).attr('name') === key;
                        }).val(values[key]);
                    }
                }
            });
        </script>
    </tags:scripts>
</tags:layout>