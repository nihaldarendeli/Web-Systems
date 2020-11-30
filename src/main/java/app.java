import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/app")
public class app extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext()
                .getRequestDispatcher("/app.jsp");
        dispatcher.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/app.jsp");

        String userID = (String)request.getParameter("userID");

//        Query q =
//                new Query("User")
//                        .setFilter(new Query.FilterPredicate("user_id", Query.FilterOperator.EQUAL, userID));
//        PreparedQuery pq = datastore.prepare(q);
//        Entity result = pq.asSingleEntity();

        request.setAttribute("userID", userID);


        dispatcher.forward(request, response);
    }
}
