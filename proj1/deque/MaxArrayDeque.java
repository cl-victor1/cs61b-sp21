package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> maxArrayDequeComparator;

    public MaxArrayDeque(Comparator<T> c) {
        maxArrayDequeComparator = c;
    }

    /** Returns the maximum element in the deque using class comparator */
    public T max() {
        return max(maxArrayDequeComparator);
    }

    /** Returns the maximum element in the deque using passed comparator */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        int maxIndex = 0;
        for (int i = 0; i < size(); i++) {
            if (c.compare(get(i), get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }
}
