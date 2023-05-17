package com.videochat.videochatrecommendation2.model;

public record MatchPeer(int matchScore, User peer) implements Comparable<MatchPeer> {

    /**
     * Comparison of two MatchPeer objects by matchScore in descending order.
     */
    @Override
    public int compareTo(MatchPeer o) {
        return Integer.compare(o.matchScore, matchScore);
    }
}
