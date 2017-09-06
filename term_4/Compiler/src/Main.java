import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * My compiler for KPI
 * Created by Igor Tymoshenko on 21.03.17.
 */

public class Main {

    private static HashMap<String, Segment> segments = new HashMap<>();
    static HashSet<String> identifies = new HashSet<>();
    static HashMap<String, Macros> macroses  = new HashMap<>();
    static HashMap<String, Variable> variables = new HashMap<>();
    static HashMap<String, Label> labels = new HashMap<>();
    private static ArrayList<String> notClosed = new ArrayList<>();
    static int errors = 0;

    public static void main(String[] argz) throws IOException{

        String path, list;
        Scanner in = new Scanner(System.in);
        if (argz.length == 1) {
            path = argz[0];
            System.out.print("Enter listing name [.lst]: ");
            list = in.next();
        } else if (argz.length == 2) {
            path = argz[0];
            list = argz[1];
        } else {
            System.out.print("Enter file name [.asm]: ");
            path = in.next();
            System.out.print("Enter listing name [.lst]: ");
            list = in.next();
        }
        in.close();
        if (!path.matches(".+[.]asm"))
            path += ".asm";
        if (!list.matches(".+[.]asm"))
            list += ".lst";

        try {
            Scanner scan = new Scanner(Paths.get(path));
            createLexicalTable(scan);
            scan.close();
            Scanner scan2 = new Scanner(Paths.get(path));
            PrintWriter writer = new PrintWriter(list);

            Pattern p = Pattern.compile("\\w+|\".+\"|'.+'|\\p{Punct}");
            Matcher m;
            String str, buf;
            boolean dirEND = false;

            //second pass
            writer.printf("Igor Tymoshenko Compiler from KV-51 group. 2017.\n\n");
            System.out.printf("Igor Tymoshenko Compiler from KV-51 group. 2017.\n");
            while (scan2.hasNextLine()){

                str = scan2.nextLine();
                if (str.contains(";"))
                    str = str.substring(0, str.indexOf(";"));

                if (str.matches("\\s*END(\\s*|\\s+.*)")) {
                    writer.printf("%24s%s\n", "", str);
                    dirEND = true;
                    break;
                } else if (str.matches("\\s*")){
                    writer.println("");
                } else if (str.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s+SEGMENT(\\s*|\\s+.*)")){
                    m = p.matcher(str);
                    m.find();
                    buf = str.substring(m.start(), m.end());
                    if (segments.containsKey(buf)){
                        writer.printf("0000  %18s%s\n", "", str);
                        m.find();
                        if (m.find()) {
                            writer.printf("%18s%s\n", "", "Error occurred");
                            errors++;
                        }
                        segments.get(buf).generateOpcodes(scan2, writer);
                        str = scan2.nextLine();
                        if (str.matches("\\s*END(\\s*|\\s+.*)")) {
                            writer.printf("%24s%s\n", "", str);
                            dirEND = true;
                            break;
                        }
                        writer.printf("%s  %18s%s\n", toHex(segments.get(buf).currentOffset), "", str);
                    } else {
                        writer.printf("%18s%s\n", "", "Error occurred");
                        errors++;
                    }
                } else if (str.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s+MACRO(\\s*|\\s+.*)")) {
                    m = p.matcher(str);
                    m.find();
                    buf = str.substring(m.start(), m.end());
                    if (macroses.containsKey(buf)) {
                        Macros macro = macroses.get(buf);
                        writer.printf("%24s%s\n", "", str);
                        m.find();
                        if (m.find() && (!macro.formalParam.matches("^[A-Z][A-Z0-9]{0,2}") || m.find())) {
                            writer.printf("%18s%s\n", "", "Error occurred");
                            errors++;
                        }
                        macro.print(scan2, writer);
                        if (scan2.hasNextLine())
                            writer.printf("%24s%s\n","", scan2.nextLine());
                    } else {
                        writer.printf("%18s%s\n", "", "Error occurred");
                        errors++;
                    }
                } else {
                    writer.printf("%24s%s\n%18s%s\n", "", str, "", "Error occurred");
                    errors++;
                }
            }

            if (!dirEND)
                writer.println("\nNo END directive");

            if (!notClosed.isEmpty()){
                writer.print("\nOpen segments: ");
                for (String seg: notClosed)
                    writer.print(" " + seg);
                writer.println();
            }
            if (!macroses.isEmpty()) {
                writer.println("\nMacros:");
                for (Map.Entry<String, Macros> entry : macroses.entrySet()) {
                    Macros lb = entry.getValue();
                    writer.println(lb.name);
                }
            }
            if (!segments.isEmpty()) {
                writer.println("\nSegments:");
                writer.println("          Name            Length\n");
                for (Map.Entry<String, Segment> entry : segments.entrySet()) {
                    Segment lb = entry.getValue();
                    writer.printf("%-4s  . . . . . . . . . .  %s\n", lb.name, toHex(lb.currentOffset));
                }
            }
            if (!labels.isEmpty() || !variables.isEmpty()) {
                writer.println("\nSymbols:");
                writer.println("          Name           Value  Seg\n");
                for (Map.Entry<String, Label> entry : labels.entrySet()) {
                    Label lb = entry.getValue();
                    writer.printf("%-4s. . . . . . . . . .  %-7s%s\n", lb.id, toHex(lb.offset), lb.seg);
                }
                writer.println("");
                for (Map.Entry<String, Variable> entry : variables.entrySet()) {
                    Variable lb = entry.getValue();
                    writer.printf("%-4s. . . . . . . . . .  %-7s%s\n", lb.id, toHex(lb.offset), lb.seg);
                }
            }

            writer.println("\n  Summary Errors:  " + errors);
            System.out.println("\n  Summary Errors:  " + errors);
            scan2.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("\nFile not found");
        }

    }


    //create lexical table from source file
    private static void createLexicalTable(Scanner scan){

        Pattern p = Pattern.compile("\\w+|\".+\"|'.+'|\\p{Punct}");
        Matcher m;
        String mstr, buf;

        while (scan.hasNextLine()){
            mstr = scan.nextLine();
            m = p.matcher(mstr);
            if (mstr.matches("(\\s*|.*\\s+)END(\\s*|\\s+.*)"))
                break;
            else if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s+MACRO(\\s*|\\s+.*)")) {
                m.find();
                buf = mstr.substring(m.start(), m.end());
                if (identifies.contains(buf))
                    continue;
                Macros macro = new Macros(buf);
                macroses.put(buf, macro);
                identifies.add(buf);
                m.find();
                if (m.find()){
                    buf = mstr.substring(m.start(), m.end());
                    macro.formalParam = buf;
                }
                if (!macro.createMacrocode(scan))
                    break;

            }else if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s+SEGMENT(\\s*|\\s+.*)")){
                m.find();
                buf = mstr.substring(m.start(), m.end());
                if (identifies.contains(buf) || macroses.size() > 2)
                    continue;
                Segment seg = new Segment(buf);
                segments.put(buf, seg);
                identifies.add(buf);
                int res = seg.createSegmentCode(scan);
                if (res == -1) {
                    notClosed.add(buf);
                    break;
                } else if (res == 1)
                    notClosed.add(buf);
            }
        }
    }



    //convert row from lexicalTable to String
    static String rowToString(LinkedList<Lexeme> row){
        StringBuilder result = new StringBuilder();
        for (Lexeme lex : row) {
            if (lex.type.equals("instruction"))
                result.append(lex.id).append("  ");
            else if (lex.id.matches("DB|DW|DD"))
                result.append("  ").append(lex.id).append(" ");
            else if (lex.type.matches("directive"))
                result.append(lex.id).append(" ");
            else if (lex.type.equals("string"))
                result.append("\"").append(lex.id).append("\"");
            else result.append(lex.id);
        }
        return result.toString();
    }


    //convert binary, decimal, string to hex
    static String toHex(String num, int mod){

        int dec = 0;
        String hex;
        StringBuilder result = new StringBuilder();

        if (num.matches("\\d+")) {
            char ch;
            for (int i = 0; i < num.length(); i++) {
                dec *= 10;
                ch = num.charAt(i);
                switch (ch) {
                    case '1': dec += 1; break;
                    case '2': dec += 2; break;
                    case '3': dec += 3; break;
                    case '4': dec += 4; break;
                    case '5': dec += 5; break;
                    case '6': dec += 6; break;
                    case '7': dec += 7; break;
                    case '8': dec += 8; break;
                    case '9': dec += 9;
                }
            }
            hex = Integer.toHexString(dec);
        } else if (num.matches("[01]+B$")) {
            char ch;
            int i = 0;
            while ((ch = num.charAt(i)) != 'B') {
                dec *= 2;
                if (ch == '1')
                    dec += 1;
                i++;
            }
            hex = Integer.toHexString(dec);
        } else {
            int i = 0;
            while (i < num.length()) {
                dec = (int)num.charAt(i);
                hex = Integer.toHexString(dec);
                int j = 0;
                while (hex.length() + j < 2){
                    result.append("0");
                    j++;
                }
                result.append(hex);
                i++;
            }
            hex = result.toString().toUpperCase();
            return hex;
        }

        int i = 0;
        while ((hex.length() + i) % mod != 0){
            result.append("0");
            i++;
        }
        hex = result.append(hex).toString().toUpperCase();
        return hex;
    }


    static String toHex(int num){

        String hex = Integer.toHexString(num);
        StringBuilder result = new StringBuilder();

        int i = 0;
        while (hex.length() + i < 4){
            result.append("0");
            i++;
        }
        hex = result.append(hex).toString().toUpperCase();
        return hex;
    }


    static int getBytes(String num){
        num = toHex(num, 2);
        return num.length()/2;
    }


    static Lexeme lexemeType(String buf, LinkedList<Lexeme> row) {

        Lexeme lex = null;
        if (buf.matches("MOV|OR|POP|CMP|JNA|MOVSW|ROL"))
            lex = new Lexeme(buf, "instruction");
        else if (buf.matches("ENDS|DW|DD|DB|PTR|BYTE|WORD"))
            lex = new Lexeme(buf, "directive");
        else if (buf.matches("[A-D][HL]")) {
            switch (buf) {
                case "AL":
                    lex = new Lexeme(buf, "reg8", "000");
                    break;
                case "CL":
                    lex = new Lexeme(buf, "reg8", "001");
                    break;
                case "DL":
                    lex = new Lexeme(buf, "reg8", "010");
                    break;
                case "BL":
                    lex = new Lexeme(buf, "reg8", "011");
                    break;
                case "AH":
                    lex = new Lexeme(buf, "reg8", "100");
                    break;
                case "CH":
                    lex = new Lexeme(buf, "reg8", "101");
                    break;
                case "DH":
                    lex = new Lexeme(buf, "reg8", "110");
                    break;
                default:
                    lex = new Lexeme(buf, "reg8", "111");
            }
        } else if (buf.matches("[A-D][X]|[DS]I|BP")) {
            switch (buf) {
                case "AX":
                    lex = new Lexeme(buf, "reg16", "000");
                    break;
                case "CX":
                    lex = new Lexeme(buf, "reg16", "001");
                    break;
                case "DX":
                    lex = new Lexeme(buf, "reg16", "010");
                    break;
                case "BX":
                    if (!row.getLast().id.equals("["))
                        lex = new Lexeme(buf, "reg16", "011");
                    else lex = new Lexeme(buf, "reg16", "00");
                    break;
                case "BP":
                    lex = new Lexeme(buf, "reg16", "01");
                    break;
                case "SI":
                    lex = new Lexeme(buf, "reg16", "0");
                    break;
                default:
                    lex = new Lexeme(buf, "reg16", "1");
            }
        } else if (buf.matches("[C-GS]S")) {
            switch (buf) {
                case "ES":
                    lex = new Lexeme(buf, "segment reg", "26");
                    break;
                case "CS":
                    lex = new Lexeme(buf, "segment reg", "2E");
                    break;
                case "SS":
                    lex = new Lexeme(buf, "segment reg", "36");
                    break;
                case "DS":
                    lex = new Lexeme(buf, "segment reg", "3E");
                    break;
                case "FS":
                    lex = new Lexeme(buf, "segment reg", "64");
                    break;
                default:
                    lex = new Lexeme(buf, "segment reg", "65");
            }
        } else if (buf.matches("\".+\"|'.+'"))
            lex = new Lexeme(buf.substring(1, buf.length() - 1), "string");
        else if (buf.matches("[,:\\[\\]+]"))
            lex = new Lexeme(buf, "single");
        else if (buf.matches("\\d+|\\d+D$"))
            lex = new Lexeme(buf, "decimal");
        else if (buf.matches("[01]+B$"))
            lex = new Lexeme(buf, "binary");
        else if (buf.matches(".*[a-z]+.*|^[0-9]\\w+|\\w{4,}"))
            lex = new Lexeme(buf, "deprecated");
        else if (buf.matches("\\w+"))
            lex = new Lexeme(buf, "identify");

        return lex;
    }
}
