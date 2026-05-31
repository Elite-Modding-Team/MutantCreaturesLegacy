package chumbanotz.mutantbeasts.entity.mutant;

import chumbanotz.mutantbeasts.MBConfig;
import chumbanotz.mutantbeasts.entity.CreeperMinionEggEntity;
import chumbanotz.mutantbeasts.entity.CreeperMinionEntity;
import chumbanotz.mutantbeasts.entity.ai.EntityAIAvoidDamage;
import chumbanotz.mutantbeasts.entity.ai.EntityAIHurtByNearestTarget;
import chumbanotz.mutantbeasts.entity.ai.MBEntityAIAttackMelee;
import chumbanotz.mutantbeasts.pathfinding.MBGroundPathNavigator;
import chumbanotz.mutantbeasts.util.EntityUtil;
import chumbanotz.mutantbeasts.util.MBSoundEvents;
import chumbanotz.mutantbeasts.util.MutatedExplosion;
import io.netty.buffer.ByteBuf;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class MutantCreeperEntity extends EntityCreeper implements IEntityAdditionalSpawnData {
    protected final BossInfoServer bossInfo;
    private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(MutantCreeperEntity.class, (DataSerializer) DataSerializers.BYTE);
    public static final int MAX_CHARGE_TIME = 100;
    public static final int MAX_DEATH_TIME = 100;
    private int chargeTime;
    private int chargeHits;
    private int lastFlashTick;
    private int flashTick;
    private boolean summonLightning;
    private DamageSource deathCause;
    public int deathTime;

    public MutantCreeperEntity(World worldIn) {
        super(worldIn);
        this.chargeHits = 3 + this.rand.nextInt(3);
        this.stepHeight = 1.0f;
        this.experienceValue = 30;
        this.setSize(1.98f, 2.8f);
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new JumpAttackGoal());
        this.tasks.addTask(1, new SpawnMinionsGoal());
        this.tasks.addTask(1, new ChargeAttackGoal());
        this.tasks.addTask(2, new MBEntityAIAttackMelee(this, 1.3));
        this.tasks.addTask(3, new EntityAIAvoidDamage(this, 1.0));
        this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByNearestTarget(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true).setUnseenMemoryTicks(100));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(MBConfig.ENTITIES.mutantCreeperArmor);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(MBConfig.ENTITIES.mutantCreeperAttackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(MBConfig.ENTITIES.mutantCreeperFollowRange);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(MBConfig.ENTITIES.mutantCreeperKnockbackResistance);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MBConfig.ENTITIES.mutantCreeperMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MBConfig.ENTITIES.mutantCreeperMovementSpeed);
        this.getEntityAttribute(SWIM_SPEED).setBaseValue(MBConfig.ENTITIES.mutantCreeperSwimSpeed);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(STATUS, (byte) 0);
    }

    public boolean getPowered() {
        return (this.dataManager.get(STATUS) & 1) != 0;
    }

    private void setPowered(boolean powered) {
        this.isImmuneToFire = powered;
        this.experienceValue = powered ? 40 : 30;
        byte b0 = this.dataManager.get(STATUS);
        this.dataManager.set(STATUS, powered ? (byte) (b0 | 1) : (byte) (b0 & 0xFFFFFFFE));
    }

    public boolean isJumpAttacking() {
        return (this.dataManager.get(STATUS) & 2) != 0;
    }

    private void setJumpAttacking(boolean jumping) {
        byte b0 = this.dataManager.get(STATUS);
        this.dataManager.set(STATUS, jumping ? (byte) (b0 | 2) : (byte) (b0 & 0xFFFFFFFD));
    }

    public boolean isCharging() {
        return (this.dataManager.get(STATUS) & 4) != 0;
    }

    private void setCharging(boolean flag) {
        byte b0 = this.dataManager.get(STATUS);
        this.dataManager.set(STATUS, flag ? (byte) (b0 | 4) : (byte) (b0 & 0xFFFFFFFB));
    }

    @Override
    public float getEyeHeight() {
        return 2.6f;
    }

    @Override
    protected PathNavigate createNavigator(World worldIn) {
        return new MBGroundPathNavigator(this, worldIn);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        super.updateFallState(y, onGroundIn, state, pos);
        if (!this.world.isRemote && this.isJumpAttacking() && (onGroundIn || state.getMaterial().isLiquid() || state.getMaterial() == Material.WEB)) {
            MutatedExplosion.create(this, this.getPowered() ? (float) MBConfig.ENTITIES.mutantCreeperJumpingStrengthCharged : (float) MBConfig.ENTITIES.mutantCreeperJumpingStrength, false, MBConfig.ENTITIES.mutantCreeperJumpingDestroysTerrain);
            this.setJumpAttacking(false);
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        // Regenerate health when target is lost except when player is in Creative
        if (this.getAttackTarget() == null && this.getHealth() < this.getMaxHealth() && MBConfig.ENTITIES.mutantCreeperNoCombatRegen) {
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
        if (MBConfig.ENTITIES.mutantCreeperBossBar) this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        if (MBConfig.ENTITIES.mutantCreeperBossBar) this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        double x = entityIn.posX - this.posX;
        double y = entityIn.posY - this.posY;
        double z = entityIn.posZ - this.posZ;
        double d = Math.sqrt(x * x + y * y + z * z);
        entityIn.motionX = x / d * 0.5;
        entityIn.motionY = y / d * (double) 0.05f + (double) 0.15f;
        entityIn.motionZ = z / d * 0.5;
        entityIn.velocityChanged = true;
        if (flag) {
            this.applyEnchantments(this, entityIn);
        }
        this.swingArm(EnumHand.MAIN_HAND);
        return flag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean flag;
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (source.isExplosion()) {
            float healAmount = amount / 2.0f;
            if (this.isEntityAlive() && this.getHealth() < this.getMaxHealth() && !(source.getTrueSource() instanceof MutantCreeperEntity)) {
                this.heal(healAmount);
                EntityUtil.sendParticlePacket(this, EnumParticleTypes.HEART, (int) (healAmount / 2.0f));
            }
            return false;
        }
        boolean bl = flag = !(source.getTrueSource() instanceof EntityCreeper) && super.attackEntityFrom(source, amount);
        if (this.isCharging() && flag && amount > 0.0f) {
            --this.chargeHits;
        }
        return flag;
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        this.setPowered(true);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        return false;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 6) {
            EntityUtil.spawnParticleAtEntity(this, EnumParticleTypes.HEART, 15);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.lastFlashTick = this.flashTick;
        if (this.isJumpAttacking()) {
            if (this.flashTick == 0) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 2.0f, this.getSoundPitch() * 0.5f);
            }
            ++this.flashTick;
        } else if (this.flashTick > 0) {
            this.flashTick = 0;
        }
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return super.canBeRidden(entityIn) && entityIn instanceof EntityLivingBase;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public float getCreeperFlashIntensity(float partialTick) {
        if (this.deathTime > 0) {
            return (float) this.deathTime / 100.0f * 255.0f;
        }
        if (this.isCharging()) {
            return (this.ticksExisted % 20 < 10 ? 0.6f : 0.0f) * 255.0f;
        }
        return ((float) this.lastFlashTick + (float) (this.flashTick - this.lastFlashTick) * partialTick) / 28.0f;
    }

    // TODO: Be sure it can drop loot
    @Override
    public void onDeath(DamageSource cause) {
        if (!this.world.isRemote) {
            this.deathCause = cause;
            this.setCharging(false);
            this.playSound(MBSoundEvents.ENTITY_MUTANT_CREEPER_DEATH, 2.0f, 1.0f);
            this.world.setEntityState(this, (byte) 3);
            if (this.recentlyHit > 0) {
                this.recentlyHit += 100;
            }
        }
    }

    @Override
    protected void onDeathUpdate() {
        ++this.deathTime;
        float explosionPower = this.getPowered() ? (float) MBConfig.ENTITIES.mutantCreeperDeathStrengthCharged : (float) MBConfig.ENTITIES.mutantCreeperDeathStrength;
        float radius = explosionPower * 1.5f;
        for (Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(radius), EntitySelectors.CAN_AI_TARGET)) {
            double x = this.posX - entity.posX;
            double y = this.posY - entity.posY;
            double z = this.posZ - entity.posZ;
            double d = Math.sqrt(x * x + y * y + z * z);
            float f2 = (float) this.deathTime / 100.0f;
            entity.motionX += x / d * (double) f2 * 0.09;
            entity.motionY += y / d * (double) f2 * 0.09;
            entity.motionZ += z / d * (double) f2 * 0.09;
        }
        this.posX += (double) (this.rand.nextFloat() * 0.2f) - (double) 0.1f;
        this.posZ += (double) (this.rand.nextFloat() * 0.2f) - (double) 0.1f;
        if (this.deathTime >= 100) {
            if (!this.world.isRemote) {
                MutatedExplosion.create(this, explosionPower, this.isBurning(), MBConfig.ENTITIES.mutantCreeperDeathDestroysTerrain);
                EntityUtil.spawnLingeringCloud(this);
                EntityUtil.dropExperience(this, this.recentlyHit, this::getExperiencePoints, this.attackingPlayer);
                super.onDeath(this.deathCause != null ? this.deathCause : DamageSource.GENERIC);
                if (this.world.getGameRules().getBoolean("doMobLoot") && this.attackingPlayer != null && MBConfig.ENTITIES.mutantCreeperSpawnsEgg) {
                    this.world.spawnEntity(new CreeperMinionEggEntity(this, this.attackingPlayer));
                }
            }
            this.setDead();
        }
    }

    @Override
    public boolean ableToCauseSkullDrop() {
        return this.world.getGameRules().getBoolean("doMobLoot");
    }

    @Deprecated
    @Override
    public boolean hasIgnited() {
        return false;
    }

    @Deprecated
    @Override
    public int getCreeperState() {
        return -1;
    }

    @Override
    protected void handleJumpWater() {
        this.motionY += 0.04f;
    }

    @Override
    protected void handleJumpLava() {
        this.handleJumpWater();
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
        float f = super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn);
        return this.getPowered() && blockStateIn.getBlockHardness(worldIn, pos) > -1.0f && ForgeEventFactory.onEntityDestroyBlock(this, pos, blockStateIn) ? Math.min(0.8f, f) : f;
    }

    @Override
    public void playLivingSound() {
        if (this.getAttackTarget() == null) {
            super.playLivingSound();
        }
    }

    @Override
    public boolean isNonBoss() {
        return MBConfig.ENTITIES.mutantCreeperBossClassification ? false : true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MBSoundEvents.ENTITY_MUTANT_CREEPER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return MBSoundEvents.ENTITY_MUTANT_CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MBSoundEvents.ENTITY_MUTANT_CREEPER_HURT;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("JumpAttacking", this.isJumpAttacking());
        compound.setBoolean("Charging", this.isCharging());
        compound.setInteger("ChargeTime", this.chargeTime);
        compound.setInteger("ChargeHits", this.chargeHits);
        compound.setBoolean("SummonLightning", this.summonLightning);
        compound.setShort("DeathTime", (short) this.deathTime);
        if (this.getPowered()) {
            compound.setBoolean("Powered", true);
        }
        for (String unusedNBT : new String[]{"powered", "Fuse", "ExplosionRadius", "ignited"}) {
            compound.removeTag(unusedNBT);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setPowered(compound.getBoolean("Powered"));
        this.setJumpAttacking(compound.getBoolean("JumpAttacking"));
        this.setCharging(compound.getBoolean("Charging"));
        this.chargeTime = compound.getInteger("ChargeTime");
        this.chargeHits = compound.getInteger("ChargeHits");
        this.summonLightning = compound.getBoolean("SummonLightning");
        this.deathTime = ((EntityCreeper) this).deathTime;

        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.flashTick);
        buffer.writeInt(this.deathTime);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.flashTick = additionalData.readInt();
        this.deathTime = additionalData.readInt();
    }

    @Override
    protected ResourceLocation getLootTable() {
        return EntityUtil.getLootTable(this);
    }

    class JumpAttackGoal
            extends EntityAIBase {
        JumpAttackGoal() {
        }

        public boolean shouldExecute() {
            return MutantCreeperEntity.this.onGround && MutantCreeperEntity.this.getAttackTarget() != null && !MutantCreeperEntity.this.isCharging() && (double) MutantCreeperEntity.this.getDistance(MutantCreeperEntity.this.getAttackTarget()) <= 1024.0 && MutantCreeperEntity.this.rand.nextFloat() * 100.0f < 0.9f;
        }

        public boolean shouldContinueExecuting() {
            return false;
        }

        public void startExecuting() {
            MutantCreeperEntity.this.isInWeb = false;
            MutantCreeperEntity.this.setJumpAttacking(true);
            MutantCreeperEntity.this.motionX = (MutantCreeperEntity.this.getAttackTarget().posX - MutantCreeperEntity.this.posX) * 0.2;
            MutantCreeperEntity.this.motionY = 1.4;
            MutantCreeperEntity.this.motionZ = (MutantCreeperEntity.this.getAttackTarget().posZ - MutantCreeperEntity.this.posZ) * 0.2;
        }
    }

    public boolean getCanSpawnHere() {
        return MBConfig.ENTITIES.mutantCreeperUndergroundSpawning ? super.getCanSpawnHere() && EntityUtil.getRandomSpawnChance(this.rand) :
                super.getCanSpawnHere() && this.world.canSeeSky(this.getPosition()) && EntityUtil.getRandomSpawnChance(this.rand);
    }

    class ChargeAttackGoal
            extends EntityAIBase {
        public ChargeAttackGoal() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            EntityLivingBase target = MutantCreeperEntity.this.getAttackTarget();
            return target != null && MutantCreeperEntity.this.onGround && !(MutantCreeperEntity.this.getMaxHealth() - MutantCreeperEntity.this.getHealth() < MutantCreeperEntity.this.getMaxHealth() / 6.0f) && MutantCreeperEntity.this.getDistanceSq(target) >= 25.0 && MutantCreeperEntity.this.getDistanceSq(target) <= 1024.0 && MutantCreeperEntity.this.rand.nextFloat() * 100.0f < 0.7f || MutantCreeperEntity.this.isCharging();
        }

        public boolean shouldContinueExecuting() {
            if (MutantCreeperEntity.this.summonLightning && MutantCreeperEntity.this.getAttackTarget() != null && MutantCreeperEntity.this.getDistanceSq(MutantCreeperEntity.this.getAttackTarget()) < 25.0 && MutantCreeperEntity.this.world.canSeeSky(MutantCreeperEntity.this.getPosition())) {
                MutantCreeperEntity.this.world.addWeatherEffect(new EntityLightningBolt(MutantCreeperEntity.this.world, MutantCreeperEntity.this.posX, MutantCreeperEntity.this.posY, MutantCreeperEntity.this.posZ, false));
                return false;
            }
            return MutantCreeperEntity.this.chargeTime < 100 && MutantCreeperEntity.this.chargeHits > 0;
        }

        public void startExecuting() {
            MutantCreeperEntity.this.setCharging(true);
            if (MutantCreeperEntity.this.rand.nextInt(MutantCreeperEntity.this.world.isThundering() ? 2 : 6) == 0 && !MutantCreeperEntity.this.getPowered()) {
                MutantCreeperEntity.this.summonLightning = true;
            }
        }

        public void updateTask() {
            MutantCreeperEntity.this.getNavigator().clearPath();
            int i = MutantCreeperEntity.this.chargeTime % 20;
            if (i == 0 || i == 20) {
                MutantCreeperEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_CREEPER_CHARGE, 0.6f, 0.7f + MutantCreeperEntity.this.rand.nextFloat() * 0.6f);
            }
            ++MutantCreeperEntity.this.chargeTime;
        }

        public void resetTask() {
            if (MutantCreeperEntity.this.chargeTime >= 100) {
                MutantCreeperEntity.this.heal(MutantCreeperEntity.this.getMaxHealth() / 4.0f);
                MutantCreeperEntity.this.world.setEntityState(MutantCreeperEntity.this, (byte) 6);
            }
            MutantCreeperEntity.this.chargeTime = 0;
            MutantCreeperEntity.this.chargeHits = 4 + MutantCreeperEntity.this.rand.nextInt(3);
            MutantCreeperEntity.this.setCharging(false);
            MutantCreeperEntity.this.summonLightning = false;
        }
    }

    class SpawnMinionsGoal
            extends EntityAIBase {
        SpawnMinionsGoal() {
        }

        public boolean shouldExecute() {
            return MutantCreeperEntity.this.getAttackTarget() != null && MutantCreeperEntity.this.getDistanceSq(MutantCreeperEntity.this.getAttackTarget()) <= 1024.0 && !MutantCreeperEntity.this.isCharging() && !MutantCreeperEntity.this.isJumpAttacking() && MutantCreeperEntity.this.rand.nextFloat() * 100.0f < 0.6f;
        }

        public void startExecuting() {
            for (int i = (int) Math.ceil(MutantCreeperEntity.this.getHealth() / MutantCreeperEntity.this.getMaxHealth() * 4.0f); i > 0; --i) {
                CreeperMinionEntity creeper = new CreeperMinionEntity(MutantCreeperEntity.this.world);
                double x = MutantCreeperEntity.this.posX + (double) (MutantCreeperEntity.this.rand.nextFloat() - MutantCreeperEntity.this.rand.nextFloat());
                double y = MutantCreeperEntity.this.posY + (double) (MutantCreeperEntity.this.rand.nextFloat() * 0.5f);
                double z = MutantCreeperEntity.this.posZ + (double) (MutantCreeperEntity.this.rand.nextFloat() - MutantCreeperEntity.this.rand.nextFloat());
                double xx = MutantCreeperEntity.this.getAttackTarget().posX - MutantCreeperEntity.this.posX;
                double yy = MutantCreeperEntity.this.getAttackTarget().posY - MutantCreeperEntity.this.posY;
                double zz = MutantCreeperEntity.this.getAttackTarget().posZ - MutantCreeperEntity.this.posZ;
                creeper.motionX = xx * (double) 0.15f + (double) (MutantCreeperEntity.this.rand.nextFloat() * 0.05f);
                creeper.motionY = yy * (double) 0.15f + (double) (MutantCreeperEntity.this.rand.nextFloat() * 0.05f);
                creeper.motionZ = zz * (double) 0.15f + (double) (MutantCreeperEntity.this.rand.nextFloat() * 0.05f);
                creeper.setLocationAndAngles(x, y, z, MutantCreeperEntity.this.rotationYaw, 0.0f);
                creeper.setOwnerId(MutantCreeperEntity.this.entityUniqueID);
                if (MutantCreeperEntity.this.getPowered()) {
                    creeper.setPowered(true);
                }
                MutantCreeperEntity.this.world.spawnEntity(creeper);
            }
        }

        public boolean shouldContinueExecuting() {
            return false;
        }
    }
}
