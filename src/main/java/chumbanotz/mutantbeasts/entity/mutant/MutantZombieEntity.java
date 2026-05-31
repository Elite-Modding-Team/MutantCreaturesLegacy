package chumbanotz.mutantbeasts.entity.mutant;

import chumbanotz.mutantbeasts.MBConfig;
import chumbanotz.mutantbeasts.entity.ai.EntityAIAvoidDamage;
import chumbanotz.mutantbeasts.entity.ai.EntityAIHurtByNearestTarget;
import chumbanotz.mutantbeasts.entity.ai.MBEntityAIAttackMelee;
import chumbanotz.mutantbeasts.pathfinding.MBGroundPathNavigator;
import chumbanotz.mutantbeasts.util.EntityUtil;
import chumbanotz.mutantbeasts.util.MBSoundEvents;
import chumbanotz.mutantbeasts.util.SeismicWave;
import chumbanotz.mutantbeasts.util.ZombieResurrection;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.ArrayList;
import java.util.List;

public class MutantZombieEntity extends EntityMob implements IEntityAdditionalSpawnData {
    protected final BossInfoServer bossInfo;
    private static final DataParameter<Integer> LIVES = EntityDataManager.createKey(MutantZombieEntity.class, (DataSerializer) DataSerializers.VARINT);
    private static final DataParameter<Byte> THROW_ATTACK_STATE = EntityDataManager.createKey(MutantZombieEntity.class, (DataSerializer) DataSerializers.BYTE);
    public static final int MAX_DEATH_TIME = 140;
    public static final int MAX_VANISH_TIME = 100;
    public static final byte MELEE_ATTACK = 1;
    public static final byte THROW_ATTACK = 2;
    public static final byte ROAR_ATTACK = 3;
    private int attackID;
    private int attackTick;
    public int throwHitTick = -1;
    public int throwFinishTick = -1;
    public int vanishTime;
    private final List<SeismicWave> seismicWavesList = new ArrayList<SeismicWave>();
    private final List<ZombieResurrection> resurrections = new ArrayList<ZombieResurrection>();
    private DamageSource deathCause;
    public int deathTime;

    public MutantZombieEntity(World worldIn) {
        super(worldIn);
        this.stepHeight = 1.0f;
        this.experienceValue = 30;
        this.setSize(1.8f, 3.2f);
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS);
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new MeleeGoal());
        this.tasks.addTask(1, new RoarGoal());
        this.tasks.addTask(1, new ThrowAttackGoal());
        this.tasks.addTask(2, new MBEntityAIAttackMelee(this, 1.2).setMaxAttackTick(0));
        this.tasks.addTask(3, new EntityAIAvoidDamage(this, 1.0));
        this.tasks.addTask(4, new EntityAIMoveThroughVillage(this, 1.0, true));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByNearestTarget(this, true, new Class[0]));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true).setUnseenMemoryTicks(100));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(MBConfig.ENTITIES.mutantZombieArmor);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(MBConfig.ENTITIES.mutantZombieAttackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(MBConfig.ENTITIES.mutantZombieFollowRange);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(MBConfig.ENTITIES.mutantZombieKnockbackResistance);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MBConfig.ENTITIES.mutantZombieMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MBConfig.ENTITIES.mutantZombieMovementSpeed);
        this.getEntityAttribute(SWIM_SPEED).setBaseValue(MBConfig.ENTITIES.mutantZombieSwimSpeed);
    }

    public boolean getCanSpawnHere() {
        return MBConfig.ENTITIES.mutantZombieUndergroundSpawning ? super.getCanSpawnHere() && EntityUtil.getRandomSpawnChance(this.rand) :
                super.getCanSpawnHere() && this.world.canSeeSky(this.getPosition()) && EntityUtil.getRandomSpawnChance(this.rand);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(LIVES, MBConfig.ENTITIES.mutantZombieLives);
        this.dataManager.register(THROW_ATTACK_STATE, (byte) 0);
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new MBGroundPathNavigator(this, worldIn);
    }

    protected float updateDistance(float p_110146_1_, float p_110146_2_) {
        return this.deathTime > 0 ? p_110146_2_ : super.updateDistance(p_110146_1_, p_110146_2_);
    }

    public int getLives() {
        return this.dataManager.get(LIVES);
    }

    private void setLives(int lives) {
        this.dataManager.set(LIVES, lives);
    }

    public boolean getThrowAttackHit() {
        return (this.dataManager.get(THROW_ATTACK_STATE) & 1) != 0;
    }

    private void setThrowAttackHit(boolean hit) {
        byte b0 = this.dataManager.get(THROW_ATTACK_STATE);
        this.dataManager.set(THROW_ATTACK_STATE, hit ? (byte) (b0 | 1) : (byte) (b0 & 0xFFFFFFFE));
    }

    public boolean getThrowAttackFinish() {
        return (this.dataManager.get(THROW_ATTACK_STATE) & 2) != 0;
    }

    private void setThrowAttackFinished(boolean finished) {
        byte b0 = this.dataManager.get(THROW_ATTACK_STATE);
        this.dataManager.set(THROW_ATTACK_STATE, finished ? (byte) (b0 | 2) : (byte) (b0 & 0xFFFFFFFD));
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
        this.world.setEntityState(this, (byte) (-attackID));
    }

    public float getEyeHeight() {
        return 2.8f;
    }

    public int getMaxSpawnedInChunk() {
        return 1;
    }

    public int getMaxFallHeight() {
        return this.getAttackTarget() != null ? (int) this.getDistance(this.getAttackTarget()) : 3;
    }

    public boolean canBePushed() {
        return !this.isOnLadder();
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!(itemStack.getItem() != Items.FLINT_AND_STEEL || this.isEntityAlive() || this.isBurning() || this.isWet())) {
            this.setFire(8);
            player.swingArm(hand);
            itemStack.damageItem(1, player);
            player.addStat(StatList.getObjectUseStats(itemStack.getItem()));
            this.world.playSound(player, this.getPosition(), SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0f, this.rand.nextFloat() * 0.4f + 0.8f);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (!this.world.isRemote) {
            if (this.attackID == 0 && (this.getRidingEntity() == entityIn || this.rand.nextInt(5) == 0 && this.getEntitySenses().canSee(entityIn))) {
                this.attackID = 2;
            }
            if (this.attackID == 0 && (this.onGround || this.world.containsAnyLiquid(this.getEntityBoundingBox()))) {
                this.attackID = 1;
            }
        }
        return true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity entity;
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (this.attackID == 3 && source != DamageSource.OUT_OF_WORLD) {
            if (this.attackTick < 10) {
                return false;
            }
            if (!source.isUnblockable()) {
                amount *= 0.15f;
            }
        }
        return ((entity = source.getTrueSource()) == null || this.attackID != 2 || entity != this.getAttackTarget()) && super.attackEntityFrom(source, amount);
    }

    public void handleStatusUpdate(byte id) {
        if (id <= 0) {
            this.attackID = Math.abs(id);
            this.attackTick = 0;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        if (this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 49.0 && Math.abs(this.posY - this.getAttackTarget().posY) <= 4.0) {
            if (this.attackID == 0 && (this.onGround || this.world.containsAnyLiquid(this.getEntityBoundingBox())) && this.rand.nextInt(20) == 0) {
                this.attackID = 1;
            }

            if (this.attackID == 0 && this.getDistanceSq(this.getAttackTarget()) < 1.0 && this.rand.nextInt(125) == 0) {
                this.attackID = 2;
            }
        }

        // Regenerate health when target is lost except when player is in Creative
        if (this.getAttackTarget() == null && this.getHealth() < this.getMaxHealth() && MBConfig.ENTITIES.mutantZombieNoCombatRegen) {
            if (this.attackingPlayer != null && this.attackingPlayer.isCreative()) {
            } else if (this.ticksExisted % 20 == 0) {
                this.heal(this.getMaxHealth() * 0.2F);
            }
        }

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
        if (MBConfig.ENTITIES.mutantZombieBossBar) this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        if (MBConfig.ENTITIES.mutantZombieBossBar) this.bossInfo.removePlayer(player);
    }

    public void onUpdate() {
        super.onUpdate();
        this.fixRotation();
        this.updateAnimation();
        this.updateMeleeGrounds();
        if (!this.world.isDaytime() && this.ticksExisted % 100 == 0 && this.isEntityAlive() && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0f);
        }
        for (int i = this.resurrections.size() - 1; i >= 0; --i) {
            ZombieResurrection zr = this.resurrections.get(i);
            if (zr.update(this)) continue;
            this.resurrections.remove(zr);
        }
        if (this.getHealth() > 0.0f) {
            this.deathTime = 0;
            this.vanishTime = 0;
        }
    }

    private void fixRotation() {
        float yaw;
        for (yaw = this.rotationYawHead - this.renderYawOffset; yaw < -180.0f; yaw += 360.0f) {
        }
        while (yaw >= 180.0f) {
            yaw -= 360.0f;
        }
        float offset = 0.1f;
        if (this.attackID == 1) {
            offset = 0.2f;
        }
        this.renderYawOffset += yaw * offset;
    }

    private void updateAnimation() {
        if (this.attackID != 0) {
            ++this.attackTick;
        }
        if (this.world.isRemote) {
            if (this.attackID == 2) {
                if (this.getThrowAttackHit()) {
                    if (this.throwHitTick == -1) {
                        this.throwHitTick = 0;
                    }
                    ++this.throwHitTick;
                }
                if (this.getThrowAttackFinish()) {
                    if (this.throwFinishTick == -1) {
                        this.throwFinishTick = 0;
                    }
                    ++this.throwFinishTick;
                }
            } else {
                this.throwHitTick = -1;
                this.throwFinishTick = -1;
            }
        }
    }

    private void updateMeleeGrounds() {
        if (!this.seismicWavesList.isEmpty()) {
            SeismicWave wave = this.seismicWavesList.remove(0);
            wave.affectBlocks(this.world, this);
            AxisAlignedBB box = new AxisAlignedBB(wave.getX(), wave.getY() + 1, wave.getZ(), wave.getX() + 2, wave.getY() + 2, wave.getZ() + 2);
            if (wave.isFirst()) {
                double addScale = this.rand.nextDouble() * 0.75;
                box = box.grow(0.25 + addScale, 0.25 + addScale * 0.5, 0.25 + addScale);
            }
            DamageSource source = DamageSource.causeMobDamage(this).setDamageIsAbsolute();
            for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, box)) {
                if (!entity.canBeCollidedWith() || this.isOnSameTeam(entity)) continue;
                if (entity.attackEntityFrom(source, wave.isFirst() ? (float) (9 + this.rand.nextInt(4)) : (float) (6 + this.rand.nextInt(3)))) {
                    this.applyEnchantments(this, entity);
                    if (entity instanceof EntityLivingBase && this.rand.nextInt(5) == 0) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 160, 1));
                    }
                }
                double x = entity.posX - this.posX;
                double z = entity.posZ - this.posZ;
                double d = Math.max(Math.sqrt(x * x + z * z), 0.001);
                entity.motionX = x / d * 0.3;
                entity.motionY = 0.04;
                entity.motionZ = z / d * 0.3;
            }
        }
    }

    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().grow(1.0);
    }

    protected boolean canBeRidden(Entity entityIn) {
        return super.canBeRidden(entityIn) && entityIn instanceof EntityLivingBase;
    }

    public boolean isPushedByWater() {
        return false;
    }

    public void onDeath(DamageSource cause) {
        if (!this.world.isRemote) {
            this.deathCause = cause;
            this.setLastAttackedEntity(this.getRevengeTarget());
            this.world.setEntityState(this, (byte) 3);
            if (this.recentlyHit > 0) {
                this.recentlyHit += 140;
            }
        }
    }

    protected void onDeathUpdate() {
        if (this.deathTime <= 25 || !this.isBurning() || this.deathTime >= 100) {
            ++this.deathTime;
        }
        if (this.isBurning()) {
            ++this.vanishTime;
        } else if (this.vanishTime > 0) {
            --this.vanishTime;
        }
        if (this.getLives() <= 0) {
            ((EntityMob) this).deathTime = this.deathTime;
        }
        if (this.deathTime >= 140) {
            this.deathTime = 0;
            this.vanishTime = 0;
            this.deathCause = null;
            this.setLives(this.getLives() - 1);
            if (this.getLastAttackedEntity() != null) {
                this.getLastAttackedEntity().setRevengeTarget(this);
            }
            this.setHealth(Math.round(this.getMaxHealth() / 3.75f));
            return;
        }
        if (this.vanishTime >= 100 || this.getLives() <= 0 && this.deathTime > 25) {
            if (!this.world.isRemote) {
                EntityUtil.dropExperience(this, this.recentlyHit, this::getExperiencePoints, this.attackingPlayer);
                super.onDeath(this.deathCause != null ? this.deathCause : DamageSource.GENERIC);
            }
            EntityUtil.spawnParticleAtEntity(this, this.isBurning() ? EnumParticleTypes.FLAME : EnumParticleTypes.EXPLOSION_NORMAL, 30);
            this.setDead();
        }
    }

    public void onKillCommand() {
        super.onKillCommand();
        this.setLives(0);
    }

    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if ((this.world.getDifficulty() == EnumDifficulty.NORMAL && this.rand.nextBoolean() || this.world.getDifficulty() == EnumDifficulty.HARD) && entityLivingIn instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager) entityLivingIn;
            EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.world);
            entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
            this.world.removeEntity(entityvillager);
            entityzombievillager.setProfession(entityvillager.getProfession());
            entityzombievillager.setChild(entityvillager.isChild());
            entityzombievillager.setNoAI(entityvillager.isAIDisabled());
            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomNameTag(entityvillager.getCustomNameTag());
                entityzombievillager.setAlwaysRenderNameTag(entityvillager.getAlwaysRenderNameTag());
            }
            this.world.spawnEntity(entityzombievillager);
            if (!entityLivingIn.isSilent()) {
                this.world.playEvent(null, 1026, entityLivingIn.getPosition(), 0);
            }
        }
    }

    public boolean isOnSameTeam(Entity entityIn) {
        if (entityIn == this || super.isOnSameTeam(entityIn)) {
            return true;
        }
        if (ZombieResurrection.canBeResurrected(entityIn.getClass()) || entityIn.getClass() == this.getClass()) {
            return this.getAttackTarget() != entityIn && ((EntityMob) entityIn).getAttackTarget() != this && this.getTeam() == null && entityIn.getTeam() == null;
        }
        return false;
    }

    protected void handleJumpWater() {
        this.motionY += 0.04f;
    }

    protected void handleJumpLava() {
        this.handleJumpWater();
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Lives", this.getLives());
        compound.setShort("DeathTime", (short) this.deathTime);
        compound.setShort("VanishTime", (short) this.vanishTime);
        if (!this.resurrections.isEmpty()) {
            NBTTagList nbtTagList = new NBTTagList();
            for (ZombieResurrection resurrection : this.resurrections) {
                NBTTagCompound compound1 = NBTUtil.createPosTag(resurrection);
                compound1.setInteger("Tick", resurrection.getTick());
                nbtTagList.appendTag(compound1);
            }
            compound.setTag("Resurrections", nbtTagList);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.deathTime = ((EntityMob) this).deathTime;

        if (compound.hasKey("Lives")) {
            this.setLives(compound.getInteger("Lives"));
        }

        this.vanishTime = compound.getShort("VanishTime");
        NBTTagList nbtTagList = compound.getTagList("Resurrections", 10);

        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound compound1 = nbtTagList.getCompoundTagAt(i);
            this.resurrections.add(i, new ZombieResurrection(this.world, NBTUtil.getPosFromTag(compound1), compound1.getInteger("Tick")));
        }

        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public boolean isNonBoss() {
        return MBConfig.ENTITIES.mutantZombieBossClassification ? false : true;
    }

    protected SoundEvent getAmbientSound() {
        return MBSoundEvents.ENTITY_MUTANT_ZOMBIE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return MBSoundEvents.ENTITY_MUTANT_ZOMBIE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return MBSoundEvents.ENTITY_MUTANT_ZOMBIE_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        if (this.deathTime == 0) {
            this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15f, 1.0f);
        }
    }

    protected ResourceLocation getLootTable() {
        return EntityUtil.getLootTable(this);
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.attackID);
        buffer.writeInt(this.attackTick);
        buffer.writeInt(this.deathTime);
        buffer.writeInt(this.vanishTime);
        buffer.writeInt(this.throwHitTick);
        buffer.writeInt(this.throwFinishTick);
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.attackID = additionalData.readInt();
        this.attackTick = additionalData.readInt();
        this.deathTime = additionalData.readInt();
        this.vanishTime = additionalData.readInt();
        this.throwHitTick = additionalData.readInt();
        this.throwFinishTick = additionalData.readInt();
    }

    class ThrowAttackGoal
            extends EntityAIBase {
        private EntityLivingBase attackTarget;
        private int hit = -1;
        private int finish = -1;

        public ThrowAttackGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            this.attackTarget = MutantZombieEntity.this.getAttackTarget();
            return this.attackTarget != null && MutantZombieEntity.this.attackID == 2;
        }

        public void startExecuting() {
            MutantZombieEntity.this.setAttackID(2);
            this.attackTarget.dismountRidingEntity();
            double x = this.attackTarget.posX - MutantZombieEntity.this.posX;
            double z = this.attackTarget.posZ - MutantZombieEntity.this.posZ;
            double d = Math.max(Math.sqrt(x * x + z * z), 0.001);
            this.attackTarget.motionX = x / d * (double) 0.8f;
            this.attackTarget.motionY = 1.6f;
            this.attackTarget.motionZ = z / d * (double) 0.8f;
            EntityUtil.sendPlayerVelocityPacket(this.attackTarget);
        }

        public boolean shouldContinueExecuting() {
            return MutantZombieEntity.this.attackID == 2 && this.finish < 10;
        }

        public void updateTask() {
            MutantZombieEntity.this.getNavigator().clearPath();
            MutantZombieEntity.this.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0f, 30.0f);
            if (MutantZombieEntity.this.attackTick == 15) {
                MutantZombieEntity.this.isInWeb = false;
                double x = this.attackTarget.posX - MutantZombieEntity.this.posX;
                double y = this.attackTarget.posY - MutantZombieEntity.this.posY;
                double z = this.attackTarget.posZ - MutantZombieEntity.this.posZ;
                double d0 = Math.max(Math.sqrt(x * x + y * y + z * z), 0.001);
                MutantZombieEntity.this.motionX = x / d0 * (double) 3.4f;
                MutantZombieEntity.this.motionY = y / d0 * (double) 1.4f;
                MutantZombieEntity.this.motionZ = z / d0 * (double) 3.4f;
            } else if (MutantZombieEntity.this.attackTick > 15) {
                double d1 = MutantZombieEntity.this.width * 2.0f * MutantZombieEntity.this.width * 2.0f;
                double distSq = MutantZombieEntity.this.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
                if (distSq < d1 && this.hit == -1) {
                    this.hit = 0;
                    MutantZombieEntity.this.setThrowAttackHit(true);
                    if (!this.attackTarget.attackEntityFrom(DamageSource.causeMobDamage(MutantZombieEntity.this), (float) MutantZombieEntity.this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
                        EntityUtil.disableShield(this.attackTarget, 150);
                    }
                    double x = this.attackTarget.posX - MutantZombieEntity.this.posX;
                    double z = this.attackTarget.posZ - MutantZombieEntity.this.posZ;
                    double d0 = Math.max(Math.sqrt(x * x + z * z), 0.001);
                    this.attackTarget.motionX = x / d0 * (double) 0.6f;
                    this.attackTarget.motionY = -1.2f;
                    this.attackTarget.motionZ = z / d0 * (double) 0.6f;
                    EntityUtil.sendPlayerVelocityPacket(this.attackTarget);
                    this.attackTarget.hurtResistantTime = 10;
                    MutantZombieEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ZOMBIE_GRUNT, 0.3f, 0.8f + MutantZombieEntity.this.rand.nextFloat() * 0.4f);
                }
                if (this.hit >= 0) {
                    ++this.hit;
                }
                if ((MutantZombieEntity.this.onGround || MutantZombieEntity.this.isInWater() || MutantZombieEntity.this.isInLava()) && this.finish == -1) {
                    this.finish = 0;
                    MutantZombieEntity.this.setThrowAttackFinished(true);
                }
                if (this.finish >= 0) {
                    ++this.finish;
                }
            }
        }

        public void resetTask() {
            MutantZombieEntity.this.setAttackID(0);
            this.attackTarget = null;
            this.hit = -1;
            this.finish = -1;
            MutantZombieEntity.this.setThrowAttackHit(false);
            MutantZombieEntity.this.setThrowAttackFinished(false);
        }
    }

    class RoarGoal
            extends EntityAIBase {
        public RoarGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            return MutantZombieEntity.this.getAttackTarget() != null && MutantZombieEntity.this.onGround && MutantZombieEntity.this.getDistanceSq(MutantZombieEntity.this.getAttackTarget()) > 16.0 && MutantZombieEntity.this.rand.nextFloat() * 100.0f < 0.35f;
        }

        public void startExecuting() {
            MutantZombieEntity.this.setAttackID(3);
            MutantZombieEntity.this.idleTime = 0;
            MutantZombieEntity.this.livingSoundTime = -MutantZombieEntity.this.getTalkInterval();
        }

        public boolean shouldContinueExecuting() {
            return MutantZombieEntity.this.attackTick < 120;
        }

        public void updateTask() {
            MutantZombieEntity.this.getNavigator().clearPath();
            if (MutantZombieEntity.this.attackTick < 75 && MutantZombieEntity.this.getAttackTarget() != null) {
                MutantZombieEntity.this.getLookHelper().setLookPositionWithEntity(MutantZombieEntity.this.getAttackTarget(), 30.0f, 30.0f);
            }
            if (MutantZombieEntity.this.attackTick == 10) {
                MutantZombieEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ZOMBIE_ROAR, 3.0f, 0.7f + MutantZombieEntity.this.rand.nextFloat() * 0.2f);
                for (Entity entity : MutantZombieEntity.this.world.getEntitiesWithinAABBExcludingEntity(MutantZombieEntity.this, MutantZombieEntity.this.getEntityBoundingBox().grow(12.0, 8.0, 12.0))) {
                    if (!entity.canBeCollidedWith() || MutantZombieEntity.this.isOnSameTeam(entity) || !(MutantZombieEntity.this.getDistanceSq(entity) <= 196.0))
                        continue;
                    double x = entity.posX - MutantZombieEntity.this.posX;
                    double z = entity.posZ - MutantZombieEntity.this.posZ;
                    double d = Math.sqrt(x * x + z * z);
                    entity.motionX = x / d * (double) 0.7f;
                    entity.motionY = 0.3f;
                    entity.motionZ = z / d * (double) 0.7f;
                    entity.attackEntityFrom(DamageSource.causeMobDamage(MutantZombieEntity.this).setDamageBypassesArmor().setDamageIsAbsolute(), (float) MBConfig.ENTITIES.mutantZombieRoarDamage);
                    EntityUtil.sendPlayerVelocityPacket(entity);
                }
            }
            if (MutantZombieEntity.this.attackTick >= 20 && MutantZombieEntity.this.attackTick < 80 && MutantZombieEntity.this.attackTick % 10 == 0) {
                int x = MathHelper.floor(MutantZombieEntity.this.posX);
                int y = MathHelper.floor(MutantZombieEntity.this.getEntityBoundingBox().minY);
                int z = MathHelper.floor(MutantZombieEntity.this.posZ);
                y = ZombieResurrection.getSuitableGround(MutantZombieEntity.this.world, x += (1 + MutantZombieEntity.this.rand.nextInt(8)) * (MutantZombieEntity.this.rand.nextBoolean() ? 1 : -1), y - 1, z += (1 + MutantZombieEntity.this.rand.nextInt(8)) * (MutantZombieEntity.this.rand.nextBoolean() ? 1 : -1));
                if (y != -1) {
                    MutantZombieEntity.this.resurrections.add(new ZombieResurrection(MutantZombieEntity.this.world, x, y, z));
                }
            }
        }

        public void resetTask() {
            MutantZombieEntity.this.setAttackID(0);
        }
    }

    class MeleeGoal
            extends EntityAIBase {
        private EntityLivingBase attackTarget;
        private double dirX = -1.0;
        private double dirZ = -1.0;

        public MeleeGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            this.attackTarget = MutantZombieEntity.this.getAttackTarget();
            return this.attackTarget != null && MutantZombieEntity.this.attackID == 1;
        }

        public void startExecuting() {
            MutantZombieEntity.this.setAttackID(1);
            MutantZombieEntity.this.livingSoundTime = -MutantZombieEntity.this.getTalkInterval();
            MutantZombieEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_ZOMBIE_ATTACK, 0.3f, 0.8f + MutantZombieEntity.this.rand.nextFloat() * 0.4f);
        }

        public boolean shouldContinueExecuting() {
            return MutantZombieEntity.this.attackTick < 25;
        }

        public void updateTask() {
            MutantZombieEntity.this.getNavigator().clearPath();
            if (MutantZombieEntity.this.attackTick < 8) {
                MutantZombieEntity.this.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0f, 30.0f);
            }
            if (MutantZombieEntity.this.attackTick == 8) {
                double x = this.attackTarget.posX - MutantZombieEntity.this.posX;
                double z = this.attackTarget.posZ - MutantZombieEntity.this.posZ;
                double d = Math.max(Math.sqrt(x * x + z * z), 0.001);
                this.dirX = x / d;
                this.dirZ = z / d;
            }
            if (MutantZombieEntity.this.attackTick == 12) {
                int x = MathHelper.floor(MutantZombieEntity.this.posX + this.dirX * 2.0);
                int y = MathHelper.floor(MutantZombieEntity.this.getEntityBoundingBox().minY);
                int z = MathHelper.floor(MutantZombieEntity.this.posZ + this.dirZ * 2.0);
                int x1 = MathHelper.floor(MutantZombieEntity.this.posX + this.dirX * 8.0);
                int z1 = MathHelper.floor(MutantZombieEntity.this.posZ + this.dirZ * 8.0);
                SeismicWave.createWaves(MutantZombieEntity.this.world, MutantZombieEntity.this.seismicWavesList, x, z, x1, z1, y);
                MutantZombieEntity.this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5f, 0.8f + MutantZombieEntity.this.rand.nextFloat() * 0.4f);
            }
        }

        public void resetTask() {
            MutantZombieEntity.this.setAttackID(0);
            this.attackTarget = null;
            this.dirX = -1.0;
            this.dirZ = -1.0;
        }
    }
}
