import com.google.appengine.api.datastore.*;
import com.google.gson.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/app")
public class app extends HttpServlet {
    String defaultSearchWord = "flowers";

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext()
                .getRequestDispatcher("/app.jsp");
        System.out.println("app doPost");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/app.jsp");
        System.out.println("app doGet");
        String userID = (String) request.getParameter("userID");
        if(userID == null){
            userID = "";
        }
        int i = Integer.parseInt(request.getParameter("index"));

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();



        Query query =
                new Query("User");

        Query.Filter userFilter = new Query.FilterPredicate("user_id", Query.FilterOperator.EQUAL, userID);

        query.setFilter(userFilter);

        PreparedQuery pq = datastore.prepare(query);

        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
        ArrayList<String> image_urls = new ArrayList<String>();
        ArrayList<String> photoIDs = new ArrayList<String>();
        ArrayList<JsonArray> colors = new ArrayList<JsonArray>();
        ArrayList<String> api_calls = new ArrayList<String>();
        String color = null;

        Gson g = new Gson();
        String urlEndpoint = "https://labs.tineye.com/multicolr/rest/color_search/?";
        String api_url = "";

        image_urls.add(results.get(i).getProperty("image_url").toString());
        photoIDs.add(results.get(i).getProperty("fb_image_id").toString());


        color = results.get(i).getProperty("colors").toString();

        JsonObject jsonObject = new JsonParser().parse(color).getAsJsonObject();

        colors.add(jsonObject.get("data").getAsJsonArray());


        JsonArray jArray = jsonObject.get("data").getAsJsonArray();


        int red = 0, green = 0, blue = 0;
        String[] hexColors = new String[jArray.size()];
        double[] weights = new double[jArray.size()];
        double total = 0;
        for (int j = 0; j < jArray.size(); j++) {
            red = jArray.get(j).getAsJsonObject().get("red").getAsInt();
            green = jArray.get(j).getAsJsonObject().get("green").getAsInt();
            blue = jArray.get(j).getAsJsonObject().get("blue").getAsInt();
            weights[j] = jArray.get(j).getAsJsonObject().get("score").getAsDouble();
            total += weights[j];
            hexColors[j] = String.format("%02x%02x%02x", red, green, blue);
        }
        int p = 100;
        double ratio = p / total;
        double newTotal = 0;
        for (int j = 0; j < weights.length; j++) {
            weights[j] = weights[j] * ratio;
            newTotal += weights[j];
        }

        if(newTotal >= 99.99){
            weights[weights.length-1] -= (newTotal-99.85);
        }

        int limit = 50;
        String q = String.format("limit=%d&colors[0]=%s&colors[1]=%s&colors[2]=%s&colors[3]=%s&colors[4]=%s&weights[0]=%f&weights[1]=%f&weights[2]=%f&weights[3]=%f&weights[4]=%f", limit, hexColors[0], hexColors[1], hexColors[2], hexColors[3], hexColors[4], weights[0], weights[1], weights[2], weights[3], weights[4]);


        String searchWord = request.getParameter("searchWord");
        String metadata = null;
        if (searchWord == null) {
            searchWord = defaultSearchWord;
        }
        if (searchWord.isEmpty()) {
            metadata = "";
        } else {
            metadata = "&metadata={\"keywords\":\"" + searchWord + "\"}";
        }
        api_url = urlEndpoint + q;
        api_calls.add(api_url);

        URL url = new URL(api_url + metadata);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        Gson gson = new Gson();
        String jsonString = sb.toString();
        JsonObject jObject = JsonParser.parseString(jsonString).getAsJsonObject();
        final JsonArray data = jObject.getAsJsonArray("result");
        List<String> list = new ArrayList<String>();
        for (JsonElement element : data) {
            list.add("https://img.tineye.com/flickr-images/?filepath=labs-flickr-public/images/" + ((JsonObject) element).get("filepath").getAsString());
        }
        request.setAttribute("images", list);
        request.setAttribute("searchWord", searchWord);


//        request.setAttribute("jsonArrayColors", colors);



        request.setAttribute("currentIndex", String.valueOf(i));

        int next = i + 1;
        String k = "";
        if (next >= results.size()) {
            k = "index=" + 0;
        } else {
            k = "index=" + next;
        }

        request.setAttribute("nextIndex", k);
        request.setAttribute("api_call", api_calls.get(0));
        request.setAttribute("image_url", image_urls.get(0));
        request.setAttribute("photoID", photoIDs.get(0));
        request.setAttribute("colors", hexColors);
        request.setAttribute("weights", weights);
        request.setAttribute("userID", userID);

        dispatcher.forward(request, response);
    }
}
