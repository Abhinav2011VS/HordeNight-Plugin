package net.abhinav.hordenight;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.*;

import java.util.Random;

public class HordeNight extends JavaPlugin {

    private int nightCounter = 0; // Tracks the night cycle
    private boolean hordeNightActive = false;
    private int hordeFrequency; // How often Horde Night happens (configured)
    private int zombieWaveSize; // How many zombies spawn per wave (configured)
    private Random random = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        hordeFrequency = getConfig().getInt("horde-frequency", 10); // Default: every 10 nights
        zombieWaveSize = getConfig().getInt("zombie-wave-size", 500); // Default: 500 zombies per wave

        getServer().getScheduler().runTaskTimer(this, this::checkNightCycle, 0L, 24000L); // Every Minecraft night
    }

    private void checkNightCycle() {
        nightCounter++;
        if (nightCounter % hordeFrequency == 0 && !hordeNightActive) {
            startHordeNight();
        }
    }

    private void startHordeNight() {
        hordeNightActive = true;
        broadcastMessage("Horde Night has begun! The apocalypse is here. Stay alive if you can...");

        // Apply apocalyptic effects
        applyApocalypseEnvironment();

        // Spawn massive zombie waves
        spawnZombieWaves();

        // Apply debuffs to players
        for (Player player : getServer().getOnlinePlayers()) {
            applyPlayerDebuff(player);
        }

        // End Horde Night after a certain time (10 minutes or 12000 ticks)
        getServer().getScheduler().runTaskLater(this, this::endHordeNight, 12000L);
    }

    private void spawnZombieWaves() {
        World world = getServer().getWorld("world");
        // Spawn 15 waves of zombies, increased to make it even more dangerous
        for (int i = 0; i < 15; i++) { // 15 waves of zombies
            spawnWave(world);
        }
    }

    private void spawnWave(World world) {
        for (int i = 0; i < zombieWaveSize; i++) {
            Location spawnLocation = getRandomLocation(world);
            Zombie zombie = (Zombie) world.spawnEntity(spawnLocation, EntityType.ZOMBIE);
            enhanceZombie(zombie);
            spawnAdditionalMobs(world, spawnLocation);
        }
    }

    private Location getRandomLocation(World world) {
        // Random spawn locations spread across the world
        double x = random.nextInt(300) - 150; // Wider spread of mobs (300 blocks)
        double z = random.nextInt(300) - 150;
        return new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);
    }

    private void enhanceZombie(Zombie zombie) {
        // Make zombies incredibly strong
        zombie.setHealth(200.0); // Zombies are much stronger now
        zombie.setCustomName("Apocalyptic Overlord");
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3)); // Super fast zombies
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 3)); // Super strong zombies
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1)); // Regeneration to make them even harder to kill
    }

    private void spawnAdditionalMobs(World world, Location location) {
        // Add even more chaos by spawning stronger mobs
        spawnPowerfulMobs(world, location, EntityType.SKELETON);
        spawnPowerfulMobs(world, location, EntityType.CREEPER);
        spawnPowerfulMobs(world, location, EntityType.SPIDER);
        spawnZombieBoss(world, location); // Spawn a powerful Zombie Boss
    }

    private void spawnZombieBoss(World world, Location location) {
        // Zombie Boss has extreme strength and can summon mobs
        Zombie boss = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
        boss.setHealth(500.0); // Boss has a lot of health
        boss.setCustomName("Zombie King");
        boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3)); // Speed boost for the boss
        boss.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 5)); // Super strong
        boss.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2)); // Regeneration for healing
        // Boss can also summon mobs every 30 seconds
        getServer().getScheduler().runTaskLater(this, () -> summonMinions(world, boss.getLocation()), 600L);
    }

    private void summonMinions(World world, Location location) {
        for (int i = 0; i < 10; i++) { // Summon 10 mobs to fight for the boss
            spawnPowerfulMobs(world, location, EntityType.ZOMBIE);
        }
    }

    private void spawnPowerfulMobs(World world, Location location, EntityType type) {
        int numMobs = random.nextInt(5) + 3; // Spawn 3-7 mobs per location
        for (int i = 0; i < numMobs; i++) {
            Entity mob = world.spawnEntity(location, type);
            enhanceMob(mob);
        }
    }

    private void enhanceMob(Entity mob) {
        // Make mobs more powerful during Horde Night
        if (mob instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) mob;
            entity.setHealth(100.0); // Make mobs much tougher
            if (mob instanceof Skeleton) {
                ((Skeleton) mob).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2)); // Speed for skeletons
            } else if (mob instanceof Spider) {
                ((Spider) mob).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2)); // Speed for spiders
            }
        }
    }

    private void applyPlayerDebuff(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 24000, 5)); // Slow players drastically
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 24000, 3)); // Hunger
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 24000, 1)); // Blindness
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 24000, 1)); // Weakness
    }

    private void applyApocalypseEnvironment() {
        World world = getServer().getWorld("world");
        // Darken the sky and increase danger with apocalyptic effects
        world.setTime(13000); // Nighttime immediately
        world.setStorm(true); // A storm during the night
        world.setWeatherDuration(24000); // Make the storm last the entire night

        // Randomly create meteor strikes (can be adjusted)
        if (random.nextInt(5) == 0) {
            triggerMeteorShower(world);
        }

        // Lighting strikes and thunder for dramatic effect
        for (Player player : getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0f, 1.0f);
        }
    }

    private void triggerMeteorShower(World world) {
        for (int i = 0; i < 10; i++) { // 10 meteor strikes
            double x = random.nextInt(1000) - 500; // Random X location
            double z = random.nextInt(1000) - 500; // Random Z location
            world.strikeLightning(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z));
            world.createExplosion(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z), 5F, false, false);
        }
    }

    private void endHordeNight() {
        hordeNightActive = false;
        broadcastMessage("The Horde Night has ended. The apocalypse subsides... but stay vigilant.");
    }

    private void broadcastMessage(String message) {
        getServer().broadcastMessage(ChatColor.RED + message);
    }
}
