public class SymbolTableNode {
    public int scopeID;
    public String value;
    public int parentScopeID;
    public String type;
    public boolean isUsed;
    public int nodeID;
    public boolean isDeclaration;
    public boolean isArray;
    public int varLinkNodeID; // which node id (of the declared vars) the var being used will access

    SymbolTableNode(int nID, int sID, String v, int psID, String t) {
        nodeID = nID;
        scopeID = sID;
        value = v;
        parentScopeID = psID;
        type = t;
        isUsed = false;
        varLinkNodeID = -1;
    }

    SymbolTableNode(int nID, int sID, String v, int psID, String t, boolean d, boolean a) {
        nodeID = nID;
        scopeID = sID;
        value = v;
        parentScopeID = psID;
        type = t;
        isUsed = false;
        isDeclaration = d;
        isArray = a;
        varLinkNodeID = -1;
    }

    SymbolTableNode(int nID, int sID, String v, int psID, String t, boolean d, boolean a, int vL) {
        nodeID = nID;
        scopeID = sID;
        value = v;
        parentScopeID = psID;
        type = t;
        isUsed = false;
        isDeclaration = d;
        isArray = a;
        varLinkNodeID = vL;
    }

    public String procRow() {
        return "nodeID:\t" + nodeID + "\tscopeID:\t" + scopeID + "\tvalue:\t" + value + "\tparentScopeID:\t"
                + parentScopeID + "\ttype:\t" + type;
    }

    public String varRow() {
        if (!isDeclaration) {
            return "nodeID:\t" + nodeID + "\tscopeID:\t" + scopeID + "\tvalue:\t" + value + "\tparentScopeID:\t"
                    + parentScopeID + "\ttype:\t" + type + "\tisDeclaration:\t" + isDeclaration + "\tisArray:\t"
                    + isArray + "\tLinkedToNodeID:\t" + varLinkNodeID;
        }
        return "nodeID:\t" + nodeID + "\tscopeID:\t" + scopeID + "\tvalue:\t" + value + "\tparentScopeID:\t"
                + parentScopeID + "\ttype:\t" + type + "\tisDeclaration:\t" + isDeclaration + "\tisArray:\t" + isArray + "\tisUsed:\t" + isUsed;
    }

}
