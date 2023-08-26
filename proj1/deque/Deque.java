package deque;

import java.util.Iterator;

public interface Deque<T>  {
    void addFirst(T T);

    void addLast(T T);

    T removeFirst();

    T removeLast();

    default boolean isEmpty() {
        return size() == 0;
    };

    int size();

    void printDeque();

    T get(int position);


    default Iterator<T> iterator() {
        return null;
    };
}
