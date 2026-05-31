package chumbanotz.mutantbeasts.entity.mutant;

import chumbanotz.mutantbeasts.MBConfig;
import chumbanotz.mutantbeasts.entity.EndersoulCloneEntity;
import chumbanotz.mutantbeasts.entity.EndersoulFragmentEntity;
import chumbanotz.mutantbeasts.entity.ai.EntityAIAvoidDamage;
import chumbanotz.mutantbeasts.entity.ai.EntityAIHurtByNearestTarget;
import chumbanotz.mutantbeasts.entity.ai.MBEntityAIAttackMelee;
import chumbanotz.mutantbeasts.entity.projectile.ThrowableBlockEntity;
import chumbanotz.mutantbeasts.item.EndersoulHandItem;
import chumbanotz.mutantbeasts.packet.HeldBlockPacket;
import chumbanotz.mutantbeasts.packet.MBPacketHandler;
import chumbanotz.mutantbeasts.packet.TeleportPacket;
import chumbanotz.mutantbeasts.pathfinding.MBGroundPathNavigator;
import chumbanotz.mutantbeasts.util.EntityUtil;
import chumbanotz.mutantbeasts.util.MBParticles;
import chumbanotz.mutantbeasts.util.MBSoundEvents;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MutantEndermanEntity extends EntityMob implements IEntityAdditionalSpawnData {
    protected final BossInfoServer bossInfo;
    private static final DataParameter<Byte> ACTIVE_ARM = EntityDataManager.createKey(MutantEndermanEntity.class, (DataSerializer) DataSerializers.BYTE);
    private static final DataParameter<Boolean> CLONE = EntityDataManager.createKey(MutantEndermanEntity.class, (DataSerializer) DataSerializers.BOOLEAN);
    public static final int MAX_DEATH_TIME = 280;
    public static final byte MELEE_ATTACK = 1;
    public static final byte THROW_ATTACK = 2;
    public static final byte STARE_ATTACK = 3;
    public static final byte TELEPORT_ATTACK = 4;
    public static final byte SCREAM_ATTACK = 5;
    public static final byte CLONE_ATTACK = 6;
    public static final byte TELESMASH_ATTACK = 7;
    public static final byte DEATH_ATTACK = 8;
    private int attackID;
    private int attackTick;
    private int prevArmScale;
    private int armScale;
    public int hasTarget;
    private BlockPos teleportPosition = BlockPos.ORIGIN;
    private int screamDelayTick;
    public final int[] heldBlock = new int[5];
    public final int[] heldBlockTick = new int[5];
    private boolean triggerThrowBlock;
    private int blockFrenzy;
    private List<Entity> capturedEntities;
    private int dirty = -1;
    private DamageSource deathCause;
    public int deathTime;

    public MutantEndermanEntity(World worldIn) {
        super(worldIn);
        this.experienceValue = 40;
        this.stepHeight = 1.5f;
        this.setSize(1.2f, 4.2f);
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PINK, BossInfo.Overlay.PROGRESS);
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new MeleeGoal());
        this.tasks.addTask(1, new ThrowBlockGoal());
        this.tasks.addTask(1, new StareGoal());
        this.tasks.addTask(1, new TeleportGoal());
        this.tasks.addTask(1, new ScreamGoal());
        this.tasks.addTask(1, new CloneGoal());
        this.tasks.addTask(1, new TeleSmashGoal());
        this.tasks.addTask(2, new MBEntityAIAttackMelee(this, 1.2).setMaxAttackTick(10));
        this.tasks.addTask(3, new EntityAIAvoidDamage(this, 1.0));
        this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0, 0.0f));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByNearestTarget(this));
        this.targetTasks.addTask(1, new FindTargetGoal(this));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityEndermite.class, 10, true, true, e -> ((EntityEndermite) e).isSpawnedByPlayer()));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(MBConfig.ENTITIES.mutantEndermanArmor);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(MBConfig.ENTITIES.mutantEndermanAttackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(MBConfig.ENTITIES.mutantEndermanFollowRange);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(MBConfig.ENTITIES.mutantEndermanKnockbackResistance);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MBConfig.ENTITIES.mutantEndermanMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MBConfig.ENTITIES.mutantEndermanMovementSpeed);
        this.getEntityAttribute(SWIM_SPEED).setBaseValue(MBConfig.ENTITIES.mutantEndermanSwimSpeed);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ACTIVE_ARM, (byte) 0);
        this.dataManager.register(CLONE, false);
    }

    public BlockPos getTeleportPosition() {
        return this.teleportPosition;
    }

    public void setTeleportPosition(BlockPos pos) {
        this.teleportPosition = pos;
        this.attackID = 4;
        if (this.world.isRemote) {
            this.spawnTeleportParticles();
        } else {
            MBPacketHandler.INSTANCE.sendToAllTracking(new TeleportPacket(this, pos), this);
        }
    }

    public int getActiveArm() {
        return this.dataManager.get(ACTIVE_ARM).byteValue();
    }

    private void setActiveArm(int armID) {
        this.dataManager.set(ACTIVE_ARM, (byte) armID);
    }

    public boolean isClone() {
        return this.dataManager.get(CLONE);
    }

    private void setClone(boolean clone) {
        this.dataManager.set(CLONE, clone);
        this.playSound(MBSoundEvents.ENTITY_MUTANT_ENDERMAN_MORPH, 2.0f, this.getSoundPitch());
    }

    public int getAttackID() {
        return this.attackID;
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    private void setAttackID(int attackID) {
        this.attackID = attackID;
        this.attackTick = 0;
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte) (-attackID));
        }
    }

    public float getEyeHeight() {
        return this.isClone() ? 2.55f : 3.9f;
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new MBGroundPathNavigator(this, worldIn).setAvoidRain(true);
    }

    public int getMaxSpawnedInChunk() {
        return 1;
    }

    public int getMaxFallHeight() {
        return this.isClone() ? 3 : super.getMaxFallHeight();
    }

    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith() && this.attackID != 4;
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (CLONE.equals(key)) {
            if (this.isClone()) {
                this.setSize(0.6f, 2.9f);
            } else {
                this.setSize(1.2f, 4.2f);
            }
        }
    }

    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
        this.setFlag(2, entitylivingbaseIn != null);
    }

    public boolean isAggressive() {
        return this.getFlag(2);
    }

    public float getArmScale(float partialTicks) {
        return ((float) this.prevArmScale + (float) (this.armScale - this.prevArmScale) * partialTicks) / 10.0f;
    }

    private void updateTargetTick() {
        this.prevArmScale = this.armScale;
        if (this.isAggressive()) {
            this.hasTarget = 20;
        }
        boolean emptyHanded = true;
        for (int i = 1; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] > 0) {
                emptyHanded = false;
            }
            if (this.hasTarget > 0) {
                if (this.heldBlock[i] <= 0) continue;
                this.heldBlockTick[i] = Math.min(10, this.heldBlockTick[i] + 1);
                continue;
            }
            this.heldBlockTick[i] = Math.max(0, this.heldBlockTick[i] - 1);
        }
        if (this.hasTarget > 0) {
            this.armScale = Math.min(10, this.armScale + 1);
        } else if (emptyHanded) {
            this.armScale = Math.max(0, this.armScale - 1);
        } else if (!this.world.isRemote) {
            boolean mobGriefing = ForgeEventFactory.getMobGriefingEvent(this.world, this);
            for (int i = 1; i < this.heldBlock.length; ++i) {
                if (this.heldBlock[i] == 0 || this.heldBlockTick[i] != 0) continue;
                BlockPos placePos = new BlockPos(this.posX - 1.5 + this.rand.nextDouble() * 4.0, this.posY - 0.5 + this.rand.nextDouble() * 2.5, this.posZ - 1.5 + this.rand.nextDouble() * 4.0);
                Block heldBlock = Block.getBlockById(this.heldBlock[i]);
                if (MutantEndermanEntity.canPlaceBlock(this.world, placePos, placePos.down(), heldBlock) && mobGriefing) {
                    this.world.setBlockState(placePos, Block.getStateById(this.heldBlock[i]));
                    SoundType soundType = heldBlock.getSoundType(heldBlock.getDefaultState(), this.world, placePos, this);
                    this.playSound(soundType.getPlaceSound(), (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
                    this.sendHoldBlock(i, 0);
                    continue;
                }
                if (mobGriefing && this.rand.nextInt(50) != 0) continue;
                this.triggerThrowBlock = true;
            }
        }
        this.hasTarget = Math.max(0, this.hasTarget - 1);
    }

    private static boolean canPlaceBlock(World world, BlockPos placePos, BlockPos downPos, Block heldBlock) {
        return heldBlock.canPlaceBlockAt(world, placePos) && world.isAirBlock(placePos) && !world.isAirBlock(downPos) && world.getBlockState(downPos).isFullCube();
    }

    private void updateScreamEntities() {
        this.screamDelayTick = Math.max(0, this.screamDelayTick - 1);
        if (this.attackID == 5 && this.attackTick >= 40 && this.attackTick <= 160) {
            if (this.attackTick == 160) {
                this.capturedEntities = null;
            } else if (this.capturedEntities == null) {
                this.capturedEntities = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(20.0, 12.0, 20.0), EndersoulFragmentEntity.IS_VALID_TARGET);
            }
            for (int i = 0; this.capturedEntities != null && i < this.capturedEntities.size(); ++i) {
                Entity entity = this.capturedEntities.get(i);
                if (this.getDistanceSq(entity) > 400.0 || !EntitySelectors.NOT_SPECTATING.apply(entity) || !entity.isAddedToWorld()) {
                    this.capturedEntities.remove(i);
                    --i;
                    continue;
                }
                if (this.attackTick == 40) {
                    entity.attackEntityFrom(DamageSource.causeMobDamage(this).setDamageBypassesArmor().setMagicDamage(), (float) MBConfig.ENTITIES.mutantEndermanScreamDamage);
                    if (entity instanceof EntityLiving) {
                        EntityLiving living = (EntityLiving) entity;
                        living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120, 3));
                        if (this.rand.nextInt(2) != 0) {
                            living.addPotionEffect(new PotionEffect(MobEffects.POISON, 120 + this.rand.nextInt(180), this.rand.nextInt(2)));
                        }
                        if (this.rand.nextInt(4) != 0) {
                            living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300 + this.rand.nextInt(300), this.rand.nextInt(2)));
                        }
                        if (this.rand.nextInt(3) != 0) {
                            living.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 120 + this.rand.nextInt(60), 10 + this.rand.nextInt(2)));
                        }
                        if (this.rand.nextInt(4) != 0) {
                            living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 120 + this.rand.nextInt(400), 0));
                        }
                    }
                }
                entity.rotationPitch += (this.rand.nextFloat() - 0.3f) * 6.0f;
            }
        }
    }

    public void handleStatusUpdate(byte id) {
        if (id <= 0) {
            this.setAttackID(Math.abs(id));
            if (this.attackID == 6) {
                this.spawnTeleportParticles();
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void onLivingUpdate() {
        this.isJumping = false;
        super.onLivingUpdate();
        if (this.attackID != 0) {
            ++this.attackTick;
        }
        if (this.world.isRemote && this.attackID == 5 && this.attackTick == 40) {
            this.spawnTeleportParticles();
        }
        if (this.attackID == 8) {
            this.deathTime = this.attackTick;
        }
        this.updateTargetTick();
        this.updateScreamEntities();
        if (this.world.isRemote && !this.isClone()) {
            double h = this.attackID != 8 ? (double) this.height : (double) (this.height + 1.0f);
            double w = this.attackID != 8 ? (double) this.width : (double) (this.width * 1.5f);
            for (int i = 0; i < 3; ++i) {
                double x = this.posX + (this.rand.nextDouble() - 0.5) * w;
                double y = this.posY + this.rand.nextDouble() * h - 0.25;
                double z = this.posZ + (this.rand.nextDouble() - 0.5) * w;
                this.world.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0);
            }
        }
    }

    private void updateBlockFrenzy() {
        this.blockFrenzy = Math.max(0, this.blockFrenzy - 1);
        if (this.getAttackTarget() != null && this.attackID == 0) {
            if (this.blockFrenzy == 0 && (this.getLastDamageSource() instanceof EntityDamageSourceIndirect || this.rand.nextInt(!this.hasPath() ? 200 : 600) == 0)) {
                this.blockFrenzy = 200 + this.rand.nextInt(80);
            }
            if (this.blockFrenzy > 0 && this.rand.nextInt(8) == 0) {
                int index = this.getFavorableHand();
                if (index == -1) {
                    return;
                }
                BlockPos pos = new BlockPos(this.posX - 2.5 + this.rand.nextDouble() * 5.0, this.posY - 0.5 + this.rand.nextDouble() * 3.0, this.posZ - 2.5 + this.rand.nextDouble() * 5.0);
                IBlockState blockState = this.world.getBlockState(pos);
                if (index != -1 && EndersoulHandItem.canCarry(this.world, pos, blockState)) {
                    this.sendHoldBlock(index, Block.getStateId(blockState));
                    if (ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
                        this.world.setBlockToAir(pos);
                    }
                }
            }
        }
    }

    private void updateTeleport() {
        EntityLivingBase entity = this.getAttackTarget();
        this.teleportByChance(entity == null ? 1600 : 800, entity);
        if ((this.isInWater() && MBConfig.ENTITIES.mutantEndermanWaterWeakness) || this.fallDistance > 3.0f || entity != null && (this.isRidingSameEntity(entity) || this.getDistanceSq(entity) > 1024.0 || !this.hasPath() && !this.getEntitySenses().canSee(entity))) {
            this.teleportByChance(10, entity);
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        if (this.ticksExisted % 100 == 0 && !this.isClone() && this.isWet() && MBConfig.ENTITIES.mutantEndermanWaterWeakness) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0f);
        }

        if (this.dirty >= 0) {
            ++this.dirty;
        }

        if (this.dirty >= 8) {
            this.dirty = -1;
            for (int i = 1; i < this.heldBlock.length; ++i) {
                if (this.heldBlock[i] <= 0) continue;
                this.sendHoldBlock(i, this.heldBlock[i]);
            }
        }

        // Regenerate health when target is lost except when player is in Creative
        if (this.getAttackTarget() == null && this.getHealth() < this.getMaxHealth() && MBConfig.ENTITIES.mutantEndermanNoCombatRegen) {
            if (this.attackingPlayer != null && this.attackingPlayer.isCreative()) {
            } else if (this.ticksExisted % 20 == 0) {
                this.heal(this.getMaxHealth() * 0.2F);
            }
        }

        this.updateBlockFrenzy();
        this.updateTeleport();

        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        if (MBConfig.ENTITIES.mutantEndermanBossBar) this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        if (MBConfig.ENTITIES.mutantEndermanBossBar) this.bossInfo.removePlayer(player);
    }

    protected void collideWithNearbyEntities() {
        if (!this.isClone()) {
            super.collideWithNearbyEntities();
        }
    }

    private int getAvailableHand() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] != 0) continue;
            list.add(i);
        }
        if (list.isEmpty()) {
            return -1;
        }
        return list.get(this.rand.nextInt(list.size()));
    }

    private int getFavorableHand() {
        ArrayList<Integer> outer = new ArrayList<Integer>();
        ArrayList<Integer> inner = new ArrayList<Integer>();
        for (int i = 1; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] != 0) continue;
            if (i <= 2) {
                outer.add(i);
                continue;
            }
            inner.add(i);
        }
        if (outer.isEmpty() && inner.isEmpty()) {
            return -1;
        }
        if (!outer.isEmpty()) {
            return outer.get(this.rand.nextInt(outer.size()));
        }
        return inner.get(this.rand.nextInt(inner.size()));
    }

    private int getThrowingHand() {
        ArrayList<Integer> outer = new ArrayList<Integer>();
        ArrayList<Integer> inner = new ArrayList<Integer>();
        for (int i = 1; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] == 0) continue;
            if (i <= 2) {
                outer.add(i);
                continue;
            }
            inner.add(i);
        }
        if (outer.isEmpty() && inner.isEmpty()) {
            return -1;
        }
        if (!inner.isEmpty()) {
            return inner.get(this.rand.nextInt(inner.size()));
        }
        return outer.get(this.rand.nextInt(outer.size()));
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (!this.world.isRemote && this.attackID == 0) {
            int i = this.getAvailableHand();
            if (!this.teleportByChance(6, entityIn)) {
                if (i != -1) {
                    boolean allHandsFree;
                    boolean bl = allHandsFree = this.heldBlock[1] == 0 && this.heldBlock[2] == 0;
                    if (allHandsFree && this.rand.nextInt(10) == 0) {
                        this.attackID = 6;
                    } else if (allHandsFree && this.rand.nextInt(7) == 0) {
                        this.attackID = 7;
                    } else {
                        this.setActiveArm(i);
                        this.attackID = 1;
                    }
                } else {
                    this.triggerThrowBlock = true;
                }
            }
        }
        if (this.isClone()) {
            boolean flag = super.attackEntityAsMob(entityIn);
            if (!this.world.isRemote && this.rand.nextInt(2) == 0) {
                double x = entityIn.posX + (this.rand.nextDouble() - 0.5) * 24.0;
                double y = entityIn.posY + (double) this.rand.nextInt(5) + 4.0;
                double z = entityIn.posZ + (this.rand.nextDouble() - 0.5) * 24.0;
                this.teleportTo(x, y, z);
            }
            if (flag) {
                this.heal(2.0f);
            }
            this.swingArm(EnumHand.MAIN_HAND);
            return flag;
        }
        return true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (source.getTrueSource() instanceof EntityDragon || source.getTrueSource() instanceof MutantEndermanEntity) {
            return false;
        }
        if ((this.attackID == 4 || this.attackID == 5) && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        boolean damaged = super.attackEntityFrom(source, amount);
        if (damaged && (this.attackID == 3 || this.attackID == 6)) {
            this.attackID = 0;
            return damaged;
        }
        if (!this.world.isRemote && this.isEntityAlive() && (this.getAttackTarget() == null || source != DamageSource.LAVA && source.getImmediateSource() != null)) {
            boolean betterDodge;
            Entity entity = source.getTrueSource();
            boolean bl = betterDodge = entity == null;
            if (source.isProjectile() || source.isExplosion() || source == DamageSource.FALL) {
                betterDodge = true;
            }
            if (this.teleportByChance(betterDodge ? 3 : 6, entity) && source != DamageSource.OUT_OF_WORLD) {
                if (entity != null && entity instanceof EntityLivingBase) {
                    this.setRevengeTarget((EntityLivingBase) entity);
                }
                return false;
            }
            this.teleportByChance(source == DamageSource.DROWN || source == DamageSource.IN_WALL ? 3 : 5, entity);
        }
        return damaged;
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return !this.isClone() && super.isPotionApplicable(potioneffectIn);
    }

    private boolean teleportByChance(int chance, @Nullable Entity entity) {
        if (this.attackID != 0 && !this.isClone()) {
            return false;
        }
        if (this.rand.nextInt(Math.max(1, chance)) == 0) {
            return entity == null ? this.teleportRandomly() : this.teleportToEntity(entity);
        }
        return false;
    }

    private boolean teleportRandomly() {
        if (this.attackID != 0 && !this.isClone()) {
            return false;
        }
        double radius = 24.0;
        double x = this.posX + (this.rand.nextDouble() - 0.5) * 2.0 * radius;
        double y = this.posY + (double) this.rand.nextInt((int) radius * 2) - radius;
        double z = this.posZ + (this.rand.nextDouble() - 0.5) * 2.0 * radius;
        return this.teleportTo(x, y, z);
    }

    private boolean teleportToEntity(Entity entity) {
        if (this.attackID != 0 && !this.isClone()) {
            return false;
        }
        double d = this.getDistanceSq(entity);
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double radius = 16.0;
        if (d < 100.0) {
            x = entity.posX + (this.rand.nextDouble() - 0.5) * 2.0 * radius;
            y = entity.posY + this.rand.nextDouble() * radius;
            z = entity.posZ + (this.rand.nextDouble() - 0.5) * 2.0 * radius;
        } else {
            Vec3d vec = new Vec3d(this.posX - entity.posX, this.getEntityBoundingBox().minY + (double) this.height / 2.0 - entity.posY + (double) entity.getEyeHeight(), this.posZ - entity.posZ);
            vec = vec.normalize();
            x = this.posX + (this.rand.nextDouble() - 0.5) * 8.0 - vec.x * radius;
            y = this.posY + (double) this.rand.nextInt(8) - vec.y * radius;
            z = this.posZ + (this.rand.nextDouble() - 0.5) * 8.0 - vec.z * radius;
        }
        return this.teleportTo(x, y, z);
    }

    private boolean teleportTo(double targetX, double targetY, double targetZ) {
        EnderTeleportEvent event = new EnderTeleportEvent(this, targetX, targetY, targetZ, 0);
        if (MinecraftForge.EVENT_BUS.post(event)) return false;
        if (!this.isServerWorld()) {
            return false;
        }
        if (this.isClone()) {
            boolean flag = EntityUtil.teleportTo(this, event.getTargetX(), event.getTargetY(), event.getTargetZ());
            if (flag) {
                this.dismountRidingEntity();
                if (!this.isSilent()) {
                    this.world.playSound(null, this.prevPosX, this.prevPosY + (double) this.height / 2.0, this.prevPosZ, MBSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT, this.getSoundCategory(), 1.0f, 1.0f);
                    this.playSound(MBSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT, 1.0f, 1.0f);
                }
            }
            return flag;
        }
        if (this.attackID == 0) {
            this.attackID = 4;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos().setPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            if (this.world.isBlockLoaded(pos)) {
                do {
                    pos.move(EnumFacing.DOWN);
                } while (pos.getY() > 0 && !this.world.getBlockState(pos).getMaterial().blocksMovement());
                pos.move(EnumFacing.UP);
            }
            if (!this.isOffsetPositionInLiquid(pos.getX() - MathHelper.floor(this.posX), pos.getY() - MathHelper.floor(this.posY), pos.getZ() - MathHelper.floor(this.posZ))) {
                this.attackID = 0;
                return false;
            }
            this.setTeleportPosition(pos.toImmutable());
            return true;
        }
        return false;
    }

    private void spawnTeleportParticles() {
        int temp = this.attackID == 4 ? 512 : 256;
        for (int i = 0; i < temp; ++i) {
            float f = (this.rand.nextFloat() - 0.5f) * 1.8f;
            float f1 = (this.rand.nextFloat() - 0.5f) * 1.8f;
            float f2 = (this.rand.nextFloat() - 0.5f) * 1.8f;
            boolean useCurrentPos = this.attackID != 4 || i < temp / 2;
            double tempX = (useCurrentPos ? this.posX : (double) this.getTeleportPosition().getX()) + (this.rand.nextDouble() - 0.5) * (double) this.width;
            double tempY = (useCurrentPos ? this.posY : (double) this.getTeleportPosition().getY()) + (this.rand.nextDouble() - 0.5) * (double) this.height + 1.5;
            double tempZ = (useCurrentPos ? this.posZ : (double) this.getTeleportPosition().getZ()) + (this.rand.nextDouble() - 0.5) * (double) this.width;
            this.world.spawnParticle(MBParticles.ENDERSOUL, tempX, tempY, tempZ, f, f1, f2);
        }
    }

    public static void teleportAttack(EntityLivingBase attacker) {
        double radius = 3.0;
        int duration = 140 + attacker.getRNG().nextInt(60);
        DamageSource damageSource = DamageSource.causeMobDamage(attacker);
        if (attacker instanceof EntityPlayer) {
            radius = 2.0;
            duration = 100;
            damageSource = DamageSource.causePlayerDamage((EntityPlayer) attacker);
        }
        for (Entity entity : attacker.world.getEntitiesInAABBexcluding(attacker, attacker.getEntityBoundingBox().grow(radius), EndersoulFragmentEntity.IS_VALID_TARGET)) {
            if (entity instanceof EntityLivingBase && entity.attackEntityFrom(damageSource, 4.0f) && attacker.getRNG().nextInt(3) == 0) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, duration));
            }
            double x = entity.posX - attacker.posX;
            double z = entity.posZ - attacker.posZ;
            double signX = x / Math.abs(x);
            double signZ = z / Math.abs(z);
            if (Double.isNaN(signX) || Double.isNaN(signZ)) {
                return;
            }
            entity.motionX = (radius * signX * 2.0 - x) * (double) 0.2f;
            entity.motionY = 0.2f;
            entity.motionZ = (radius * signZ * 2.0 - z) * (double) 0.2f;
        }
    }

    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().grow(3.5);
    }

    protected boolean canBeRidden(Entity entityIn) {
        return super.canBeRidden(entityIn) && entityIn instanceof EntityLivingBase;
    }

    public boolean canEntityBeSeen(Entity entityIn) {
        return !entityIn.getClass().getSimpleName().equals("EntityGorgon") && super.canEntityBeSeen(entityIn);
    }

    public boolean isPushedByWater() {
        return false;
    }

    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.capturedEntities = null;
        if (!this.world.isRemote) {
            this.deathCause = cause;
            if (this.isClone()) {
                this.setClone(false);
            }
            this.setAttackID(8);
            if (this.world.getGameRules().getBoolean("doMobLoot")) {
                super.dropEquipment(this.recentlyHit > 0, ForgeHooks.getLootingLevel(this, cause.getTrueSource(), cause));
            }
            if (this.recentlyHit > 0) {
                this.recentlyHit += 280;
            }
        }
    }

    protected void onDeathUpdate() {
        this.motionX = 0.0;
        this.motionY = Math.min(this.motionY, 0.0);
        this.motionZ = 0.0;
        if (this.deathTime == 80) {
            this.playSound(MBSoundEvents.ENTITY_MUTANT_ENDERMAN_DEATH, 5.0f, this.getSoundPitch());
        }
        if (this.deathTime >= 60) {
            if (this.deathTime < 80 && this.capturedEntities == null) {
                this.capturedEntities = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(10.0, 8.0, 10.0), EndersoulFragmentEntity.IS_VALID_TARGET);
            }
            if (!this.world.isRemote && this.rand.nextInt(3) != 0 && MBConfig.ENTITIES.mutantEndermanSpawnsFragments) {
                EndersoulFragmentEntity orb = new EndersoulFragmentEntity(this.world);
                orb.setPosition(this.posX, this.posY + 3.8, this.posZ);
                orb.motionX = (this.rand.nextFloat() - 0.5f) * 1.5f;
                orb.motionY = (this.rand.nextFloat() - 0.5f) * 1.5f;
                orb.motionZ = (this.rand.nextFloat() - 0.5f) * 1.5f;
                this.world.spawnEntity(orb);
            }
        }
        if (this.deathTime >= 80 && this.deathTime < 260 && this.capturedEntities != null) {
            for (int i = 0; i < this.capturedEntities.size(); ++i) {
                Entity entity = this.capturedEntities.get(i);
                if (EndersoulFragmentEntity.isProtected(entity) || !EntitySelectors.NOT_SPECTATING.apply(entity) || !entity.isAddedToWorld()) {
                    this.capturedEntities.remove(i);
                    --i;
                    continue;
                }
                if (entity.fallDistance > 4.5f) {
                    entity.fallDistance = 4.5f;
                }
                if (!(this.getDistanceSq(entity) > 64.0)) continue;
                double x = this.posX - entity.posX;
                double z = this.posZ - entity.posZ;
                double d = Math.sqrt(x * x + z * z);
                entity.motionX = (double) 0.8f * x / d;
                entity.motionZ = (double) 0.8f * z / d;
                if (!(this.posY + 4.0 > entity.posY)) continue;
                entity.motionY = Math.max(entity.motionY, 0.4f);
            }
        }
        if (!this.world.isRemote && this.deathTime >= 100 && this.deathTime < 150 && this.deathTime % 6 == 0 && this.world.getGameRules().getBoolean("doMobLoot")) {
            DamageSource source = this.deathCause != null ? this.deathCause : DamageSource.GENERIC;
            this.dropLoot(this.recentlyHit > 0, ForgeHooks.getLootingLevel(this, source.getTrueSource(), source), source);
        }
        if (this.deathTime >= 280) {
            EntityUtil.dropExperience(this, this.recentlyHit, this::getExperiencePoints, this.attackingPlayer);
            this.setDead();
        }
    }

    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
    }

    protected boolean canDropLoot() {
        return false;
    }

    public EntityItem entityDropItem(ItemStack stack, float offsetY) {
        return super.entityDropItem(stack, this.deathTime > 0 ? 3.84f : offsetY);
    }

    public String getName() {
        return this.isClone() ? I18n.translateToLocal("entity.mutantbeasts.endersoul_clone.name") : super.getName();
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("ScreamDelay", this.screamDelayTick);
        compound.setInteger("BlockFrenzy", this.blockFrenzy);
        compound.setShort("DeathTime", (short) this.deathTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.screamDelayTick = compound.getInteger("ScreamDelay");
        this.blockFrenzy = compound.getInteger("BlockFrenzy");
        this.deathTime = ((EntityMob) this).deathTime;

        if (this.deathTime > 0) {
            this.attackID = 8;
            this.attackTick = this.deathTime;
            ((EntityMob) this).deathTime = 0;
        }

        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void playLivingSound() {
        if (!this.isClone()) {
            super.playLivingSound();
        }
    }

    @Override
    public boolean isNonBoss() {
        return MBConfig.ENTITIES.mutantEndermanBossClassification ? false : true;
    }

    protected SoundEvent getAmbientSound() {
        return MBSoundEvents.ENTITY_MUTANT_ENDERMAN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return MBSoundEvents.ENTITY_MUTANT_ENDERMAN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return MBSoundEvents.ENTITY_MUTANT_ENDERMAN_HURT;
    }

    protected ResourceLocation getLootTable() {
        return EntityUtil.getLootTable(this);
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.attackID);
        buffer.writeInt(this.attackTick);
        buffer.writeInt(this.deathTime);
        buffer.writeInt(this.armScale);
        buffer.writeInt(this.hasTarget);
        buffer.writeLong(this.teleportPosition.toLong());
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.attackID = additionalData.readInt();
        this.attackTick = additionalData.readInt();
        this.deathTime = additionalData.readInt();
        this.armScale = additionalData.readInt();
        this.hasTarget = additionalData.readInt();
        this.teleportPosition = BlockPos.fromLong(additionalData.readLong());
    }

    public void sendHoldBlock(int blockIndex, int blockId) {
        this.heldBlock[blockIndex] = blockId;
        this.heldBlockTick[blockIndex] = 0;
        if (!this.world.isRemote) {
            MBPacketHandler.INSTANCE.sendToAllTracking(new HeldBlockPacket(this, blockId, blockIndex), this);
        }
    }

    private boolean isBeingLookedAtBy(EntityLivingBase target) {
        if (target instanceof EntityLiving) {
            return ((EntityLiving) target).getAttackTarget() == this && target.canEntityBeSeen(this);
        }
        Vec3d playerVec = target.getLook(1.0f).normalize();
        Vec3d targetVec = new Vec3d(this.posX - target.posX, this.getEntityBoundingBox().minY + (double) this.getEyeHeight() - (target.posY + (double) target.getEyeHeight()), this.posZ - target.posZ);
        double length = targetVec.length();
        double d = playerVec.dotProduct(targetVec = targetVec.normalize());
        return d > 1.0 - 0.08 / length && target.canEntityBeSeen(this);
    }

    public class ThrowBlockGoal
            extends EntityAIBase {
        public boolean shouldExecute() {
            if (MutantEndermanEntity.this.attackID != 0) {
                return false;
            }
            if (!MutantEndermanEntity.this.triggerThrowBlock && MutantEndermanEntity.this.rand.nextInt(28 - MutantEndermanEntity.this.hurtTime) != 0) {
                return false;
            }
            if (MutantEndermanEntity.this.getAttackTarget() != null && !MutantEndermanEntity.this.getEntitySenses().canSee(MutantEndermanEntity.this.getAttackTarget())) {
                return false;
            }
            int id = MutantEndermanEntity.this.getThrowingHand();
            if (id == -1) {
                return false;
            }
            MutantEndermanEntity.this.setActiveArm(id);
            return true;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.attackTick = 0;
            MutantEndermanEntity.this.setAttackID(2);
            int id = MutantEndermanEntity.this.getActiveArm();
            MutantEndermanEntity.this.world.spawnEntity(new ThrowableBlockEntity(MutantEndermanEntity.this.world, MutantEndermanEntity.this, id));
            MutantEndermanEntity.this.sendHoldBlock(id, 0);
        }

        public boolean shouldContinueExecuting() {
            return MutantEndermanEntity.this.attackID == 2 && MutantEndermanEntity.this.attackTick < 14;
        }

        public void resetTask() {
            MutantEndermanEntity.this.setAttackID(0);
            MutantEndermanEntity.this.setActiveArm(0);
            MutantEndermanEntity.this.triggerThrowBlock = false;
        }
    }

    class TeleSmashGoal
            extends EntityAIBase {
        private EntityLivingBase attackTarget;

        public TeleSmashGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            this.attackTarget = MutantEndermanEntity.this.getAttackTarget();
            return this.attackTarget != null && MutantEndermanEntity.this.attackID == 7;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.setAttackID(7);
            this.attackTarget.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 5));
            this.attackTarget.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 160 + this.attackTarget.getRNG().nextInt(160), 0));
        }

        public boolean shouldContinueExecuting() {
            return MutantEndermanEntity.this.attackID == 7 && MutantEndermanEntity.this.attackTick < 30;
        }

        public void updateTask() {
            MutantEndermanEntity.this.getNavigator().clearPath();
            if (MutantEndermanEntity.this.attackTick < 20) {
                MutantEndermanEntity.this.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0f, 30.0f);
            }
            if (MutantEndermanEntity.this.attackTick == 17) {
                this.attackTarget.dismountRidingEntity();
            }
            if (MutantEndermanEntity.this.attackTick == 18) {
                double x = this.attackTarget.posX + (double) ((this.attackTarget.getRNG().nextFloat() - 0.5f) * 14.0f);
                double y = this.attackTarget.posY + (double) this.attackTarget.getRNG().nextFloat() + (this.attackTarget instanceof EntityPlayer ? 13.0 : 7.0);
                double z = this.attackTarget.posZ + (double) ((this.attackTarget.getRNG().nextFloat() - 0.5f) * 14.0f);
                EntityUtil.sendParticlePacket(this.attackTarget, MBParticles.ENDERSOUL, 256);
                this.attackTarget.setPositionAndUpdate(x, y, z);
                this.attackTarget.world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, this.attackTarget.getSoundCategory(), 1.2f, 0.9f + this.attackTarget.getRNG().nextFloat() * 0.2f);
                this.attackTarget.attackEntityFrom(DamageSource.causeMobDamage(MutantEndermanEntity.this).setDamageBypassesArmor().setMagicDamage(), (float) MBConfig.ENTITIES.mutantEndermanTelesmashDamage);
            }
        }

        public void resetTask() {
            MutantEndermanEntity.this.setAttackID(0);
            this.attackTarget = null;
        }
    }

    class TeleportGoal
            extends EntityAIBase {
        public TeleportGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            return MutantEndermanEntity.this.attackID == 4;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.attackTick = 0;
            MutantEndermanEntity.this.getNavigator().clearPath();
            if (MutantEndermanEntity.this.getAttackTarget() != null) {
                MutantEndermanEntity.this.getLookHelper().setLookPositionWithEntity(MutantEndermanEntity.this.getAttackTarget(), 30.0f, 30.0f);
            }
            MutantEndermanEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            MutantEndermanEntity.teleportAttack(MutantEndermanEntity.this);
            MutantEndermanEntity.this.setPosition((double) MutantEndermanEntity.this.getTeleportPosition().getX() + 0.5, MutantEndermanEntity.this.getTeleportPosition().getY(), (double) MutantEndermanEntity.this.getTeleportPosition().getZ() + 0.5);
            MutantEndermanEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            MutantEndermanEntity.teleportAttack(MutantEndermanEntity.this);
            MutantEndermanEntity.this.setPosition(MutantEndermanEntity.this.prevPosX, MutantEndermanEntity.this.prevPosY, MutantEndermanEntity.this.prevPosZ);
        }

        public boolean shouldContinueExecuting() {
            return this.shouldExecute() && MutantEndermanEntity.this.attackTick < 10;
        }

        public void resetTask() {
            MutantEndermanEntity.this.fallDistance = 0.0f;
            MutantEndermanEntity.this.setPosition((double) MutantEndermanEntity.this.getTeleportPosition().getX() + 0.5, MutantEndermanEntity.this.getTeleportPosition().getY(), (double) MutantEndermanEntity.this.getTeleportPosition().getZ() + 0.5);
            MutantEndermanEntity.this.setAttackID(0);
            MutantEndermanEntity.this.dismountRidingEntity();
            MutantEndermanEntity.this.prevPosX = MutantEndermanEntity.this.posX;
            MutantEndermanEntity.this.prevPosY = MutantEndermanEntity.this.posY;
            MutantEndermanEntity.this.prevPosZ = MutantEndermanEntity.this.posZ;
        }
    }

    class ScreamGoal
            extends EntityAIBase {
        public ScreamGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            if (MutantEndermanEntity.this.getAttackTarget() != null && MutantEndermanEntity.this.attackID == 0) {
                return MutantEndermanEntity.this.screamDelayTick <= 0 && MutantEndermanEntity.this.rand.nextInt(MutantEndermanEntity.this.isWet() ? 400 : 1200) == 0;
            }
            return false;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.setAttackID(5);
            MutantEndermanEntity.this.livingSoundTime = -MutantEndermanEntity.this.getTalkInterval();
        }

        public boolean shouldContinueExecuting() {
            return MutantEndermanEntity.this.attackTick < 165;
        }

        public void updateTask() {
            MutantEndermanEntity.this.getNavigator().clearPath();
            if (MutantEndermanEntity.this.attackTick == 40) {
                if (MutantEndermanEntity.this.world.isRaining() && ForgeEventFactory.getMobGriefingEvent(MutantEndermanEntity.this.world, MutantEndermanEntity.this)) {
                    MutantEndermanEntity.this.world.getWorldInfo().setRaining(false);
                }
                MutantEndermanEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ENDERMAN_SCREAM, 6.0f, 0.7f + MutantEndermanEntity.this.rand.nextFloat() * 0.2f);
            }
        }

        public void resetTask() {
            MutantEndermanEntity.this.setAttackID(0);
            MutantEndermanEntity.this.screamDelayTick = 600;
        }
    }

    public boolean getCanSpawnHere() {
        if (this.rand.nextInt(3) == 0) {
            return false;
        } else if (this.world.provider.getDimensionType() == DimensionType.THE_END && this.rand.nextInt(2600) != 0) {
            return false;
        } else {
            return super.getCanSpawnHere() && this.world.canSeeSky(this.getPosition()) && EntityUtil.getRandomSpawnChance(this.rand);
        }
    }

    class CloneGoal
            extends EntityAIBase {
        private final List<EndersoulCloneEntity> cloneList = new ArrayList<EndersoulCloneEntity>();
        private EntityLivingBase attackTarget;

        CloneGoal() {
        }

        public boolean shouldExecute() {
            if (MutantEndermanEntity.this.getAttackTarget() == null) {
                return false;
            }
            if (MutantEndermanEntity.this.heldBlock[1] == 0 && MutantEndermanEntity.this.heldBlock[2] == 0) {
                return MutantEndermanEntity.this.attackID == 6 || MutantEndermanEntity.this.attackID == 0 && MutantEndermanEntity.this.rand.nextInt(300) == 0;
            }
            return false;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.setAttackID(6);
            MutantEndermanEntity.this.setClone(true);
            MutantEndermanEntity.this.extinguish();
            MutantEndermanEntity.this.clearActivePotions();
            MutantEndermanEntity.this.stepHeight = 1.0f;
            MutantEndermanEntity.this.hurtResistantTime = 15;
            this.attackTarget = MutantEndermanEntity.this.getAttackTarget();
            for (int i = 0; i < 7; ++i) {
                double x = this.attackTarget.posX + (MutantEndermanEntity.this.rand.nextDouble() - 0.5) * 24.0;
                double y = this.attackTarget.posY + 8.0;
                double z = this.attackTarget.posZ + (MutantEndermanEntity.this.rand.nextDouble() - 0.5) * 24.0;
                this.createClone(x, y, z);
            }
            double x = this.attackTarget.posX + (MutantEndermanEntity.this.rand.nextDouble() - 0.5) * 24.0;
            double y = this.attackTarget.posY + 8.0;
            double z = this.attackTarget.posZ + (MutantEndermanEntity.this.rand.nextDouble() - 0.5) * 24.0;
            EntityUtil.teleportTo(MutantEndermanEntity.this, x, y, z);
            this.createClone(MutantEndermanEntity.this.prevPosX, MutantEndermanEntity.this.prevPosY, MutantEndermanEntity.this.prevPosZ);
            EntityUtil.divertAttackers(MutantEndermanEntity.this, this.getRandomClone());
        }

        public boolean shouldContinueExecuting() {
            return MutantEndermanEntity.this.attackID == 6 && MutantEndermanEntity.this.getAttackTarget() != null && MutantEndermanEntity.this.getAttackTarget().isEntityAlive() && !this.cloneList.isEmpty() && MutantEndermanEntity.this.isClone() && MutantEndermanEntity.this.attackTick < 600;
        }

        public void updateTask() {
            for (int i = this.cloneList.size() - 1; i >= 0; --i) {
                EndersoulCloneEntity clone = this.cloneList.get(i);
                if (!clone.isEntityAlive()) {
                    this.cloneList.remove(i);
                    continue;
                }
                if (clone.getAttackTarget() == MutantEndermanEntity.this.getAttackTarget()) continue;
                clone.setAttackTarget(MutantEndermanEntity.this.getAttackTarget());
            }
        }

        public void resetTask() {
            MutantEndermanEntity.this.setAttackID(0);
            MutantEndermanEntity.this.setClone(false);
            for (EndersoulCloneEntity clone : this.cloneList) {
                if (!clone.isEntityAlive()) continue;
                clone.setDead();
            }
            this.cloneList.clear();
            MutantEndermanEntity.this.getNavigator().clearPath();
            MutantEndermanEntity.this.stepHeight = 1.4f;
            this.attackTarget.setRevengeTarget(MutantEndermanEntity.this);
            this.attackTarget = null;
        }

        private void createClone(double x, double y, double z) {
            EndersoulCloneEntity clone = new EndersoulCloneEntity(MutantEndermanEntity.this, x, y, z);
            clone.setAttackTarget(this.attackTarget);
            MutantEndermanEntity.this.world.spawnEntity(clone);
            this.cloneList.add(clone);
        }

        private EndersoulCloneEntity getRandomClone() {
            return this.cloneList.isEmpty() ? null : this.cloneList.get(MutantEndermanEntity.this.rand.nextInt(this.cloneList.size()));
        }
    }

    class MeleeGoal
            extends EntityAIBase {
        MeleeGoal() {
        }

        public boolean shouldExecute() {
            return MutantEndermanEntity.this.attackID == 1;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.setAttackID(1);
        }

        public boolean shouldContinueExecuting() {
            return this.shouldExecute() && MutantEndermanEntity.this.attackTick < 10;
        }

        public void updateTask() {
            if (MutantEndermanEntity.this.attackTick == 3) {
                MutantEndermanEntity.this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, MutantEndermanEntity.this.getSoundPitch());
                DamageSource damageSource = DamageSource.causeMobDamage(MutantEndermanEntity.this);
                boolean lower = MutantEndermanEntity.this.getActiveArm() >= 3;
                float attackDamage = (float) MutantEndermanEntity.this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                for (Entity entity : MutantEndermanEntity.this.world.getEntitiesWithinAABBExcludingEntity(MutantEndermanEntity.this, MutantEndermanEntity.this.getEntityBoundingBox().grow(4.0))) {
                    if (!entity.canBeCollidedWith() || entity instanceof MutantEndermanEntity) continue;
                    double dist = MutantEndermanEntity.this.getDistance(entity);
                    double x = MutantEndermanEntity.this.posX - entity.posX;
                    double z = MutantEndermanEntity.this.posZ - entity.posZ;
                    if (!(MutantEndermanEntity.this.getEntityBoundingBox().minY <= entity.getEntityBoundingBox().maxY) || !(dist <= 4.0) || !(EntityUtil.getHeadAngle(MutantEndermanEntity.this, x, z) < 3.0f + (1.0f - (float) dist / 4.0f) * 40.0f))
                        continue;
                    if (entity.attackEntityFrom(damageSource, attackDamage > 0.0f ? attackDamage + (lower ? 1.0f : 3.0f) : 0.0f)) {
                        MutantEndermanEntity.this.applyEnchantments(MutantEndermanEntity.this, entity);
                    }
                    float power = 0.4f + MutantEndermanEntity.this.rand.nextFloat() * 0.2f;
                    if (!lower) {
                        power += 0.2f;
                    }
                    entity.motionX = -x / dist * (double) power;
                    entity.motionY = power * 0.6f;
                    entity.motionZ = -z / dist * (double) power;
                }
            }
        }

        public void resetTask() {
            MutantEndermanEntity.this.setAttackID(0);
        }
    }

    class StareGoal
            extends EntityAIBase {
        private EntityLivingBase attackTarget;

        public StareGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            this.attackTarget = MutantEndermanEntity.this.getAttackTarget();
            return MutantEndermanEntity.this.attackID == 3 && this.attackTarget != null;
        }

        public void startExecuting() {
            MutantEndermanEntity.this.setAttackID(3);
            MutantEndermanEntity.this.livingSoundTime = -MutantEndermanEntity.this.getTalkInterval();
            MutantEndermanEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ENDERMAN_STARE, 2.5f, 0.7f + MutantEndermanEntity.this.rand.nextFloat() * 0.2f);
        }

        public boolean shouldContinueExecuting() {
            if (this.attackTarget instanceof EntityLiving) {
                return false;
            }
            return MutantEndermanEntity.this.attackID == 3 && this.attackTarget.isEntityAlive() && MutantEndermanEntity.this.attackTick <= 100 && MutantEndermanEntity.this.isBeingLookedAtBy(this.attackTarget);
        }

        public void updateTask() {
            MutantEndermanEntity.this.getNavigator().clearPath();
            MutantEndermanEntity.this.getLookHelper().setLookPositionWithEntity(this.attackTarget, 45.0f, 45.0f);
        }

        public void resetTask() {
            MutantEndermanEntity.this.setAttackID(0);
            this.attackTarget.dismountRidingEntity();
            this.attackTarget.attackEntityFrom(DamageSource.causeMobDamage(MutantEndermanEntity.this).setDamageBypassesArmor().setMagicDamage(), (float) MBConfig.ENTITIES.mutantEndermanStareDamage);
            this.attackTarget.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 160 + MutantEndermanEntity.this.rand.nextInt(140)));
            double x = MutantEndermanEntity.this.posX - this.attackTarget.posX;
            double z = MutantEndermanEntity.this.posZ - this.attackTarget.posZ;
            this.attackTarget.motionX = x * (double) 0.1f;
            this.attackTarget.motionY = 0.3f;
            this.attackTarget.motionZ = z * (double) 0.1f;
            EntityUtil.sendPlayerVelocityPacket(this.attackTarget);
            this.attackTarget = null;
        }
    }

    static class FindTargetGoal
            extends EntityAINearestAttackableTarget<EntityPlayer> {
        public FindTargetGoal(MutantEndermanEntity mutantEnderman) {
            super(mutantEnderman, EntityPlayer.class, 0, false, false, target -> {
                if (mutantEnderman.isBeingLookedAtBy(target)) {
                    mutantEnderman.attackID = 3;
                    return true;
                }
                return EndersoulFragmentEntity.isProtected(target);
            });
        }

        public boolean shouldExecute() {
            return ((MutantEndermanEntity) this.taskOwner).attackID == 0 && super.shouldExecute();
        }

        public void resetTask() {
            super.resetTask();
            this.targetEntity = null;
        }
    }
}
