import java.util.LinkedList;

/**
 * Created by tigorini on 27.03.17.
 */
class Lexeme {

    private String id;
    private String type;

    Lexeme(){}
    Lexeme(Lexeme lex){
        this.id = lex.id;
        this.type = lex.type;
    }
    String getType(){return type;}
    String getId(){return id;}

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
        return getId() + " (" + getType() + ")";
    }
}
