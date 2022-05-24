import java.util.LinkedList;

public class ScopeNode {
    public int scopeID;
    public LinkedList<ScopeNode> children;

    ScopeNode(int s){
        scopeID = s;
        children = new LinkedList<>();
    }

    public void addChild(ScopeNode sn){
        children.add(sn);
    }
}
