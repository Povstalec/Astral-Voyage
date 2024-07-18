package net.povstalec.astralvoyage.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.astralvoyage.common.entities.PilotSeatEntity;

public class PilotSeatRenderer extends EntityRenderer<PilotSeatEntity>
{
    public PilotSeatRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public boolean shouldRender(PilotSeatEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(PilotSeatEntity pEntity) {
        return null;
    }
}
