package com.videochat.videochatrecommendation2.util;

import java.util.Objects;

public class BiPair<T> {
    private final T first;
    private final T second;

    public BiPair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public static <T> BiPair<T> of(T first, T second) {
        return new BiPair<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BiPair<?> biPair = (BiPair<?>) o;
        return (Objects.equals(first, biPair.first) && Objects.equals(second, biPair.second))
                || (Objects.equals(first, biPair.second) && Objects.equals(second, biPair.first));
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second) + Objects.hash(second, first);
    }

    @Override
    public String toString() {
        return "BiPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}

