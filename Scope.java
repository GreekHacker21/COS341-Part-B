public class Scope {

    public Node root;
    int scopeControl;

    Scope(Node r){
        root = r;
        scopeControl = 0;
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
            scope(root.children.get(i),scopeControl);
        }
        //main scoping
        for(int i = mainIndex; i < root.children.size(); i++){
            mainScopeAssign(root.children.get(i));
        }

        return root;
    }

    public void scope(Node n, int scope){
        if(n.value.equals("ProcDefs")){
            scope++;
            scopeControl++;
        }
        n.setScopeID(scope);
        for (int i = 0; i < n.children.size(); i++) {
            scope(n.children.get(i),scope);
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
