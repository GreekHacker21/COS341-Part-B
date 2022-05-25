import java.util.LinkedList;

public class SemanticRules {

    public Node root;
    public ScopeNode scopeInfo;
    public SymbolTable ProcedureTable;
    public SymbolTable VariableTable;
    public LinkedList<Node> leafNodes;
    public ScopeNode currentScope;
    public int parentScopeID;
    public boolean isInChildScope;
    public SymbolTableNode parentChecker;

    SemanticRules(Node r, ScopeNode sI, LinkedList<Node> lN) {
        root = r;
        scopeInfo = sI;
        ProcedureTable = new SymbolTable();
        VariableTable = new SymbolTable();
        leafNodes = lN;
    }

    public int analysis() {
        try {
            Procedures();
            Variables();
            TypeChecking();
            ValueFlowAnalysis();
            return 0;
        } catch (SemanticError error) {
            System.out.println("\nSEMANTIC ERROR");
            System.out.println(error.getMessage());
            return 1;
        }

    }

    public void Procedures() throws SemanticError {
        LinkedList<Integer> indexes = new LinkedList<>();
        for (int i = 0; i < leafNodes.size(); i++) {
            if (leafNodes.get(i).value.equals("proc")) {
                indexes.add(i + 2);
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
                        throw new SemanticError("There is a procedure parents with the same name as a its child ("
                                + leafNodes.get(indexes.get(i)).value + ")");
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
                indexesForCalls.add(i + 2);
                callHasProc.add(false);
            }
        }
        for (int i = 0; i < indexes.size(); i++) {
            procIsUsed.add(false);
        }
        // putting a procs in the table
        for (int i = 0; i < indexes.size(); i++) {
            searchParentScope(leafNodes.get(indexes.get(i)).scopeID);
            ProcedureTable
                    .addRow(new SymbolTableNode(leafNodes.get(indexes.get(i)).id, leafNodes.get(indexes.get(i)).scopeID,
                            leafNodes.get(indexes.get(i)).value, parentScopeID, "proc"));
        }
        // putting all proc calls in the table
        for (int i = 0; i < indexesForCalls.size(); i++) {
            searchParentScope(leafNodes.get(indexesForCalls.get(i)).scopeID);
            ProcedureTable.addRow(new SymbolTableNode(leafNodes.get(indexesForCalls.get(i)).id,
                    leafNodes.get(indexesForCalls.get(i)).scopeID,
                    leafNodes.get(indexesForCalls.get(i)).value, parentScopeID, "call"));
        }
        LinkedList<SymbolTableNode> procs = ProcedureTable.requestType("proc");
        LinkedList<SymbolTableNode> calls = ProcedureTable.requestType("call");
        // Part 1 - every proc has a call
        for (int i = 0; i < procs.size(); i++) {
            boolean error = true;
            for (int j = 0; j < calls.size(); j++) {
                if (procs.get(i).value.equals(calls.get(j).value)) {
                    if (procs.get(i).scopeID == calls.get(j).scopeID
                            || procs.get(i).parentScopeID == calls.get(j).scopeID) {
                        error = false;
                    }
                }
            }
            if (error) {
                throw new SemanticError("(APPL-DECL error): " + procs.get(i).value + " is not used.");
            }
        }
        // Part 2 - every call has an appropriate proc
        for (int i = 0; i < calls.size(); i++) {
            boolean error = true;
            for (int j = 0; j < procs.size(); j++) {
                if (procs.get(j).value.equals(calls.get(i).value)) {
                    if (procs.get(j).scopeID == calls.get(i).scopeID
                            || procs.get(j).parentScopeID == calls.get(i).scopeID) {
                        error = false;
                    }
                }
            }
            if (error) {
                throw new SemanticError(
                        "(DECL-APPL error): " + calls.get(i).value + " is not declared within this scope call.");
            }
        }

    }

    public void searchScope(int scope) {
        searchScopeRecursive(scopeInfo, scope);
    }

    public void searchParentScope(int scope) {
        searchParentScopeRecursive(scopeInfo, scope);
    }

    public void searchScopeRecursive(ScopeNode n, int scope) {
        if (n.scopeID == scope) {
            currentScope = n;
        }

        for (int i = 0; i < n.children.size(); i++) {
            searchScopeRecursive(n.children.get(i), scope);
        }
    }

    public void searchParentScopeRecursive(ScopeNode n, int scope) {
        if (scope == 0) {
            parentScopeID = -1;
        }
        for (int i = 0; i < n.children.size(); i++) {
            if (n.children.get(i).scopeID == scope) {
                parentScopeID = n.scopeID;
            }
            searchParentScopeRecursive(n.children.get(i), scope);
        }
    }

    public void Variables() throws SemanticError {
        LinkedList<Integer> variableIndexes = new LinkedList<>();
        LinkedList<Integer> arrayIndexes = new LinkedList<>();
        for (int i = 0; i < leafNodes.size(); i++) {
            if (leafNodes.get(i).value.equals("arr")) {
                arrayIndexes.add(i);
            }
            if (leafNodes.get(i).value.equals("TYP") && !(leafNodes.get(i - 1).value.equals("arr"))) {
                variableIndexes.add(i);
            }
        }

        // VARIABLE CHECKS

        // Cannot be duplicate name within same scope
        for (int i = 0; i < variableIndexes.size(); i++) {
            for (int j = 0; j < variableIndexes.size(); j++) {
                Node left = leafNodes.get(variableIndexes.get(i) + 4);
                Node right = leafNodes.get(variableIndexes.get(j) + 4);
                if ((left.value.equals(right.value)) && (i != j) && (left.scopeID == right.scopeID)) {
                    throw new SemanticError("(conflicting declaration): " + left.value
                            + " has multiple declarations within the same scope");
                }
            }
        }

        // ARRAY CHECKS

        // Cannot be duplicate name within same scope
        for (int i = 0; i < arrayIndexes.size(); i++) {
            for (int j = 0; j < arrayIndexes.size(); j++) {
                Node left = leafNodes.get(arrayIndexes.get(i) + 9);
                Node right = leafNodes.get(arrayIndexes.get(j) + 9);
                if ((left.value.equals(right.value)) && (i != j) && (left.scopeID == right.scopeID)) {
                    throw new SemanticError("(conflicting declaration): " + left.value
                            + " has multiple declarations within the same scope");
                }
            }
        }

        // Adding array and variable to variable table
        for (int i = 0; i < variableIndexes.size(); i++) {
            searchParentScope(leafNodes.get(variableIndexes.get(i) + 4).scopeID);
            VariableTable.addRow(new SymbolTableNode(leafNodes.get(variableIndexes.get(i) + 4).id,
                    leafNodes.get(variableIndexes.get(i) + 4).scopeID, leafNodes.get(variableIndexes.get(i) + 4).value,
                    parentScopeID, leafNodes.get(variableIndexes.get(i) + 1).value, true, false));
        }
        for (int i = 0; i < arrayIndexes.size(); i++) {
            searchParentScope(leafNodes.get(arrayIndexes.get(i) + 9).scopeID);
            VariableTable.addRow(new SymbolTableNode(leafNodes.get(arrayIndexes.get(i) + 9).id,
                    leafNodes.get(arrayIndexes.get(i) + 9).scopeID, leafNodes.get(arrayIndexes.get(i) + 9).value,
                    parentScopeID, leafNodes.get(arrayIndexes.get(i) + 2).value, true, true));
        }
        // Finding the var and field uses
        LinkedList<Integer> variableUseIndexes = new LinkedList<>();
        LinkedList<Integer> arrayUseIndexes = new LinkedList<>();
        for (int i = 0; i < leafNodes.size(); i++) {
            if (leafNodes.get(i).value.equals("LHS") || leafNodes.get(i).value.equals("Expr")) {
                i++;
                if (leafNodes.get(i).value.equals("Var")) {
                    variableUseIndexes.add(i);
                }
                if (leafNodes.get(i).value.equals("Field")) {
                    arrayUseIndexes.add(i);
                }
            }
        }
        // System.out.println("count for var: " + variableUseIndexes.size() + " count
        // for arr: " + arrayUseIndexes.size());
        // Var and Field check if the declaration is in the same scope as usage
        LinkedList<Integer> cleanup = new LinkedList<>();
        for (int i = 0; i < variableUseIndexes.size(); i++) {
            Node temp = leafNodes.get(variableUseIndexes.get(i) + 2);
            searchParentScope(temp.scopeID);
            if (VariableTable.lookup(temp.scopeID, temp.value, true, false)) {
                LinkedList<SymbolTableNode> connections = VariableTable.lookupNodes(temp.scopeID, temp.value, true,
                        false);
                VariableTable.addRow(new SymbolTableNode(temp.id, temp.scopeID, temp.value, parentScopeID, temp.type,
                        false, false, connections.get(0).nodeID));
                cleanup.add(variableUseIndexes.get(i));

            }
        }
        for (int i = 0; i < cleanup.size(); i++) {
            variableUseIndexes.remove(cleanup.get(i));
        }
        cleanup.clear();
        for (int i = 0; i < arrayUseIndexes.size(); i++) {
            Node temp = leafNodes.get(arrayUseIndexes.get(i) + 2);
            searchParentScope(temp.scopeID);
            if (VariableTable.lookup(temp.scopeID, temp.value, true, true)) {
                LinkedList<SymbolTableNode> connections = VariableTable.lookupNodes(temp.scopeID, temp.value, true,
                        true);
                VariableTable.addRow(new SymbolTableNode(temp.id, temp.scopeID, temp.value, parentScopeID, temp.type,
                        false, true, connections.get(0).nodeID));
                cleanup.add(arrayUseIndexes.get(i));
            }
        }
        for (int i = 0; i < cleanup.size(); i++) {
            arrayUseIndexes.remove(cleanup.get(i));
        }
        // System.out.println("count for var: " + variableUseIndexes.size() + " count
        // for arr: " + arrayUseIndexes.size());
        cleanup.clear();
        // Var and Field check if the declaration is in a parent scope as usage
        for (int i = 0; i < variableUseIndexes.size(); i++) {
            Node temp = leafNodes.get(variableUseIndexes.get(i) + 2);
            checkAncestor(temp.scopeID, temp, false);
            if (parentChecker == null) {
                throw new SemanticError(
                        "(APPL-DECL error): " + temp.value + " has no appropriate variable declaration");
            }
        }
        for (int i = 0; i < arrayUseIndexes.size(); i++) {
            Node temp = leafNodes.get(arrayUseIndexes.get(i) + 2);
            checkAncestor(temp.scopeID, temp, true);
            if (parentChecker == null) {
                throw new SemanticError("(APPL-DECL error): " + temp.value + " has no appropriate array declaration");
            }
        }
        // Final check if declarations are used
        VariableTable.connect();
        LinkedList<SymbolTableNode> declarations = VariableTable.declarations();
        for (int i = 0; i < declarations.size(); i++) {
            if (!declarations.get(i).isUsed) {
                throw new SemanticError("(DECL-APPL error): " + declarations.get(i).value
                        + " is not used. (within scope " + declarations.get(i).scopeID + ")");
            }
        }
    }

    public void checkAncestor(int scope, Node n, boolean arr) {
        parentChecker = null;
        if (scope == 0) {
            return;
        }
        searchParentScope(scope);
        checkAncestorRecursive(parentScopeID, n, arr);
    }

    public void checkAncestorRecursive(int scope, Node n, boolean arr) {
        if (scope == -1) {
            return;
        }
        searchParentScope(n.scopeID);
        if (VariableTable.lookup(scope, n.value, true, arr)) {
            LinkedList<SymbolTableNode> connections = VariableTable.lookupNodes(scope, n.value, true,
                    arr);
            VariableTable.addRow(new SymbolTableNode(n.id, n.scopeID, n.value, parentScopeID, n.type,
                    false, arr, connections.get(0).nodeID));
            parentChecker = connections.get(0);
        } else {
            searchParentScope(scope);
            checkAncestorRecursive(parentScopeID, n, arr);
        }

    }

    public boolean isInChildren(int l, int r) {
        isInChildScope = false;
        searchScope(l);
        ScopeNode leftID = currentScope;
        recursiveChildScopeCheck(leftID, r);
        return isInChildScope;
    }

    public void recursiveChildScopeCheck(ScopeNode n, int check) {
        if (n.scopeID == check) {
            isInChildScope = true;
        }

        for (int i = 0; i < n.children.size(); i++) {
            recursiveChildScopeCheck(n.children.get(i), check);
        }
    }

    public void TypeChecking() throws SemanticError {
        VariableTable.addTreeAsList(leafNodes);
        VariableTable.setAllUDNTypesToU();
        VariableTable.assignDefaultTypes();
        VariableCheck(); //unfinished
        ArrayCheck(); //unfinished
    }

    public void VariableCheck(){

    }

    public void ArrayCheck() throws SemanticError {
        LinkedList<SymbolTableNode> arrays = VariableTable.getArrays();
        for(int i = 0; i < arrays.size(); i++){
            if(arrays.get(i).isDeclaration){

            }else{

            }
        }
    }

    public Node getNodeByNodeID(int id){
        for(int i = 0; i < leafNodes.size(); i++){
            if(leafNodes.get(i).id==id){
                return leafNodes.get(i);
            }
        }
        return null;
    }

    public void ValueFlowAnalysis() throws SemanticError {

    }

    public void displayProcTable() {
        System.out.println("\nProcedure Table:");
        ProcedureTable.displayProcTable();
    }

    public void displayVarTable() {
        System.out.println("\nVariable Table:");
        VariableTable.displayVarTable();
    }

}