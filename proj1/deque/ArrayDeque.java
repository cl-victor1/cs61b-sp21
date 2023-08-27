package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst = 0;
    private int nextLast = 1;

    public ArrayDeque() {
        items = (T []) new Object[8];
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
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
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
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
    public T get(int pos) {
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
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if ((float)size / items.length < 0.25 && items.length >= 16) {
            resize(items.length / 2);
            nextFirst = items.length - 1;
            nextLast = size;
        }
        if (nextFirst + 1 >= items.length) {
            T first = items[0];
            size--;
            nextFirst = 0;
            return first;
        } else {
            T first = items[nextFirst + 1];
            size--;
            nextFirst++;
            return first;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if ((float)size / items.length < 0.25 && items.length >= 16) {
            resize(items.length / 2);
            nextFirst = items.length - 1;
            nextLast = size;
        }
        if (nextLast - 1 < 0) {
            T last = items[items.length - 1];
            size--;
            nextLast = items.length - 1;
            return last;
        } else {
            T last = items[nextLast - 1];
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
            if (index >= items.length) {
                index = 0;
                T first = items[index];
                System.out.println(first);
                printed++;
                index++;
            } else {
                T first = items[index];
                System.out.println(first);
                printed++;
                index++;
            }
        }
    }

    private void resize(int capacity) {
        T[] array = (T []) new Object[capacity];
        if (nextFirst < nextLast) {
            if (nextLast > size) {
                System.arraycopy(items, nextFirst + 1, array, 0, size);
            }
            else {
                System.arraycopy(items, nextFirst + 1, array, 0, size - nextLast);
                System.arraycopy(items, 0, array, size - nextLast, nextLast);
            }
        }
        else if (nextFirst >= nextLast) {
            if (nextFirst < items.length - 1) {
                System.arraycopy(items, nextFirst + 1, array, 0, size - nextLast);
                System.arraycopy(items, 0, array, size - nextLast, nextLast);
            }
            else {
                System.arraycopy(items, 0, array, 0, size);
            }
        }
        items = array;
    }

    private class ArrayIterator implements Iterator<T> {
        private int pos;
        public ArrayIterator() {
            pos = 0;
        }
        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            T returnItem = items[pos];
            pos += 1;
            return returnItem;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        Deque<T> other = (ArrayDeque<T>) o;
        Deque<T> that = this;

        if (that.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < that.size(); i += 1) {
            if (!that.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}
