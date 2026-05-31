package chumbanotz.mutantbeasts;

import net.minecraftforge.common.config.Config;

@Config(modid = MutantBeasts.MOD_ID, name = "MutantBeasts")
public class MBConfig {
    @Config.LangKey("cfg.mutantbeasts.general")
    @Config.Name("General")
    public static final GeneralSettings GENERAL = new GeneralSettings();

    @Config.LangKey("cfg.mutantbeasts.entities")
    @Config.Name("Entities")
    public static final EntitySettings ENTITIES = new EntitySettings();

    @Config.LangKey("cfg.mutantbeasts.items")
    @Config.Name("Items")
    public static final ItemSettings ITEMS = new ItemSettings();

    public static class GeneralSettings {
        // TODO: Are config options for particle IDs really needed?
        @Config.Name("Endersoul Particle ID")
        @Config.Comment("The particles created by mutant endermen")
        @Config.RequiresMcRestart
        public int endersoulParticleID = 100;

        @Config.Name("Skull Spirit Particle ID")
        @Config.Comment("The particles created by Chemical X")
        @Config.RequiresMcRestart
        public int skullSpiritParticleID = 101;

        // TODO: This does nothing currently
        /*@Config.Name("Custom Spawn Egg Texture")
        @Config.Comment
                ({
                        "Gives the spawn eggs from this mod the texture they had from Mutant Creatures",
                        "Disable this if your game crashes with the mod 'CodeChickenLib' installed"
                })
        @Config.RequiresMcRestart
        public boolean useCustomSpawnEggTexture = true;*/
    }

    public static class EntitySettings {
        @Config.Name("Global Spawn Rate")
        @Config.Comment
                ({
                        "Affects spawn rate of all mutants",
                        "The smaller the number, the lower the chance"
                })
        @Config.RangeInt(min = 1)
        public int globalSpawnRate = 10;

        @Config.Name("Mutant Creeper Armor")
        @Config.Comment("The amount of armor the Mutant Creeper has")
        public double mutantCreeperArmor = 10.0D;

        @Config.Name("Mutant Creeper Attack Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Creeper")
        public double mutantCreeperAttackDamage = 5.0D;

        @Config.Name("Mutant Creeper Boss Bar")
        @Config.Comment("Displays a boss bar on the top of the screen when the Mutant Creeper is present")
        @Config.RequiresMcRestart
        public boolean mutantCreeperBossBar = true;

        @Config.Name("Mutant Creeper Boss Classification")
        @Config.Comment
                ({
                        "Determines the Mutant Creeper as a boss mob",
                        "This will be affected by anything checking for boss mobs specifically"
                })
        @Config.RequiresMcRestart
        public boolean mutantCreeperBossClassification = true;

        @Config.Name("Mutant Creeper Death Explosion Destroys Terrain")
        @Config.Comment("Allows the Mutant Creeper to destroy terrain when exploding from death")
        @Config.RequiresMcRestart
        public boolean mutantCreeperDeathDestroysTerrain = true;

        @Config.Name("Mutant Creeper Death Explosion Strength")
        @Config.Comment("The strength of the explosion caused by the Mutant Creeper's death")
        public double mutantCreeperDeathStrength = 8.0D;

        @Config.Name("Mutant Creeper Death Explosion Strength (Charged)")
        @Config.Comment("The strength of the explosion caused by the Mutant Creeper's death while charged")
        public double mutantCreeperDeathStrengthCharged = 12.0D;

        @Config.Name("Mutant Creeper Follow Range")
        @Config.Comment("The amount of blocks the Mutant Creeper follows entities")
        public double mutantCreeperFollowRange = 35.0D;

        @Config.Name("Mutant Creeper Jumping Explosion Destroys Terrain")
        @Config.Comment("Allows the Mutant Creeper to destroy terrain when exploding from its jump attack")
        @Config.RequiresMcRestart
        public boolean mutantCreeperJumpingDestroysTerrain = true;

        @Config.Name("Mutant Creeper Jumping Explosion Strength")
        @Config.Comment("The strength of the explosion caused by the Mutant Creeper's jump attack")
        public double mutantCreeperJumpingStrength = 4.0D;

        @Config.Name("Mutant Creeper Jumping Explosion Strength (Charged)")
        @Config.Comment("The strength of the explosion caused by the Mutant Creeper's jump attack while charged")
        public double mutantCreeperJumpingStrengthCharged = 6.0D;

        @Config.Name("Mutant Creeper Knockback Resistance")
        @Config.Comment("The amount of knockback resistance the Mutant Creeper has")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mutantCreeperKnockbackResistance = 1.0D;

        @Config.Name("Mutant Creeper Max Health")
        @Config.Comment("The amount of maximum health the Mutant Creeper has")
        public double mutantCreeperMaxHealth = 150.0D;

        @Config.Name("Mutant Creeper Movement Speed")
        @Config.Comment("The amount of movement speed the Mutant Creeper has")
        public double mutantCreeperMovementSpeed = 0.26D;

        @Config.Name("Mutant Creeper Out of Combat Health Regen")
        @Config.Comment
                ({
                        "Makes the Mutant Creeper regenerate health quickly when it has no target",
                        "This will not happen to players in Creative mode"
                })
        @Config.RequiresMcRestart
        public boolean mutantCreeperNoCombatRegen = true;

        @Config.Name("Mutant Creeper Egg Spawn")
        @Config.Comment("Allows the Mutant Creeper to spawn a creeper egg on death")
        @Config.RequiresMcRestart
        public boolean mutantCreeperSpawnsEgg = true;

        @Config.Name("Mutant Creeper Spawn Probability")
        @Config.Comment
                ({
                        "The weighted probability for the Mutant Creeper to spawn",
                        "Set to 0 to disable"
                })
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int mutantCreeperSpawnRate = 1;

        @Config.Name("Mutant Creeper Swim Speed")
        @Config.Comment("The amount of swim speed the Mutant Creeper has")
        public double mutantCreeperSwimSpeed = 4.5D;

        @Config.Name("Mutant Creeper Underground Spawning")
        @Config.Comment
                ({
                        "Makes the Mutant Creeper spawn anywhere without being restricted to the surface",
                        "Not recommended unless you know what you're doing"
                })
        @Config.RequiresMcRestart
        public boolean mutantCreeperUndergroundSpawning = false;

        @Config.Name("Mutant Enderman Armor")
        @Config.Comment("The amount of armor the Mutant Enderman has")
        public double mutantEndermanArmor = 10.0D;

        @Config.Name("Mutant Enderman Attack Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Enderman")
        public double mutantEndermanAttackDamage = 7.0D;

        @Config.Name("Mutant Enderman Block Damage")
        @Config.Comment
                ({
                        "The amount of damage dealt by the Mutant Snowman's block throwing attack",
                        "This also affects the damage dealt by blocks thrown from the Endersoul Hand"
                })
        public double mutantEndermanBlockDamage = 8.0D;

        @Config.Name("Mutant Enderman Boss Bar")
        @Config.Comment("Displays a boss bar on the top of the screen when the Mutant Enderman is present")
        @Config.RequiresMcRestart
        public boolean mutantEndermanBossBar = true;

        @Config.Name("Mutant Enderman Boss Classification")
        @Config.Comment
                ({
                        "Determines the Mutant Enderman as a boss mob",
                        "This will be affected by anything checking for boss mobs specifically"
                })
        @Config.RequiresMcRestart
        public boolean mutantEndermanBossClassification = true;

        @Config.Name("Mutant Enderman Follow Range")
        @Config.Comment("The amount of blocks the Mutant Enderman follows entities")
        public double mutantEndermanFollowRange = 96.0D;

        @Config.Name("Mutant Enderman Forced Look (Stare) Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Enderman's forced look (stare) attack")
        public double mutantEndermanStareDamage = 2.0D;

        @Config.Name("Mutant Enderman Knockback Resistance")
        @Config.Comment("The amount of knockback resistance the Mutant Enderman has")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mutantEndermanKnockbackResistance = 1.0D;

        @Config.Name("Mutant Enderman Max Health")
        @Config.Comment("The amount of maximum health the Mutant Enderman has")
        public double mutantEndermanMaxHealth = 200.0D;

        @Config.Name("Mutant Enderman Movement Speed")
        @Config.Comment("The amount of movement speed the Mutant Enderman has")
        public double mutantEndermanMovementSpeed = 0.3D;

        @Config.Name("Mutant Enderman Out of Combat Health Regen")
        @Config.Comment
                ({
                        "Makes the Mutant Enderman regenerate health quickly when it has no target",
                        "This will not happen to players in Creative mode"
                })
        @Config.RequiresMcRestart
        public boolean mutantEndermanNoCombatRegen = true;

        @Config.Name("Mutant Enderman Renders Teleport")
        @Config.Comment
                ({
                        "Allows the Mutant Enderman to render itself on where they will teleport",
                        "Disable this if you keep getting crashes related to the 'ObfuscationReflectionHelper'"
                })
        @Config.RequiresMcRestart
        public boolean mutantEndermanRendersTeleport = true;

        @Config.Name("Mutant Enderman Scream Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Enderman's scream attack")
        public double mutantEndermanScreamDamage = 4.0D;

        @Config.Name("Mutant Enderman Telesmash Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Enderman's telesmash attack")
        public double mutantEndermanTelesmashDamage = 6.0D;

        @Config.Name("Mutant Enderman Fragments Spawn")
        @Config.Comment("Allows the Mutant Enderman to spawn endersoul fragments while dying")
        @Config.RequiresMcRestart
        public boolean mutantEndermanSpawnsFragments = true;

        @Config.Name("Mutant Enderman Spawn Probability")
        @Config.Comment
                ({
                        "The weighted probability for the Mutant Enderman to spawn",
                        "Set to 0 to disable"
                })
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int mutantEndermanSpawnRate = 1;

        @Config.Name("Mutant Enderman Swim Speed")
        @Config.Comment("The amount of swim speed the Mutant Enderman has")
        public double mutantEndermanSwimSpeed = 1.0D;

        @Config.Name("Mutant Enderman Underground Spawning")
        @Config.Comment
                ({
                        "Makes the Mutant Enderman spawn anywhere without being restricted to the surface",
                        "Not recommended unless you know what you're doing"
                })
        @Config.RequiresMcRestart
        public boolean mutantEndermanUndergroundSpawning = false;

        @Config.Name("Mutant Enderman Water Weakness")
        @Config.Comment("Allows the Mutant Enderman to take damage while in water or when exposed to rain")
        @Config.RequiresMcRestart
        public boolean mutantEndermanWaterWeakness = false;

        @Config.Name("Mutant Skeleton Armor")
        @Config.Comment("The amount of armor the Mutant Skeleton has")
        public double mutantSkeletonArmor = 10.0D;

        @Config.Name("Mutant Skeleton Arrow Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Skeleton's arrows")
        public double mutantSkeletonArrowDamage = 12.0D;

        @Config.Name("Mutant Skeleton Arrow Phasing")
        @Config.Comment
                ({
                        "Allows arrows fired by the Mutant Skeleton to hit through multiple mobs",
                        "Setting to false will only make their arrows hit one mob at a time"
                })
        public boolean mutantSkeletonArrowPhasing = true;

        @Config.Name("Mutant Skeleton Attack Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Skeleton")
        public double mutantSkeletonAttackDamage = 4.0D;

        @Config.Name("Mutant Skeleton Bone Drops")
        @Config.Comment
                ({
                        "Allows you to pick up the bones dropped by the Mutant Skeleton",
                        "While disabled, the parts spawned during death will be decorative and despawn quickly"
                })
        public boolean mutantSkeletonBoneDrops = true;

        @Config.Name("Mutant Skeleton Boss Bar")
        @Config.Comment("Displays a boss bar on the top of the screen when the Mutant Skeleton is present")
        @Config.RequiresMcRestart
        public boolean mutantSkeletonBossBar = true;

        @Config.Name("Mutant Skeleton Boss Classification")
        @Config.Comment
                ({
                        "Determines the Mutant Skeleton as a boss mob",
                        "This will be affected by anything checking for boss mobs specifically"
                })
        @Config.RequiresMcRestart
        public boolean mutantSkeletonBossClassification = true;

        @Config.Name("Mutant Skeleton Constrict (Ribcage) Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Skeleton's constrict (ribcage) attack")
        public double mutantSkeletonConstrictDamage = 9.0D;

        @Config.Name("Mutant Skeleton Death Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Skeleton's death (getting hit by flying bones will hurt!)")
        public double mutantSkeletonDeathDamage = 7.0D;

        @Config.Name("Mutant Skeleton Follow Range")
        @Config.Comment("The amount of blocks the Mutant Skeleton follows entities")
        public double mutantSkeletonFollowRange = 50.0D;

        @Config.Name("Mutant Skeleton Knockback Resistance")
        @Config.Comment("The amount of knockback resistance the Mutant Skeleton has")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mutantSkeletonKnockbackResistance = 1.0D;

        @Config.Name("Mutant Skeleton Legacy Ambient Sound")
        @Config.Comment("Enables the original Mutant Skeleton ambient sound from older versions")
        public boolean mutantSkeletonLegacyAmbientSound = false;

        @Config.Name("Mutant Skeleton Legacy Death Sound")
        @Config.Comment("Enables the original Mutant Skeleton death sound from older versions")
        public boolean mutantSkeletonLegacyDeathSound = false;

        @Config.Name("Mutant Skeleton Legacy Hurt Sound")
        @Config.Comment("Enables the original Mutant Skeleton hurt sound from older versions")
        public boolean mutantSkeletonLegacyHurtSound = false;

        @Config.Name("Mutant Skeleton Legacy Step Sound")
        @Config.Comment("Enables the original Mutant Skeleton step sound from older versions")
        public boolean mutantSkeletonLegacyStepSound = false;

        @Config.Name("Mutant Skeleton Max Health")
        @Config.Comment("The amount of maximum health the Mutant Skeleton has")
        public double mutantSkeletonMaxHealth = 150.0D;

        @Config.Name("Mutant Skeleton Movement Speed")
        @Config.Comment("The amount of movement speed the Mutant Skeleton has")
        public double mutantSkeletonMovementSpeed = 0.27D;

        @Config.Name("Mutant Skeleton Multishot Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Skeleton's multishot arrows")
        public double mutantSkeletonMultishotDamage = 9.0D;

        @Config.Name("Mutant Skeleton Out of Combat Health Regen")
        @Config.Comment
                ({
                        "Makes the Mutant Skeleton regenerate health quickly when it has no target",
                        "This will not happen to players in Creative mode"
                })
        @Config.RequiresMcRestart
        public boolean mutantSkeletonNoCombatRegen = true;

        @Config.Name("Mutant Skeleton Spawn Probability")
        @Config.Comment
                ({
                        "The weighted probability for the Mutant Skeleton to spawn",
                        "Set to 0 to disable"
                })
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int mutantSkeletonSpawnRate = 1;

        @Config.Name("Mutant Skeleton Swim Speed")
        @Config.Comment("The amount of swim speed the Mutant Skeleton has")
        public double mutantSkeletonSwimSpeed = 5.0D;

        @Config.Name("Mutant Skeleton Underground Spawning")
        @Config.Comment
                ({
                        "Makes the Mutant Skeleton spawn anywhere without being restricted to the surface",
                        "Not recommended unless you know what you're doing"
                })
        @Config.RequiresMcRestart
        public boolean mutantSkeletonUndergroundSpawning = false;

        @Config.Name("Mutant Snow Golem Armor")
        @Config.Comment("The amount of armor the Mutant Snow Golem has")
        public double mutantSnowGolemArmor = 0.0D;

        @Config.Name("Mutant Snow Golem Ice Chunk Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Snow Golem's ice chunk throwing attack")
        public double mutantSnowGolemIceChunkDamage = 6.0D;

        @Config.Name("Mutant Snow Golem Knockback Resistance")
        @Config.Comment("The amount of knockback resistance the Mutant Snow Golem has")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mutantSnowGolemKnockbackResistance = 1.0D;

        @Config.Name("Mutant Snow Golem Max Health")
        @Config.Comment("The amount of maximum health the Mutant Snow Golem has")
        public double mutantSnowGolemMaxHealth = 80.0D;

        @Config.Name("Mutant Snow Golem Movement Speed")
        @Config.Comment("The amount of movement speed the Mutant Snow Golem has")
        public double mutantSnowGolemMovementSpeed = 0.26D;

        @Config.Name("Mutant Snow Golem Nether Weakness")
        @Config.Comment("Allows the Mutant Snow Golem to take damage while in the Nether")
        @Config.RequiresMcRestart
        public boolean mutantSnowGolemNetherWeakness = false;

        @Config.Name("Mutant Snow Golem Swim Speed")
        @Config.Comment("The amount of swim speed the Mutant Snow Golem has")
        public double mutantSnowGolemSwimSpeed = 1.0D;

        @Config.Name("Mutant Snow Golem Water Weakness")
        @Config.Comment("Allows the Mutant Snow Golem to take damage while in water or when exposed to rain")
        @Config.RequiresMcRestart
        public boolean mutantSnowGolemWaterWeakness = false;

        @Config.Name("Mutant Zombie Armor")
        @Config.Comment("The amount of armor the Mutant Zombie has")
        public double mutantZombieArmor = 12.0D;

        @Config.Name("Mutant Zombie Attack Damage")
        @Config.Comment
                ({
                        "The amount of damage dealt by the Mutant Zombie",
                        "This also affects the damage dealt by the Mutant Zombie's throw attack"
                })
        public double mutantZombieAttackDamage = 12.0D;

        @Config.Name("Mutant Zombie Boss Bar")
        @Config.Comment("Displays a boss bar on the top of the screen when the Mutant Zombie is present")
        @Config.RequiresMcRestart
        public boolean mutantZombieBossBar = true;

        @Config.Name("Mutant Zombie Boss Classification")
        @Config.Comment
                ({
                        "Determines the Mutant Zombie as a boss mob",
                        "This will be affected by anything checking for boss mobs specifically"
                })
        @Config.RequiresMcRestart
        public boolean mutantZombieBossClassification = true;

        @Config.Name("Mutant Zombie Follow Range")
        @Config.Comment("The amount of blocks the Mutant Zombie follows entities")
        public double mutantZombieFollowRange = 35.0D;

        @Config.Name("Mutant Zombie Knockback Resistance")
        @Config.Comment("The amount of knockback resistance the Mutant Zombie has")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double mutantZombieKnockbackResistance = 1.0D;

        @Config.Name("Mutant Zombie Lives")
        @Config.Comment
                ({
                        "The amount of lives the Mutant Zombie has",
                        "Each time the Mutant Zombie is defeated it will lose one live",
                        "It will keep reviving itself until it is out of lives (less than 0) or set on fire",
                        "Set to 0 to disable extra lives and have the Mutant Zombie die normally"
                })
        @Config.RangeInt(min = 0)
        public int mutantZombieLives = 3;

        @Config.Name("Mutant Zombie Max Health")
        @Config.Comment("The amount of maximum health the Mutant Zombie has")
        public double mutantZombieMaxHealth = 150.0D;

        @Config.Name("Mutant Zombie Movement Speed")
        @Config.Comment("The amount of movement speed the Mutant Zombie has")
        public double mutantZombieMovementSpeed = 0.26D;

        @Config.Name("Mutant Zombie Out of Combat Health Regen")
        @Config.Comment
                ({
                        "Makes the Mutant Zombie regenerate health quickly when it has no target",
                        "This will not happen to players in Creative mode"
                })
        @Config.RequiresMcRestart
        public boolean mutantZombieNoCombatRegen = true;

        @Config.Name("Mutant Zombie Roar Damage")
        @Config.Comment("The amount of damage dealt by the Mutant Zombie's roar attack")
        public double mutantZombieRoarDamage = 3.0D;

        @Config.Name("Mutant Zombie Spawn Probability")
        @Config.Comment
                ({
                        "The weighted probability for the Mutant Zombie to spawn",
                        "Set to 0 to disable"
                })
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int mutantZombieSpawnRate = 1;

        @Config.Name("Mutant Zombie Swim Speed")
        @Config.Comment("The amount of swim speed the Mutant Zombie has")
        public double mutantZombieSwimSpeed = 4.0D;

        @Config.Name("Mutant Zombie Underground Spawning")
        @Config.Comment
                ({
                        "Makes the Mutant Creeper spawn anywhere without being restricted to the surface",
                        "Not recommended unless you know what you're doing"
                })
        @Config.RequiresMcRestart
        public boolean mutantZombieUndergroundSpawning = false;

        @Config.Name("Spider-Pig Armor")
        @Config.Comment("The amount of armor the Spider-Pig has")
        public double spiderPigArmor = 0.0D;

        @Config.Name("Spider-Pig Attack Damage")
        @Config.Comment("The amount of damage dealt by the Spider-Pig")
        public double spiderPigAttackDamage = 3.0D;

        @Config.Name("Spider-Pig Knockback Resistance")
        @Config.Comment("The amount of knockback resistance the Spider-Pig has")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        public double spiderPigKnockbackResistance = 0.0D;

        @Config.Name("Spider-Pig Max Health")
        @Config.Comment("The amount of maximum health the Spider-Pig has")
        public double spiderPigMaxHealth = 40.0D;

        @Config.Name("Spider-Pig Movement Speed")
        @Config.Comment("The amount of movement speed the Spider-Pig has")
        public double spiderPigMovementSpeed = 0.25D;

        @Config.Name("Spider-Pig Swim Speed")
        @Config.Comment("The amount of swim speed the Spider-Pig has")
        public double spiderPigSwimSpeed = 1.0D;

        @Config.Name("Creeper Minion Allowed On Shoulder")
        @Config.Comment
                ({
                        "This option is here because in 1.12.2, the way the parrot is rendered on a player's shoulder is hard-coded and won't render any other mob",
                        "When true, the normal parrot layer is replaced with a custom one to work with creeper minions",
                        "Disable this if there are compatibility issues with other mods"
                })
        @Config.RequiresMcRestart
        public boolean creeperMinionOnShoulder = true;
    }

    public static class ItemSettings {
        @Config.Name("Creeper Shard Charges")
        @Config.Comment
                ({
                        "The charges required for the Creeper Shard to fully charge",
                        "One charge is always gained when hitting any mob with it",
                        "Right-clicking with a fully charged Creeper Shard will cause an explosion and revert the shard back to zero"
                })
        @Config.RangeInt(min = 1)
        public int creeperShardCharges = 32;

        @Config.Name("Endersoul Hand Attack Speed")
        @Config.Comment("The amount of attack speed the Endersoul Hand has")
        public double endersoulHandAttackSpeed = 1.6D;

        @Config.Name("Endersoul Hand Cooldown")
        @Config.Comment("The cooldown of the Endersoul Hand's teleportation ability when sneaking and right-clicking with it")
        public int endersoulHandCooldown = 40;

        @Config.Name("Endersoul Hand Damage")
        @Config.Comment("The amount of damage dealt by the Endersoul Hand")
        public double endersoulHandDamage = 6.0D;

        @Config.Name("Endersoul Hand Durability")
        @Config.Comment("The amount of durability the Endersoul Hand has")
        @Config.RangeInt(min = 1)
        public int endersoulHandDurability = 240;

        @Config.Name("Endersoul Hand Enchantability")
        @Config.Comment("The amount of enchantability the Endersoul Hand has")
        public int endersoulHandEnchantability = 20;

        @Config.Name("Endersoul Hand Teleportation")
        @Config.Comment
                ({
                        "Allows the Endersoul Hand to teleport you to any block within a radius when sneaking and right-clicking",
                        "Setting to false will disable this ability completely"
                })
        @Config.RequiresMcRestart
        public boolean endersoulHandTeleports = true;

        @Config.Name("Endersoul Hand Teleportation Cost")
        @Config.Comment("The cost in durability when teleporting with the Endersoul Hand")
        public int endersoulHandTeleportationCost = 4;

        @Config.Name("Endersoul Hand Teleportation Radius")
        @Config.Comment("The maximum radius in blocks when teleporting with the Endersoul Hand")
        public double endersoulHandTeleportationRadius = 128.0D;

        @Config.Name("Hulk Hammer Attack Speed")
        @Config.Comment("The amount of attack speed the Hulk Hammer has")
        public double hulkHammerAttackSpeed = 1.0D;

        @Config.Name("Hulk Hammer Cooldown")
        @Config.Comment("The cooldown of the Hulk Hammer's ground pound attack when right-clicking with it")
        public int hulkHammerCooldown = 25;

        @Config.Name("Hulk Hammer Damage")
        @Config.Comment("The amount of damage dealt by the Hulk Hammer (also affects the damage of the ground pound attack)")
        public double hulkHammerDamage = 9.0D;

        @Config.Name("Hulk Hammer Enchantability")
        @Config.Comment("The amount of enchantability the Hulk Hammer has")
        public int hulkHammerEnchantability = 10;

        @Config.Name("Hulk Hammer Durability")
        @Config.Comment("The amount of durability the Hulk Hammer has")
        @Config.RangeInt(min = 1)
        public int hulkHammerDurability = 64;

        @Config.Name("Hulk Hammer Shield Disabling")
        @Config.Comment("Allows the Hulk Hammer to disable shields")
        @Config.RequiresMcRestart
        public boolean hulkHammerDisablesShields = true;

        @Config.Name("Mutant Skeleton Armor Durability")
        @Config.Comment
                ({
                        "The amount of durability Mutant Skeleton Armor has (the number is calculated below)",
                        "Helmet: X * 11",
                        "Chestplate: X * 16",
                        "Leggings: X * 15",
                        "Boots: X * 13"
                })
        public int mutantSkeletonArmorDurability = 15;

        @Config.Name("Mutant Skeleton Armor Enchantability")
        @Config.Comment("The amount of enchantability Mutant Skeleton Armor has")
        public int mutantSkeletonArmorEnchantability = 9;

        @Config.Name("Mutant Skeleton Armor Protection (Boots)")
        @Config.Comment("The amount of armor this piece of Mutant Skeleton Armor has")
        public int mutantSkeletonArmorProtectionBoots = 2;

        @Config.Name("Mutant Skeleton Armor Protection (Chestplate)")
        @Config.Comment("The amount of armor this piece of Mutant Skeleton Armor has")
        public int mutantSkeletonArmorProtectionChestplate = 6;

        @Config.Name("Mutant Skeleton Armor Protection (Skull)")
        @Config.Comment("The amount of armor this piece of Mutant Skeleton Armor has")
        public int mutantSkeletonArmorProtectionHelmet = 2;

        @Config.Name("Mutant Skeleton Armor Protection (Leggings)")
        @Config.Comment("The amount of armor this piece of Mutant Skeleton Armor has")
        public int mutantSkeletonArmorProtectionLeggings = 5;

        @Config.Name("Mutant Skeleton Armor Toughness")
        @Config.Comment("The amount of toughness Mutant Skeleton Armor has")
        public double mutantSkeletonArmorToughness = 0.0D;

        @Config.Name("Mutant Skeleton Boots Jump Boost Ability")
        @Config.Comment
                ({
                        "Allows the Mutant Skeleton Boots to grant Jump Boost while worn",
                        "While sprinting, it will grant Jump Boost II instead"
                })
        @Config.RequiresMcRestart
        public boolean mutantSkeletonBootsJumpBoost = true;

        @Config.Name("Mutant Skeleton Chestplate Bow Charging Ability")
        @Config.Comment("Allows the Mutant Skeleton Chestplate to make bows charge at four times the original rate while worn")
        @Config.RequiresMcRestart
        public boolean mutantSkeletonChestplateBowCharging = true;

        @Config.Name("Mutant Skeleton Leggings Speed Ability")
        @Config.Comment("Allows the Mutant Skeleton Leggings to grant Speed II while worn")
        @Config.RequiresMcRestart
        public boolean mutantSkeletonLeggingsSpeed = true;

        @Config.Name("Mutant Skeleton Skull Arrow Ability")
        @Config.Comment
                ({
                        "Allows the Mutant Skeleton Skull to grant special bonuses (and negatives) to arrows fired from bows",
                        "All critical mid-air arrow shots will deal double the damage, while non-airborne arrows will deal half the damage",
                        "There is a 50% chance for fired arrows to not be depleted like the Infinity enchantment"
                })
        @Config.RequiresMcRestart
        public boolean mutantSkeletonHelmetArrow = true;
    }
}
