package expensetracker.datastructures;

public class BinarySearchTree {
    private class Node {
        Comparable val;
        Node left, right;
        Node(Comparable v) { val = v; }
    }

    private Node root; // Root of the tree

    // Insert a value into the tree
    public void insert(Comparable v) {
        root = insertRec(root, v);
    }

    // Recursive helper for insertion
    private Node insertRec(Node n, Comparable v) {
        if (n == null) return new Node(v);
        if (v.compareTo(n.val) < 0) n.left = insertRec(n.left, v);
        else n.right = insertRec(n.right, v);
        return n;
    }

    // Check if a value exists in the tree
    public boolean contains(Comparable v) {
        return containsRec(root, v);
    }

    // Recursive helper for search
    private boolean containsRec(Node n, Comparable v) {
        if (n == null) return false;
        int c = v.compareTo(n.val);
        if (c == 0) return true;
        return c < 0 ? containsRec(n.left, v) : containsRec(n.right, v);
    }
}