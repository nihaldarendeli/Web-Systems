import com.google.api.client.util.ArrayMap;
import com.google.appengine.api.datastore.*;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.json.Json;
import javax.json.JsonValue;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/app")
public class app extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext()
                .getRequestDispatcher("/app.jsp");
        System.out.println("app doPost");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/app.jsp");
        System.out.println("app doGet");
        String userID = (String)request.getParameter("userID");
        String index = request.getParameter("index");

        System.out.println("userID:"+userID);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

//        Query q =
//                new Query("User")
//                        .setFilter(new Query.FilterPredicate("user_id", Query.FilterOperator.EQUAL, userID));
//        PreparedQuery pq = datastore.prepare(q);
//        Entity result = pq.asSingleEntity();

//        EntityQuery.Builder queryBuilder = Query.newEntityQueryBuilder().setKind("User")
//                .setLimit(pageSize);
//        request.setAttribute("userID", userID);
//
//        Query<Entity> query = Query.newEntityQueryBuilder()
//                .setKind("User")
//                .setFilter(CompositeFilter.and(
//                        PropertyFilter.eq("done", false), PropertyFilter.ge("priority", 4)))
//                .setOrderBy(OrderBy.desc("priority"))
//                .build();ConceptsTest.java
//        Query q =
//                new Query("User")
//                        .setFilter(new Query.FilterPredicate("userID", Query.FilterOperator.EQUAL, userID));
//        PreparedQuery pq = datastore.prepare(q);
//
//        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
//        System.out.println(results.toString());



        Query query =
                new Query("User");

        Query.Filter userFilter = new Query.FilterPredicate("user_id", Query.FilterOperator.EQUAL, userID);

//        Query.Filter colorFilter = new Query.FilterPredicate("colors", Query.FilterOperator.EQUAL, colors);

//        Query.Filter imageLink = new Query.FilterPredicate("image_url", Query.FilterOperator.EQUAL, imageLink);

//        Query.Filter photoID = new Query.FilterPredicate("fb_image_id", Query.FilterOperator.EQUAL, photoID);

        PreparedQuery pq = datastore.prepare(query);

        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
//        List<List<ColorInfo>> allColors = null;
            List<ColorInfo> cols = new ArrayList<ColorInfo>();
        System.out.println(results.size());
        ArrayList<String> image_urls = new ArrayList<String>();
        ArrayList<String> photoIDs = new ArrayList<String>();
//        ArrayList<String> colors = new ArrayList<String>();
        ArrayList<JsonArray> colors = new ArrayList<JsonArray>();
        ArrayList<String> api_calls = new ArrayList<String>();

        ArrayList<String> hex_array = new ArrayList<String>();

        String color = null;
//        String colors = null;

        Gson g = new Gson();
        String urlEndpoint = "https://labs.tineye.com/multicolr/rest/color_search/?";
        String api_url = "";
        for(int i = 0; i < results.size(); i++){

            image_urls.add(results.get(i).getProperty("image_url").toString());
            photoIDs.add(results.get(i).getProperty("fb_image_id").toString());


            color = results.get(i).getProperty("colors").toString();

            JsonObject jsonObject = new JsonParser().parse(color).getAsJsonObject();

            colors.add(jsonObject.get("data").getAsJsonArray());


            JsonArray jArray = jsonObject.get("data").getAsJsonArray();


            int red = 0, green=0,blue=0;
            String[] hexColors = new String[jArray.size()];
            float[] weights = new float[jArray.size()];

            for (int j = 0; j < jArray.size(); j++) {
                red =  jArray.get(j).getAsJsonObject().get("red").getAsInt();
                green =  jArray.get(j).getAsJsonObject().get("green").getAsInt();
                blue =  jArray.get(j).getAsJsonObject().get("blue").getAsInt();
                weights[j] = jArray.get(j).getAsJsonObject().get("score").getAsFloat();
                hexColors[j] = String.format("%02x%02x%02x", red, green, blue);
//                System.out.println(hexColors[j]);
            }
            int limit = 10;
            String q = String.format("limit=%d&colors[0]=%s&colors[1]=%s&colors[2]=%s&colors[3]=%s&colors[4]=%s&weights[0]=%.2f&weights[1]=%.2f&weights[2]=%.2f&weights[3]=%.2f&weights[4]=%.2f", limit,hexColors[0],hexColors[1],hexColors[2],hexColors[3],hexColors[4],weights[0],weights[1],weights[2],weights[3],weights[4]);
//            String searchWord = "flowers";
//            String metadata = "&metadata={\"keywords\":\""+searchWord+"\"}";
            api_url = urlEndpoint + q;
            api_calls.add(api_url);
        }

//        JsonObject jsonObject = new JsonParser().parse(color).getAsJsonObject();
//
////        System.out.println(jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("red"));
////        JsonArray jsonArrayColors = jsonObject.get("data").getAsJsonArray();
        request.setAttribute("jsonArrayColors", colors);





        System.out.println(api_calls.toString());

        request.setAttribute("api_calls", api_calls);

        request.setAttribute("image_urls", image_urls);
        request.setAttribute("photoIDs", photoIDs);
        request.setAttribute("colors", colors);
        request.setAttribute("userID", userID);
        System.out.println(image_urls.toString());
        System.out.println(photoIDs.toString());
        System.out.println(colors.toString());
        dispatcher.forward(request, response);
    }
}
