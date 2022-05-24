import java.util.LinkedList;

public class Node {
    
    public static int counter = 0;
    public int id;
    public String value;
    public String type;
    public LinkedList<Node> children;
    public Node parent;
    public int scopeID = -1;

    Node(String v){
        id = counter++;
        value = v;
        children = new LinkedList<>();
    }

    Node(String v, String t){
        id = counter++;
        value = v;
        type = t;
        children = new LinkedList<>();
    }

    public void addChild(Node c){
        children.add(c);
        c.addParent(this);
    }

    public void addParent(Node p){
        parent = p;
    }

    public void setScopeID(int sID){
        scopeID = sID;
    }

    public int getScopeID(){
        return scopeID;
    }
}
