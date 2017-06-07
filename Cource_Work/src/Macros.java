import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Macros {

    String id;
    private ArrayList<String> formalParam = new ArrayList<>();
    private ArrayList<String> actualParam = new ArrayList<>();
    List<LinkedList<Lexeme>> macroCode = new ArrayList<>();

    Macros(String id){
        this.id = id;
    }

    void addFormPar(String par){
        formalParam.add(par);
    }

    void put(LinkedList<Lexeme> row){
        macroCode.add(new LinkedList<>(row));
    }
}
