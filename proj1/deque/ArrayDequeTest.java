package deque;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void removeFirst() {
        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        lld1.addLast("111");
        lld1.addLast("222");
        lld1.addLast("333");
        lld1.addLast("444");

        assertEquals("111", lld1.removeFirst());
    }
}
