import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * My compiler for KPI
 * Created by Igor Tymoshenko on 21.03.17.
 */
public class Main {

    private static List<LinkedList<Lexeme>> lexicalTable = new ArrayList<>();
    private static Lexeme lexeme = new Lexeme();
    private static ArrayList<Segment> segments = new ArrayList<>();
    private static ArrayList<String> identifys = new ArrayList<>();
    private static HashMap<String, Label> labels = new HashMap<>();
    private static ArrayList<Integer> errors = new ArrayList<>();
    private static ArrayList<Integer> instrBytes = new ArrayList<>();
    private static ArrayList<String> opcodes = new ArrayList<>();
    private static HashMap<String, Macros> macroses = new HashMap();


    public static void main(String[] argz) throws IOException{

        CreateLexicalTable("test/test.asm");
        for (int i = 0; i < lexicalTable.size(); i++)
            System.out.println(i + ": " + rowToString(lexicalTable.get(i)));
        System.out.println("Labels:");
        for (Map.Entry<String, Label> entry: labels.entrySet()){
            Label lb = entry.getValue();
            System.out.println(lb.id + "  " + lb.indx);
        }

        String str;
        for (LinkedList<Lexeme> row : lexicalTable) {
            str = rowToString(row);
            if (str.equals("")) {
                errors.add(0);
                opcodes.add("");
                instrBytes.add(0);
                continue;
            }

            if (row.get(0).type.equals("instruction")) {
                if (str.matches("MOVSW")) {
                    errors.add(0);
                    instrBytes.add(1);
                    opcodes.add("A5");
                } else if (row.get(0).id.equals("POP")) {
                    if (str.matches("POP\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\]")) {
                        instrBytes.add(3);
                        if (row.get(4).id.equals("BX") && row.get(6).id.equals("SI"))
                            opcodes.add("SG: 8F 00");
                        else if (row.get(4).id.equals("BX") && row.get(6).id.equals("DI"))
                            opcodes.add("SG: 8F 01");
                        else if (row.get(4).id.equals("BP") && row.get(6).id.equals("SI"))
                            opcodes.add("SG: 8F 02");
                        else if (row.get(4).id.equals("BP") && row.get(6).id.equals("DI"))
                            opcodes.add("SG: 8F 00");
                        errors.add(0);
                    } else if (str.matches("POP\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+[0-9]+\\]")) {
                        instrBytes.add(4);
                        if (row.get(4).id.equals("BX") && row.get(6).id.equals("SI"))
                            opcodes.add("SG: 8F 00 " + row.get(8).id);
                        else if (row.get(4).id.equals("BX") && row.get(6).id.equals("DI"))
                            opcodes.add("SG: 8F 01 " + row.get(8).id);
                        else if (row.get(4).id.equals("BP") && row.get(6).id.equals("SI"))
                            opcodes.add("SG: 8F 02 " + row.get(8).id);
                        else if (row.get(4).id.equals("BP") && row.get(6).id.equals("DI"))
                            opcodes.add("SG: 8F 00 " + row.get(8).id);
                        errors.add(0);
                    } else if (str.matches("POP\\s*\\[(BP|BX)\\+(DI|SI)\\]")) {
                        instrBytes.add(2);
                        if (row.get(2).id.equals("BX") && row.get(4).id.equals("SI"))
                            opcodes.add("8F 00");
                        else if (row.get(2).id.equals("BX") && row.get(4).id.equals("DI"))
                            opcodes.add("8F 01");
                        else if (row.get(2).id.equals("BP") && row.get(4).id.equals("SI"))
                            opcodes.add("8F 02");
                        else if (row.get(2).id.equals("BP") && row.get(4).id.equals("DI"))
                            opcodes.add("8F 00");
                        errors.add(0);
                    } else if (str.matches("POP\\s*\\[(BP|BX)\\+(DI|SI)\\+[0-9]+\\]")) {
                        instrBytes.add(3);
                        if (row.get(2).id.equals("BX") && row.get(4).id.equals("SI"))
                            opcodes.add("8F 40 " + row.get(6).id);
                        else if (row.get(2).id.equals("BX") && row.get(4).id.equals("DI"))
                            opcodes.add("8F 41 " + row.get(6).id);
                        else if (row.get(2).id.equals("BP") && row.get(4).id.equals("SI"))
                            opcodes.add("8F 42 " + row.get(6).id);
                        else if (row.get(2).id.equals("BP") && row.get(4).id.equals("DI"))
                            opcodes.add("8F 40 " + row.get(6).id);
                        errors.add(0);
                    } else {
                        instrBytes.add(0);
                        opcodes.add("");
                        errors.add(1);
                    }
                } else if (row.get(0).id.equals("ROL")) {
                    if (str.matches("ROL\\s*[A-D][HL],1")) {
                        instrBytes.add(2);
                        opcodes.add("D0 C_"); //AH-0...AL-4...
                        errors.add(0);
                    } else if (str.matches("ROL\\s*[A-D]X")) {
                        instrBytes.add(2);
                        opcodes.add("D1 C_"); // A-0, D-1, B-2, C-3
                        errors.add(0);
                    } else {
                        instrBytes.add(0);
                        opcodes.add("");
                        errors.add(1);
                    }
                } else if (row.get(0).id.equals("CMP")) {
                    if (str.matches("CMP\\s*[A-D][HL],\\[(BP|BX)\\+(DI|SI)\\]")) {
                        instrBytes.add(2);
                        opcodes.add("3A __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D][HL],[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\]")) {
                        instrBytes.add(3);
                        opcodes.add("SG: 3A __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D][HL],\\[(BP|BX)\\+(DI|SI)\\+\\d+\\]")) {
                        instrBytes.add(3);
                        opcodes.add("3A __ " + row.get(8).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D][HL],[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\]")) {
                        instrBytes.add(4);
                        opcodes.add("SG: 3A __ " + row.get(10).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D]X,\\[(BP|BX)\\+(DI|SI)\\]")) {
                        instrBytes.add(2);
                        opcodes.add("3B __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D]X,[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\]")) {
                        instrBytes.add(4);
                        opcodes.add("SG: 3B __ ");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D]X,\\[(BP|BX)\\+(DI|SI)\\+\\d+\\]")) {
                        instrBytes.add(4);
                        opcodes.add("3B __ " + row.get(8).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[A-D]X,[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\]")) {
                        instrBytes.add(4);
                        opcodes.add("SG: 3B __ " + row.get(10).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*\\[(BP|BX)\\+(DI|SI)\\],[A-D][HL]")) {
                        instrBytes.add(2);
                        opcodes.add("38 __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],[A-D][HL]")) {
                        instrBytes.add(3);
                        opcodes.add("SG: 38 __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D][HL]")) {
                        instrBytes.add(3);
                        opcodes.add("38 __ " + row.get(6).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D][HL]")) {
                        instrBytes.add(4);
                        opcodes.add("SG: 38 __ " + row.get(8).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*\\[(BP|BX)\\+(DI|SI)\\],[A-D]X")) {
                        instrBytes.add(2);
                        opcodes.add("39 __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],[A-D]X")) {
                        instrBytes.add(3);
                        opcodes.add("SG: 39 __");
                        errors.add(0);
                    } else if (str.matches("CMP\\s*\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D]X")) {
                        instrBytes.add(3);
                        opcodes.add("39 __ " + row.get(6).id);
                        errors.add(0);
                    } else if (str.matches("CMP\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D]X")) {
                        instrBytes.add(4);
                        opcodes.add("SG: 39 __ " + row.get(8).id);
                        errors.add(0);
                    } else {
                        instrBytes.add(0);
                        opcodes.add("");
                        errors.add(1);
                    }
                } else if (row.get(0).id.equals("MOV")) {
                    if (str.matches("MOV\\s*[A-D][HL],[A-D][HL]")) {
                        instrBytes.add(2);
                        opcodes.add("8A __");
                        errors.add(0);
                    } else if (str.matches("MOV\\s*[A-D]X,[A-D]X")) {
                        instrBytes.add(2);
                        opcodes.add("8B __");
                        errors.add(0);
                    } else {
                        instrBytes.add(0);
                        opcodes.add("");
                        errors.add(1);
                    }
                } else if (row.get(0).id.equals("OR")) {
                    if (str.matches("OR\\s*BYTE\\s*PTR\\s*\\[(BP|BX)\\+(DI|SI)\\],(\\d+|\\d*B|\".\"|'.')")) {
                        instrBytes.add(3);
                        opcodes.add("80 __ " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*BYTE\\s*PTR\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],(\\d+|\\d*B|\".\"|'.')")) {
                        instrBytes.add(4);
                        opcodes.add("SG: 80 __ " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*BYTE\\s*PTR\\s*\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|\\d*B|\".\"|'.')")) {
                        instrBytes.add(4);
                        opcodes.add("80 __ " + row.get(8).id + " " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*BYTE\\s*PTR\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|\\d*B|\".\"|'.')")) {
                        instrBytes.add(5);
                        opcodes.add("SG: 80 __ " + row.get(8).id + " " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*WORD\\s*PTR\\s*\\[(BP|BX)\\+(DI|SI)\\],(\\d+|\\d*B|\".{1,2}\"|'.{1,2}')")) {
                        instrBytes.add(4);
                        opcodes.add("80 __ " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*WORD\\s*PTR\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],(\\d+|\\d*B|\".{1,2}\"|'.{1,2}')")) {
                        instrBytes.add(5);
                        opcodes.add("SG: 80 __ " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*WORD\\s*PTR\\s*\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|\\d*B|\".{1,2}\"|'.{1,2}')")) {
                        instrBytes.add(5);
                        opcodes.add("80 __ " + row.get(8).id + " " + row.getLast().id);
                        errors.add(0);
                    } else if (str.matches("OR\\s*WORD\\s*PTR\\s*[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|\\d*B|\".{1,2}\"|'{1,2}.')")) {
                        instrBytes.add(6);
                        opcodes.add("SG: 80 __ " + row.get(8).id + " " + row.getLast().id);
                        errors.add(0);
                    } else {
                        instrBytes.add(0);
                        opcodes.add("");
                        errors.add(1);
                    }
                } else if (row.get(0).id.equals("JNA") && labels.containsKey(row.get(1).id)) {
                    instrBytes.add(2);
                    opcodes.add("76 __");
                    errors.add(0);
                } else {
                    instrBytes.add(0);
                    opcodes.add("");
                    errors.add(1);
                }
            /*}else if (row.get(0).type.equals("identify")){
                if (row.get(1).id.equals("SEGMENT")){
                    instrBytes.add(0);
                    opcodes.add("kek");
                    errors.add(0);
                }*/
            }else{
                instrBytes.add(0);
                opcodes.add("");
                errors.add(1);
            }
        }

        for (int i = 0; i < opcodes.size(); i++)
            System.out.println(i + ": " + opcodes.get(i));
    }

    //create lexical table fom source file
    private static void CreateLexicalTable(String file) throws IOException{

        try {
            Scanner scan = new Scanner(Paths.get(file));

            Pattern p = Pattern.compile("\\w+|\".+\"|'.+'|\\p{Punct}");
            Matcher m;
            String mstr, buf;
            LinkedList<Lexeme> rowLexemes = new LinkedList<>();
            boolean endFlag = true;

            while (scan.hasNextLine() && endFlag){
                mstr = scan.nextLine();
                m = p.matcher(mstr);
                //checking for macros
                if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s*MACRO\\s*.*")) {
                    m.find();
                    buf = mstr.substring(m.start(), m.end());
                    Macros macro = new Macros(buf);
                    macroses.put(buf, macro);
                    m.find();
                    while (m.find()){
                        buf = mstr.substring(m.start(), m.end());
                        macro.addFormPar(buf);
                    }
                    //creating macrocode
                    boolean exitFlag = false;
                    while (scan.hasNextLine()){
                        mstr = scan.nextLine();
                        m = p.matcher(mstr);
                        while (m.find()) {
                            buf = mstr.substring(m.start(), m.end());
                            if (buf.matches("ENDM")){
                                exitFlag = true;
                                mstr = scan.nextLine();
                                break;
                            }else if (buf.matches(";"))
                                break;
                            else if (buf.matches("MOV|OR|POP|CMP|JNA|MOVSW|ROL"))
                                lexeme.addToList(rowLexemes, buf, "instruction");
                            else if (buf.matches("SEGMENT|ENDS|END|ENDM|DW|DD|DB|PTR|BYTE|WORD|DWORD|MACRO"))
                                lexeme.addToList(rowLexemes, buf, "directive");
                            else if (buf.matches("[A-D][HL]"))
                                lexeme.addToList(rowLexemes, buf, "8-bit");
                            else if (buf.matches("[A-D][X]|[DS]I|[ISB]P"))
                                lexeme.addToList(rowLexemes, buf, "16-bit");
                            else if (buf.matches("[C-GS]S"))
                                lexeme.addToList(rowLexemes, buf, "segment register");
                            else if (buf.matches("\".+\"|'.+'"))
                                lexeme.addToList(rowLexemes, buf.substring(1, buf.length() - 1), "string");
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
                        if (exitFlag)
                            break;
                        macro.put(new LinkedList<>(rowLexemes));
                        rowLexemes.clear();
                    }
                //checking for label
                }else if (mstr.matches("(\\s*@[A-Z0-9]{1,2}\\s*:)|(\\s*[A-Z][A-Z0-9]{0,2}\\s*:)\\s*")){
                    m.find();
                    buf = mstr.substring(m.start(), m.end());
                    labels.put(buf, new Label(buf, lexicalTable.size()));
                }

                m = p.matcher(mstr);
                while (m.find()) {
                    buf = mstr.substring(m.start(), m.end());
                    if (buf.matches("END")){
                        lexeme.addToList(rowLexemes, buf, "directive");
                        endFlag = false;
                        break;
                    }else if (buf.matches(";"))
                        break;
                    else if (buf.matches("MOV|OR|POP|CMP|JNA|MOVSW|ROL"))
                        lexeme.addToList(rowLexemes, buf, "instruction");
                    else if (buf.matches("SEGMENT|ENDS|ENDM|DW|DD|DB|PTR|BYTE|WORD|MACRO"))
                        lexeme.addToList(rowLexemes, buf, "directive");
                    else if (buf.matches("[A-D][HL]"))
                        lexeme.addToList(rowLexemes, buf, "8-bit");
                    else if (buf.matches("[A-D][X]|[DS]I|[ISB]P"))
                        lexeme.addToList(rowLexemes, buf, "16-bit");
                    else if (buf.matches("[C-GS]S"))
                        lexeme.addToList(rowLexemes, buf, "segment register");
                    else if (buf.matches("\".+\"|'.+'"))
                        lexeme.addToList(rowLexemes, buf.substring(1, buf.length() - 1), "string");
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

        } catch (FileNotFoundException e) {
            System.out.println("I can`t found this file. Maybe, it doesn't exist ?");
        }
    }

    //convert row from lexicalTable to String
    private static String rowToString(LinkedList<Lexeme> row){
        StringBuilder result = new StringBuilder();
        for (Lexeme lex: row){
            if (lex.type.matches("identify|instruction|directive|deprecated") && !labels.containsKey(lex.id))
                result.append(lex.id).append(" ");
            else if (lex.type.equals("string"))
                result.append("\"").append(lex.id).append("\"");
            else result.append(lex.id);
        }
        return result.toString();
    }

    //convert binary, decimal, string to hex
    private static String toHex(){
        String hex = "working on it...";
        return hex;
    }
}
