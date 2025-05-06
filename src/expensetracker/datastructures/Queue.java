package expensetracker.datastructures;

public class Queue {
    // Node class to represent each element in the queue
    private class Node {
        Object val;
        Node next;
        Node(Object v) { val = v; }
    }

    private Node head, tail;

    // Enqueue a value into the queue (add to the end)
    public void enqueue(Object v) {
        Node n = new Node(v);
        if (tail != null) tail.next = n;
        else head = n;
        tail = n;
    }

    // Dequeue a value from the queue (remove from the front)
    public Object dequeue() {
        if (head == null) throw new RuntimeException("Queue empty");
        Object v = head.val;
        head = head.next;
        if (head == null) tail = null;
        return v;
    }

    // Peek at the front value without removing it
    public Object peek() {
        if (head == null) throw new RuntimeException("Queue empty");
        return head.val;
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        return head == null;
    }
}