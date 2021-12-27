// Portals is a simple text based dungeon crawler game
// The player is trapped by a corrupted portal generator
// and needs to fight through enemies and collect portal
// shards to fix the generator and escape

import java.util.Scanner;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

public class Main {
    static Scanner scan = new Scanner(System.in);

    static String TEXT_RESET = "\033[0m";
    static String TEXT_RED = "\033[38;5;9m";
    static String TEXT_ORANGE = "\033[38;5;208m";
    static String TEXT_DARK_GRAY = "\033[38;5;246m";
    static String TEXT_LIGHT_BLUE = "\033[96m";
    static String TEXT_LIGHT_GREEN = "\033[38;5;47m";

    static String PLAYER_SYMBOL = TEXT_LIGHT_BLUE + "♦" + TEXT_RESET;
    static String ENEMY_SYMBOL = TEXT_RED + "X" + TEXT_RESET;

    static int[] ENT_TOP = {1,0};
    static int[] ENT_BOTTOM = {1,2};
    static int[] ENT_LEFT = {0,1};
    static int[] ENT_RIGHT = {2,1};

    static String name;

    static int health;
    static int shieldMax;
    static int shield;
    static String weapon;
    static double baseDamage;
    static double penetration;

    // these are multipliers
    static double strength;
    static double agility;

    // damage, speed, and penetration multipliers
    static double[] attackFast;
    static double[] attackNormal;
    static double[] attackHeavy;

    // block[0] < 1 always
    static double[] block;

    static int[] position;

    static Enemy trionian;
    static Enemy vortrice;
    static Enemy muric;
    static Enemy thauria;
    static Enemy boss;

    static int shardCount;
    static int shardMax;

    static long startTime;
    static long fastest;

    static int playCount = 0;


    public static void main(String[] args){
        while (true){
            health = 100;
            shieldMax = 50;
            shield = 50;
            weapon = "steel sword belonging to your father";
            baseDamage = 25;
            penetration = 25;

            // these are multipliers
            strength = 1;
            agility = 1;

            // damage, speed, and penetration multipliers
            attackFast = new double[] {0.5,3,2};
            attackNormal = new double[] {1,1,1};
            attackHeavy = new double[] {2,0.5,3};

            // block[0] < 1 always
            block = new double[] {0.75, 3};

            shardCount = 0;
            shardMax = 6;
            // TODO: reset values to non-testing (currently done)

            clear();
            boolean quit = startScreen();

            if (quit) break;

            resetEnemies();

            clear();
            controlsHelp();
            next();
            scan.nextLine();
            clear();

            // sets player name
            System.out.println("What is your name? You can always change this later.");
            name = nextLineDark().strip();
            System.out.println("Alright, your name is " + TEXT_LIGHT_BLUE + name + TEXT_RESET + "!");
            next();
            scan.nextLine();
            clear();

            Room mainRoom = new Room(ENT_BOTTOM);
            // start.print();
            position = ENT_BOTTOM.clone();
            mainRoom.enemies = new int[][] {{0,0,1},{0,2,0},{0,0,1}};
            
            int enemyInt;
            int itemInt;

            if (playCount == 0){
                System.out.println("Welp, it looks like the portal generator is corrupted");
                System.out.println("You slump against the wall in despair");
                next();
                scan.nextLine();
                clear();

                System.out.println("You're stuck down here, in this endless maze of rooms and portals");
                wait(1300);
                System.out.print("With those...");
                wait(1000);
                System.out.print(" things\n");
                wait(1000);
                System.out.println("Their noises gets closer every second");
                wait(1000);
                System.out.println("You activate your shield, a blue sphere of light surrounds you, illuminating the dark room");
                wait(2000);
                System.out.println("You grab your weapon, the " + weapon);
                next();
                scan.nextLine();
                clear();
                
                System.out.println("Suddenly, you remember every room has a portal shard");
                System.out.println("You'll need " + shardMax + " to repair the generator");
                next();
                scan.nextLine();
                clear();

                minimap(mainRoom);
                System.out.println("This is a map of the room");
                System.out.println("The orange portal is the one you entered from, you cannot go back through it");
                System.out.println("The blue portals take you to a new room");
                System.out.println();
                System.out.println("Once you leave a room, you can never return");
                next();
                scan.nextLine();
                clear();

                minimap(mainRoom);
                System.out.println("The " + PLAYER_SYMBOL + " is your position");
                System.out.println("Enemies are marked by " + ENEMY_SYMBOL);
                System.out.println("Question marks are locations you have not yet explored");
                System.out.println("Each location has an item, which improve your various statistics");
                next();
                scan.nextLine();
                clear();
            }

            System.out.println("Go though the rooms\nFind the shards\nRepair the generator\nGET OUT");
            next();
            scan.nextLine();
            clear();

            int random;
            char[] goodLuck = "GOOD LUCK".toCharArray();
            for (int j = 0; j < 5; j++){
                for (int i = 0; i < goodLuck.length; i++){
                    random = randInt(1, 3);
                    if (random == 1) System.out.print(TEXT_LIGHT_BLUE);
                    else if (random == 2) System.out.print(TEXT_ORANGE);
                    else if (random == 3) System.out.print(TEXT_RESET);
                    System.out.print(goodLuck[i]);
                }
                try {
                    Thread.sleep(randInt(100, 1000));
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                clear();
            }
            System.out.print(TEXT_RESET);

            startTime = System.currentTimeMillis();

            boolean win = true;
            while (shardCount < shardMax && win){
                mainRoom.refresh(position);
                while (true){
                    clear();
                    resetEnemies();
                    movement(mainRoom);
                    if (inIntArray(-1, position) || inIntArray(3, position)){
                        position[0] = (position[0] + 3) % 3;
                        position[1] = (position[1] + 3) % 3;
                        break;
                    }
                    enemyInt = mainRoom.enemies[position[0]][position[1]];
                    itemInt = mainRoom.items[position[0]][position[1]];
                    if (enemyInt != 0){
                        clear();
                        if (enemyInt == 1) interactEnemy(trionian);
                        if (enemyInt == 2) interactEnemy(vortrice);
                        if (enemyInt == 3) interactEnemy(muric);
                        if (enemyInt == 4) interactEnemy(thauria);
                        if (health <= 0){
                            win = false;
                            break;
                        }
                        clear();
                        mainRoom.enemies[position[0]][position[1]] = 0;
                    }
                    if (itemInt != 0){
                        clear();
                        interactItem(itemInt);
                        clear();
                        mainRoom.items[position[0]][position[1]] = 0;
                    }
                    if (mainRoom.shard[position[0]][position[1]] != 0){
                        shardCount++;
                        System.out.println("You found a portal shard");
                        if (shardCount < shardMax){
                            System.out.println("You need " + (shardMax - shardCount) + " more to control the portals and escape");
                        }
                        else {
                            System.out.println("You've collected " + shardMax + " portal shards!");
                            System.out.println("Go to a portal to repair the corrupted generator and escape this treacherous world");
                        }
                        next();
                        scan.nextLine();
                        clear();
                        mainRoom.shard[position[0]][position[1]] = 0;
                    }
                }
            }
            if (win){
                clear();
                System.out.println("Well, it looks like you've finally made it out.");
                wait(1000);
                System.out.println("Something isn't right. What is that...");
                next();
                scan.nextLine();
                clear();
                interactEnemy(boss);
            }
            clear();
            gameEnd(win);
        }
        scan.close();
    }

    // clears the console
    // post: the console is blank
    public static void clear(){
        System.out.print("\033[H\033[2J");
    };

    // prints "next" in orange
    // post: prints "next" in orange
    public static void next(){
        System.out.println(TEXT_ORANGE + "next" + TEXT_RESET);
    }
    
    // prints "back" in orange
    // post: prints "back" in orange
    public static void back(){
        System.out.println(TEXT_ORANGE + "back" + TEXT_RESET);
    }
    
    // clears the console, prints "invalid input" and waits until user presses enter before continuing, then clears the console
    // post: clears the console, prints "invalid input" and waits until user presses enter before continuing, then clears the console
    public static void invalidInput(){
        clear();
        System.out.println("invalid input");
        back();
        scan.nextLine();
        clear();
    }

    // waits time amount of time
    // pre: time is in milliseconds
    // post: waits the given amount of time
    public static void wait(int time){
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    // generates a random number from a to b, including a but excluding b
    // pre: assumes a < b
    // post: returns x in between a (inclusive) and b (exclusive)
    public static double randNumber(double a, double b){
        double x = Math.random();
        return x * (b - a) + a;
    }

    // generates a random integer from a to b, inclusive
    // pre: a and b are integers, assumes a < b
    // post: returns an integer between a and b, inclusive
    public static int randInt(int a, int b){
        return (int)Math.floor(randNumber(a, b + 1));
    }

    // changes the name variable
    // pre: console is cleared before function is called
    // post: the name variable is changed to the new value the user enters
    public static void editName(){
        System.out.println("EDIT NAME");
        System.out.println("Your name is currently " + TEXT_LIGHT_BLUE + name + TEXT_RESET);
        System.out.println("Enter a new name:");
        name = nextLineDark().strip();
        System.out.println("Alright, your name is now " + TEXT_LIGHT_BLUE + name + "!");
    }

    // displays the main menu of the game
    // pre: console is cleared before function is called
    // post: returns true if player quits, false if not
    public static boolean startScreen(){
        String TITLE = "    ____             __        __    \n   / __ \\____  _____/ /_____ _/ /____\n  / /_/ / __ \\/ ___/ __/ __ `/ / ___/\n / ____/ /_/ / /  / /_/ /_/ / (__  ) \n/_/    \\____/_/   \\__/\\__,_/_/____/  ";
        char[] TITLE_ARRAY = TITLE.toCharArray();
        int random;
        for (int j = 0; j < 5; j++){
            for (int i = 0; i < TITLE.length(); i++){
                random = randInt(1, 3);
                if (random == 1) System.out.print(TEXT_LIGHT_BLUE);
                else if (random == 2) System.out.print(TEXT_ORANGE);
                else if (random == 3) System.out.print(TEXT_RESET);
                System.out.print(TITLE_ARRAY[i]);
            }
            try {
                Thread.sleep(randInt(100, 1000));
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            clear();
        }
        while (true){
            System.out.println(TEXT_LIGHT_BLUE + TITLE + TEXT_RESET);
            System.out.println("             m - menu");
            System.out.println("             s - start");
            System.out.println("             q - quit");
            String choice = nextLineDark().toLowerCase().strip();
            if (choice.equals("q")){
                clear();
                return true;
            }
            else if (choice.equals("m")){
                clear();
                menu();
            }
            else if (choice.equals("s")){
                clear();
                break;
            }
            else {
                invalidInput();
            }
        }
        return false;
    }

    // displays the menu
    // pre: console is cleared before function is called
    // post: allows the player to navigate the menu
    public static void menu(){
        String input;
        while (true){
            System.out.println("Enter the letters following to navigate to the corresponding page");
            System.out.println(TEXT_LIGHT_BLUE + "c " + TEXT_RESET + "- controls");
            System.out.println(TEXT_LIGHT_BLUE + "n " + TEXT_RESET + "- edit your name");
            back();
            input = scan.nextLine().toLowerCase().strip();
            clear();
            if (input.equals("")){
                break;
            }
            else if (input.equals("c")){
                controlsHelp();
                back();
                scan.nextLine();
                clear();
            }
            else if (input.equals("n")){
                editName();
                back();
                scan.nextLine();
                clear();
            }
            else {
                System.out.println("Invalid option");
                back();
                scan.nextLine();
                clear();
            }
        }
    }

    // exact funcitonality as Scanner.nextLine() but input text is made dark
    // pre: user enters an input
    // post: returns user input
    public static String nextLineDark(){
        String input;
        System.out.print(TEXT_DARK_GRAY);
        input = scan.nextLine();
        System.out.print(TEXT_RESET);
        return input;
    }

    // shows the controls help page
    // pre: console is cleared before function is called
    // post: prints the controls
    public static void controlsHelp(){
        System.out.println("CONTROLS");
        System.out.println(TEXT_LIGHT_BLUE + "a "+ TEXT_RESET + "- move left");
        System.out.println(TEXT_LIGHT_BLUE + "w "+ TEXT_RESET + "- move up");
        System.out.println(TEXT_LIGHT_BLUE + "s "+ TEXT_RESET + "- move down");
        System.out.println(TEXT_LIGHT_BLUE + "d "+ TEXT_RESET + "- move right");
        System.out.println(TEXT_LIGHT_BLUE + "m "+ TEXT_RESET + "- menu");
        System.out.println(TEXT_LIGHT_BLUE + "return " + TEXT_RESET + "- " + TEXT_ORANGE + "next " + TEXT_RESET + "or " + TEXT_ORANGE + "back" + TEXT_RESET);
    }

    // formats a length of time in milliseconds to h:m:s.ms format
    // pre: timeMillis is a long representing time in milliseconds
    // post: returns a formatted String of the time length in h:m:s.ms
    public static String formatTime(long timeMillis){
        long ms = timeMillis % 1000;
        long second = (timeMillis / 1000) % 60;
        long minute = (timeMillis / 60000) % 60;
        long hour = timeMillis / 360000;
        return hour + ":" + minute + ":" + second + "." + ms;
    }

    // ends the game, win if state is true, loses if false
    // clears inventory and returns player to main menu
    // pre: state is a boolean
    // post: prints a win screen if true as well as the time it took the player to finish, a loss screen if false
    public static void gameEnd(boolean state){
        if (state){
            System.out.println("Congradulations, you fixed the portal generator and finally escaped");
            if (fastest == 0 || System.currentTimeMillis() - startTime < fastest) fastest = System.currentTimeMillis() - startTime;
            System.out.println("You finished the game in " + formatTime(System.currentTimeMillis() - startTime));
            System.out.println("The fastest run was " + formatTime(fastest));
            next();
            scan.nextLine();
            clear();
        }
        else {
            System.out.println(TEXT_RED + "You died");
            System.out.println("Looks like you weren't quite up to the task");
            next();
            scan.nextLine();
            clear();
        }
        playCount++;
    }

    // combat with foe, ends game if player loses
    // pre: console is cleared before function is called, foe is an Enemy object and its stats have been reset via resetEnemies()
    // post: continues if player wins, calls gameEnd(false) if player loses
    public static void interactEnemy(Enemy foe){
        shield = shieldMax;
        String choice;
        int foeChoice;
        double random;
        String attackName;
        String foeAttackName;
        int damage;
        double[] attack = new double[3];
        double[] foeAttack = new double[3];

        System.out.println("You've come across a " + foe.type);
        next();
        scan.nextLine();
        clear();


        while (true){
            while (true){
                healthbars(foe);
                System.out.println("1 - charge");
                System.out.println("2 - attack");
                System.out.println("3 - heavy blows");
                System.out.println("4 - block");
                System.out.println();
                choice = nextLineDark().toLowerCase().strip();
                if (choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4")) break;
                invalidInput();
            }
            foeChoice = randInt(1,3);
            if (choice.equals("4")){
                attackName = "block";
                // player blocks, enemy blocks
                if (foeChoice == 3){
                    foeAttackName = "block";
                    shield += (int)(0.2 * shieldMax);
                    if (shield > shieldMax) shield = shieldMax;
                    clear();
                    healthbars(foe);
                    combatMessage(attackName, foeAttackName, foe.type);
                    System.out.println("Nobody attacked");
                    next();
                    scan.nextLine();
                    clear();
                }
                // player blocks, enemy attacks
                else {
                    if (foeChoice == 1){
                        foeAttack = foe.attack1;
                        foeAttackName = foe.attack1Name;
                    }
                    else {
                        foeAttack = foe.attack2;
                        foeAttackName = foe.attack2Name;
                    }
                    random = randNumber(0, agility + foe.agility * foeAttack[1]);
                    if (random > agility){
                        // enemy hits
                        if (foe.penetration * foeAttack[2] > block[1]){
                            damage = (int)(foe.baseDamage * foeAttack[0] * foe.strength * block[0]);
                            shield -= damage;
                            if (shield <= 0){
                                health += shield;
                                shield = 0;
                            }
                            if (health < 0) health = 0;
                            clear();
                            healthbars(foe);
                            combatMessage(attackName, foeAttackName, foe.type);
                            System.out.println("The " + foe.type + " did " + damage + " damage");
                            next();
                            scan.nextLine();
                            clear();
                        }
                        // blocked
                        else {
                            shield += (int)(0.2 * shieldMax);
                            if (shield > shieldMax) shield = shieldMax;
                            clear();
                            healthbars(foe);
                            combatMessage(attackName, foeAttackName, foe.type);
                            System.out.println("You blocked the attack");
                            next();
                            scan.nextLine();
                            clear();
                        }
                    }
                    // dodged
                    else {
                        shield += (int)(0.2 * shieldMax);
                        if (shield > shieldMax) shield = shieldMax;
                        clear();
                        healthbars(foe);
                        combatMessage(attackName, foeAttackName, foe.type);
                        System.out.println("You dodged the attack");
                        next();
                        scan.nextLine();
                        clear();
                    }
                }
            }
            else {
                if (choice.equals("1")){
                    attack = attackFast.clone();
                    attackName = "Quick Thrust";
                }
                else if (choice.equals("2")){
                    attack = attackNormal.clone();
                    attackName = "Fierce Strike";
                }
                else {
                    attack = attackHeavy.clone();
                    attackName = "Heavy Blow";
                }
                // player attacks, enemy blocks
                if (foeChoice == 3){
                    foeAttackName = "block";
                    random = randNumber(0, agility * attack[1] + foe.agility);
                    if (random > agility * attack[1] + foe.agility){
                        // player hits
                        if (penetration * attack[2] > foe.block[1]){
                            damage = (int)(baseDamage * attack[0] * strength * foe.block[0]);
                            foe.shield -= damage;
                            if (foe.shield <= 0){
                                foe.health += foe.shield;
                                shield = 0;
                            }
                            if (foe.health < 0) foe.health = 0;
                            clear();
                            healthbars(foe);
                            combatMessage(attackName, foeAttackName, foe.type);
                            System.out.println("You did " + damage + " damage");
                            next();
                            scan.nextLine();
                            clear();
                        }
                        // player attack blocked
                        else {
                            clear();
                            healthbars(foe);
                            combatMessage(attackName, foeAttackName, foe.type);
                            System.out.println("The " + foe.type + " blocked your attack");
                            next();
                            scan.nextLine();
                            clear();
                        }
                    }
                    // enemy dodged
                    else {
                        clear();
                        healthbars(foe);
                        combatMessage(attackName, foeAttackName, foe.type);
                        System.out.println("The " + foe.type + " dodged your attack");
                        next();
                        scan.nextLine();
                        clear();
                    }
                }
                // player attacks, enemy attacks
                else {
                    if (foeChoice == 1){
                        foeAttack = foe.attack1;
                        foeAttackName = foe.attack1Name;
                    }
                    else {
                        foeAttack = foe.attack2;
                        foeAttackName = foe.attack2Name;
                    }
                    random = randNumber(0, (agility * attack[1] + penetration) * strength + (foe.agility * foeAttack[1] + foe.penetration) * foe.strength);
                    // enemy hits
                    if (random > (agility * attack[1] + penetration) * strength){
                        damage = (int)(foe.baseDamage * foeAttack[0] * foe.strength);
                        shield -= damage;
                        if (shield <= 0){
                            health += shield;
                            shield = 0;
                        }
                        if (health < 0) health = 0;
                        clear();
                        healthbars(foe);
                        combatMessage(attackName, foeAttackName, foe.type);
                        System.out.println("The " + foe.type + " did " + damage + " damage");
                        next();
                        scan.nextLine();
                        clear();
                    }
                    // player hits
                    else {
                        damage = (int)(baseDamage * attack[0] * strength);
                        foe.shield -= damage;
                        if (foe.shield <= 0){
                            foe.health += foe.shield;
                            foe.shield = 0;
                        }
                        if (foe.health < 0) foe.health = 0;
                        clear();
                        healthbars(foe);
                        combatMessage(attackName, foeAttackName, foe.type);
                        System.out.println("You did " + damage + " damage");
                        next();
                        scan.nextLine();
                        clear();
                    }
                }
            }
            if (foe.health <= 0){
                clear();
                System.out.println("You've defeated a " + foe.type);
                next();
                scan.nextLine();
                break;
            }
            if (health <= 0) break;
        }
    }

    // interacts with the item specified by itemIndex
    // pre: itemIndex is an integer between 1 and 5, inclusive
    // post: boosts player stat depending on item
    public static void interactItem(int itemIndex){
        Dictionary<Integer, String> items = new Hashtable<Integer, String>();
        items.put(1, "Small Health Potion");
        items.put(2, "Large Health Potion");
        items.put(3, "Shield Crystal");
        items.put(4, "Strength Potion");
        items.put(5, "Six Pack of Energy Drinks");

        System.out.println("You found a " + items.get(itemIndex) + " on the ground");
        if (itemIndex == 1){
            System.out.println("You drink it, a wave of relief washes over you");
            System.out.println("You can feel the warm blood rushing through your veins again");
            health += 10;
        }
        else if (itemIndex == 2){
            System.out.println("The potion tastes of heaven");
            System.out.println("You feel like a phoenix rising from its ashes as the sweet liquid washes down your throat");
            health += 25;
        }
        else if (itemIndex == 3){
            System.out.println("You put it inside your shield generator, the thin blue sphere of light surrounding you glows brighter");
            shieldMax += 10;
        }
        else if (itemIndex == 4){
            System.out.println("It tastes metallic, like blood");
            System.out.println("Your grip on your weapon tightens, you feel like you can break through a bick wall with your bare hands");
            strength *= 1.2;
        }
        else if (itemIndex == 5){
            System.out.println("You drink it all");
            System.out.println("Your vision sharpens, everything around you seems to move in slow motion");
            System.out.println("A chunk of ceiling falls down, but you catch it within milliseconds");
        }
        next();
        scan.nextLine();
    }

    // gets the player to choose where to move 
    // pre: values in position are integers between 0 and 2, inclusive, currentRoom is a Room object
    // post: the player moves to a position specified
    public static void movement(Room currentRoom){
        String choice;
        int positionInt = position[1] * 3 + position[0] + 1;

        while (true){
            minimap(currentRoom);
            
            choice = nextLineDark().toLowerCase().strip();
            if (choice.equals("m")){
                clear();
                menu();
                continue;
            }
            else if (choice.equals("w")){
                if (positionInt == 1 || positionInt == 3 || ((Arrays.equals(position, currentRoom.entrance) && Arrays.equals(currentRoom.entrance, ENT_TOP)))){
                    invalidInput();
                    continue;
                }
                else position[1]--;
            }
            else if (choice.equals("a")){
                if (positionInt == 1 || positionInt == 7 || ((Arrays.equals(position, currentRoom.entrance) && Arrays.equals(currentRoom.entrance, ENT_LEFT)))){
                    invalidInput();
                    continue;
                }
                else position[0]--;
            }
            else if (choice.equals("s")){
                if (positionInt == 7 || positionInt == 9 || ((Arrays.equals(position, currentRoom.entrance) && Arrays.equals(currentRoom.entrance, ENT_BOTTOM)))){
                    invalidInput();
                    continue;
                }
                else position[1]++;
            }
            else if (choice.equals("d")){
                if (positionInt == 3 || positionInt == 9 || ((Arrays.equals(position, currentRoom.entrance) && Arrays.equals(currentRoom.entrance, ENT_RIGHT)))){
                    invalidInput();
                    continue;
                }
                else position[0]++;
            }
            else {
                invalidInput();
                continue;
            }
            break;
        }
    }
    
    // displays the minimap
    // pre: console is cleared before function is called
    // post: prints a map of the current room, enemies are marked as "X", the player marked as "♦", and a "?" for unvisited squares without enemies
    public static void minimap(Room currentRoom){
        String[] mapList = new String[9];
        for (int y = 0; y < 3; y++){
            for (int x = 0; x < 3; x++){
                if (position[0] == x && position[1] == y) mapList[y * 3 + x % 3] = PLAYER_SYMBOL;
                else if (currentRoom.enemies[x][y] != 0) mapList[y * 3 + x % 3] = ENEMY_SYMBOL;
                else if (currentRoom.items[x][y] == 0 && currentRoom.shard[x][y] == 0) mapList[y * 3 + x % 3] = " ";
                else mapList[y * 3 + x % 3] = "?";
            }
        }
        if (Arrays.equals(currentRoom.entrance, ENT_TOP)){
            System.out.println("╔═══╤" + TEXT_ORANGE + "───" + TEXT_RESET + "╤═══╗");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[0], mapList[1], mapList[2]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf(TEXT_LIGHT_BLUE + "│" + TEXT_RESET + " %s │ %s │ %s " + TEXT_LIGHT_BLUE + "│" + TEXT_RESET + "\n",mapList[3], mapList[4], mapList[5]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[6], mapList[7], mapList[8]);
            System.out.println("╚═══╧" + TEXT_LIGHT_BLUE + "───" + TEXT_RESET + "╧═══╝");
        }
        else if (Arrays.equals(currentRoom.entrance, ENT_RIGHT)){
            System.out.println("╔═══╤" + TEXT_LIGHT_BLUE + "───" + TEXT_RESET + "╤═══╗");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[0], mapList[1], mapList[2]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf(TEXT_LIGHT_BLUE + "│" + TEXT_RESET + " %s │ %s │ %s " + TEXT_ORANGE + "│" + TEXT_RESET + "\n",mapList[3], mapList[4], mapList[5]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[6], mapList[7], mapList[8]);
            System.out.println("╚═══╧" + TEXT_LIGHT_BLUE + "───" + TEXT_RESET + "╧═══╝");
        }
        else if (Arrays.equals(currentRoom.entrance, ENT_BOTTOM)){
            System.out.println("╔═══╤" + TEXT_LIGHT_BLUE + "───" + TEXT_RESET + "╤═══╗");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[0], mapList[1], mapList[2]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf(TEXT_LIGHT_BLUE + "│" + TEXT_RESET + " %s │ %s │ %s " + TEXT_LIGHT_BLUE + "│" + TEXT_RESET + "\n",mapList[3], mapList[4], mapList[5]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[6], mapList[7], mapList[8]);
            System.out.println("╚═══╧" + TEXT_ORANGE + "───" + TEXT_RESET + "╧═══╝");
        }
        else if (Arrays.equals(currentRoom.entrance, ENT_LEFT)){
            System.out.println("╔═══╤" + TEXT_LIGHT_BLUE + "───" + TEXT_RESET + "╤═══╗");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[0], mapList[1], mapList[2]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf(TEXT_ORANGE + "│" + TEXT_RESET + " %s │ %s │ %s " + TEXT_LIGHT_BLUE + "│" + TEXT_RESET + "\n",mapList[3], mapList[4], mapList[5]);
            System.out.println("╟───┼───┼───╢");
            System.out.printf("║ %s │ %s │ %s ║\n", mapList[6], mapList[7], mapList[8]);
            System.out.println("╚═══╧" + TEXT_LIGHT_BLUE + "───" + TEXT_RESET + "╧═══╝");
        }
    }

    // displays healthbars of player and enemy, only to be used inside interactEnemy()
    // pre: console is cleared before function is called, foe is an Enemy object, function is only called inside interactEnemy()
    // post: displays the health bars of the player and enemy, green for health, blue for shield
    public static void healthbars(Enemy foe){
        int size = 70;
        String healthColour = "\033[48;5;2m";
        String healthTextColour = "\033[38;5;2m";
        String shieldColour = "\033[48;5;14m";
        String shieldTextColour = "\033[38;5;14m";
        double scale = 10;

        String healthString = healthTextColour + health + TEXT_RESET;
        int healthLength = Integer.toString(health).length();
        if (shield > 0){
            healthString = healthString + " + " + shieldTextColour + shield;
            healthLength += (Integer.toString(shield).length() + 3);
        }
        String foeHealthString = healthTextColour + foe.health + TEXT_RESET;
        int foeHealthLength = Integer.toString(foe.health).length();
        if (foe.shield > 0){
            foeHealthString = shieldTextColour + foe.shield + TEXT_RESET + " + " + foeHealthString;
            foeHealthLength += (Integer.toString(foe.shield).length() + 3);
        }
        if ((int)(Math.ceil(health/scale) + Math.ceil(shield/scale) + Math.ceil(foe.health/scale) + Math.ceil(foe.shield/scale)) + 10 > size) size = (int)(Math.ceil(health/scale) + Math.ceil(shield/scale) + Math.ceil(foe.health/scale) + Math.ceil(foe.shield/scale)) + 10;

        System.out.print(name);
        for (int i = 0; i < size - (name.length() + foe.type.length()); i++) System.out.print(" ");
        System.out.print(foe.type + "\n");

        System.out.print(healthString);
        for (int i = 0; i < size - (healthLength + foeHealthLength); i++) System.out.print(" ");
        System.out.print(foeHealthString);
        System.out.print(TEXT_RESET + "\n");

        for (int i = 0; i < (int)Math.ceil(health/scale); i++) System.out.print(healthColour + " ");
        for (int i = 0; i < (int)Math.ceil(shield/scale); i++) System.out.print(shieldColour + " ");
        for (int i = 0; i < size - (int)(Math.ceil(health/scale) + Math.ceil(shield/scale) + Math.ceil(foe.health/scale) + Math.ceil(foe.shield/scale)); i++) System.out.print(TEXT_RESET + " ");
        for (int i = 0; i < (int)Math.ceil(foe.shield/scale); i++) System.out.print(shieldColour + " ");
        for (int i = 0; i < (int)Math.ceil(foe.health/scale); i++) System.out.print(healthColour + " ");

        System.out.println(TEXT_RESET + "\n");
    }

    // displays attack messages
    // pre: attackName and foeAttackName should correspond to the attack names of of the player and an Enemy object
    // post: prints an message showing which attacks the player and enemy chose
    public static void combatMessage(String attackName, String foeAttackName, String foeType){
        System.out.println("You used " + TEXT_ORANGE + attackName + TEXT_RESET + ", the " + foeType + " used " + TEXT_ORANGE + foeAttackName + TEXT_RESET);
    }

    // resets enemy health
    // post: the statistics of all enemies are reset
    public static void resetEnemies(){
        trionian = new Enemy(
            "Trionian",
            75,
            25,
            20,
            20,
            0.7,
            5.0,
            "Rush",
            new double[] {0.7,2,2},
            "Spin",
            new double[] {1,3,0.7},
            new double[] {0.9, 20}
        );

        vortrice = new Enemy(
            "Vortrice", 
            100, 
            50, 
            25, 
            25, 
            1, 
            1,
            "Slash",
            new double[] {1,1,1}, 
            "Thrust",
            new double[] {0.6,2,2},
            new double[] {0.75, 30}
        );
        
        muric = new Enemy(
            "Muric", 
            150, 
            100, 
            30, 
            30, 
            1.2, 
            0.3, 
            "Hammerfist", 
            new double[] {1.6,0.3,1}, 
            "Groundpound",
            new double[] {1.8,0.2,0.4},
            new double[] {0.5,40}    
        );

        thauria = new Enemy(
            "Thauria", 
            100, 
            200, 
            20, 
            50, 
            1,
            1, 
            "Chain lightning", 
            new double[] {1.3,2,2}, 
            "Poisonous curse", 
            new double[] {0.5,0.6,1.1}, 
            new double[] {0.95,18}
        );
        boss = new Enemy(
            "Snale", 
            500, 
            200, 
            30, 
            30, 
            2, 
            0.5, 
            "Slime grapeshot", 
            new double[] {1,4,2}, 
            "Alcoholic rage",
            new double[] {2,2,1.5},
            new double[] {0.67,50});
    }

    // checks if integer x is in the integer array arr
    // pre: x is an integer
    // post: returns true if x found in arr, false otherwise
    public static boolean inIntArray(int x, int[] arr){
        for (int i = 0; i < arr.length; i++) if (arr[i] == x) return true;
        return false;
    }
}