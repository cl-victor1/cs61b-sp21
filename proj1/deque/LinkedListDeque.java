package deque;


public class LinkedListDeque<Pig> implements Deque<Pig> {
    private Node sentinel = new Node();
    private int size;
    public class Node {
        public Pig item;
        public Node next;
        public Node prev;
        public Node (){
        }
        public Node (Pig object){
            this.item = object;
        }
        public Node (Pig object, Node n) {
            this.item = object;
            this.next = n;
        }
    }

    public LinkedListDeque() {
    }
    @Override
    public void addFirst(Pig pig) {
        Node item = new Node(pig);
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
    public void addLast(Pig pig) {
        Node item = new Node(pig);
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
    public Pig removeFirst() {
        if (sentinel.next == null) {
            return null;
        }
        Node first = sentinel.next;
        Pig value = first.item;
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
    public Pig removeLast() {
        if (sentinel.prev == null) {
            return null;
        }
        Node last = sentinel.prev;
        Pig value = last.item;
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
    public Pig get(int position) {
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

    Node index = sentinel;
    public Pig getRecursive(int pos) {
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
}
