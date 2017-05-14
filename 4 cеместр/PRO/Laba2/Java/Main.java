
public class Main {

    public static void main(String[] argz) {

        int m = 8, n = 8;
        int[][] mass = new int[m][n];
        IOClass.input(mass,m,n);
        IOClass.output(mass,m,n);
        Search.last_max(mass,m,n);
    }

}
