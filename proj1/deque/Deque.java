package deque;

public interface Deque<Pig>  {
    void addFirst(Pig pig);

    void addLast(Pig pig);

    Pig removeFirst();

    Pig removeLast();

    default boolean isEmpty() {
        return size() == 0;
    };

    int size();

    void printDeque();

    Pig get(int position);
}
