package expensetracker.datastructures;

import java.util.Iterator;
import java.util.function.Function;

// A generic singly linked list implementation
public class LinkedList<T> implements Iterable<T> {
    private class Node {
        T val;
        Node next;
        Node(T v) { val = v; }
    }

    private Node head;
    private int size;

    // Add a new element to the front of the list
    public void add(T v) {
        Node n = new Node(v);
        n.next = head;
        head = n;
        size++;
    }

    // Remove the first occurrence of the specified value
    public void remove(T v) {
        if (head == null) return;
        if (head.val.equals(v)) {
            head = head.next;
            size--;
            return;
        }
        // Traverse the list to find the value
        Node prev = head, cur = head.next;
        while (cur != null) {
            if (cur.val.equals(v)) {
                prev.next = cur.next;
                size--;
                return;
            }
            prev = cur;
            cur = cur.next;
        }
    }

    // Get the number of elements in the list
    public int size() {
        return size;
    }

    // Get the value at the specified index (0-based)
    public T getAt(int idx) {
        Node cur = head;
        for (int i = size - 1; i > idx; i--) {
            cur = cur.next;
        }
        return cur.val;
    }

    // Provide an iterator to traverse the list
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node cur = head;
            public boolean hasNext() { return cur != null; }
            public T next() {
                T v = cur.val;
                cur = cur.next;
                return v;
            }
        };
    }

    // Compute the sum of values in the list using a custom function
    public double sum(Function<T, Double> f) {
        double s = 0;
        for (T v : this) {
            s += f.apply(v);
        }
        return s;
    }
}