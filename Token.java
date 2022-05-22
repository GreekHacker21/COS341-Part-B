public class Token {

    public String value, type;
    public Token next;
    public int line;

    Token(String v, String t, int l){
        value = v;
        type = t;
        next = null;
        line = l;
        //System.out.println(value);
    }

    Token(String v){
        value = v;
        next = null;
    }
}
