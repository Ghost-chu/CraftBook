package com.mcsunnyside.craftbooklimiter;

public class Count {
    int count = 0;

    public int grow() {
        return count++;
    }

    public int decay() {
        return count--;
    }

    public int get() {
        return count;
    }

    public boolean reach(int limit) {
        return count >= limit;
    }
}
