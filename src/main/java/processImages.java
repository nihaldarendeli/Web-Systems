import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import org.apache.commons.io.IOUtils;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.protobuf.ByteString;

@WebServlet("/processImages")
//@GetMapping(value = "/processImages")
public class processImages extends HttpServlet {



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Create dataStore instance
        DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

        String userID = (String)request.getParameter("userID");
        //line spacer for error
        //line spacer for error
        //line spacer for error
        //line spacer for error
        //line spacer for error
        //line spacer for error
        ArrayList<String> photoID = new ArrayList<String>(Arrays.asList(request.getParameterValues("photoID")));
        ArrayList<String> imageLinks = new ArrayList<String>(Arrays.asList(request.getParameterValues("imageLinks")));
        System.out.println(imageLinks);

        processImage(dataStore, imageLinks, userID, photoID);

        RequestDispatcher dispatcher = getServletContext()
                .getRequestDispatcher("/app.jsp");
        dispatcher.forward(request, response);




    }

    private void processImage(DatastoreService dataStore, ArrayList<String> imageLinks, String UserID, ArrayList<String> photoID) throws IOException {



        try {

            if (imageLinks != null) {
                AtomicInteger index = new AtomicInteger();

                imageLinks.forEach(photo -> {


                    Entity user = ifAlreadyExists(dataStore, photo);
                    if (user == null){

                        //Retrieve dominant colors through Vision API
                        List<ColorInfo> dominantColors = null;
                        try {
                            dominantColors = getDominantColors(photo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //upload image ID, image link, and dominant colors to data store
                        if (dominantColors != null){
                            user = uploadToDataStore(dominantColors, photo, dataStore,  UserID, photoID.get(index.get()));
                            index.getAndIncrement();
                        }

                    }
                });
            }




        }//end try
        catch (Exception e) {
            e.printStackTrace();
        }
    }//end func

    //Saving to data store.
    private Entity uploadToDataStore(List<ColorInfo> dominantColors, String imageLink, DatastoreService datastore, String userID, String photoID) {

        String colors = dominantColors.toString();


        if(colors != null && !colors.isEmpty()) {

            Entity user = new Entity("User");

            user.setProperty("user_id", userID);
            user.setProperty("fb_image_id", photoID);
            user.setProperty("image_url", imageLink);
            user.setProperty("colors", colors);

            datastore.put(user);

            return user;

        }
        return null;
    }

    private Entity ifAlreadyExists(DatastoreService datastore, String imageLink) {
        Query q =
                new Query("User")
                        .setFilter(new FilterPredicate("imageLink", FilterOperator.EQUAL, imageLink));
        PreparedQuery pq = datastore.prepare(q);
        Entity result = pq.asSingleEntity();
        return result;
    }

    public static byte[] downloadFile(URL url) throws Exception {
        try (InputStream in = url.openStream()) {
            byte[] bytes = IOUtils.toByteArray(in);
            return bytes;
        }
    }

    private List<ColorInfo> getDominantColors(String imageLink) throws Exception {

            byte[]  imgBytes = downloadFile(new URL(imageLink));
            ByteString byteString = ByteString.copyFrom(imgBytes);

            List<AnnotateImageRequest> requests = new ArrayList<>();

///ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
            Image img = Image.newBuilder().setContent(byteString).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            List<ColorInfo> dominantColors = new ArrayList<ColorInfo>();

            // Initialize client that will be used to send requests. This client only needs to be created
            // once, and can be reused for multiple requests. After completing all of your requests, call
            // the "close" method on the client to safely clean up any remaining background resources.
            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        System.out.format("Error: %s%n", res.getError().getMessage());

                    }

                    // For full list of available annotations, see http://g.co/cloud/vision/docs
                    DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
                    dominantColors = colors.getColorsList();


                    //return imageResponse.getLabelAnnotationsList();
                }

            }
            return dominantColors;


    }//end getDominantColors

}
