package expensetracker.datastructures;

/**
 * Simple LIFO stack with its own Node chain; no built-ins
 */
public class Stack {
    private class Node {
        Object val;
        Node next;

        Node(Object v) {
            val = v;
        }
    }

    private Node top;

    public void push(Object v) {
        Node n = new Node(v);
        n.next = top;
        top = n;
    }

    public Object pop() {
        if (top == null) throw new RuntimeException("Stack is empty");
        Object v = top.val;
        top = top.next;
        return v;
    }

    public Object peek() {
        if (top == null) throw new RuntimeException("Stack is empty");
        return top.val;
    }

    public boolean isEmpty() {
        return top == null;
    }
}
