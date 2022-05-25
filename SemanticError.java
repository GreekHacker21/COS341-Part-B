public class SemanticError extends Exception {
    String message;

    SemanticError(String m){
        message = m;
    }

    public String getMessage(){
        return message;
    }
}
