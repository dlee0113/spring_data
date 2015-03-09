<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/static/css/pagination.css"/>
    <script type="text/javascript" src="/static/js/vendor/jquery.pagination.js"></script>
    <script type="text/javascript" src="/static/js/contact.search.js"></script>
    <title></title>
</head>
<body>
<div id="search-term" class="hidden">${searchTerm}</div>
<div id="pagination-next-label" class="hidden"><spring:message code="pagination.next.page.label"/></div>
<div id="pagination-previous-label" class="hidden"><spring:message code="pagination.previous.page.label"/></div>
<h1><spring:message code="search.result.list.title"/></h1>
<div>
    <div id="result-count-holder" class="hidden">
        <p><span id="result-count"></span> <spring:message code="search.result.count.description"/>: ${searchTerm}</p>
    </div>
    <div id="contact-list">

    </div>
</div>
<script id="template-contact-list" type="text/x-handlebars-template">
    {{#if results}}
        {{#each results}}
            <div class="well contact-list-item">
                <a href="/contact/{{id}}">{{lastName}} {{firstName}}</a>
            </div>
        {{/each}}
    {{else}}
        <p><spring:message code="search.result.list.label.no.contacts"/>: ${searchTerm}</p>
    {{/if}}
</script>
</body>
</html>