package chumbanotz.mutantbeasts;

import chumbanotz.mutantbeasts.entity.*;
import chumbanotz.mutantbeasts.entity.mutant.MutantCreeperEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantEndermanEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantSkeletonEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantSnowGolemEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantZombieEntity;
import chumbanotz.mutantbeasts.entity.mutant.SpiderPigEntity;
import chumbanotz.mutantbeasts.entity.projectile.ChemicalXEntity;
import chumbanotz.mutantbeasts.entity.projectile.MutantArrowEntity;
import chumbanotz.mutantbeasts.entity.projectile.ThrowableBlockEntity;
import chumbanotz.mutantbeasts.item.ChemicalXItem;
import chumbanotz.mutantbeasts.item.CreeperShardItem;
import chumbanotz.mutantbeasts.item.EndersoulHandItem;
import chumbanotz.mutantbeasts.item.HulkHammerItem;
import chumbanotz.mutantbeasts.item.MutantSkeletonArmorItem;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = "mutantbeasts")
public class RegistryHandler {
    private static int entityId;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                RegistryHandler.setRegistryName("chemical_x", new ChemicalXItem().setMaxStackSize(1)),
                RegistryHandler.setRegistryName("creeper_minion_tracker", new Item().setMaxStackSize(1)),
                RegistryHandler.setRegistryName("creeper_shard", new CreeperShardItem().setMaxStackSize(1).setMaxDamage(MBConfig.ITEMS.creeperShardCharges)),
                RegistryHandler.setRegistryName("endersoul_hand", new EndersoulHandItem().setMaxStackSize(1).setMaxDamage(MBConfig.ITEMS.endersoulHandDurability)),
                RegistryHandler.setRegistryName("hulk_hammer", new HulkHammerItem().setMaxStackSize(1).setMaxDamage(MBConfig.ITEMS.hulkHammerDurability)),
                RegistryHandler.setRegistryName("mutant_skeleton_arms", new Item()),
                RegistryHandler.setRegistryName("mutant_skeleton_limb", new Item()),
                RegistryHandler.setRegistryName("mutant_skeleton_shoulder_pad", new Item()),
                RegistryHandler.setRegistryName("mutant_skeleton_rib", new Item()),
                RegistryHandler.setRegistryName("mutant_skeleton_rib_cage", new Item()),
                RegistryHandler.setRegistryName("mutant_skeleton_pelvis", new Item()),
                RegistryHandler.setRegistryName("mutant_skeleton_skull", new MutantSkeletonArmorItem(EntityEquipmentSlot.HEAD)),
                RegistryHandler.setRegistryName("mutant_skeleton_chestplate", new MutantSkeletonArmorItem(EntityEquipmentSlot.CHEST)),
                RegistryHandler.setRegistryName("mutant_skeleton_leggings", new MutantSkeletonArmorItem(EntityEquipmentSlot.LEGS)),
                RegistryHandler.setRegistryName("mutant_skeleton_boots", new MutantSkeletonArmorItem(EntityEquipmentSlot.FEET)));
    }

    @SubscribeEvent
    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                RegistryHandler.createSoundEvent("entity.creeper_minion.ambient"),
                RegistryHandler.createSoundEvent("entity.creeper_minion.death"),
                RegistryHandler.createSoundEvent("entity.creeper_minion.hurt"),
                RegistryHandler.createSoundEvent("entity.creeper_minion.primed"),
                RegistryHandler.createSoundEvent("entity.creeper_minion_egg.hatch"),
                RegistryHandler.createSoundEvent("entity.endersoul_clone.death"),
                RegistryHandler.createSoundEvent("entity.endersoul_clone.teleport"),
                RegistryHandler.createSoundEvent("entity.endersoul_fragment.explode"),
                RegistryHandler.createSoundEvent("entity.mutant_creeper.ambient"),
                RegistryHandler.createSoundEvent("entity.mutant_creeper.charge"),
                RegistryHandler.createSoundEvent("entity.mutant_creeper.death"),
                RegistryHandler.createSoundEvent("entity.mutant_creeper.hurt"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.ambient"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.death"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.hurt"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.morph"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.scream"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.stare"),
                RegistryHandler.createSoundEvent("entity.mutant_enderman.teleport"),
                RegistryHandler.createSoundEvent("entity.mutant_skeleton.ambient"),
                RegistryHandler.createSoundEvent("entity.mutant_skeleton.death"),
                RegistryHandler.createSoundEvent("entity.mutant_skeleton.hurt"),
                RegistryHandler.createSoundEvent("entity.mutant_skeleton.step"),
                RegistryHandler.createSoundEvent("entity.mutant_snow_golem.death"),
                RegistryHandler.createSoundEvent("entity.mutant_snow_golem.hurt"),
                RegistryHandler.createSoundEvent("entity.mutant_zombie.ambient"),
                RegistryHandler.createSoundEvent("entity.mutant_zombie.attack"),
                RegistryHandler.createSoundEvent("entity.mutant_zombie.death"),
                RegistryHandler.createSoundEvent("entity.mutant_zombie.grunt"),
                RegistryHandler.createSoundEvent("entity.mutant_zombie.hurt"),
                RegistryHandler.createSoundEvent("entity.mutant_zombie.roar"),
                RegistryHandler.createSoundEvent("entity.spider_pig.ambient"),
                RegistryHandler.createSoundEvent("entity.spider_pig.death"),
                RegistryHandler.createSoundEvent("entity.spider_pig.hurt"));

        EntityParrot.registerMimicSound(MutantCreeperEntity.class, SoundEvents.E_PARROT_IM_CREEPER);
        EntityParrot.registerMimicSound(MutantSkeletonEntity.class, SoundEvents.E_PARROT_IM_SKELETON);
        EntityParrot.registerMimicSound(MutantZombieEntity.class, SoundEvents.E_PARROT_IM_ZOMBIE);
    }

    @SubscribeEvent
    public static void fixMissingMappings(RegistryEvent.MissingMappings<SoundEvent> event) {
        for (RegistryEvent.MissingMappings.Mapping mapping : event.getMappings()) {
            if (!mapping.key.getPath().startsWith("entity.mutant_husk")) continue;
            mapping.ignore();
        }
    }

    private static <T extends EntityLiving> EntityEntryBuilder<Entity> createEntityEntry(String name, Class<T> entityClass, int eggPrimary, int eggSecondary) {
        return RegistryHandler.createEntityEntry(name, entityClass).egg(eggPrimary, eggSecondary).tracker(80, 3, true);
    }

    private static <T extends Entity> EntityEntryBuilder<Entity> createEntityEntry(String name, Class<T> entityClass) {
        if (EntityLiving.class.isAssignableFrom(entityClass))
            LootTableList.register(MutantBeasts.prefix("entities/" + name));
        return EntityEntryBuilder.create().entity(entityClass).id(MutantBeasts.prefix(name), entityId++).name("mutantbeasts." + name);
    }

    private static EntityEntryBuilder<?> createEntry(String name, Class<? extends EntityLiving> entityClass, int eggPrimary, int eggSecondary) {
        return RegistryHandler.createEntry(name, entityClass).egg(eggPrimary, eggSecondary).tracker(80, 3, true);
    }

    private static EntityEntryBuilder<?> createEntry(String name, Class<? extends Entity> entityClass) {
        if (EntityLiving.class.isAssignableFrom(entityClass)) {
            LootTableList.register(MutantBeasts.prefix("entities/" + name));
        }
        return EntityEntryBuilder.create().entity(entityClass).id(MutantBeasts.prefix(name), entityId++).name("mutantbeasts." + name);
    }

    private static void copySpawnsForMutant(Class<? extends EntityLiving> classToAdd, Class<? extends EntityLiving> classToCopy, EnumCreatureType creatureType, int weight) {
        Iterator iterator = ForgeRegistries.BIOMES.iterator();
        while (iterator.hasNext()) {
            Biome biome = (Biome) iterator.next();
            if (!biome.getRegistryName().getNamespace().equals("minecraft")) continue;
            biome.getSpawnableList(creatureType).stream().filter(entry -> entry.entityClass == classToCopy).findFirst().ifPresent(
                    spawnListEntry -> biome.getSpawnableList(creatureType).add(new Biome.SpawnListEntry(classToAdd, weight, 1, 1)));
        }
    }

    @SubscribeEvent
    public static void onEntityEntryRegistry(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(
                RegistryHandler.createEntry("body_part", BodyPartEntity.class).tracker(64, 10, true).build(),
                RegistryHandler.createEntry("chemical_x", ChemicalXEntity.class).tracker(160, 10, true).build(),
                RegistryHandler.createEntityEntry("endersoul_clone", EndersoulCloneEntity.class, 15027455, 15027455).build(),
                RegistryHandler.createEntry("creeper_minion", CreeperMinionEntity.class, 894731, 0xB7B7B7).build(),
                RegistryHandler.createEntry("creeper_minion_egg", CreeperMinionEggEntity.class).tracker(160, 20, true).build(),
                RegistryHandler.createEntry("endersoul_fragment", EndersoulFragmentEntity.class).tracker(64, 10, true).build(),
                RegistryHandler.createEntry("mutant_arrow", MutantArrowEntity.class).tracker(80, 3, true).build(),
                RegistryHandler.createEntry("mutant_creeper", MutantCreeperEntity.class, 5349438, 11013646).build(),
                RegistryHandler.createEntry("mutant_enderman", MutantEndermanEntity.class, 0x161616, 8860812).build(),
                RegistryHandler.createEntry("mutant_skeleton", MutantSkeletonEntity.class, 0xC1C1C1, 6310217).build(),
                RegistryHandler.createEntry("mutant_snow_golem", MutantSnowGolemEntity.class, 0xE5FFFF, 16753434).build(),
                RegistryHandler.createEntry("mutant_zombie", MutantZombieEntity.class, 7969893, 44975).build(),
                RegistryHandler.createEntry("skull_spirit", SkullSpiritEntity.class).tracker(160, 20, false).build(),
                RegistryHandler.createEntry("spider_pig", SpiderPigEntity.class, 3419431, 15771042).build(),
                RegistryHandler.createEntry("throwable_block", ThrowableBlockEntity.class).tracker(64, 100, true).build());

        if (MBConfig.ENTITIES.mutantCreeperSpawnRate > 0) {
            RegistryHandler.copySpawnsForMutant(MutantCreeperEntity.class, EntityCreeper.class, EnumCreatureType.MONSTER, MBConfig.ENTITIES.mutantCreeperSpawnRate);
        }

        if (MBConfig.ENTITIES.mutantEndermanSpawnRate > 0) {
            RegistryHandler.copySpawnsForMutant(MutantEndermanEntity.class, EntityEnderman.class, EnumCreatureType.MONSTER, MBConfig.ENTITIES.mutantEndermanSpawnRate);
        }

        if (MBConfig.ENTITIES.mutantSkeletonSpawnRate > 0) {
            RegistryHandler.copySpawnsForMutant(MutantSkeletonEntity.class, EntitySkeleton.class, EnumCreatureType.MONSTER, MBConfig.ENTITIES.mutantSkeletonSpawnRate);
        }

        if (MBConfig.ENTITIES.mutantZombieSpawnRate > 0) {
            RegistryHandler.copySpawnsForMutant(MutantZombieEntity.class, EntityZombie.class, EnumCreatureType.MONSTER, MBConfig.ENTITIES.mutantZombieSpawnRate);
        }
    }

    private static SoundEvent createSoundEvent(String name) {
        ResourceLocation registryName = MutantBeasts.prefix(name);
        return new SoundEvent(registryName).setRegistryName(registryName);
    }

    private static <T extends IForgeRegistryEntry<T>> T setRegistryName(String name, T entry) {
        ResourceLocation registryName = MutantBeasts.prefix(name);

        if (entry instanceof Item) {
            ((Item) entry).setTranslationKey("mutantbeasts." + registryName.getPath());
            ((Item) entry).setCreativeTab(MutantBeasts.CREATIVE_TAB);
        }

        return entry.setRegistryName(registryName);
    }
}
