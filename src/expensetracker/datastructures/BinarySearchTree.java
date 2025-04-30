package expensetracker.datastructures;

/**
 * Basic unbalanced BST; raw Comparable nodes
 */
public class BinarySearchTree {
    private class Node {
        Comparable val;
        Node left, right;

        Node(Comparable v) {
            val = v;
        }
    }

    private Node root;

    public void insert(Comparable v) {
        root = insertRec(root, v);
    }

    private Node insertRec(Node node, Comparable v) {
        if (node == null) return new Node(v);
        if (v.compareTo(node.val) < 0)
            node.left = insertRec(node.left, v);
        else
            node.right = insertRec(node.right, v);
        return node;
    }

    public void inorder() {
        inorderRec(root);
    }

    private void inorderRec(Node node) {
        if (node == null) return;
        inorderRec(node.left);
        System.out.println(node.val);
        inorderRec(node.right);
    }

    public boolean contains(Comparable v) {
        return containsRec(root, v);
    }

    private boolean containsRec(Node node, Comparable v) {
        if (node == null) return false;
        int cmp = v.compareTo(node.val);
        if (cmp == 0) return true;
        return cmp < 0
                ? containsRec(node.left, v)
                : containsRec(node.right, v);
    }
}
