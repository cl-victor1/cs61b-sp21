package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private Node sentinel = new Node();
    private int size;

    private class Node {
        private T item;
        private Node next;
        private Node prev;
        public Node () {}
        public Node (T object) {
            this.item = object;
        }
        public Node (T object, Node n) {
            this.item = object;
            this.next = n;
        }
    }

    public LinkedListDeque() {
    }

    @Override
    public void addFirst(T T) {
        Node item = new Node(T);
        Node temp = sentinel.next;
        sentinel.next = item;
        item.prev = sentinel;
        if (temp != null) {
            item.next = temp;
            temp.prev = item;
        } else {
            item.next = sentinel;
            sentinel.prev = item;
        }
        this.size++;
    }

    @Override
    public void addLast(T T) {
        Node item = new Node(T);
        if (sentinel.prev == null) {
            sentinel.prev = item;
            item.next = sentinel;
            sentinel.next = item;
            item.prev = sentinel;
        } else {
            Node temp = sentinel.prev;
            sentinel.prev = item;
            item.prev = temp;
            temp.next = item;
            item.next = sentinel;
        }
        this.size++;
    }

    @Override
    public T removeFirst() {
        if (sentinel.next == null) {
            return null;
        }
        Node first = sentinel.next;
        T value = first.item;
        if (first.next != sentinel) {
            first.prev = null;
            sentinel.next = first.next;
            first.next = null;
            sentinel.next.prev = sentinel;
        } else {
            first.prev = null;
            first.next = null;
            sentinel.next = null;
            sentinel.prev = null;
        }
        this.size--;
        return value;
    }

    @Override
    public T removeLast() {
        if (sentinel.prev == null) {
            return null;
        }
        Node last = sentinel.prev;
        T value = last.item;
        if (last.prev != sentinel) {
            last.next = null;
            sentinel.prev = last.prev;
            last.prev = null;
            sentinel.prev.next = sentinel;
        } else {
            last.prev = null;
            last.next = null;
            sentinel.next = null;
            sentinel.prev = null;
        }
        this.size--;
        return value;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void printDeque() {
        int index = 0;
        Node start = sentinel;
        while (index < size) {
            System.out.println(start.next.item);
            start = start.next;
            index++;
        }
    }

    @Override
    public T get(int position) {
        if (position >= size()) {
            return null;
        }
        int index = 0;
        Node start = sentinel.next;
        while (index < position) {
            start = start.next;
            index++;
        }
        return start.item;
    }

    //pos自后向前，index自前向后。
    private Node index = sentinel;
    public T getRecursive(int pos) {
        if (pos >= size()) {
            return null;
        }
        if (pos == 0) {
            //初始化index
            Node temp = index;
            index = sentinel;
            return temp.next.item;
        } else {
            index = index.next;
            return getRecursive(pos - 1);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<T> {
        private Node current = sentinel.next;
        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T returnItem = current.item;
            current = current.next;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> other = (Deque<T>) o;
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
