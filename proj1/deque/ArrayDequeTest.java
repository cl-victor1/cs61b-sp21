package deque;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void removeFirst() {
        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        lld1.addFirst("111");
        lld1.addFirst("222");
        lld1.addFirst("333");
        lld1.addFirst("444");
        lld1.addFirst("555");
        lld1.addFirst("666");
        lld1.addFirst("777");
        lld1.addFirst("888");
        lld1.removeLast();

        assertEquals("111", lld1.get(4));
    }
}
