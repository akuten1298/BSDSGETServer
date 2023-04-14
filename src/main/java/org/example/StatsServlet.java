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
import java.util.Collection;
import java.util.HashSet;

@WebServlet("/stats/*")
public class StatsServlet extends HttpServlet {

    private static MongoCollection<Document> collection;

    private static String NUM_LIKES = "numLikes";
    private static String NUM_DISLIKES = "numDislikes";
    private static String MONGO_ID = "_id";
    private static String SWIPE_DB = "swipe";
    private static String LIKES = "likes";
    private static String DISLIKES = "dislikes";
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
            Document myDoc = cache.retrieveFromCache(userId);
            // check if it exists in cache
            if (myDoc == null) {
                myDoc = collection.find(Filters.eq(MONGO_ID, userId)).first();
            } else {
                System.err.println("it was in the cache!");
            }
            StatsResponse statsResponse;
            if(myDoc != null) {
                // add to cache
                cache.insertToCache(userId, myDoc);

                int likes = myDoc.get(LIKES)  == null ? 0 : new HashSet<>((Collection) myDoc.get(LIKES)).size();
                int disLikes = myDoc.get(DISLIKES)  == null ? 0 : new HashSet<>((Collection) myDoc.get(DISLIKES)).size();

                statsResponse = new StatsResponse(likes, disLikes);
                String resp = gson.toJson(statsResponse);
                printWriter.print(resp);
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                InvalidResponse invalidResponse = new InvalidResponse("User Not Found");
                String resp = gson.toJson(invalidResponse);
                printWriter.print(resp);
            }
        }
        cache.emptyCacheIfFull();
        printWriter.flush();
        printWriter.close();
    }
}
