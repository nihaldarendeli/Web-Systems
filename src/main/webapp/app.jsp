<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: atiyakailany
  Date: 11/25/20
  Time: 9:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<html>
<head>
    <link rel="stylesheet" href="./css/index.css" type="text/css"/>
    <title>Title</title>
</head>
<body>
<%

    //    JsonArray color = (JsonArray) request.getAttribute("jsonArrayColors");
    String userID = (String) request.getAttribute("userID");
    String image_url = (String) request.getAttribute("image_url");
    String api_call = (String) request.getAttribute("api_call");
    String photoID = (String) request.getAttribute("photoID");
//    String THISFUCKINGVARIABLE = String.valueOf(request.getAttribute("currentIndex"));
    String currentIndex = (String) request.getAttribute("currentIndex");

    String nextIndex = (String) request.getAttribute("nextIndex");
    String searchWord = (String) request.getAttribute("searchWord");

//    ArrayList<String> photoIDs = (ArrayList<String>) request.getAttribute("photoIDs");
//    ArrayList<String> colors = (ArrayList<String>) request.getAttribute("colors");
%>
current index is ${currentIndex}<br>
searchword is ${searchWord}
<section class="page">
    <div class="color-section">
        <h1 class="title">Dominant Colors</h1>

        <div class="color-container">
            <%
                String colors[] = (String[]) request.getAttribute("colors");
                double weights[] = (double[]) request.getAttribute("weights");

                for (int i = 0; i < colors.length; i++) {
//            out.println(String.format("<div class=\"color\" style=\"width:%2.f%;background-color:%s;\"></div>",weights[i],colors[i]));
                    out.println("<div class=\"color\" style=\"width:" + weights[i] + "%;background-color:" + colors[i] + ";\"></div>");
                }
            %>
            <%--    <dom-repeat style="display: none;"><template is="dom-repeat"></template></dom-repeat>--%>
        </div>
    </div>
    <div class="current-picture">
        <h1 class="title">Current Image</h1>
        <img src="${image_url}" alt="${photoID}"/>
    </div>
</section>


<section class="page">
    <div class="flex-center">
        <div>Current filter: <b>${searchWord}</b></div>
        <form id="form_home" action="${pageContext.request.contextPath}/app" method="get" >
            <input  type="text" name="searchWord" id="searchWord">
            <input type="hidden" name="index" id="index" value="${currentIndex}">
<%--            <input type="hidden" name="imageID" id="imageID">--%>
            <%--    <div id="status"></div>--%>
            <input id="filter" type="submit" class="btn btn-default btn-block" value="Filter">

        </form>
    </div>
    <table align="center">
        <%--    <tr bgcolor="#949494">--%>
        <%--        <th>images</th>--%>
        <%--    </tr>--%>
        <%
            JsonObject S = (JsonObject) request.getAttribute("apiData");
////            JsonObject json = (JsonObject) request.getAttribute("data");
//            if(json != null) {
//                out.print("<tr><td>" + json + "</td>\n");
//            }else{
//                out.print("<tr><td>json is null</td>\n");
//            }
            List<String> images = (List<String>) request.getAttribute("images");

            int numCol = 10;
            for (int i = 0, j = 0; i < images.toArray().length; i++, j++) {
                if (j % numCol == 0) {
                    out.println("<tr>");
                }
                String imageLink = images.get(i);
                out.println("<td><img src=" + imageLink + "></td>");
                if (j % numCol == numCol - 1) {
                    out.println("</tr>");
                }
            }
        %>
    </table>
    <div style="padding: 15px">
        <a style="text-decoration: none"href="app?${nextIndex}&searchWord=${searchWord}">
            <div class="next-button">
                Next
            </div>
        </a>
    </div>
</section>


</body>
</html>
