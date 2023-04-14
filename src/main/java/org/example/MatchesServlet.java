package org.example;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/matches/*")
public class MatchesServlet extends HttpServlet {

    private static MongoCollection<Document> collection;
    private static String SWIPE_DB = "swipe";
    private static String MONGO_ID = "_id";
    private static String LIKES = "likes";
    private static TwinderCache cache;

    @Override
    public void init() throws ServletException {
        MongoConfig mongoConfig = MongoConfig.getInstance();
        MongoDatabase database = mongoConfig.getDatabase();
        collection = database.getCollection(SWIPE_DB);
        cache = new TwinderCache();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
        ServletOutputStream printWriter = res.getOutputStream();
        Gson gson = new Gson();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            InvalidResponse invalidResponse = new InvalidResponse("missing parameters");
            String resp = gson.toJson(invalidResponse);
            printWriter.print(resp);
        } else {
            String userId = urlPath.substring(1);
            res.setStatus(HttpServletResponse.SC_OK);
            // check if it exists in cache
            Document myDoc = cache.retrieveFromCache(userId);
            if (myDoc == null) {
                myDoc = collection.find(Filters.eq(MONGO_ID, userId)).first();
            }
            MatchesResponse matchesResponse;
            if(myDoc != null) {
                // add to cache
                cache.insertToCache(userId, myDoc);
                List<String> matchList = new ArrayList<>();
                if(myDoc.get(LIKES) != null) {
                    Set<String> likesSet = new HashSet<>((Collection) myDoc.get(LIKES));
                    for(String swipee : likesSet) {
                        Document swipeeDoc = collection.find(Filters.eq(MONGO_ID, swipee)).first();
                        if(swipeeDoc == null)
                            continue;
                        if(swipeeDoc.get(LIKES) != null) {
                            Set<String> swipeeLikesSet = new HashSet<>((Collection) swipeeDoc.get(LIKES));
                            if(swipeeLikesSet.contains(userId))
                                matchList.add(swipee);
                        }
                    }
                }

                matchesResponse = new MatchesResponse(matchList);
                String resp = gson.toJson(matchesResponse);
                printWriter.print(resp);
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                InvalidResponse invalidResponse = new InvalidResponse("User Not Found");
                String resp = gson.toJson(invalidResponse);
                printWriter.print(resp);
            }
        }

        printWriter.flush();
        printWriter.close();
    }
}
