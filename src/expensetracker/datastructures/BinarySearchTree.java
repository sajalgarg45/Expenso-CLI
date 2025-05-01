package expensetracker.datastructures;

public class BinarySearchTree {
    private class Node { Comparable val; Node left,right; Node(Comparable v){val=v;} }
    private Node root;
    public void insert(Comparable v){ root=insertRec(root,v); }
    private Node insertRec(Node n, Comparable v){
        if(n==null) return new Node(v);
        if(v.compareTo(n.val)<0) n.left=insertRec(n.left,v);
        else n.right=insertRec(n.right,v);
        return n;
    }
    public boolean contains(Comparable v){ return containsRec(root,v); }
    private boolean containsRec(Node n, Comparable v){
        if(n==null) return false;
        int c=v.compareTo(n.val);
        if(c==0) return true;
        return c<0 ? containsRec(n.left,v) : containsRec(n.right,v);
    }
}