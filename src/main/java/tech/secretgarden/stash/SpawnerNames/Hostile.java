package tech.secretgarden.stash.SpawnerNames;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Hostile {
    public static String BLAZE = "Blaze";
    public static String CREEPER = "Creeper";
    public static String GHAST = "Ghast";
    public static String SILVERFISH = "Silverfish";
    public static String SKELETON = "Skeleton";
    public static String SLIME = "Slime";
    public static String ZOMBIE = "Zombie";
    public static String ZOMBIE_VILLAGER = "Zombie Villager";
    public static String DROWNED = "Drowned";
    public static String WITCH = "Witch";
    public static String VINDICATOR = "Vindicator";
    public static String EVOKER = "Evoker";
    public static String PILLAGER = "Pillager";
    public static String RAVAGER = "Ravager";
    public static String VEX = "Vex";
    public static String PIGLIN_BRUTE = "Piglin Brute";

    public final static int SIZE = 16;

    public static String[] entityList = new String[SIZE];
    public static HashMap<String, EntityType> entityMap = new HashMap<>();

    public static void initMap() {
        entityMap.put(BLAZE, EntityType.BLAZE);
        entityMap.put(CREEPER, EntityType.CREEPER);
        entityMap.put(GHAST, EntityType.GHAST);
        entityMap.put(SILVERFISH, EntityType.SILVERFISH);
        entityMap.put(SKELETON, EntityType.SKELETON);
        entityMap.put(SLIME, EntityType.SLIME);
        entityMap.put(ZOMBIE, EntityType.ZOMBIE);
        entityMap.put(ZOMBIE_VILLAGER, EntityType.ZOMBIE_VILLAGER);
        entityMap.put(DROWNED, EntityType.DROWNED);
        entityMap.put(WITCH, EntityType.WITCH);
        entityMap.put(VINDICATOR, EntityType.VINDICATOR);
        entityMap.put(PILLAGER, EntityType.PILLAGER);
        entityMap.put(EVOKER, EntityType.EVOKER);
        entityMap.put(RAVAGER, EntityType.RAVAGER);
        entityMap.put(VEX, EntityType.VEX);
        entityMap.put(PIGLIN_BRUTE, EntityType.PIGLIN_BRUTE);
    }
}
