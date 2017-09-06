
class Lexeme {

    String id;
    String type;
    String code;

    Lexeme(Lexeme lex){
        id = lex.id;
        type = lex.type;
        code = lex.code;
    }
    Lexeme(String id, String type){
        this.id = id;
        this.type = type;
        this.code = "no";
    }
    Lexeme(String id, String type, String code){
        this.id = id;
        this.type = type;
        this.code = code;
    }

    @Override
    public String toString() {
        return id;
    }
}
