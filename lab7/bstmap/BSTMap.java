package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K, V>{
    private Node root;

    private class Node {
        private K key;
        private V value;
        private Node left;
        private Node right;
        private int size;
        public Node (K key, V value) {
            this.key = key;
            this.value = value;
            this.size = 0;
        }
        public Node() {
            this.size = 0;
        }
    }

    public BSTMap(){};


    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(Node n, K k) {
        if (n == null){
            return false;
        }
        if (k.equals(n.key)) {
            return true;
        }
        if (k.compareTo(n.key) < 0){
            return containsKey(n.left, k);
        }
        else {
            return containsKey(n.right, k);
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key){
        return get(root, key);
    }

    private V get(Node n, K k){
        if (n == null){
            return null;
        }
        if (k.equals(n.key)) {
            return n.value;
        }
        if (k.compareTo(n.key) < 0){
            return get(n.left, k);
        }
        else {
            return get(n.right, k);
        }
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size(){
        if (root == null) {
            return 0;
        }
        return root.size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value){
        root = put(root, key, value);
        root.size += 1;
    }

    private Node put(Node n, K k, V value){
        if (n == null){
            return new Node(k, value);
        }
        if (k.compareTo(n.key) < 0) {
            n.left = put(n.left, k, value);
        }
        else if (k.compareTo(n.key) > 0) {
            n.right = put(n.right, k, value);
        }
        return n;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet(){
        throw new UnsupportedOperationException("Unsupported");
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key){
        throw new UnsupportedOperationException("Unsupported");
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value){
        throw new UnsupportedOperationException("Unsupported");
    }
    @Override
    public Iterator<K> iterator(){
        throw new UnsupportedOperationException("Unsupported");
    }
}