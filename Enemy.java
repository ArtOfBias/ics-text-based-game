public class Enemy {
    String type;

    int health;
    int shield;
    double baseDamage;
    double penetration;
    
    // these are multipliers
    double strength;
    double agility;

    // attack: damage, speed, penetration
    String attack1Name;
    double[] attack1 = new double[3];
    String attack2Name;
    double[] attack2 = new double[3];
    // defend: reduction, armour
    double[] block = new double[2];

    public Enemy(
            String type,
            int health,
            int shield,
            double baseDamage,
            double penetration,
            double strength,
            double agility,
            String attack1Name,
            double[] attack1,
            String attack2Name,
            double[] attack2,
            double[] block
        ){
        this.type = type;
        this.health = health;
        this.shield = shield;
        this.baseDamage = baseDamage;
        this.penetration = penetration;
        this.strength = strength;
        this.agility = agility;
        this.attack1Name = attack1Name;
        this.attack1 = attack1.clone();
        this.attack2Name = attack2Name;
        this.attack2 = attack2.clone();
        this.block = block.clone();
    }
}