import java.util.LinkedList;

public class Node {
    
    public static int counter = 0;
    public int id;
    public String value;
    public String type;
    public LinkedList<Node> children;

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
    }
}
