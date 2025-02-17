package chumbanotz.mutantbeasts.client.renderer.entity;

import chumbanotz.mutantbeasts.MutantBeasts;
import chumbanotz.mutantbeasts.entity.EndersoulCloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class EndersoulCloneRenderer
extends RenderLiving<EndersoulCloneEntity> {
    private static final ResourceLocation TEXTURE = MutantBeasts.getEntityTexture("endersoul");

    public EndersoulCloneRenderer(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelEnderman(0.0f), 0.5f);
        this.shadowOpaque = 0.5f;
    }

    public void doRender(EndersoulCloneEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ((ModelEnderman)this.mainModel).isAttacking = entity.isAggressive();
        if (entity.isAggressive()) {
            x += entity.getRNG().nextGaussian() * 0.02;
            z += entity.getRNG().nextGaussian() * 0.02;
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected void renderModel(EndersoulCloneEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        EndersoulCloneRenderer.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, this.mainModel, 1.0f);
    }

    protected float getDeathMaxRotation(EndersoulCloneEntity entityLivingBaseIn) {
        return 0.0f;
    }

    protected ResourceLocation getEntityTexture(EndersoulCloneEntity entity) {
        return TEXTURE;
    }

    public static void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, ModelBase model, float alpha) {
        EndersoulCloneRenderer.render(entityIn, TEXTURE, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, model, alpha);
    }

    public static void render(Entity entityIn, ResourceLocation texture, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, ModelBase model, float alpha) {
        GlStateManager.depthMask((entityIn == null || !entityIn.isInvisible() ? 1 : 0) != 0);
        GlStateManager.disableLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = ageInTicks * 0.008f;
        GlStateManager.translate(f, f, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int var5 = 61680;
        int var6 = var5 % 65536;
        int var7 = var5 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var6, (float)var7);
        GlStateManager.color(0.9f, 0.3f, 1.0f, alpha);
        GlStateManager.enableLighting();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        int i = entityIn.getBrightnessForRender();
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
}
