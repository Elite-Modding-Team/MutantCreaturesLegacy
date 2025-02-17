package chumbanotz.mutantbeasts.client.renderer.entity;

import chumbanotz.mutantbeasts.MutantBeasts;
import chumbanotz.mutantbeasts.client.model.MutantCreeperModel;
import chumbanotz.mutantbeasts.client.renderer.entity.layers.LayerCreeperCharge;
import chumbanotz.mutantbeasts.entity.mutant.MutantCreeperEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class MutantCreeperRenderer
extends RenderLiving<MutantCreeperEntity> {
    private static final ResourceLocation TEXTURE = MutantBeasts.getEntityTexture("mutant_creeper");

    public MutantCreeperRenderer(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new MutantCreeperModel(), 1.5f);
        this.addLayer(new LayerCreeperCharge<>(this, new MutantCreeperModel(2.0f)));
    }

    protected void preRenderCallback(MutantCreeperEntity entitylivingbaseIn, float partialTickTime) {
        float scale = 1.2f;
        if (entitylivingbaseIn.deathTime > 0) {
            float f1 = (float)entitylivingbaseIn.deathTime / 100.0f;
            scale -= f1 * 0.4f;
        }
        GlStateManager.scale(scale, scale, scale);
    }

    protected int getColorMultiplier(MutantCreeperEntity entitylivingbaseIn, float lightBrightness, float partialTickTime) {
        if (entitylivingbaseIn.isJumpAttacking() && entitylivingbaseIn.deathTime == 0) {
            float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
            if ((int)(f * 10.0f) % 2 == 0) {
                return 0;
            }
            int i = (int)(f * 0.2f * 255.0f);
            i = MathHelper.clamp(i, 0, 255);
            return i << 24 | 0x30FFFFFF;
        }
        int a = (int)entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime) * -1;
        int r = 255;
        int g = 255;
        int b = 255;
        if (entitylivingbaseIn.getPowered()) {
            r = 160;
            g = 180;
        }
        return a << 24 | r << 16 | g << 8 | b;
    }

    protected float getDeathMaxRotation(MutantCreeperEntity entityLivingBaseIn) {
        return 0.0f;
    }

    protected ResourceLocation getEntityTexture(MutantCreeperEntity entity) {
        return TEXTURE;
    }
}
