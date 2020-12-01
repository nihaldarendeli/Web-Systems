<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="com.google.gson.JsonArray" %><%--
  Created by IntelliJ IDEA.
  User: atiyakailany
  Date: 11/25/20
  Time: 9:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%

//    JsonArray color = (JsonArray) request.getAttribute("jsonArrayColors");
    String userID = (String) request.getAttribute("userID");
    ArrayList<String> image_urls = (ArrayList<String>) request.getAttribute("image_urls");
    ArrayList<String> api_calls = (ArrayList<String>) request.getAttribute("api_calls");
//    ArrayList<String> photoIDs = (ArrayList<String>) request.getAttribute("photoIDs");
//    ArrayList<String> colors = (ArrayList<String>) request.getAttribute("colors");
%>
<script>
    // var counter = 0
    function foo(){
        <%--// alert("hello" + <%=userID%>)--%>
        <%--var userID = <%=userID%>;--%>

        var image_urls = `<%=image_urls%>`;
        var api_calls = `<%=api_calls%>`;
        <%--var one_url = ${image_urls[0]};--%>
        <%--console.log(typeof image_urls)--%>
        <%--var colors = `<%=colors%>`;--%>

        // console.log(one_url)
        <%--console.log(<%=image_urls%>)--%>
        <%--console.log(<%=photoIDs%>)--%>
        <%--console.log(<%=colors%>)--%>
        // console.log(counter++)
        // photoIDs
    }
</script>

<hr/>
<code>
    ${image_urls[0]}
</code>
<br>
<br>
<br>
<br>
<code>
    ${photoIDs[0]}
</code>
<br>
<br>
<br>
<br>
<code>
    ${colors[0]}
</code>
<br>
<button onClick=foo()>
    test button
</button>
successful!

    <br>
this user id is
<div id="userID"> ${userID} </div> <br>

<hr/>


</body>
</html>
