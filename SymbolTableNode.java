public class SymbolTableNode {
    public int scopeID;
    public String value;
    public int parentScopeID;
    public String type;
    public boolean isUsed;
    public int nodeID;

    SymbolTableNode(int nID, int sID, String v, int psID, String t){
        nodeID = nID;
        scopeID = sID;
        value = v;
        parentScopeID = psID;
        type = t;
        isUsed = false;
    }

    public String row(){
        return "scopeID:\t" + scopeID + "\tvalue:\t" + value + "\tparentScopeID:\t" + parentScopeID + "\ttype:\t" + type;
    }

}
