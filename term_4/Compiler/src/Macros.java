import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Macros {

    String name;
    String formalParam;
    String actualParam;
    LinkedList<LinkedList<Lexeme>> macroCode = new LinkedList<>();
    int callNumbers;

    Macros(String name){
        this.name = name;
        formalParam = null;
        actualParam = null;
        callNumbers = 0;
    }


    boolean createMacrocode(Scanner scan){

        Pattern p = Pattern.compile("\\w+|\".+\"|'.+'|\\p{Punct}");
        Matcher m;
        String mstr, buf;
        LinkedList<Lexeme> rowLexemes = new LinkedList<>();

        while (scan.hasNextLine()) {
            mstr = scan.nextLine();
            m = p.matcher(mstr);
            while (m.find()) {
                buf = mstr.substring(m.start(), m.end());
                if (buf.matches("\\s*ENDM\\s*")) {
                    return true;
                } else if (buf.matches(";"))
                    break;
                else rowLexemes.add(Main.lexemeType(buf, rowLexemes));
            }
            macroCode.add(new LinkedList<>(rowLexemes));
            rowLexemes.clear();
        }
        return false;
    }

    void print(Scanner scan, PrintWriter writer){

        String str;
        for (LinkedList<Lexeme> row : macroCode) {
            str = scan.nextLine();
            writer.printf("%24s%s\n", "", str);
            for (Lexeme lex: row)
                if (lex.type.equals("deprecated")) {
                    writer.printf("%18s%s\n", "", "Error occurred");
                    Main.errors++;
                    break;
                }
        }
    }

}
