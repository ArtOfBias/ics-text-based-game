public class Room {
    int[][] items = new int[3][3];
    int[][] enemies = new int[3][3];
    int[][] shard = new int[3][3];
    int[] entrance = new int[2];

    // entrance is either {0,1}, {1,0}, {1,2}, {2,1}
    public Room(int entrance[]){
        this.entrance = entrance.clone();
        refresh(entrance);
    }

    // newEntrance is either {0,1}, {1,0}, {1,2}, {2,1}
    public void refresh(int newEntrance[]){
        entrance = newEntrance.clone();
        // places items
        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                if (x == entrance[0] && y == entrance[1]) items[x][y] = 0;
                else items[x][y] = randInt(1, 5);
            }
        }

        // places enemies
        int enemyCount = randInt(3, 5);
        int enemyPosition;
        int[] enemiesInt = new int[enemyCount];
        for (int i = 0; i < enemyCount; i++){
            enemyPosition = randInt(1, 9);
            while (enemyPosition == 3 * entrance[1] + entrance[0] + 1 || inIntArray(enemyPosition, enemiesInt)){
                enemyPosition = randInt(1, 9);
            }
            enemiesInt[i] = enemyPosition;
        }
        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                if (inIntArray(3 * y + x + 1, enemiesInt)) enemies[x][y] = randInt(1, 4);
                else enemies[x][y] = 0;
            }
        }

        // places shard
        int shardPositionInt = randInt(1, 9);
        while (shardPositionInt == 3 * entrance[1] + entrance[0] + 1){
            shardPositionInt = randInt(1, 9);
        }
        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                if ((3 * y + x + 1) == shardPositionInt) shard[x][y] = 1;
                else shard[x][y] = 0;
            }
        }
    }

    // prints out the locations of player, items, enemies, and portal shard
    public void print(){
        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                if (x == entrance[0] && y == entrance[1]) System.out.print("x");
                else if (shard[x][y] == 1) System.out.print("a");
                else System.out.print("_");
                System.out.print(" ");
            }
            System.out.print("\n");
        }

        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                System.out.print(items[x][y] + " ");
            }
            System.out.print("\n");
        }

        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                System.out.print(enemies[x][y] + " ");
            }
            System.out.print("\n");
        }
    }

    // checks if integer x is in the integer array arr
    // pre: x is an integer
    // post: returns true if x found in arr, false otherwise
    private static boolean inIntArray(int x, int[] arr){
        for (int i = 0; i < arr.length; i++) if (arr[i] == x) return true;
        return false;
    }

    // generates a random integer from a to b, inclusive
    // pre: a and b are integers, assumes a < b
    // post: returns an integer between a and b, inclusive
    private static int randInt(int a, int b){
        return (int)Math.floor(randNumber(a, b + 1));
    }

    // generates a random number from a to b, including a but excluding b
    // pre: assumes a < b
    // post: returns x in between a (inclusive) and b (exclusive)
    private static double randNumber(double a, double b){
        double x = Math.random();
        return x * (b - a) + a;
    }
}