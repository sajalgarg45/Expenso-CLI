package expensetracker.datastructures;

import java.util.Iterator;
import java.util.function.Function;

public class LinkedList<T> implements Iterable<T> {
    private class Node { T val; Node next; Node(T v){val=v;} }
    private Node head; private int size;

    public void add(T v) {
        Node n = new Node(v); n.next = head; head = n; size++;
    }
    public void remove(T v) {
        if (head==null) return;
        if (head.val.equals(v)) { head = head.next; size--; return; }
        Node prev = head, cur = head.next;
        while (cur!=null) {
            if (cur.val.equals(v)) { prev.next = cur.next; size--; return; }
            prev = cur; cur = cur.next;
        }
    }
    public int size() { return size; }
    public T getAt(int idx) {
        Node cur = head;
        for (int i = size - 1; i > idx; i--) cur = cur.next;
        return cur.val;
    }
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node cur = head;
            public boolean hasNext() { return cur!=null; }
            public T next() { T v=cur.val; cur=cur.next; return v; }
        };
    }
    public double sum(Function<T,Double> f) {
        double s=0; for (T v:this) s+=f.apply(v); return s;
    }
}