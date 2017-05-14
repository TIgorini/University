import java.util.Random;

class IOClass {

    static void input(int[][] mass, int m, int n){

        int i, j;
        Random random = new Random();
        for (i = 0; i < m; i++)
            for (j = 0; j < n; j++)
                mass[i][j] = random.nextInt(9);
    }

    static void output(int[][] mass, int m, int n){

        System.out.println("Mass:");
        int i, j;
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++)
                System.out.print(mass[i][j] + " ");
            System.out.println();
        }
    }
}
