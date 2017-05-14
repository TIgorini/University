   
class Search{    

    static void last_max(int[][] mass, int m, int n){

        int i, j,
            max_posi = 0,
            max_posj = 0;
        int max = mass[0][0];
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {

                if (max <= mass[i][j]){
                    max = mass[i][j];
                    max_posi = i;
                    max_posj = j;
                }
            }
        }
        System.out.println("Last max: " + max);
        System.out.println("Position: " + max_posi + ", " + max_posj);
    }
}    
