package expensetracker.datastructures;

public class Stack {
    private class Node {
        Object val;
        Node next;
        Node(Object v) { val = v; }
    }
    private Node top;

    // Push a value onto the stack
    public void push(Object v) {
        Node n = new Node(v);
        n.next = top;
        top = n;
    }

    // Pop the top value from the stack
    public Object pop() {
        if (top == null) throw new RuntimeException("Stack empty");
        Object v = top.val;
        top = top.next;
        return v;
    }

    // Peek at the top value without removing it
    public Object peek() {
        if (top == null) throw new RuntimeException("Stack empty");
        return top.val;
    }

    // Check if the stack is empty
    public boolean isEmpty() {
        return top == null;
    }
}