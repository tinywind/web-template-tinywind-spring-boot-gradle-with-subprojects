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
    <style>
        section {border: solid 1px grey; padding: 2em; margin-bottom: 2em;}
        h3 {margin-bottom: 1em;}
        table {table-layout: fixed;}
        td, th {padding: 0.5em; border: solid 1px grey;}
    </style>

    <section>
        <h3>로그아웃 테스트</h3>
        <button type="button" onclick="logout()">로그아웃</button>
    </section>

    <section>
        <h3>엑셀 다운 테스트</h3>
        <a target="_blank" href="<c:url value="/user/download-excel"/>">사용자 정보 엑셀 다운로드</a>
    </section>

    <section>
        <h3>파일 리스트</h3>
        <table style="table-layout: fixed;">
            <thead>
            <tr>
                <th>id</th>
                <th>fileName</th>
                <th>path</th>
                <th>size</th>
                <th>createdAt</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="e" items="${files}">
                <tr style="cursor: pointer" onclick="download('${g.htmlQuote(e.path)}')">
                    <td>${e.id}</td>
                    <td>${g.htmlQuote(e.originalName)}</td>
                    <td>${g.htmlQuote(e.path)}</td>
                    <td>${e.size}</td>
                    <td><fmt:formatDate value="${e.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                        <a href="<c:url value="/files/download?file=${e.path}" />" target="_blank">다운로드</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>

    <section>
        <h3>파일 올리기 테스트</h3>

        <input type="file" name="file"/>
        <progress value="0" max="100" style="width:100%"></progress>

        <div>
            id: <input type="text" readonly name="id"/>
            <br/>
            originalName: <input type="text" readonly name="originalName"/>
            <br/>
            path: <input type="text" readonly name="path"/>
            <br/>
            size: <input type="text" readonly name="size"/>
            <br/>
            createdAt: <input type="text" readonly name="createdAt"/>
        </div>
    </section>
    <tags:scripts>
        <script>
            function logout() {
                restSelf.get("/api/auth/logout").done(function () {
                    location.href = contextPath + '/';
                });
            }

            $('[type="file"]').change(function () {
                uploadFile(this.files[0], $('progress')).done(function (response) {
                    $('[name=id]').val(response.data.id);
                    $('[name=originalName]').val(response.data.originalName);
                    $('[name=path]').val(response.data.path);
                    $('[name=size]').val(response.data.size);
                    $('[name=createdAt]').val(response.data.createdAt);
                });
            });
        </script>
    </tags:scripts>
</tags:layout>