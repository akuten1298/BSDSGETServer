package org.example;

import java.util.List;

public class MatchesResponse {
    List<String> matchList;

    public MatchesResponse(List<String> matchList) {
        this.matchList = matchList;
    }

    public List<String> getMatchList() {
        return matchList;
    }
}
