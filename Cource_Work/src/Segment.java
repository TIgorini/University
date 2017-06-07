
public class Segment {

    String name;
    private int begIndx, endIndx;

    Segment(){
        begIndx = 0;
        endIndx = 0;
    }
    Segment(String name, int begIndx){
        this.name = name;
        this.begIndx = begIndx;
    }

    public void setEndIndx(int endIndx){
        this.endIndx = endIndx;
    }

    public boolean isSegment(){
        return endIndx - begIndx > 0;
    }
}
