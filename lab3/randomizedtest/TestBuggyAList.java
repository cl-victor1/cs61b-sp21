package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import net.sf.saxon.functions.ConstantFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void testThreeAddThreeRemove() {
      BuggyAList<Integer> buggy = new BuggyAList<>();
      AListNoResizing<Integer> list = new AListNoResizing<>();
      buggy.addLast(4);
      buggy.addLast(5);
      buggy.addLast(6);
      list.addLast(4);
      list.addLast(5);
      list.addLast(6);

      int first1 = buggy.removeLast();
      int first2 = list.removeLast();
      int second1 = buggy.removeLast();
      int second2 = list.removeLast();
      int third1 = buggy.removeLast();
      int third2 = list.removeLast();

      assertEquals(first1, first2);
      assertEquals(second1, second2);
      assertEquals(third1, third2);
  }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> bug = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                bug.addLast(randVal);

            } else if (operationNumber == 1) {
                // size
                int size1 = L.size();
                int size2 = bug.size();
                assertEquals(size1, size2);

            } else if (operationNumber == 2 && L.size() > 0 && bug.size() > 0) {
                // getLast
                int getLast1 = L.getLast();
                int getLast2 = bug.getLast();
                assertEquals(getLast1, getLast2);

            } else if (operationNumber == 3 && L.size() > 0 && bug.size() > 0) {
                // removeLast
                int removeLast1 = L.removeLast();
                int removeLast2 = bug.removeLast();
                assertEquals(removeLast1, removeLast2);

            }
        }
    }
}

