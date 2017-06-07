import java.util.LinkedList;
import java.util.List;

public class Label {

    String id;
    int indx;

    Label(String id, int index){
        this.id = id;
        indx = index;
    }

    public int getDelta(List<LinkedList<Lexeme>> lexicalTable, int curIndx){

        int res;
        if (indx > curIndx){
            res = 0x1a;
        }else{
            res = 0x0a;
        }
        return res;
    }
}
