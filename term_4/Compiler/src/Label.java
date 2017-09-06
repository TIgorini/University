
class Label {

    String id;
    int offset;
    String seg;

    Label(String id, int address, String seg){
        this.id = id;
        offset = address;
        this.seg = seg;
    }

    int getDelta(int curOffset){

        int res = offset - curOffset;
        if (res > 127 || res < -128)
            return -1;
        if (res < 0)
            res = 256 + res;
        return res;
    }
}
