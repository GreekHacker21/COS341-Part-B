public class SymbolTableNode {
    public int scopeID;
    public String value;
    public int parentScopeID;
    public String type;
    public boolean isUsed;

    SymbolTableNode(int sID, String v, int psID, String t){
        scopeID = sID;
        value = v;
        parentScopeID = psID;
        type = t;
    }

}
