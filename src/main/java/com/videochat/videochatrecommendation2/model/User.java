package com.videochat.videochatrecommendation2.model;

import java.util.List;
import java.util.Objects;

public record User(String username, Gender myGender, Gender preferredGender,
                   List<Interests> interests) implements Comparable<User> {
    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, myGender, preferredGender, interests);
    }
}
