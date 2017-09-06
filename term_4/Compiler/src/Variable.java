
class Variable {

    String id;
    int offset;
    String seg;

    Variable(String id, int address, String seg){
        this.id = id;
        offset = address;
        this.seg = seg;
    }
}
