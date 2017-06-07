import java.util.LinkedList;

class Lexeme {

    String id;
    String type;

    Lexeme(){}
    Lexeme(Lexeme lex){
        this.id = lex.id;
        this.type = lex.type;
    }

    Lexeme put(String id, String type){
        this.id = id;
        this.type = type;
        return this;
    }

    void addToList(LinkedList<Lexeme> list, String id, String type){
            list.add(new Lexeme(this.put(id, type)));
    }

    @Override
    public String toString() {
        return id;
    }
}
