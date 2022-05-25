import java.util.LinkedList;
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

    public void displayTable(){
        for(int i = 0; i < rows.size(); i++){
            System.out.println(rows.elementAt(i).row());
        }
    }

    public LinkedList<SymbolTableNode> requestType(String t){
        LinkedList<SymbolTableNode> temp = new LinkedList<>();
        for(int i = 0; i < rows.size(); i++){
            if(rows.elementAt(i).type.equals(t)){
                temp.add(rows.elementAt(i));
            }
        }
        return temp;
    }
}
