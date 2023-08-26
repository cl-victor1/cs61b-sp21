package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private Node sentinel = new Node();
    private int size;

    public class Node {
        private T item;
        private Node next;
        private Node prev;
        public Node (){
        }
        public Node (T object){
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

    public T getRecursive(int pos) {
        Node index = sentinel;
        if (pos >= size()) {
            return null;
        }
        if (pos == 0) {
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
        private Node current = sentinel;
        @Override
        public boolean hasNext() {
            return current.next != null;
        }

        @Override
        public T next() {
            T returnItem = current.next.item;
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
        if (this.getClass() != o.getClass()){
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (this.size() != other.size()) {
            return false;
        }
        Node index1 = this.sentinel;
        Node index2 = other.sentinel;
        while (index1.next != null) {
            if (index1.next.item != index2.next.item) {
                return false;
            }
            index1 = index1.next;
            index2 = index2.next;
        }
        return true;
    }

}
