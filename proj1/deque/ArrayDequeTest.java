package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void getFirst() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addFirst(3);
        lld1.addFirst(4);
        lld1.addFirst(5);
        lld1.addFirst(6);
        lld1.addFirst(7);
        lld1.addFirst(8);
        lld1.addLast(9);

        assertEquals(1, (long)lld1.get(8));
    }

    @Test
    public void printDequeTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addFirst(1);
        lld1.addFirst(2);
        lld1.addFirst(3);
        lld1.addFirst(4);
        lld1.addFirst(5);
        lld1.addFirst(6);
        lld1.addFirst(7);
        lld1.addFirst(8);
        lld1.addFirst(9);
        lld1.addLast(10);
        lld1.printDeque();
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> lld2 = new ArrayDeque<Integer>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                lld2.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = lld2.size();
                System.out.println(size);
            } else if (operationNumber == 2) {
                // removeFirst
                lld2.removeFirst();
            } else if (operationNumber == 3) {
                // removeLast
                lld2.removeLast();
            } else if (operationNumber == 4) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                lld2.addFirst(randVal);
            }
        }
    }
}
