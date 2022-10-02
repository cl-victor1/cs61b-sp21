package deque;

public class ArrayDeque<Pig> {
    private Pig[] items;
    private int size;
    public int nextFirst = 4;
    public int nextLast = 5;
    public ArrayDeque() {
        items = (Pig []) new Object[8];
        size = 0;
    }
    public void addFirst(Pig item) {
        items[nextFirst] = item;
        size++;
        if (nextFirst - 1 >= 0) {
            nextFirst--;
        } else {
            nextFirst = items.length - 1;
        }
    }
    public void addLast(Pig item) {
        items[nextLast] = item;
        size++;
        if (nextLast + 1 < items.length) {
            nextLast++;
        } else {
            nextLast = 0;
        }
    }
    public Pig get(int pos) {
        return items[pos];
    }

    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size() == 0;
    }
    public Pig removeFirst() {
        Pig first = items[nextFirst + 1];
        size--;
        return first;
    }
    public Pig removeLast() {
        Pig last = items[nextLast - 1];
        size--;
        return last;
    }
}
