package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConfig {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private static MongoConfig instance = null;
    private static String primaryEC2IP = "35.90.20.84";


    public static MongoConfig getInstance() {
        if(instance == null)
            instance = new MongoConfig();
        return instance;
    }

    private MongoConfig() {
        ConnectionString connectionString = new ConnectionString("mongodb://admin:password@" + primaryEC2IP + ":27017/?maxPoolSize=50");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .readPreference(ReadPreference.secondaryPreferred()) // set the read preference to secondary preferred
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("twinder");

    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
