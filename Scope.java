import java.util.LinkedList;

public class Scope {

    public Node root;
    int scopeControl;
    ScopeNode scopeInfo;
    LinkedList<Integer> scopeIDs;
    LinkedList<int[]> family;

    Scope(Node r){
        root = r;
        scopeControl = 0;
        scopeInfo = new ScopeNode(0);
        scopeIDs = new LinkedList<>();
        scopeIDs.add(0);
        family = new LinkedList<>();
    }

    public Node run(){
        int mainIndex = 0;
        for(int i = 0; i < root.children.size(); i++){
            if(root.children.get(i).value.equals("main")){
                mainIndex = i;
                i = root.children.size();
            }
        }
        //prior to main scoping
        for(int i = 0; i < mainIndex; i++){
            scope(root.children.get(i),scopeControl, scopeInfo);
        }
        //main scoping
        for(int i = mainIndex; i < root.children.size(); i++){
            mainScopeAssign(root.children.get(i));
        }

        return root;
    }

    public ScopeNode scopeHierachy(){
        /*
        System.out.println("Family print:");
        for(int i = 0; i < family.size(); i++){
            System.out.println(family.get(i)[0] + " " + family.get(i)[1]);
        }
        */
        for(int i = 0; i < family.size(); i++){
            buildFamily(scopeInfo, family.get(i));
        }
        return scopeInfo;
    }

    public void scope(Node n, int scope, ScopeNode sN){
        if(n.value.equals("ProcDefs")){
            scope++;
            scopeControl++;
            scope = scopeControl;
        }
        n.setScopeID(scope);
        ScopeNode c = new ScopeNode(scope);
        if(!scopeIDs.contains(scope)){
            //System.out.println(sN.scopeID + " " + c.scopeID);
            //sN.addChild(c);
            scopeIDs.add(scope);
            int[] collect = {sN.scopeID,c.scopeID};
            family.add(collect);
        }
        for (int i = 0; i < n.children.size(); i++) {
            scope(n.children.get(i),scope,c);
        }
    }

    public void buildFamily(ScopeNode n,int[] arr) {
        if(n.scopeID==arr[0]){
            n.addChild(new ScopeNode(arr[1]));
        }

        for (int i = 0; i < n.children.size(); i++) {
            buildFamily(n.children.get(i), arr);
        }
    }

    public void mainScopeAssign(Node n) {
        n.setScopeID(0);
        for (int i = 0; i < n.children.size(); i++) {
            mainScopeAssign(n.children.get(i));
        }
    }

    public void printTree(Node n, String indent, boolean last) {
        System.out.println(indent + "+- " + n.value);
        indent += last ? "   " : "|  ";

        for (int i = 0; i < n.children.size(); i++) {
            printTree(n.children.get(i), indent, i == n.children.size() - 1);
        }
    }
}
