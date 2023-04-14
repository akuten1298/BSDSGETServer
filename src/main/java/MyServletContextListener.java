import com.mongodb.client.MongoClient;
import org.example.MongoConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author aakash
 */
public class MyServletContextListener implements ServletContextListener {

  public void contextDestroyed(ServletContextEvent sce) {
    MongoConfig mongoConfig = MongoConfig.getInstance();
    MongoClient mongoClient = mongoConfig.getMongoClient();
    try {
      mongoClient.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Server stopped!");
  }

  public void contextInitialized(ServletContextEvent sce) {
    // Code to be executed when the server starts
    System.out.println("Server started!");
    MongoConfig mongoConfig = MongoConfig.getInstance();
  }
}
