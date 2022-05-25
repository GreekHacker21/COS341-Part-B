import java.util.Stack;

public class SymbolTable {
    Stack<SymbolTableNode> rows;

    SymbolTable(){
        rows = new Stack<>();
    }

    public void addRow(SymbolTableNode node){
        rows.push(node);
    }

    public boolean searchValue(String value){
        for(int i = rows.size()-1; i >= 0; i++){
            if(rows.elementAt(i).value.equals(value)){
                return true;
            }
        }
        return false;
    }
}
