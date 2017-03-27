import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * My compiler for KPI
 * Created by Igor Tymoshenko on 21.03.17.
 */
public class Asmc {

    public static void main(String[] argz) throws IOException{

        try {
            Scanner scan = new Scanner(Paths.get(argz[0]));

            List<LinkedList<Lexeme>> lexicalTable = new ArrayList<>();
            LinkedList<Lexeme> rowLexemes = new LinkedList<>();
            Lexeme lexeme = new Lexeme();
            Pattern p = Pattern.compile("\\w+|\".*\"|'.*'|\\p{Punct}");
            Matcher m;
            String mstr, buf;

            while (scan.hasNextLine()){
                mstr = scan.nextLine();
                m = p.matcher(mstr);
                while (m.find()) {
                    buf = mstr.substring(m.start(), m.end());
                    if (buf.matches(";"))
                        break;
                    else if (buf.matches("MOV|OR|POP|CMP|JNA|MOVSW|ROL"))
                        lexeme.addToList(rowLexemes, buf, "instruction");
                    else if (buf.matches("SEGMENT|ENDS|END|ENDM|DW|DD|DB|PTR|BYTE|WORD|DWORD|MACRO"))
                        lexeme.addToList(rowLexemes, buf, "directive");
                    else if (buf.matches("[A-D][HL]"))
                        lexeme.addToList(rowLexemes, buf, "8-bit");
                    else if (buf.matches("[A-D][X]|[DS]I|[DES]|[ISB]P"))
                        lexeme.addToList(rowLexemes, buf, "16-bit");
                    else if (buf.matches("[C-GS]S"))
                        lexeme.addToList(rowLexemes, buf, "segment register");
                    else if (buf.matches("\".*\"|'.*'"))
                        lexeme.addToList(rowLexemes, buf.substring(1, buf.length()-1), "string");
                    else if (buf.matches("[,:\\[\\]+]"))
                        lexeme.addToList(rowLexemes, buf, "single");
                    else if (buf.matches("\\d+|\\d+D$"))
                        lexeme.addToList(rowLexemes, buf, "decimal");
                    else if (buf.matches("[01]+B$"))
                        lexeme.addToList(rowLexemes, buf, "binary");
                    else if (buf.matches(".*[a-z]+.*|^[0-9]\\w+|\\w{4,}"))
                        lexeme.addToList(rowLexemes, buf, "deprecated");
                    else if (buf.matches("\\w+"))
                        lexeme.addToList(rowLexemes, buf, "identify");
                }
                lexicalTable.add(new LinkedList<>(rowLexemes));
                rowLexemes.clear();
            }
            scan.close();
            for (int i = 0; i < lexicalTable.size(); i++)
                System.out.println(i+1 + ": " + lexicalTable.get(i));

        } catch (FileNotFoundException e) {
            System.out.println("I can`t found this file. Maybe, it doesn't exist ?");
        }
    }
}
