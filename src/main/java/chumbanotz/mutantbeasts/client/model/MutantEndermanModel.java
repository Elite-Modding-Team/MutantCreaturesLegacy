package chumbanotz.mutantbeasts.client.model;

import chumbanotz.mutantbeasts.entity.mutant.MutantEndermanEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class MutantEndermanModel extends ModelBase {
    private final ModelRenderer pelvis;

    private final ModelRenderer abdomen;

    private final ModelRenderer chest;

    private final ModelRenderer neck;

    private final ModelRenderer head;

    private final ModelRenderer mouth;

    private final Arm rightArm;

    private final Arm leftArm;

    private final Arm lowerRightArm;

    private final Arm lowerLeftArm;

    private final ModelRenderer legjoint1;

    private final ModelRenderer legjoint2;

    private final ModelRenderer leg1;

    private final ModelRenderer leg2;

    private final ModelRenderer foreleg1;

    private final ModelRenderer foreleg2;

    private float partialTick;

    public MutantEndermanModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.pelvis = new ModelRenderer(this);
        this.pelvis.setRotationPoint(0.0F, -15.5F, 8.0F);
        this.abdomen = new ModelRenderer(this, 32, 0);
        this.abdomen.addBox(-4.0F, -10.0F, -2.0F, 8, 10, 4);
        this.pelvis.addChild(this.abdomen);
        this.chest = new ModelRenderer(this, 50, 8);
        this.chest.addBox(-5.0F, -16.0F, -3.0F, 10, 16, 6);
        this.chest.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.abdomen.addChild(this.chest);
        this.neck = new ModelRenderer(this, 32, 14);
        this.neck.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 3);
        this.neck.setRotationPoint(0.0F, -15.0F, 0.0F);
        this.chest.addChild(this.neck);
        this.head = new ModelRenderer(this);
        this.head.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8, 6, 8, 0.5F);
        this.head.setTextureOffset(0, 14).addBox(-4.0F, 3.0F, -8.0F, 8, 2, 8, 0.5F);
        this.head.setRotationPoint(0.0F, -5.0F, 3.0F);
        this.neck.addChild(this.head);
        this.mouth = new ModelRenderer(this, 0, 24);
        this.mouth.addBox(-4.0F, 3.0F, -8.0F, 8, 2, 8);
        this.head.addChild(this.mouth);
        this.rightArm = new Arm(this, this.chest, true);
        this.leftArm = new Arm(this, this.chest, false);
        this.lowerRightArm = new Arm(this, this.chest, true);
        this.lowerRightArm.arm.rotationPointY += 6.0F;
        this.lowerLeftArm = new Arm(this, this.chest, false);
        this.lowerLeftArm.arm.rotationPointY += 6.0F;
        this.legjoint1 = new ModelRenderer(this);
        this.legjoint1.setRotationPoint(-1.5F, 0.0F, 0.75F);
        this.abdomen.addChild(this.legjoint1);
        this.legjoint2 = new ModelRenderer(this);
        this.legjoint2.setRotationPoint(1.5F, 0.0F, 0.75F);
        this.abdomen.addChild(this.legjoint2);
        this.leg1 = new ModelRenderer(this, 0, 34);
        this.leg1.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3, 0.5F);
        this.leg1.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.legjoint1.addChild(this.leg1);
        this.leg2 = new ModelRenderer(this, 0, 34);
        this.leg2.mirror = true;
        this.leg2.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3, 0.5F);
        this.leg2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.legjoint2.addChild(this.leg2);
        this.foreleg1 = new ModelRenderer(this, 12, 34);
        this.foreleg1.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3, 0.5F);
        this.foreleg1.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.leg1.addChild(this.foreleg1);
        this.foreleg2 = new ModelRenderer(this, 12, 34);
        this.foreleg2.mirror = true;
        this.foreleg2.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3, 0.5F);
        this.foreleg2.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.leg2.addChild(this.foreleg2);
    }

    public static void resetAngles(ModelRenderer model) {
        model.rotateAngleX = 0.0F;
        model.rotateAngleY = 0.0F;
        model.rotateAngleZ = 0.0F;
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setAngles();
        MutantEndermanEntity mutantEnderman = (MutantEndermanEntity) entity;
        animate(mutantEnderman, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.lowerRightArm.arm.setScale(mutantEnderman.getArmScale(this.partialTick));
        this.lowerLeftArm.arm.setScale(mutantEnderman.getArmScale(this.partialTick));
        this.pelvis.render(scale);
    }

    private void setAngles() {
        this.pelvis.rotationPointY = -15.5F;
        this.abdomen.rotateAngleX = 0.31415927F;
        this.chest.rotateAngleX = 0.3926991F;
        this.chest.rotateAngleY = 0.0F;
        this.chest.rotateAngleZ = 0.0F;
        this.neck.rotateAngleX = 0.19634955F;
        this.neck.rotateAngleZ = 0.0F;
        this.head.rotateAngleX = -0.7853982F;
        this.head.rotateAngleY = 0.0F;
        this.head.rotateAngleZ = 0.0F;
        this.mouth.rotateAngleX = 0.0F;
        this.rightArm.setAngles();
        this.leftArm.setAngles();
        this.lowerRightArm.setAngles();
        this.lowerRightArm.arm.rotateAngleX += 0.1F;
        this.lowerRightArm.arm.rotateAngleZ -= 0.2F;
        this.lowerLeftArm.setAngles();
        this.lowerLeftArm.arm.rotateAngleX += 0.1F;
        this.lowerLeftArm.arm.rotateAngleZ += 0.2F;
        this.legjoint1.rotateAngleX = 0.0F;
        this.legjoint2.rotateAngleX = 0.0F;
        this.leg1.rotateAngleX = -0.8975979F;
        this.leg1.rotateAngleY = 0.0F;
        this.leg1.rotateAngleZ = 0.2617994F;
        this.leg2.rotateAngleX = -0.8975979F;
        this.leg2.rotateAngleY = 0.0F;
        this.leg2.rotateAngleZ = -0.2617994F;
        this.foreleg1.rotateAngleX = 0.7853982F;
        this.foreleg1.rotateAngleZ = -0.1308997F;
        this.foreleg2.rotateAngleX = 0.7853982F;
        this.foreleg2.rotateAngleZ = 0.1308997F;
    }

    private void animate(MutantEndermanEntity enderman, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
        float walkSpeed = 0.3F;
        float walkAnim1 = (MathHelper.sin((limbSwing - 0.8F) * walkSpeed) + 0.8F) * limbSwingAmount;
        float walkAnim2 = -(MathHelper.sin((limbSwing + 0.8F) * walkSpeed) - 0.8F) * limbSwingAmount;
        float walkAnim3 = (MathHelper.sin((limbSwing + 0.8F) * walkSpeed) - 0.8F) * limbSwingAmount;
        float walkAnim4 = -(MathHelper.sin((limbSwing - 0.8F) * walkSpeed) + 0.8F) * limbSwingAmount;
        float[] walkAnim = new float[5];
        Arrays.fill(walkAnim, MathHelper.sin(limbSwing * walkSpeed) * limbSwingAmount);
        float breatheAnim = MathHelper.sin(ageInTicks * 0.15F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        int arm;
        for (arm = 1; arm < enderman.heldBlock.length; arm++) {
            if (enderman.heldBlock[arm] != 0) {
                animateHoldBlock(enderman.heldBlockTick[arm], arm, (enderman.hasTarget > 0));
                walkAnim[arm] = walkAnim[arm] * 0.4F;
            }
        }
        if (enderman.getAttackID() == 1) {
            arm = enderman.getActiveArm();
            animateMelee(enderman.getAttackTick(), arm);
            walkAnim[arm] = 0.0F;
        }
        if (enderman.getAttackID() == 2) {
            arm = enderman.getActiveArm();
            animateThrowBlock(enderman.getAttackTick(), arm);
        }
        if (enderman.getAttackID() == 5) {
            animateScream(enderman.getAttackTick());
            float scale = 1.0F - MathHelper.clamp(enderman.getAttackTick() / 6.0F, 0.0F, 1.0F);
            faceYaw *= scale;
            facePitch *= scale;
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            walkAnim3 *= scale;
            walkAnim4 *= scale;
            Arrays.fill(walkAnim, 0.0F);
        }
        if (enderman.getAttackID() == 7) animateTeleSmash(enderman.getAttackTick());
        if (enderman.getAttackID() == 8) {
            animateDeath(enderman.deathTime);
            float scale = 1.0F - MathHelper.clamp(enderman.deathTime / 6.0F, 0.0F, 1.0F);
            faceYaw *= scale;
            facePitch *= scale;
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            walkAnim3 *= scale;
            walkAnim4 *= scale;
            Arrays.fill(walkAnim, 0.0F);
        }
        this.head.rotateAngleX += facePitch * 0.5F;
        this.head.rotateAngleY += faceYaw * 0.7F;
        this.head.rotateAngleZ -= faceYaw * 0.7F;
        this.neck.rotateAngleX += facePitch * 0.3F;
        this.chest.rotateAngleX += facePitch * 0.2F;
        this.mouth.rotateAngleX += breatheAnim * 0.02F + 0.02F;
        this.neck.rotateAngleX -= breatheAnim * 0.02F;
        this.rightArm.arm.rotateAngleZ += breatheAnim * 0.004F;
        this.leftArm.arm.rotateAngleZ -= breatheAnim * 0.004F;
        ModelRenderer[] arr$ = this.rightArm.finger;
        int len$ = arr$.length;
        int i$;
        for (i$ = 0; i$ < len$; i$++) {
            ModelRenderer finger = arr$[i$];
            finger.rotateAngleZ += breatheAnim * 0.05F;
        }
        this.rightArm.thumb.rotateAngleZ -= breatheAnim * 0.05F;
        arr$ = this.leftArm.finger;
        len$ = arr$.length;
        for (i$ = 0; i$ < len$; i$++) {
            ModelRenderer finger = arr$[i$];
            finger.rotateAngleZ -= breatheAnim * 0.05F;
        }
        this.leftArm.thumb.rotateAngleZ += breatheAnim * 0.05F;
        this.lowerRightArm.arm.rotateAngleZ += breatheAnim * 0.002F;
        this.lowerLeftArm.arm.rotateAngleZ -= breatheAnim * 0.002F;
        arr$ = this.lowerRightArm.finger;
        len$ = arr$.length;
        for (i$ = 0; i$ < len$; i$++) {
            ModelRenderer finger = arr$[i$];
            finger.rotateAngleZ += breatheAnim * 0.02F;
        }
        this.lowerRightArm.thumb.rotateAngleZ -= breatheAnim * 0.02F;
        arr$ = this.lowerLeftArm.finger;
        len$ = arr$.length;
        for (i$ = 0; i$ < len$; i$++) {
            ModelRenderer finger = arr$[i$];
            finger.rotateAngleZ -= breatheAnim * 0.02F;
        }
        this.lowerLeftArm.thumb.rotateAngleZ += breatheAnim * 0.02F;
        this.pelvis.rotationPointY -= Math.abs(walkAnim[0]);
        this.chest.rotateAngleY -= walkAnim[0] * 0.06F;
        this.rightArm.arm.rotateAngleX -= walkAnim[1] * 0.6F;
        this.leftArm.arm.rotateAngleX += walkAnim[2] * 0.6F;
        this.rightArm.forearm.rotateAngleX -= walkAnim[1] * 0.2F;
        this.leftArm.forearm.rotateAngleX += walkAnim[2] * 0.2F;
        this.lowerRightArm.arm.rotateAngleX -= walkAnim[3] * 0.3F;
        this.lowerLeftArm.arm.rotateAngleX += walkAnim[4] * 0.3F;
        this.lowerRightArm.forearm.rotateAngleX -= walkAnim[3] * 0.1F;
        this.lowerLeftArm.forearm.rotateAngleX += walkAnim[4] * 0.1F;
        this.legjoint1.rotateAngleX += walkAnim1 * 0.6F;
        this.legjoint2.rotateAngleX += walkAnim2 * 0.6F;
        this.foreleg1.rotateAngleX += walkAnim3 * 0.3F;
        this.foreleg2.rotateAngleX += walkAnim4 * 0.3F;
    }

    private void animateHoldBlock(int fullTick, int armID, boolean hasTarget) {
        float tick = (fullTick + this.partialTick) / 10.0F;
        if (!hasTarget) tick = (fullTick == 0) ? 0.0F : ((fullTick - this.partialTick) / 10.0F);
        float f = MathHelper.sin(tick * 3.1415927F / 2.0F);
        if (armID == 1) {
            this.rightArm.arm.rotateAngleZ += f * 0.8F;
            this.rightArm.forearm.rotateAngleZ += f * 0.6F;
            this.rightArm.hand.rotateAngleY += f * 0.8F;
            (this.rightArm.finger[0]).rotateAngleX += -f * 0.2F;
            (this.rightArm.finger[2]).rotateAngleX += f * 0.2F;
            for (int i = 0; i < this.rightArm.finger.length; i++)
                (this.rightArm.finger[i]).rotateAngleZ += f * 0.6F;
            this.rightArm.thumb.rotateAngleZ += -f * 0.4F;
        } else if (armID == 2) {
            this.leftArm.arm.rotateAngleZ += -f * 0.8F;
            this.leftArm.forearm.rotateAngleZ += -f * 0.6F;
            this.leftArm.hand.rotateAngleY += -f * 0.8F;
            (this.leftArm.finger[0]).rotateAngleX += -f * 0.2F;
            (this.leftArm.finger[2]).rotateAngleX += f * 0.2F;
            for (int i = 0; i < this.leftArm.finger.length; i++)
                (this.leftArm.finger[i]).rotateAngleZ += -f * 0.6F;
            this.leftArm.thumb.rotateAngleZ += f * 0.4F;
        } else if (armID == 3) {
            this.lowerRightArm.arm.rotateAngleZ += f * 0.5F;
            this.lowerRightArm.forearm.rotateAngleZ += f * 0.4F;
            this.lowerRightArm.hand.rotateAngleY += f * 0.4F;
            (this.lowerRightArm.finger[0]).rotateAngleX += -f * 0.2F;
            (this.lowerRightArm.finger[2]).rotateAngleX += f * 0.2F;
            for (int i = 0; i < this.lowerRightArm.finger.length; i++)
                (this.lowerRightArm.finger[i]).rotateAngleZ += f * 0.6F;
            this.lowerRightArm.thumb.rotateAngleZ += -f * 0.4F;
        } else if (armID == 4) {
            this.lowerLeftArm.arm.rotateAngleZ += -f * 0.5F;
            this.lowerLeftArm.forearm.rotateAngleZ += -f * 0.4F;
            this.lowerLeftArm.hand.rotateAngleY += -f * 0.4F;
            (this.lowerLeftArm.finger[0]).rotateAngleX += -f * 0.2F;
            (this.lowerLeftArm.finger[2]).rotateAngleX += f * 0.2F;
            for (int i = 0; i < this.lowerLeftArm.finger.length; i++)
                (this.lowerLeftArm.finger[i]).rotateAngleZ += -f * 0.6F;
            this.lowerLeftArm.thumb.rotateAngleZ += f * 0.4F;
        }
    }

    private void animateMelee(int fullTick, int armID) {
        int right = ((armID & 0x1) == 1) ? 1 : -1;
        Arm arm = getArmFromID(armID);
        if (fullTick < 2) {
            float tick = (fullTick + this.partialTick) / 2.0F;
            float f = MathHelper.sin(tick * 3.1415927F / 2.0F);
            arm.arm.rotateAngleX += f * 0.2F;
            (arm.finger[0]).rotateAngleZ += f * 0.3F * right;
            (arm.finger[1]).rotateAngleZ += f * 0.3F * right;
            (arm.finger[2]).rotateAngleZ += f * 0.3F * right;
            (arm.foreFinger[0]).rotateAngleZ += -f * 0.5F * right;
            (arm.foreFinger[1]).rotateAngleZ += -f * 0.5F * right;
            (arm.foreFinger[2]).rotateAngleZ += -f * 0.5F * right;
        } else if (fullTick < 5) {
            float tick = ((fullTick - 2) + this.partialTick) / 3.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.chest.rotateAngleY += -f1 * 0.1F * right;
            arm.arm.rotateAngleX += f * 1.1F - 1.1F;
            arm.forearm.rotateAngleX += -f * 0.4F;
            (arm.finger[0]).rotateAngleZ += 0.3F * right;
            (arm.finger[1]).rotateAngleZ += 0.3F * right;
            (arm.finger[2]).rotateAngleZ += 0.3F * right;
            (arm.foreFinger[0]).rotateAngleZ += -0.5F * right;
            (arm.foreFinger[1]).rotateAngleZ += -0.5F * right;
            (arm.foreFinger[2]).rotateAngleZ += -0.5F * right;
        } else if (fullTick < 6) {
            this.chest.rotateAngleY += -0.1F * right;
            arm.arm.rotateAngleX -= 1.1F;
            arm.forearm.rotateAngleX -= 0.4F;
            (arm.finger[0]).rotateAngleZ += 0.3F * right;
            (arm.finger[1]).rotateAngleZ += 0.3F * right;
            (arm.finger[2]).rotateAngleZ += 0.3F * right;
            (arm.foreFinger[0]).rotateAngleZ += -0.5F * right;
            (arm.foreFinger[1]).rotateAngleZ += -0.5F * right;
            (arm.foreFinger[2]).rotateAngleZ += -0.5F * right;
        } else if (fullTick < 10) {
            float tick = ((fullTick - 6) + this.partialTick) / 4.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            this.chest.rotateAngleY += -f * 0.1F * right;
            arm.arm.rotateAngleX += -f * 1.1F;
            arm.forearm.rotateAngleX += -f * 0.4F;
            (arm.finger[0]).rotateAngleZ += f * 0.3F * right;
            (arm.finger[1]).rotateAngleZ += f * 0.3F * right;
            (arm.finger[2]).rotateAngleZ += f * 0.3F * right;
            (arm.foreFinger[0]).rotateAngleZ += -f * 0.5F * right;
            (arm.foreFinger[1]).rotateAngleZ += -f * 0.5F * right;
            (arm.foreFinger[2]).rotateAngleZ += -f * 0.5F * right;
        }
    }

    private void animateThrowBlock(int fullTick, int armID) {
        if (armID == 1) {
            if (fullTick < 4) {
                float tick = (fullTick + this.partialTick) / 4.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
                this.rightArm.arm.rotateAngleX += -f1 * 1.5F;
                this.rightArm.arm.rotateAngleZ += f * 0.8F;
                this.rightArm.forearm.rotateAngleZ += f * 0.6F;
                this.rightArm.hand.rotateAngleY += f * 0.8F;
                (this.rightArm.finger[0]).rotateAngleX += -f * 0.2F;
                (this.rightArm.finger[2]).rotateAngleX += f * 0.2F;
                for (int i = 0; i < this.rightArm.finger.length; i++)
                    (this.rightArm.finger[i]).rotateAngleZ += f * 0.6F;
                this.rightArm.thumb.rotateAngleZ += -f * 0.4F;
            } else if (fullTick < 7) {
                this.rightArm.arm.rotateAngleX -= 1.5F;
            } else if (fullTick < 14) {
                float tick = ((fullTick - 7) + this.partialTick) / 7.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                this.rightArm.arm.rotateAngleX += -f * 1.5F;
            }
        } else if (armID == 2) {
            if (fullTick < 4) {
                float tick = (fullTick + this.partialTick) / 4.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
                this.leftArm.arm.rotateAngleX += -f1 * 1.5F;
                this.leftArm.arm.rotateAngleZ += -f * 0.8F;
                this.leftArm.forearm.rotateAngleZ += -f * 0.6F;
                this.leftArm.hand.rotateAngleY += -f * 0.8F;
                (this.leftArm.finger[0]).rotateAngleX += -f * 0.2F;
                (this.leftArm.finger[2]).rotateAngleX += f * 0.2F;
                for (int i = 0; i < this.leftArm.finger.length; i++)
                    (this.leftArm.finger[i]).rotateAngleZ += -f * 0.6F;
                this.leftArm.thumb.rotateAngleZ += f * 0.4F;
            } else if (fullTick < 7) {
                this.leftArm.arm.rotateAngleX -= 1.5F;
            } else if (fullTick < 14) {
                float tick = ((fullTick - 7) + this.partialTick) / 7.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                this.leftArm.arm.rotateAngleX += -f * 1.5F;
            }
        } else if (armID == 3) {
            if (fullTick < 4) {
                float tick = (fullTick + this.partialTick) / 4.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
                this.lowerRightArm.arm.rotateAngleX += -f1 * 1.5F;
                this.lowerRightArm.arm.rotateAngleZ += f * 0.5F;
                this.lowerRightArm.forearm.rotateAngleZ += f * 0.4F;
                this.lowerRightArm.hand.rotateAngleY += f * 0.4F;
                (this.lowerRightArm.finger[0]).rotateAngleX += -f * 0.2F;
                (this.lowerRightArm.finger[2]).rotateAngleX += f * 0.2F;
                for (int i = 0; i < this.lowerRightArm.finger.length; i++)
                    (this.lowerRightArm.finger[i]).rotateAngleZ += f * 0.6F;
                this.lowerRightArm.thumb.rotateAngleZ += -f * 0.4F;
            } else if (fullTick < 7) {
                this.lowerRightArm.arm.rotateAngleX -= 1.5F;
            } else if (fullTick < 14) {
                float tick = ((fullTick - 7) + this.partialTick) / 7.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                this.lowerRightArm.arm.rotateAngleX += -f * 1.5F;
            }
        } else if (armID == 4) {
            if (fullTick < 4) {
                float tick = (fullTick + this.partialTick) / 4.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
                this.lowerLeftArm.arm.rotateAngleX += -f1 * 1.5F;
                this.lowerLeftArm.arm.rotateAngleZ += -f * 0.5F;
                this.lowerLeftArm.forearm.rotateAngleZ += -f * 0.4F;
                this.lowerLeftArm.hand.rotateAngleY += -f * 0.4F;
                (this.lowerLeftArm.finger[0]).rotateAngleX += -f * 0.2F;
                (this.lowerLeftArm.finger[2]).rotateAngleX += f * 0.2F;
                for (int i = 0; i < this.lowerLeftArm.finger.length; i++)
                    (this.lowerLeftArm.finger[i]).rotateAngleZ += -f * 0.6F;
                this.lowerLeftArm.thumb.rotateAngleZ += f * 0.4F;
            } else if (fullTick < 7) {
                this.lowerLeftArm.arm.rotateAngleX -= 1.5F;
            } else if (fullTick < 14) {
                float tick = ((fullTick - 7) + this.partialTick) / 7.0F;
                float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
                this.lowerLeftArm.arm.rotateAngleX += -f * 1.5F;
            }
        }
    }

    private void animateScream(int fullTick) {
        if (fullTick < 35) {
            float tick = (fullTick + this.partialTick) / 35.0F;
            float f = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.abdomen.rotateAngleX += f * 0.3F;
            this.chest.rotateAngleX += f * 0.4F;
            this.neck.rotateAngleX += f * 0.2F;
            this.head.rotateAngleX += f * 0.3F;
            this.rightArm.arm.rotateAngleX += -f * 0.6F;
            this.rightArm.arm.rotateAngleY += f * 0.4F;
            this.rightArm.forearm.rotateAngleX += -f * 0.8F;
            this.rightArm.hand.rotateAngleZ += -f * 0.4F;
            int i;
            for (i = 0; i < 3; i++) {
                (this.rightArm.finger[i]).rotateAngleZ += f * 0.3F;
                (this.rightArm.foreFinger[i]).rotateAngleZ += -f * 0.5F;
            }
            this.leftArm.arm.rotateAngleX += -f * 0.6F;
            this.leftArm.arm.rotateAngleY += -f * 0.4F;
            this.leftArm.forearm.rotateAngleX += -f * 0.8F;
            this.leftArm.hand.rotateAngleZ += f * 0.4F;
            for (i = 0; i < 3; i++) {
                (this.leftArm.finger[i]).rotateAngleZ += -f * 0.3F;
                (this.leftArm.foreFinger[i]).rotateAngleZ += f * 0.5F;
            }
            this.lowerRightArm.arm.rotateAngleX += -f * 0.4F;
            this.lowerRightArm.arm.rotateAngleY += f * 0.2F;
            this.lowerRightArm.forearm.rotateAngleX += -f * 0.8F;
            this.lowerRightArm.hand.rotateAngleZ += -f * 0.4F;
            for (i = 0; i < 3; i++) {
                (this.lowerRightArm.finger[i]).rotateAngleZ += f * 0.3F;
                (this.lowerRightArm.foreFinger[i]).rotateAngleZ += -f * 0.5F;
            }
            this.lowerLeftArm.arm.rotateAngleX += -f * 0.4F;
            this.lowerLeftArm.arm.rotateAngleY += -f * 0.2F;
            this.lowerLeftArm.forearm.rotateAngleX += -f * 0.8F;
            this.lowerLeftArm.hand.rotateAngleZ += f * 0.4F;
            for (i = 0; i < 3; i++) {
                (this.lowerLeftArm.finger[i]).rotateAngleZ += -f * 0.3F;
                (this.lowerLeftArm.foreFinger[i]).rotateAngleZ += f * 0.5F;
            }
        } else if (fullTick < 40) {
            this.abdomen.rotateAngleX += 0.3F;
            this.chest.rotateAngleX += 0.4F;
            this.neck.rotateAngleX += 0.2F;
            this.head.rotateAngleX += 0.3F;
            this.rightArm.arm.rotateAngleX -= 0.6F;
            this.rightArm.arm.rotateAngleY += 0.4F;
            this.rightArm.forearm.rotateAngleX -= 0.8F;
            this.rightArm.hand.rotateAngleZ -= 0.4F;
            int i;
            for (i = 0; i < 3; i++) {
                (this.rightArm.finger[i]).rotateAngleZ += 0.3F;
                (this.rightArm.foreFinger[i]).rotateAngleZ -= 0.5F;
            }
            this.leftArm.arm.rotateAngleX -= 0.6F;
            this.leftArm.arm.rotateAngleY -= 0.4F;
            this.leftArm.forearm.rotateAngleX -= 0.8F;
            this.leftArm.hand.rotateAngleZ += 0.4F;
            for (i = 0; i < 3; i++) {
                (this.leftArm.finger[i]).rotateAngleZ -= 0.3F;
                (this.leftArm.foreFinger[i]).rotateAngleZ += 0.5F;
            }
            this.lowerRightArm.arm.rotateAngleX -= 0.4F;
            this.lowerRightArm.arm.rotateAngleY += 0.2F;
            this.lowerRightArm.forearm.rotateAngleX -= 0.8F;
            this.lowerRightArm.hand.rotateAngleZ -= 0.4F;
            for (i = 0; i < 3; i++) {
                (this.lowerRightArm.finger[i]).rotateAngleZ += 0.3F;
                (this.lowerRightArm.foreFinger[i]).rotateAngleZ -= 0.5F;
            }
            this.lowerLeftArm.arm.rotateAngleX -= 0.4F;
            this.lowerLeftArm.arm.rotateAngleY -= 0.2F;
            this.lowerLeftArm.forearm.rotateAngleX -= 0.8F;
            this.lowerLeftArm.hand.rotateAngleZ += 0.4F;
            for (i = 0; i < 3; i++) {
                (this.lowerLeftArm.finger[i]).rotateAngleZ -= 0.3F;
                (this.lowerLeftArm.foreFinger[i]).rotateAngleZ += 0.5F;
            }
        } else if (fullTick < 44) {
            float tick = ((fullTick - 40) + this.partialTick) / 4.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.abdomen.rotateAngleX += -f * 0.1F + 0.4F;
            this.chest.rotateAngleX += f * 0.1F + 0.3F;
            this.chest.rotateAngleZ += f1 * 0.5F;
            this.neck.rotateAngleX += f * 0.2F;
            this.neck.rotateAngleZ += f1 * 0.2F;
            this.head.rotateAngleX += f * 1.2F - 0.8F;
            this.head.rotateAngleZ += f1 * 0.4F;
            this.mouth.rotateAngleX += f1 * 0.6F;
            this.rightArm.arm.rotateAngleX += -f * 0.6F;
            this.rightArm.arm.rotateAngleY += 0.4F;
            this.rightArm.forearm.rotateAngleX += -f * 0.8F;
            this.rightArm.hand.rotateAngleZ += -f * 0.4F;
            int i;
            for (i = 0; i < 3; i++) {
                (this.rightArm.finger[i]).rotateAngleZ += f * 0.3F;
                (this.rightArm.foreFinger[i]).rotateAngleZ += -f * 0.5F;
            }
            this.leftArm.arm.rotateAngleX += -f * 0.6F;
            this.leftArm.arm.rotateAngleY -= 0.4F;
            this.leftArm.forearm.rotateAngleX += -f * 0.8F;
            this.leftArm.hand.rotateAngleZ += f * 0.4F;
            for (i = 0; i < 3; i++) {
                (this.leftArm.finger[i]).rotateAngleZ += -f * 0.3F;
                (this.leftArm.foreFinger[i]).rotateAngleZ += f * 0.5F;
            }
            this.lowerRightArm.arm.rotateAngleX += -f * 0.4F;
            this.lowerRightArm.arm.rotateAngleY += -f * 0.1F + 0.3F;
            this.lowerRightArm.forearm.rotateAngleX += -f * 0.8F;
            this.lowerRightArm.hand.rotateAngleZ += -f * 0.4F;
            for (i = 0; i < 3; i++) {
                (this.lowerRightArm.finger[i]).rotateAngleZ += f * 0.3F;
                (this.lowerRightArm.foreFinger[i]).rotateAngleZ += -f * 0.5F;
            }
            this.lowerLeftArm.arm.rotateAngleX += -f * 0.4F;
            this.lowerLeftArm.arm.rotateAngleY += f * 0.1F - 0.3F;
            this.lowerLeftArm.forearm.rotateAngleX += -f * 0.8F;
            this.lowerLeftArm.hand.rotateAngleZ += f * 0.4F;
            for (i = 0; i < 3; i++) {
                (this.lowerLeftArm.finger[i]).rotateAngleZ += -f * 0.3F;
                (this.lowerLeftArm.foreFinger[i]).rotateAngleZ += f * 0.5F;
            }
            this.leg1.rotateAngleZ += f1 * 0.1F;
            this.leg2.rotateAngleZ += -f1 * 0.1F;
        } else if (fullTick < 155) {
            float tick = ((fullTick - 44) + this.partialTick) / 111.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            this.abdomen.rotateAngleX += 0.4F;
            this.chest.rotateAngleX += 0.3F;
            this.chest.rotateAngleZ += f - 0.5F;
            this.neck.rotateAngleZ += f * 0.4F - 0.2F;
            this.head.rotateAngleX -= 0.8F;
            this.head.rotateAngleZ += f * 0.8F - 0.4F;
            this.mouth.rotateAngleX += 0.6F;
            this.rightArm.arm.rotateAngleY += 0.4F;
            this.leftArm.arm.rotateAngleY -= 0.4F;
            this.lowerRightArm.arm.rotateAngleY += 0.3F;
            this.lowerLeftArm.arm.rotateAngleY -= 0.3F;
            this.leg1.rotateAngleZ += 0.1F;
            this.leg2.rotateAngleZ -= 0.1F;
        } else if (fullTick < 160) {
            float tick = ((fullTick - 155) + this.partialTick) / 5.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            this.abdomen.rotateAngleX += f * 0.4F;
            this.chest.rotateAngleX += f * 0.3F;
            this.chest.rotateAngleZ += -f * 0.5F;
            this.neck.rotateAngleZ += -f * 0.2F;
            this.head.rotateAngleX += -f * 0.8F;
            this.head.rotateAngleZ += -f * 0.4F;
            this.mouth.rotateAngleX += f * 0.6F;
            this.rightArm.arm.rotateAngleY += f * 0.4F;
            this.leftArm.arm.rotateAngleY += -f * 0.4F;
            this.lowerRightArm.arm.rotateAngleY += f * 0.3F;
            this.lowerLeftArm.arm.rotateAngleY += -f * 0.3F;
            this.leg1.rotateAngleZ += f * 0.1F;
            this.leg2.rotateAngleZ += -f * 0.1F;
        }
    }

    private void animateTeleSmash(int fullTick) {
        if (fullTick < 18) {
            float tick = (fullTick + this.partialTick) / 18.0F;
            float f = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.chest.rotateAngleX += -f * 0.3F;
            this.rightArm.arm.rotateAngleY += f * 0.2F;
            this.rightArm.arm.rotateAngleZ += f * 0.8F;
            this.rightArm.hand.rotateAngleY += f * 1.7F;
            this.leftArm.arm.rotateAngleY += -f * 0.2F;
            this.leftArm.arm.rotateAngleZ += -f * 0.8F;
            this.leftArm.hand.rotateAngleY += -f * 1.7F;
            this.lowerRightArm.arm.rotateAngleY += f * 0.2F;
            this.lowerRightArm.arm.rotateAngleZ += f * 0.6F;
            this.lowerRightArm.hand.rotateAngleY += f * 1.7F;
            this.lowerLeftArm.arm.rotateAngleY += -f * 0.2F;
            this.lowerLeftArm.arm.rotateAngleZ += -f * 0.6F;
            this.lowerLeftArm.hand.rotateAngleY += -f * 1.7F;
        } else if (fullTick < 20) {
            float tick = ((fullTick - 18) + this.partialTick) / 2.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.chest.rotateAngleX += -f * 0.3F;
            this.rightArm.arm.rotateAngleX += -f1 * 0.8F;
            this.rightArm.arm.rotateAngleY += 0.2F;
            this.rightArm.arm.rotateAngleZ += 0.8F;
            this.rightArm.hand.rotateAngleY += 1.7F;
            this.leftArm.arm.rotateAngleX += -f1 * 0.8F;
            this.leftArm.arm.rotateAngleY -= 0.2F;
            this.leftArm.arm.rotateAngleZ -= 0.8F;
            this.leftArm.hand.rotateAngleY -= 1.7F;
            this.lowerRightArm.arm.rotateAngleX += -f1 * 0.9F;
            this.lowerRightArm.arm.rotateAngleY += 0.2F;
            this.lowerRightArm.arm.rotateAngleZ += 0.6F;
            this.lowerRightArm.hand.rotateAngleY += 1.7F;
            this.lowerLeftArm.arm.rotateAngleX += -f1 * 0.9F;
            this.lowerLeftArm.arm.rotateAngleY -= 0.2F;
            this.lowerLeftArm.arm.rotateAngleZ -= 0.6F;
            this.lowerLeftArm.hand.rotateAngleY -= 1.7F;
        } else if (fullTick < 24) {
            this.rightArm.arm.rotateAngleX -= 0.8F;
            this.rightArm.arm.rotateAngleY += 0.2F;
            this.rightArm.arm.rotateAngleZ += 0.8F;
            this.rightArm.hand.rotateAngleY += 1.7F;
            this.leftArm.arm.rotateAngleX -= 0.8F;
            this.leftArm.arm.rotateAngleY -= 0.2F;
            this.leftArm.arm.rotateAngleZ -= 0.8F;
            this.leftArm.hand.rotateAngleY -= 1.7F;
            this.lowerRightArm.arm.rotateAngleX -= 0.9F;
            this.lowerRightArm.arm.rotateAngleY += 0.2F;
            this.lowerRightArm.arm.rotateAngleZ += 0.6F;
            this.lowerRightArm.hand.rotateAngleY += 1.7F;
            this.lowerLeftArm.arm.rotateAngleX -= 0.9F;
            this.lowerLeftArm.arm.rotateAngleY -= 0.2F;
            this.lowerLeftArm.arm.rotateAngleZ -= 0.6F;
            this.lowerLeftArm.hand.rotateAngleY -= 1.7F;
        } else if (fullTick < 30) {
            float tick = ((fullTick - 24) + this.partialTick) / 6.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            this.rightArm.arm.rotateAngleX += -f * 0.8F;
            this.rightArm.arm.rotateAngleY += f * 0.2F;
            this.rightArm.arm.rotateAngleZ += f * 0.8F;
            this.rightArm.hand.rotateAngleY += f * 1.7F;
            this.leftArm.arm.rotateAngleX += -f * 0.8F;
            this.leftArm.arm.rotateAngleY += -f * 0.2F;
            this.leftArm.arm.rotateAngleZ += -f * 0.8F;
            this.leftArm.hand.rotateAngleY += -f * 1.7F;
            this.lowerRightArm.arm.rotateAngleX += -f * 0.9F;
            this.lowerRightArm.arm.rotateAngleY += f * 0.2F;
            this.lowerRightArm.arm.rotateAngleZ += f * 0.6F;
            this.lowerRightArm.hand.rotateAngleY += f * 1.7F;
            this.lowerLeftArm.arm.rotateAngleX += -f * 0.9F;
            this.lowerLeftArm.arm.rotateAngleY += -f * 0.2F;
            this.lowerLeftArm.arm.rotateAngleZ += -f * 0.6F;
            this.lowerLeftArm.hand.rotateAngleY += -f * 1.7F;
        }
    }

    private void animateDeath(int deathTick) {
        if (deathTick < 80) {
            float tick = (deathTick + this.partialTick) / 80.0F;
            float f = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.head.rotateAngleX += f * 0.4F;
            this.neck.rotateAngleX += f * 0.3F;
            this.pelvis.rotationPointY += -f * 12.0F;
            this.rightArm.arm.rotateAngleX += -f * 0.4F;
            this.rightArm.arm.rotateAngleY += f * 0.4F;
            this.rightArm.arm.rotateAngleZ += f * 0.6F;
            this.rightArm.forearm.rotateAngleX += -f * 1.2F;
            this.leftArm.arm.rotateAngleX += -f * 0.4F;
            this.leftArm.arm.rotateAngleY += -f * 0.2F;
            this.leftArm.arm.rotateAngleZ += -f * 0.6F;
            this.leftArm.forearm.rotateAngleX += -f * 1.2F;
            this.lowerRightArm.arm.rotateAngleX += -f * 0.4F;
            this.lowerRightArm.arm.rotateAngleY += f * 0.4F;
            this.lowerRightArm.arm.rotateAngleZ += f * 0.6F;
            this.lowerRightArm.forearm.rotateAngleX += -f * 1.2F;
            this.lowerLeftArm.arm.rotateAngleX += -f * 0.4F;
            this.lowerLeftArm.arm.rotateAngleY += -f * 0.2F;
            this.lowerLeftArm.arm.rotateAngleZ += -f * 0.6F;
            this.lowerLeftArm.forearm.rotateAngleX += -f * 1.2F;
            this.leg1.rotateAngleX += -f * 0.9F;
            this.leg1.rotateAngleY += f * 0.3F;
            this.leg2.rotateAngleX += -f * 0.9F;
            this.leg2.rotateAngleY += -f * 0.3F;
            this.foreleg1.rotateAngleX += f * 1.6F;
            this.foreleg2.rotateAngleX += f * 1.6F;
        } else if (deathTick < 84) {
            float tick = ((deathTick - 80) + this.partialTick) / 4.0F;
            float f = MathHelper.cos(tick * 3.1415927F / 2.0F);
            float f1 = MathHelper.sin(tick * 3.1415927F / 2.0F);
            this.head.rotateAngleX += f * 0.4F;
            this.mouth.rotateAngleX += f1 * 0.6F;
            this.neck.rotateAngleX += f * 0.4F - 0.1F;
            this.chest.rotateAngleX += -f1 * 0.8F;
            this.abdomen.rotateAngleX += -f1 * 0.2F;
            this.pelvis.rotationPointY -= 12.0F;
            this.rightArm.arm.rotateAngleX += -f * 0.4F;
            this.rightArm.arm.rotateAngleY += -f * 1.4F + 1.8F;
            this.rightArm.arm.rotateAngleZ += f * 0.6F;
            this.rightArm.forearm.rotateAngleX += -f * 1.2F;
            this.leftArm.arm.rotateAngleX += -f * 0.4F;
            this.leftArm.arm.rotateAngleY += f * 1.6F - 1.8F;
            this.leftArm.arm.rotateAngleZ += -f * 0.6F;
            this.leftArm.forearm.rotateAngleX += -f * 1.2F;
            this.lowerRightArm.arm.rotateAngleX += -f * 0.5F + 0.1F;
            this.lowerRightArm.arm.rotateAngleY += -f * 1.1F + 1.5F;
            this.lowerRightArm.arm.rotateAngleZ += f * 0.6F;
            this.lowerRightArm.forearm.rotateAngleX += -f * 1.2F;
            this.lowerLeftArm.arm.rotateAngleX += -f * 0.5F + 0.1F;
            this.lowerLeftArm.arm.rotateAngleY += f * 1.1F - 1.5F;
            this.lowerLeftArm.arm.rotateAngleZ += -f * 0.6F;
            this.lowerLeftArm.forearm.rotateAngleX += -f * 1.2F;
            this.leg1.rotateAngleX += -f * 1.7F + 0.8F;
            this.leg1.rotateAngleY += f * 0.3F;
            this.leg1.rotateAngleZ += f1 * 0.2F;
            this.leg2.rotateAngleX += -f * 1.7F + 0.8F;
            this.leg2.rotateAngleY += -f * 0.3F;
            this.leg2.rotateAngleZ += -f1 * 0.2F;
            this.foreleg1.rotateAngleX += f * 1.6F;
            this.foreleg2.rotateAngleX += f * 1.6F;
        } else {
            this.mouth.rotateAngleX += 0.6F;
            this.neck.rotateAngleX -= 0.1F;
            this.chest.rotateAngleX -= 0.8F;
            this.abdomen.rotateAngleX -= 0.2F;
            this.pelvis.rotationPointY -= 12.0F;
            this.rightArm.arm.rotateAngleY += 1.8F;
            this.leftArm.arm.rotateAngleY -= 1.8F;
            this.lowerRightArm.arm.rotateAngleX += 0.1F;
            this.lowerRightArm.arm.rotateAngleY += 1.5F;
            this.lowerLeftArm.arm.rotateAngleX += 0.1F;
            this.lowerLeftArm.arm.rotateAngleY -= 1.5F;
            this.leg1.rotateAngleX += 0.8F;
            this.leg1.rotateAngleZ += 0.2F;
            this.leg2.rotateAngleX += 0.8F;
            this.leg2.rotateAngleZ -= 0.2F;
        }
    }

    public Arm getArmFromID(int armID) {
        return (armID == 1) ? this.rightArm : ((armID == 2) ? this.leftArm : ((armID == 3) ? this.lowerRightArm : this.lowerLeftArm));
    }

    public void postRenderArm(float scale, int armID) {
        this.pelvis.postRender(scale);
        this.abdomen.postRender(scale);
        this.chest.postRender(scale);
        getArmFromID(armID).postRender(scale);
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        this.partialTick = partialTickTime;
    }

    static class Arm {
        private final ScalableModelRenderer arm;

        private final ModelRenderer forearm;

        private final ModelRenderer hand;

        private final ModelRenderer[] finger;

        private final ModelRenderer[] foreFinger;

        private final ModelRenderer thumb;

        private final boolean right;

        public Arm(ModelBase model, ModelRenderer connect, boolean right) {
            this.right = right;
            this.finger = new ModelRenderer[3];
            this.foreFinger = new ModelRenderer[3];
            this.arm = new ScalableModelRenderer(model, 92, 0);
            this.arm.mirror = !this.right;
            this.arm.addBox(-1.5F, 0.0F, -1.5F, 3, 22, 3, 0.1F);
            this.arm.setRotationPoint(this.right ? -4.0F : 4.0F, -14.0F, 0.0F);
            connect.addChild(this.arm);
            this.forearm = new ModelRenderer(model, 104, 0);
            this.forearm.mirror = !this.right;
            this.forearm.addBox(-1.5F, 0.0F, -1.5F, 3, 18, 3);
            this.forearm.setRotationPoint(0.0F, 21.0F, 1.0F);
            this.arm.addChild(this.forearm);
            this.hand = new ModelRenderer(model);
            this.hand.setRotationPoint(0.0F, 17.5F, 0.0F);
            this.forearm.addChild(this.hand);
            float fingerScale = 0.6F;
            int i;
            for (i = 0; i < this.finger.length; i++) {
                this.finger[i] = new ModelRenderer(model, 76, 0);
                (this.finger[i]).mirror = !this.right;
                this.finger[i].addBox(-0.5F, 0.0F, -0.5F, 1, (i == 1) ? 6 : 5, 1, fingerScale);
            }
            this.finger[0].setRotationPoint(this.right ? -0.5F : 0.5F, 0.0F, -1.0F);
            this.finger[1].setRotationPoint(this.right ? -0.5F : 0.5F, 0.0F, 0.0F);
            this.finger[2].setRotationPoint(this.right ? -0.5F : 0.5F, 0.0F, 1.0F);
            for (i = 0; i < this.foreFinger.length; i++) {
                this.foreFinger[i] = new ModelRenderer(model, 76, 0);
                (this.foreFinger[i]).mirror = !this.right;
                this.foreFinger[i].addBox(-0.5F, 0.0F, -0.5F, 1, (i == 1) ? 6 : 5, 1, fingerScale - 0.01F);
                this.foreFinger[i].setRotationPoint(0.0F, 0.5F + ((i == 1) ? 6 : 5), 0.0F);
            }
            for (i = 0; i < this.finger.length; i++) {
                this.hand.addChild(this.finger[i]);
                this.finger[i].addChild(this.foreFinger[i]);
            }
            this.thumb = new ModelRenderer(model, 76, 0);
            this.thumb.mirror = this.right;
            this.thumb.addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1, fingerScale);
            this.thumb.setRotationPoint(this.right ? 0.5F : -0.5F, 0.0F, -0.5F);
            this.hand.addChild(this.thumb);
        }

        private void setAngles() {
            MutantEndermanModel.resetAngles(this.arm);
            MutantEndermanModel.resetAngles(this.forearm);
            MutantEndermanModel.resetAngles(this.hand);
            for (int i = 0; i < this.finger.length; i++) {
                MutantEndermanModel.resetAngles(this.finger[i]);
                MutantEndermanModel.resetAngles(this.foreFinger[i]);
            }
            MutantEndermanModel.resetAngles(this.thumb);
            if (this.right) {
                this.arm.rotateAngleX = -0.5235988F;
                this.arm.rotateAngleZ = 0.5235988F;
                this.forearm.rotateAngleX = -0.62831855F;
                this.hand.rotateAngleY = -0.3926991F;
                (this.finger[0]).rotateAngleX = -0.2617994F;
                (this.finger[1]).rotateAngleZ = 0.17453294F;
                (this.finger[2]).rotateAngleX = 0.2617994F;
                (this.foreFinger[0]).rotateAngleZ = -0.2617994F;
                (this.foreFinger[1]).rotateAngleZ = -0.3926991F;
                (this.foreFinger[2]).rotateAngleZ = -0.2617994F;
                this.thumb.rotateAngleX = -0.62831855F;
                this.thumb.rotateAngleZ = -0.3926991F;
            } else {
                this.arm.rotateAngleX = -0.5235988F;
                this.arm.rotateAngleZ = -0.5235988F;
                this.forearm.rotateAngleX = -0.62831855F;
                this.hand.rotateAngleY = 0.3926991F;
                (this.finger[0]).rotateAngleX = -0.2617994F;
                (this.finger[1]).rotateAngleZ = -0.17453294F;
                (this.finger[2]).rotateAngleX = 0.2617994F;
                (this.foreFinger[0]).rotateAngleZ = 0.2617994F;
                (this.foreFinger[1]).rotateAngleZ = 0.3926991F;
                (this.foreFinger[2]).rotateAngleZ = 0.2617994F;
                this.thumb.rotateAngleX = -0.62831855F;
                this.thumb.rotateAngleZ = 0.3926991F;
            }
        }

        private void postRender(float scale) {
            this.arm.postRender(scale);
            this.forearm.postRender(scale);
            this.hand.postRender(scale);
        }
    }
}
