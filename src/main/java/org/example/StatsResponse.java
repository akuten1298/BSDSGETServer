package org.example;

public class StatsResponse {
    private int numLikes;
    private int numDislikes;

    public StatsResponse() {
        this.numLikes = 0;
        this.numDislikes = 0;
    }
    public StatsResponse(int numLikes, int numDislikes) {
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumDislikes() {
        return numDislikes;
    }

}
