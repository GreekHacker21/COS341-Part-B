import java.util.LinkedList;

public class SemanticRules {

    public Node root;
    public ScopeNode scopeInfo;
    public SymbolTable ProcedureTable;
    public SymbolTable VariableTable;
    public LinkedList<Node> leafNodes;
    public ScopeNode currentScope;

    SemanticRules(Node r, ScopeNode sI, LinkedList<Node> lN) {
        root = r;
        scopeInfo = sI;
        ProcedureTable = new SymbolTable();
        VariableTable = new SymbolTable();
        leafNodes = lN;
    }

    public void analysis() {
        try {
            Procedures();
            Variables();
            TypeChecking();
            ValueFlowAnalysis();
        } catch (SemanticError error) {
            System.out.println("SEMANTIC ERROR");
            System.out.println(error.getMessage());
        }

    }

    public void Procedures() throws SemanticError {
        LinkedList<Integer> indexes = new LinkedList<>();
        for (int i = 0; i < leafNodes.size(); i++) {
            if (leafNodes.get(i).value.equals("proc")) {
                indexes.add(i + 1);
            }
        }
        // check if any are named main
        for (int i = 0; i < indexes.size(); i++) {
            if (leafNodes.get(indexes.get(i)).value.equals("main")) {
                throw new SemanticError("There is a procedure named main");
            }
        }
        // child proc declaration may not have same name as parent
        for (int i = 0; i < indexes.size(); i++) {
            searchScope(leafNodes.get(indexes.get(i)).scopeID);
            LinkedList<ScopeNode> childScopes = currentScope.children;
            for (int j = 0; j < indexes.size(); j++) {
                for (int k = 0; k < childScopes.size(); k++) {
                    if (leafNodes.get(indexes.get(i)).value.equals(leafNodes.get(indexes.get(j)).value)
                            && (childScopes.get(k).scopeID == leafNodes.get(indexes.get(j)).scopeID)) {
                        throw new SemanticError("There is a procedure parents with the same name as a its child");
                    }
                }
            }
        }
        // no duplicate names in the same scope
        for (int i = 0; i < indexes.size(); i++) {
            for (int j = 0; j < indexes.size(); j++) {
                if (leafNodes.get(indexes.get(i)).value.equals(leafNodes.get(indexes.get(j)).value)
                        && (leafNodes.get(indexes.get(i)).scopeID == leafNodes.get(indexes.get(j)).scopeID)
                        && (i != j)) {
                    throw new SemanticError(
                            "There are two procedures within the same scope that have an identical name");
                }
            }
        }
        // a proc can call itself and/or child proc's SET UP for Part 1 and Part 2
        LinkedList<Boolean> procIsUsed = new LinkedList<>();
        LinkedList<Integer> indexesForCalls = new LinkedList<>();
        LinkedList<Boolean> callHasProc = new LinkedList<>();
        for (int i = 0; i < leafNodes.size(); i++) {
            if (leafNodes.get(i).value.equals("call")) {
                indexesForCalls.add(i + 1);
                callHasProc.add(false);
            }
        }
        for (int i = 0; i < indexes.size(); i++) {
            procIsUsed.add(false);
        }
        // Part 1 - every proc has a call
        for (int i = 0; i < indexes.size(); i++) {
            searchScope(leafNodes.get(indexes.get(i)).scopeID);
            LinkedList<ScopeNode> childScopes = currentScope.children;
            for (int j = 0; j < indexesForCalls.size(); j++) {
                if (leafNodes.get(indexes.get(i)).value.equals(leafNodes.get(indexesForCalls.get(j)).value)
                        && (leafNodes.get(indexes.get(i)).scopeID == leafNodes.get(indexesForCalls.get(j)).scopeID)) {
                    procIsUsed.set(i, true);
                }
                for (int k = 0; k < childScopes.size(); k++) {
                    if (leafNodes.get(indexes.get(i)).value.equals(leafNodes.get(indexesForCalls.get(j)).value)
                            && (childScopes.get(k).scopeID == leafNodes.get(indexesForCalls.get(j)).scopeID)) {
                        procIsUsed.set(i, true);
                    }
                }
            }
        }
        if (procIsUsed.contains(false)) {
            for (int i = 0; i < procIsUsed.size(); i++) {
                if (!procIsUsed.get(i)) {
                    throw new SemanticError("(DECL-APPL error): proc " + leafNodes.get(indexes.get(i)).value + " is not used");
                }
            }
        }
        // Part 2 - every call has an appropriate proc
        for (int i = 0; i < indexesForCalls.size(); i++) {
            for (int j = 0; j < indexes.size(); j++) {
                searchScope(leafNodes.get(indexes.get(j)).scopeID);
                LinkedList<ScopeNode> childScopes = currentScope.children;
                if (leafNodes.get(indexesForCalls.get(i)).value.equals(leafNodes.get(indexes.get(j)).value)
                        && (leafNodes.get(indexesForCalls.get(i)).scopeID == leafNodes.get(indexes.get(j)).scopeID)) {
                    callHasProc.set(i, true);
                }
                for (int k = 0; k < childScopes.size(); k++) {
                    if (leafNodes.get(indexesForCalls.get(i)).value.equals(leafNodes.get(indexes.get(j)).value)
                            && (childScopes.get(k).scopeID == leafNodes.get(indexesForCalls.get(i)).scopeID)) {
                        callHasProc.set(i, true);
                    }
                }
            }
        }
        if (callHasProc.contains(false)) {
            for (int i = 0; i < callHasProc.size(); i++) {
                if (!callHasProc.get(i)) {
                    throw new SemanticError("(APPL-DECL error): proc " + leafNodes.get(indexesForCalls.get(i)).value + " is not defined");
                }
            }
        }

        //procedure symbol table
        

    }

    public void searchScope(int scope) {
        searchScopeRecursive(scopeInfo, scope);
    }

    public void searchScopeRecursive(ScopeNode n, int scope) {
        if (n.scopeID == scope) {
            currentScope = n;
        }

        for (int i = 0; i < n.children.size(); i++) {
            searchScopeRecursive(n.children.get(i), scope);
        }
    }

    public void Variables() throws SemanticError {

    }

    public void TypeChecking() throws SemanticError {

    }

    public void ValueFlowAnalysis() throws SemanticError {

    }

}