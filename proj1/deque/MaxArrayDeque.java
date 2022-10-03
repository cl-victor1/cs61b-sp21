package deque;

import java.util.Comparator;

public class MaxArrayDeque<Pig> extends ArrayDeque<Pig> {

    private Comparator<Pig> MaxArrayDequeComparator;

    public MaxArrayDeque(Comparator<Pig> c) {

        MaxArrayDequeComparator = c;
    }

    /** Returns the maximum element in the deque using class comparator */
    public Pig max() {
        return max(MaxArrayDequeComparator);
    }

    /** Returns the maximum element in the deque using passed comparator */
    public Pig max(Comparator<Pig> c) {
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