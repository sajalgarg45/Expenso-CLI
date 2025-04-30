package expensetracker.datastructures;

/**
 * Simple FIFO queue with its own Node chain; no built-ins
 */
public class Queue {
    private class Node {
        Object val;
        Node next;

        Node(Object v) {
            val = v;
        }
    }

    private Node head;
    private Node tail;

    public void enqueue(Object v) {
        Node n = new Node(v);
        if (tail != null) tail.next = n;
        else head = n;
        tail = n;
    }

    public Object dequeue() {
        if (head == null) throw new RuntimeException("Queue is empty");
        Object v = head.val;
        head = head.next;
        if (head == null) tail = null;
        return v;
    }

    public Object peek() {
        if (head == null) throw new RuntimeException("Queue is empty");
        return head.val;
    }

    public boolean isEmpty() {
        return head == null;
    }
}
