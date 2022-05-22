public class SyntaxError extends Exception {
    String message;

    SyntaxError(String m){
        message = m;
    }

    public String getMessage(){
        return message;
    }
}
