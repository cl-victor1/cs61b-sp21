package deque;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayDeque<Pig> implements Deque<Pig> {
    private Pig[] items;
    private int size;
    private int nextFirst = 4;
    private int nextLast = 5;
    public ArrayDeque() {
        items = (Pig []) new Object[8];
        size = 0;
    }
    @Override
    public void addFirst(Pig item) {
        if (size == items.length) {
            resize(size * 2);
            nextFirst = items.length - 1;
            nextLast = size;
        }
        items[nextFirst] = item;
        size++;
        if (nextFirst - 1 >= 0) {
            nextFirst--;
        } else {
            nextFirst = items.length - 1;
        }
    }
    @Override
    public void addLast(Pig item) {
        if (size == items.length) {
            resize(size * 2);
            nextFirst = items.length - 1;
            nextLast = size;
        }
        items[nextLast] = item;
        size++;
        if (nextLast + 1 < items.length) {
            nextLast++;
        } else {
            nextLast = 0;
        }
    }
    @Override
    public Pig get(int pos) {
        if (nextFirst + pos  + 1 < items.length) {
            return items[nextFirst + pos  + 1];
        } else {
            return items[nextFirst + pos  + 1 - items.length];
        }

    }
    @Override
    public int size() {
        return size;
    }

    @Override
    public Pig removeFirst() {
        if (size == 0) {
            return null;
        }
        /*if ((float)size / items.length < 0.25) {
            resize(size / 2);
            nextFirst = items.length - 1;
            nextLast = size;
        }*/
        if (nextFirst + 1 >= items.length) {
            Pig first = items[0];
            size--;
            nextFirst = 0;
            return first;
        } else {
            Pig first = items[nextFirst + 1];
            size--;
            nextFirst++;
            return first;
        }
    }
    @Override
    public Pig removeLast() {
        if (size == 0) {
            return null;
        }
        /*if ((float)size / items.length < 0.25) {
            resize(size / 2);
            nextFirst = items.length - 1;
            nextLast = size;
        }*/
        if (nextLast - 1 < 0) {
            Pig last = items[items.length - 1];
            size--;
            nextLast = items.length - 1;
            return last;
        } else {
            Pig last = items[nextLast - 1];
            size--;
            nextLast--;
            return last;
        }
    }
    @Override
    public void printDeque() {
        int index = nextFirst + 1;
        int printed = 0;
        while (printed < size) {
            if (nextFirst + 1 >= items.length) {
                Pig first = items[0];
                System.out.println(first);
                printed++;
                nextFirst = 0;
            } else {
                Pig first = items[nextFirst + 1];
                System.out.println(first);
                printed++;
                nextFirst++;
            }
        }
    }
    private void resize(int capacity) {
        Pig[] array = (Pig []) new Object[capacity];
        System.arraycopy(items, nextFirst + 1, array, 0, size - nextFirst - 1);
        System.arraycopy(items, 0, array, size - nextFirst - 1, nextFirst + 1);
        items = array;
    }

    public Iterator<Pig> iterator() {
        return Arrays.stream(items).iterator();
    }

}
