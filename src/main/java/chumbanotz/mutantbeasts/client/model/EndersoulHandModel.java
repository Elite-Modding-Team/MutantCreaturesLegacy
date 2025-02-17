package chumbanotz.mutantbeasts.client.model;

import chumbanotz.mutantbeasts.MutantBeasts;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.function.Function;
import javax.vecmath.Matrix4f;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

public class EndersoulHandModel extends ModelBase {
    public static final ResourceLocation GUI_LOCATION = new ModelResourceLocation(MutantBeasts.prefix("endersoul_hand_gui"), "inventory");
    public static final ResourceLocation MODEL_LOCATION = new ModelResourceLocation(MutantBeasts.prefix("endersoul_hand_model"), "inventory");
    private final ModelRenderer hand;
    private final ModelRenderer[] finger;
    private final ModelRenderer[] foreFinger;
    private final ModelRenderer thumb;

    public EndersoulHandModel() {
        int i;
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.finger = new ModelRenderer[3];
        this.foreFinger = new ModelRenderer[3];
        this.hand = new ModelRenderer(this);
        this.hand.setRotationPoint(0.0f, 17.5f, 0.0f);
        float fingerScale = 0.6f;
        for (i = 0; i < this.finger.length; ++i) {
            this.finger[i] = new ModelRenderer(this, i * 4, 0);
            this.finger[i].addBox(-0.5f, 0.0f, -0.5f, 1, i == 1 ? 6 : 5, 1, fingerScale);
        }
        this.finger[0].setRotationPoint(-0.5f, 0.0f, -1.0f);
        this.finger[1].setRotationPoint(-0.5f, 0.0f, 0.0f);
        this.finger[2].setRotationPoint(-0.5f, 0.0f, 1.0f);
        for (i = 0; i < this.foreFinger.length; ++i) {
            this.foreFinger[i] = new ModelRenderer(this, 1 + i * 5, 0);
            this.foreFinger[i].addBox(-0.5f, 0.0f, -0.5f, 1, i == 1 ? 6 : 5, 1, fingerScale - 0.01f);
            this.foreFinger[i].setRotationPoint(0.0f, 0.5f + (float) (i == 1 ? 6 : 5), 0.0f);
        }
        for (i = 0; i < this.finger.length; ++i) {
            this.hand.addChild(this.finger[i]);
            this.finger[i].addChild(this.foreFinger[i]);
        }
        this.thumb = new ModelRenderer(this, 14, 0);
        this.thumb.addBox(-0.5f, 0.0f, -0.5f, 1, 5, 1, fingerScale);
        this.thumb.setRotationPoint(0.5f, 0.0f, -0.5f);
        this.hand.addChild(this.thumb);
    }

    private void resetAngles(ModelRenderer model) {
        model.rotateAngleX = 0.0f;
        model.rotateAngleY = 0.0f;
        model.rotateAngleZ = 0.0f;
    }

    public void setAngles() {
        this.resetAngles(this.hand);
        for (int i = 0; i < this.finger.length; ++i) {
            this.resetAngles(this.finger[i]);
            this.resetAngles(this.foreFinger[i]);
        }
        this.resetAngles(this.thumb);
        this.hand.rotateAngleY = -0.3926991f;
        this.finger[0].rotateAngleX = -0.2617994f;
        this.finger[1].rotateAngleZ = 0.17453294f;
        this.finger[2].rotateAngleX = 0.2617994f;
        this.foreFinger[0].rotateAngleZ = -0.2617994f;
        this.foreFinger[1].rotateAngleZ = -0.3926991f;
        this.foreFinger[2].rotateAngleZ = -0.2617994f;
        this.thumb.rotateAngleX = -0.62831855f;
        this.thumb.rotateAngleZ = -0.3926991f;
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setAngles();
        this.hand.render(0.0625f);
    }

    public static class Baked
            extends BakedModelWrapper<IBakedModel> {
        private final IBakedModel bakedModel;

        public Baked(IBakedModel originalModel, IBakedModel bakedModel) {
            super(originalModel);
            this.bakedModel = bakedModel;
        }

        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            if (cameraTransformType != ItemCameraTransforms.TransformType.GUI && cameraTransformType != ItemCameraTransforms.TransformType.GROUND && cameraTransformType != ItemCameraTransforms.TransformType.FIXED) {
                return this.bakedModel.handlePerspective(cameraTransformType);
            }
            return super.handlePerspective(cameraTransformType);
        }
    }

    public static class Unbaked
            implements IModel {
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            try {
                IBakedModel originalModel = ModelLoaderRegistry.getModel(GUI_LOCATION).bake(state, format, bakedTextureGetter);
                IBakedModel bakedModel = ModelLoaderRegistry.getModel(MODEL_LOCATION).bake(state, format, bakedTextureGetter);
                return new Baked(originalModel, bakedModel);
            } catch (Exception exception) {
                throw new RuntimeException("Failed to load models for the endersoul hand", exception);
            }
        }

        public Collection<ResourceLocation> getDependencies() {
            return ImmutableList.of(GUI_LOCATION, MODEL_LOCATION);
        }
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE;


        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals("mutantbeasts") && modelLocation.getPath().equals("endersoul_hand");
        }

        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            return new Unbaked();
        }
    }
}
