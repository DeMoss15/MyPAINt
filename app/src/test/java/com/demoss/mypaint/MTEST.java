package com.demoss.mypaint;

import org.junit.Test;

public class MTEST {

    @Test
    public void runTest() {
        String[] q = {"a", "b", "b", "b", "c"};
        System.out.println(getSameNeighborsCount(q));
    }

    int getSameNeighborsCount(String[] q) {
        if (q.length == 0 || q.length == 1) return q.length;
        int counter = 0;
        if (q[0].equals(q[1])) counter++;
        if (q[q.length - 2].equals(q[q.length - 1])) counter++;
        for (int i = 1; i < q.length - 2; i++) {
            if (q[i - 1].equals(q[i]) && q[i].equals(q[i + 1])) counter++;
        }
        return counter;
    }
}
