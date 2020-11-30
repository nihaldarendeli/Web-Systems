<%--
  Created by IntelliJ IDEA.
  User: atiyakailany
  Date: 11/25/20
  Time: 9:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%       String log = (String) request.getAttribute("errorLogs");
        String log2 = (String) request.getAttribute("anotherLog");
        out.print(log);
        String userID = (String) request.getAttribute("userID");
%>
<hr/>
<% out.print(log2);%>
successful!

${userID} <br>

<hr/>

${log2}
</body>
</html>
