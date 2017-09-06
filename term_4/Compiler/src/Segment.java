import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Segment {

    String name;
     List<LinkedList<Lexeme>> segmentCode = new ArrayList<>();
    private ArrayList<Integer> bytes = new ArrayList<>();
    int currentOffset;

    Segment(String name) {
        this.name = name;
        currentOffset = 0;
    }

      // -1  directive END
     // 0   segment closed
    // 1   segment not closed
    int createSegmentCode(Scanner scan){

        Pattern p = Pattern.compile("\\w+|\".+\"|'.+'|\\p{Punct}");
        Matcher m;
        String mstr, buf;
        LinkedList<Lexeme> rowLexemes = new LinkedList<>();

        while (scan.hasNextLine()){
            mstr = scan.nextLine();
            m = p.matcher(mstr);
            rowLexemes.clear();

            while (m.find()) {
                buf = mstr.substring(m.start(), m.end());
                if (buf.equals("END")) {
                    return -1;
                } else if (buf.matches(";"))
                    break;
                else rowLexemes.add(Main.lexemeType(buf, rowLexemes));
            }

            if (mstr.matches("\\s*" + name + "\\s+ENDS(\\s*|\\s+.*)")) {
                return 0;
            }
            //checking for macros
            if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s+MACRO(\\s*|\\s+.*)")) {
                buf = rowLexemes.get(0).id;
                if (Main.identifies.contains(buf))
                    continue;
                Macros macro = new Macros(buf);
                Main.macroses.put(buf, macro);
                Main.identifies.add(buf);
                if (rowLexemes.size() >= 2)
                    macro.formalParam = rowLexemes.get(2).id;
                if (macro.createMacrocode(scan))
                    break;
                continue;
            //checking for label
            } else if (mstr.matches("(\\s*@[A-Z0-9]{1,2}\\s*:)|(\\s*[A-Z][A-Z0-9]{0,2}\\s*:)\\s*")){
                buf = rowLexemes.get(0).id;
                if (Main.identifies.contains(buf))
                    rowLexemes.get(0).type = "deprecated";
                else {
                    Main.identifies.add(buf);
                    Main.labels.put(buf, new Label(buf, currentOffset, name));
                }
            //checking for variable
            }else if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s*(DB|DW|DD)\\s+.*")) {
                buf = rowLexemes.get(0).id;
                if (Main.identifies.contains(buf))
                    rowLexemes.get(0).type = "deprecated";
                else {
                    Main.identifies.add(buf);
                    Main.variables.put(buf, new Variable(buf, currentOffset, name));
                }
            //checking for macros call
            }else if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}(\\s*|\\s+.*)")){
                segmentCode.add(new LinkedList<>(rowLexemes));
                if (!Main.macroses.containsKey(rowLexemes.get(0).id)){
                    rowLexemes.get(0).code = "undefined";
                    currentOffset += generateOffset(rowLexemes);
                    continue;
                }
                Macros macro = Main.macroses.get(rowLexemes.get(0).id);
                //take actual parameters
                if (macro.formalParam != null && rowLexemes.size() >= 2)
                    if (rowLexemes.get(1).type.equals("string"))
                        macro.actualParam = '\"' + rowLexemes.get(1).id + '\"';
                    else
                        macro.actualParam = rowLexemes.get(1).id;

                if (macro.formalParam == macro.actualParam || macro.actualParam != null){
                    macro.callNumbers++;
                    for (LinkedList<Lexeme> row: macro.macroCode){
                        rowLexemes = (LinkedList<Lexeme>) row.clone();
                        //if there is parameter in row, replace it by actual parameter
                        if (macro.actualParam != null)
                            for (int j = 0; j < rowLexemes.size(); j++) {
                                if (macro.formalParam.equals(rowLexemes.get(j).id))
                                    rowLexemes.set(j, Main.lexemeType(macro.actualParam, rowLexemes));
                            }
                        mstr = Main.rowToString(rowLexemes);
                        if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s*:\\s*")){
                            buf = rowLexemes.get(0).id;
                            if (Main.identifies.contains(buf))
                                rowLexemes.get(0).type = "deprecated";
                            else {
                                Main.identifies.add(buf);
                                Main.labels.put(buf, new Label(buf, currentOffset, name));
                            }
                        }else if (mstr.matches("\\s*[A-Z][A-Z0-9]{0,2}\\s+(DB|DW|DD)\\s+.*")) {
                            buf = rowLexemes.get(0).id;
                            if (Main.identifies.contains(buf))
                                rowLexemes.get(0).type = "deprecated";
                            else {
                                Main.identifies.add(buf);
                                Main.variables.put(buf, new Variable(buf, currentOffset, name));
                            }
                        }
                        segmentCode.add(new LinkedList<>(rowLexemes));
                        currentOffset += generateOffset(rowLexemes);
                    }
                }
                macro.actualParam = null;
                continue;
            }
            segmentCode.add(new LinkedList<>(rowLexemes));
            currentOffset += generateOffset(rowLexemes);
        }
        return 1;
    }


    private int generateOffset(LinkedList<Lexeme> row){

        int byteCounter = 0;
        String str = Main.rowToString(row);
        if (str.matches("\\s*"))
            return 0;

        if (row.get(0).type.equals("instruction")) {
            if (str.matches("MOVSW(\\s*|\\s+.*)")) {
                byteCounter += 1;
            } else if (row.get(0).id.equals("POP")) {
                if (str.matches("POP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                    if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                            || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;

                } else if (str.matches("POP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                    if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                            || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("POP\\s+\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                    byteCounter += 2;

                } else if (str.matches("POP\\s+\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(6).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                }
            } else if (row.get(0).id.equals("ROL")) {
                if (str.matches("ROL\\s+([A-D][HL]|[A-D]X),1(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                }
            } else if (row.get(0).id.equals("CMP")) {
                if (str.matches("CMP\\s+[A-D][HL],\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                    byteCounter += 2;

                } else if (str.matches("CMP\\s+[A-D][HL],[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;

                } else if (str.matches("CMP\\s+[A-D][HL],\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+[A-D][HL],[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(10).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+[A-D]X,\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                    byteCounter += 2;

                } else if (str.matches("CMP\\s+[A-D]X,[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;

                } else if (str.matches("CMP\\s+[A-D]X,\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+[A-D]X,[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(10).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\],[A-D][HL](\\s*|\\s+.*)")) {
                    byteCounter += 2;

                } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],[A-D][HL](\\s*|\\s+.*)")) {
                    if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                            || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;

                } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D][HL](\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(6).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D][HL](\\s*|\\s+.*)")) {
                    if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                            || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\],[A-D]X(\\s*|\\s+.*)")) {
                    byteCounter += 2;

                } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],[A-D]X(\\s*|\\s+.*)")) {
                    if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                            || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;

                } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D]X(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(6).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D]X(\\s*|\\s+.*)")) {
                    if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                            || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                }
            } else if (row.get(0).id.equals("MOV")) {
                if (str.matches("MOV\\s+[A-D][HL],[A-D][HL](\\s*|\\s+.*)")) {
                    byteCounter += 2;

                } else if (str.matches("MOV\\s+[A-D]X,[A-D]X(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                }
            } else if (row.get(0).id.equals("OR")) {
                if (str.matches("OR\\s+BYTE PTR \\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    byteCounter += 1;

                } else if (str.matches("OR\\s+BYTE PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    byteCounter += 1;

                } else if (str.matches("OR\\s+BYTE PTR \\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                    byteCounter +=1;

                } else if (str.matches("OR\\s+BYTE PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(10).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                    byteCounter += 1;

                } else if (str.matches("OR\\s+WORD PTR \\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(9).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("OR\\s+WORD PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(13).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("OR\\s+WORD PTR \\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    byteCounter += 2;
                    if (Main.getBytes(row.get(8).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                    if (Main.getBytes(row.get(11).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;

                } else if (str.matches("OR\\s+WORD PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                    if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                            || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                        byteCounter += 2;
                    else byteCounter += 3;
                    if (Main.getBytes(row.get(10).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                    if (Main.getBytes(row.get(13).id) >= 2)
                        byteCounter += 2;
                    else byteCounter += 1;
                }
            } else if (str.matches("JNA\\s+.*"))
                if (Main.labels.containsKey(row.get(1).id)) {
                    if (Main.labels.get(row.get(1).id).getDelta(currentOffset + 2) == -1)
                        byteCounter += 4;
                    else byteCounter += 2;
                } else {
                    byteCounter += 4;
            }
        }else if (row.get(0).type.equals("identify")){
            if (str.matches(row.get(0).id + "\\s*:(\\s*|\\s+.*)") && Main.labels.containsKey(row.get(0).id)){
                byteCounter += 0;
            } else if (str.matches(row.get(0).id + "\\s+(DB|DW|DD)\\s+.+")) {
                if (row.get(1).id.equals("DB")) {
                    if (row.get(2).type.equals("string"))
                        byteCounter += Main.getBytes(row.get(2).id);
                    else
                        byteCounter += 1;
                } else if (row.get(1).id.equals("DW")) {
                    byteCounter += 2;
                } else
                    byteCounter += 4;
            }
        }
        bytes.add(byteCounter);
        return byteCounter;
    }


    void generateOpcodes(Scanner scan, PrintWriter writer){

        currentOffset = 0;
        String opcode;
        String str, source;
        int macroNow = 0;
        int i_bytes = 0;
        boolean errFlag;

        for (LinkedList<Lexeme> row : segmentCode) {

            errFlag = false;
            opcode = "";
            str = Main.rowToString(row);

            if (macroNow != 0) {
                macroNow--;
                source = "    |   " + str;
            } else
                source = scan.nextLine();

            if (str.matches("\\s*")) {
                writer.printf("");
                continue;
            }

            if (row.get(0).type.equals("instruction")) {
                if (str.matches("MOVSW(\\s*|\\s+.*)")) {
                    opcode = "A5";
                    if (row.size() > 1)
                        errFlag = true;
                } else if (row.get(0).id.equals("POP")) {
                    if (str.matches("POP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                        if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                                || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                            opcode = "8F " + makeModrm("00", "000", row.get(4).code + row.get(6).code);
                        else
                            opcode = row.get(1).code + ": 8F " + makeModrm("00", "000", row.get(4).code + row.get(6).code);
                        if (row.size() > 8)
                            errFlag = true;

                    } else if (str.matches("POP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                        if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                                || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                            opcode = "8F " + makeModrm(Main.toHex(row.get(8).id, 2), "000", row.get(4).code + row.get(6).code);
                        else opcode = row.get(1).code + ": 8F "
                                + makeModrm(Main.toHex(row.get(8).id, 2), "000", row.get(4).code + row.get(6).code);

                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("POP\\s+\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                        opcode = "8F " + makeModrm("00", "000", row.get(2).code + row.get(4).code);
                        if (row.size() > 6)
                            errFlag = true;

                    } else if (str.matches("POP\\s+\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                        opcode = "8F " + makeModrm(Main.toHex(row.get(6).id, 2), "000", row.get(2).code + row.get(4).code);
                        if (Main.getBytes(row.get(6).id) <= 2)
                            opcode += " " + Main.toHex(row.get(6).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(6).id, 2).substring(Main.toHex(row.get(6).id, 2).length() - 4);
                        if (row.size() > 8)
                            errFlag = true;
                    } else errFlag = true;

                } else if (row.get(0).id.equals("ROL")) {
                    if (str.matches("ROL\\s+[A-D][HL],1(\\s*|\\s+.*)")) {
                        opcode = "D0 " + makeModrm("11", "000", row.get(1).code);
                        if (row.size() > 4)
                            errFlag = true;

                    } else if (str.matches("ROL\\s+[A-D]X,1(\\s*|\\s+.*)")) {
                        opcode = "D1 " + makeModrm("11", "000", row.get(1).code);
                        if (row.size() > 4)
                            errFlag = true;
                    } else errFlag = true;

                } else if (row.get(0).id.equals("CMP")) {
                    if (str.matches("CMP\\s+[A-D][HL],\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                        opcode = "3A " + makeModrm("00", row.get(1).code, row.get(4).code + row.get(6).code);
                        if (row.size() > 8)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D][HL],[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                        if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            opcode = "3A " + makeModrm("00", row.get(1).code, row.get(6).code + row.get(8).code);
                        else opcode = row.get(3).code + ": 3A "
                                + makeModrm("00", row.get(1).code, row.get(6).code + row.get(8).code);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D][HL],\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                        opcode = "3A " + makeModrm(Main.toHex(row.get(8).id, 2), row.get(1).code, row.get(4).code + row.get(6).code);
                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D][HL],[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                        if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            opcode = "3A " + makeModrm(Main.toHex(row.get(10).id, 2), row.get(1).code, row.get(6).code + row.get(8).code);
                        else opcode = row.get(3).code + ": 3A "
                                + makeModrm(Main.toHex(row.get(10).id, 2), row.get(1).code, row.get(6).code + row.get(8).code);

                        if (Main.getBytes(row.get(10).id) <= 2)
                            opcode += " " + Main.toHex(row.get(10).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(10).id, 2).substring(Main.toHex(row.get(10).id, 2).length() - 4);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D]X,\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                        opcode = "3B " + makeModrm("00", row.get(1).code, row.get(4).code + row.get(6).code);
                        if (row.size() > 8)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D]X,[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\](\\s*|\\s+.*)")) {
                        if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            opcode = "3B " + makeModrm("00", row.get(1).code, row.get(6).code + row.get(8).code);
                        else opcode = row.get(3).code + ": 3B "
                                + makeModrm("00", row.get(1).code, row.get(6).code + row.get(8).code);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D]X,\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                        opcode = "3B " + makeModrm(Main.toHex(row.get(8).id, 2), row.get(1).code, row.get(4).code + row.get(6).code);
                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[A-D]X,[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\](\\s*|\\s+.*)")) {
                        if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            opcode = "3B " + makeModrm(Main.toHex(row.get(10).id, 2), row.get(1).code, row.get(6).code + row.get(8).code);
                        else opcode = row.get(3).code + ": 3B "
                                + makeModrm(Main.toHex(row.get(10).id, 2), row.get(1).code, row.get(6).code + row.get(8).code);

                        if (Main.getBytes(row.get(10).id) <= 2)
                            opcode += " " + Main.toHex(row.get(10).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(10).id, 2).substring(Main.toHex(row.get(10).id, 2).length() - 4);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\],[A-D][HL](\\s*|\\s+.*)")) {
                        opcode = "38 " + makeModrm("00", row.get(7).code, row.get(2).code + row.get(4).code);
                        if (row.size() > 8)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],[A-D][HL](\\s*|\\s+.*)")) {
                        if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                                || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                            opcode = "38 " + makeModrm("00", row.get(9).code, row.get(4).code + row.get(6).code);
                        else opcode = row.get(1).code + ": 38 "
                                + makeModrm("00", row.get(9).code, row.get(4).code + row.get(6).code);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D][HL](\\s*|\\s+.*)")) {
                        opcode = "38 " + makeModrm(Main.toHex(row.get(6).id, 2), row.get(9).code, row.get(2).code + row.get(4).code);
                        if (Main.getBytes(row.get(6).id) <= 2)
                            opcode += " " + Main.toHex(row.get(6).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(6).id, 2).substring(Main.toHex(row.get(6).id, 2).length() - 4);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D][HL](\\s*|\\s+.*)")) {
                        if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                                || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                            opcode = "38 " + makeModrm(Main.toHex(row.get(8).id, 2), row.get(11).code, row.get(4).code + row.get(6).code);
                        else opcode = row.get(1).code + ": 38 "
                                + makeModrm(Main.toHex(row.get(8).id, 2), row.get(11).code, row.get(4).code + row.get(6).code);

                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\],[A-D]X(\\s*|\\s+.*)")) {
                        opcode = "39 " + makeModrm("00", row.get(7).code, row.get(2).code + row.get(4).code);
                        if (row.size() > 8)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],[A-D]X(\\s*|\\s+.*)")) {
                        if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                                || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                            opcode = "39 " + makeModrm("00", row.get(9).code, row.get(4).code + row.get(6).code);
                        else opcode = row.get(1).code + ": 39 "
                                + makeModrm("00", row.get(9).code, row.get(4).code + row.get(6).code);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D]X(\\s*|\\s+.*)")) {
                        opcode = "39 " + makeModrm(Main.toHex(row.get(6).id, 2), row.get(9).code, row.get(2).code + row.get(4).code);
                        if (Main.getBytes(row.get(6).id) <= 2)
                            opcode += " " + Main.toHex(row.get(6).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(6).id, 2).substring(Main.toHex(row.get(6).id, 2).length() - 4);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("CMP\\s+[C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],[A-D]X(\\s*|\\s+.*)")) {
                        if ((row.get(1).id.equals("DS") && !row.get(4).id.equals("BP"))
                                || (row.get(1).id.equals("SS") && row.get(4).id.equals("BP")))
                            opcode = "39 " + makeModrm(Main.toHex(row.get(8).id, 2), row.get(11).code, row.get(4).code + row.get(6).code);
                        else opcode = row.get(1).code + ": 39 "
                                + makeModrm(Main.toHex(row.get(8).id, 2), row.get(11).code, row.get(4).code + row.get(6).code);

                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);
                        if (row.size() > 12)
                            errFlag = true;
                    } else errFlag = true;

                } else if (row.get(0).id.equals("MOV")) {
                    if (str.matches("MOV\\s+[A-D][HL],[A-D][HL](\\s*|\\s+.*)")) {
                        opcode = "8A " + makeModrm("11", row.get(1).code, row.get(3).code);
                        if (row.size() > 4)
                            errFlag = true;

                    } else if (str.matches("MOV\\s+[A-D]X,[A-D]X(\\s*|\\s+.*)")) {
                        opcode = "8B " + makeModrm("11", row.get(1).code, row.get(3).code);
                        if (row.size() > 4)
                            errFlag = true;
                    } else errFlag = true;

                } else if (row.get(0).id.equals("OR")) {
                    String pref = "";
                    if (str.matches("OR\\s+BYTE PTR \\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        opcode = "80 " + makeModrm("00", "001", row.get(4).code + row.get(6).code);

                        if (Main.getBytes(Main.toHex(row.get(9).id, 2)) == 1)
                            opcode += " " + Main.toHex(row.get(9).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(9).id, 2).substring(Main.toHex(row.get(9).id, 2).length() - 2);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("OR\\s+BYTE PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        if ((row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || (row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            opcode = makeModrm("00", "001", row.get(6).code + row.get(8).code);
                        else
                            opcode = row.get(3).code + ": 80 " + makeModrm("00", "001", row.get(6).code + row.get(8).code);

                        if (Main.getBytes(Main.toHex(row.get(11).id, 2)) == 1)
                            opcode += " " + Main.toHex(row.get(11).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(11).id, 2).substring(Main.toHex(row.get(11).id, 2).length() - 2);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("OR\\s+BYTE PTR \\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        opcode = pref + "80 " + makeModrm(Main.toHex(row.get(8).id, 2), "001", row.get(4).code + row.get(6).code);
                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);

                        if (Main.getBytes(Main.toHex(row.get(11).id, 2)) == 1)
                            opcode += " " + Main.toHex(row.get(11).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(11).id, 2).substring(Main.toHex(row.get(11).id, 2).length() - 2);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("OR\\s+BYTE PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        if (!(row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || !(row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            pref = row.get(3).code + ": ";
                        opcode = pref + "80 " + makeModrm(Main.toHex(row.get(10).id, 2), "001", row.get(6).code + row.get(8).code);

                        if (Main.getBytes(row.get(10).id) <= 2)
                            opcode += " " + Main.toHex(row.get(10).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(10).id, 2).substring(Main.toHex(row.get(10).id, 2).length() - 4);

                        if (Main.getBytes(Main.toHex(row.get(13).id, 2)) == 1)
                            opcode += " " + Main.toHex(row.get(13).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(13).id, 2).substring(Main.toHex(row.get(13).id, 2).length() - 2);
                        if (row.size() > 14)
                            errFlag = true;

                    } else if (str.matches("OR\\s+WORD PTR \\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        opcode = makeModrm("00", "001", row.get(4).code + row.get(6).code);

                        if (Main.getBytes(Main.toHex(row.get(9).id, 2)) == 1)
                            opcode = "83 " + opcode + " " + Main.toHex(row.get(9).id, 2);
                        else opcode = "81 " + opcode + " "
                                + Main.toHex(row.get(9).id, 2).substring(Main.toHex(row.get(9).id, 2).length() - 4);
                        if (row.size() > 10)
                            errFlag = true;

                    } else if (str.matches("OR\\s+WORD PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        if (!(row.get(3).id.equals("DS") && !row.get(6).id.equals("BP"))
                                || !(row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            pref = row.get(3).code + ": ";
                        opcode = makeModrm("00", "001", row.get(6).code + row.get(8).code);

                        if (Main.getBytes(Main.toHex(row.get(11).id, 2)) == 1)
                            opcode = pref + "83 " + opcode + " " + Main.toHex(row.get(11).id, 2);
                        else opcode = pref + "81" + opcode + " "
                                + Main.toHex(row.get(11).id, 2).substring(Main.toHex(row.get(11).id, 2).length() - 4);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("OR\\s+WORD PTR \\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        opcode = makeModrm(Main.toHex(row.get(8).id, 2), "001", row.get(4).code + row.get(6).code);
                        if (Main.getBytes(row.get(8).id) <= 2)
                            opcode += " " + Main.toHex(row.get(8).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(8).id, 2).substring(Main.toHex(row.get(8).id, 2).length() - 4);

                        if (Main.getBytes(Main.toHex(row.get(11).id, 2)) == 1)
                            opcode = "83 " + opcode + " " + Main.toHex(row.get(11).id, 2);
                        else opcode = "81 " + opcode + " "
                                + Main.toHex(row.get(11).id, 2).substring(Main.toHex(row.get(11).id, 2).length() - 4);
                        if (row.size() > 12)
                            errFlag = true;

                    } else if (str.matches("OR\\s+WORD PTR [C-GS]S:\\[(BP|BX)\\+(DI|SI)\\+\\d+\\],(\\d+|[01]+B|\".+\"|'.+')(\\s*|\\s+.*)")) {
                        if (!(row.get(3).id.equals("DS") && row.get(6).id.equals("BP"))
                                || !(row.get(3).id.equals("SS") && row.get(6).id.equals("BP")))
                            pref = row.get(3).code + ": ";
                        opcode = makeModrm(Main.toHex(row.get(10).id, 2), "001", row.get(6).code + row.get(8).code);

                        if (Main.getBytes(row.get(10).id) <= 2)
                            opcode += " " + Main.toHex(row.get(10).id, 2);
                        else
                            opcode += " " + Main.toHex(row.get(10).id, 2).substring(Main.toHex(row.get(10).id, 2).length() - 4);

                        if (Main.getBytes(Main.toHex(row.get(13).id, 2)) == 1)
                            opcode = pref + "83 " + opcode + " " + Main.toHex(row.get(13).id, 2);
                        else opcode = pref + "81 " + opcode + " "
                                + Main.toHex(row.get(13).id, 2).substring(Main.toHex(row.get(13).id, 2).length() - 4);
                        if (row.size() > 14)
                            errFlag = true;
                    } else errFlag = true;

                } else if (str.matches("JNA\\s+.*"))
                    if (Main.labels.containsKey(row.get(1).id)) {
                        int delt = Main.labels.get(row.get(1).id).getDelta(currentOffset + 2);
                        if (bytes.get(i_bytes) == 4) {
                            opcode = "0F 86 " + Main.toHex(Main.labels.get(row.get(1).id).offset);
                        } else
                            opcode = "76 " + Main.toHex(Integer.toString(delt), 2);
                        if (row.size() > 2)
                            errFlag = true;
                    } else {
                        opcode = "OF 86 0000";
                        errFlag = true;
                    }

            } else if (row.get(0).type.equals("identify")) {
                if (str.matches(row.get(0).id + "\\s*:(\\s*|\\s+.*)") && Main.labels.containsKey(row.get(0).id)) {
                    opcode = "";
                    if (row.size() > 2)
                        errFlag = true;
                } else if (str.matches(row.get(0).id + "\\s+(DB|DW|DD)\\s+.+")) {
                    if (row.get(1).id.equals("DB")) {
                        if (row.get(2).type.equals("string")) {
                            writer.printf(Main.toHex(currentOffset) + "  ");
                            int j = 0;
                            StringBuilder op = new StringBuilder();
                            str = row.get(2).id;
                            while (j < str.length() && j < 6) {
                                op.append(Main.toHex(str.substring(j, j + 1), 2)).append(" ");
                                j++;
                            }
                            writer.printf("%-18s%s\n", op.toString(), source);
                            op = new StringBuilder();
                            while (j < str.length()) {
                                op.append(Main.toHex(str.substring(j, j + 1), 2)).append(" ");
                                j++;
                                if (j % 6 == 0) {
                                    writer.printf("%6s%-18s\n", "", op.toString());
                                    op = new StringBuilder();
                                }
                            }
                            if (!op.toString().equals(""))
                                writer.printf("%6s%-18s\n", "", op.toString());
                            currentOffset += bytes.get(i_bytes);
                            i_bytes++;
                            continue;
                        } else if (row.get(2).type.matches("decimal|binary")){
                            opcode = Main.toHex(row.get(2).id, 2);
                            if (opcode.length() > 2) {
                                errFlag = true;
                                opcode = opcode.substring(opcode.length() - 2);
                            }
                        } else errFlag = true;
                    } else if (row.get(1).id.equals("DW") && row.get(2).type.matches("decimal|binary")) {
                        opcode = Main.toHex(row.get(2).id, 4);
                        if (opcode.length() > 4) {
                            errFlag = true;
                            opcode = opcode.substring(opcode.length() - 4);
                        }
                    } else if (row.get(2).type.matches("decimal|binary")){
                        opcode = Main.toHex(row.get(2).id, 8);
                        if (opcode.length() > 8){
                            errFlag = true;
                            opcode = opcode.substring(opcode.length() - 8);
                        }
                    } else errFlag = true;
                    if (row.size() > 3)
                        errFlag = true;
                } else if (Main.macroses.containsKey(row.get(0).id) && !row.get(0).code.equals("undefined")
                        && Main.macroses.get(row.get(0).id).callNumbers != 0) {
                    writer.printf("%24s%s\n", "", source);
                    macroNow = Main.macroses.get(row.get(0).id).macroCode.size();
                    Main.macroses.get(row.get(0).id).callNumbers--;
                    continue;
                } else {
                    writer.printf("%24s%s\n", "", source);
                    writer.printf("%18s%s\n", "", "Error occurred");
                    Main.errors++;
                    continue;
                }
            } else {
                writer.printf("%24s%s\n", "", source);
                writer.printf("%18s%s\n", "", "Error occurred");
                Main.errors++;
                continue;
            }

            writer.printf(Main.toHex(currentOffset) + "  ");
            currentOffset += bytes.get(i_bytes);
            writer.printf("%-18s%s\n", opcode, source);
            if (errFlag) {
                writer.printf("%18s%s\n", "", "Error occurred");
                Main.errors++;
            }
            i_bytes++;
        }
    }


    private String makeModrm(String mod, String reg, String rm){

        if (!mod.equals("00") && !mod.equals("11") )
            if (mod.length() == 2)
                mod = "01";
            else
                mod = "10";

        String res = mod + reg + rm + "B";
        res = Main.toHex(res, 2);
        return res;
    }

}
