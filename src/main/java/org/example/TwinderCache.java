package org.example;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class TwinderCache {
    private static int cacheCapacity = 100;
    private static int cacheExpirationTime = 5;

    private static Cache<String, Document> cache;

    public TwinderCache() {
        cache = Caffeine.newBuilder()
                .maximumSize(cacheCapacity)
                .expireAfterAccess(cacheExpirationTime, TimeUnit.MINUTES)
                .build();
    }

    public static void insertToCache(String userID, Document doc) {
        cache.put(userID, doc);
    }

    public static Document retrieveFromCache(String userID) {
        Document doc = cache.getIfPresent(userID);
        return doc;
    }

    public static Cache<String, Document> getCache() {
        return cache;
    }

    public static void emptyCacheIfFull() {
        if (cache.estimatedSize() > cacheCapacity) {
            cache.invalidateAll();
        }
    }
}
