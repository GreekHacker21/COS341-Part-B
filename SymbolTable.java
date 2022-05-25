import java.util.LinkedList;
import java.util.Stack;

public class SymbolTable {
    Stack<SymbolTableNode> rows;
    LinkedList<Node> leafNodes;

    SymbolTable() {
        rows = new Stack<>();
    }

    public void addTreeAsList(LinkedList<Node> lN) {
        leafNodes = lN;
    }

    public void addRow(SymbolTableNode node) {
        rows.push(node);
    }

    public boolean searchValue(String value) {
        for (int i = rows.size() - 1; i >= 0; i++) {
            if (rows.elementAt(i).value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void displayProcTable() {
        for (int i = 0; i < rows.size(); i++) {
            System.out.println(rows.elementAt(i).procRow());
        }
    }

    public void displayVarTable() {
        for (int i = 0; i < rows.size(); i++) {
            System.out.println(rows.elementAt(i).varRow());
        }
    }

    public LinkedList<SymbolTableNode> requestType(String t) {
        LinkedList<SymbolTableNode> temp = new LinkedList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).type.equals(t)) {
                temp.add(rows.elementAt(i));
            }
        }
        return temp;
    }

    public boolean lookup(int sID, String v, boolean isD, boolean isA) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).scopeID == (sID) && (rows.elementAt(i).value.equals(v))
                    && (rows.elementAt(i).isDeclaration == isD) && (rows.elementAt(i).isArray == isA)) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<SymbolTableNode> lookupNodes(int sID, String v, boolean isD, boolean isA) {
        LinkedList<SymbolTableNode> connections = new LinkedList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).scopeID == (sID) && (rows.elementAt(i).value.equals(v))
                    && (rows.elementAt(i).isDeclaration == isD) && (rows.elementAt(i).isArray == isA)) {
                connections.add(rows.elementAt(i));
            }
        }
        return connections;
    }

    public void connect() {
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.size(); j++) {
                if (rows.elementAt(i).isDeclaration && rows.elementAt(i).nodeID == rows.elementAt(j).varLinkNodeID) {
                    rows.elementAt(i).isUsed = true;
                }
            }
        }
    }

    public LinkedList<SymbolTableNode> declarations() {
        LinkedList<SymbolTableNode> d = new LinkedList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).isDeclaration) {
                d.add(rows.elementAt(i));
            }
        }
        return d;
    }

    public LinkedList<SymbolTableNode> getArrays() {
        LinkedList<SymbolTableNode> d = new LinkedList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).isArray) {
                d.add(rows.elementAt(i));
            }
        }
        return d;
    }

    public LinkedList<SymbolTableNode> getVars() {
        LinkedList<SymbolTableNode> d = new LinkedList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).isArray) {
                d.add(rows.elementAt(i));
            }
        }
        return d;
    }

    public void setAllUDNTypesToU() {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.elementAt(i).type.equals("userDefinedName")) {
                rows.elementAt(i).type = "U";
            }
        }
    }

    public void assignDefaultTypes() {
        for (int i = 0; i < rows.size(); i++) {
            switch (rows.elementAt(i).type) {
                case "string":
                    rows.elementAt(i).type = "S";
                    break;
                case "num":
                    rows.elementAt(i).type = "N";
                    break;
                case "bool":
                    rows.elementAt(i).type = "B";
                    break;
            }
        }
    }
}
