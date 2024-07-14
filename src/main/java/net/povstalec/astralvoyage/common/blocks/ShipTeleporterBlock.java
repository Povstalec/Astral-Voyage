package net.povstalec.astralvoyage.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class ShipTeleporterBlock extends Block {

    public ShipTeleporterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            ItemStack stack = player.getItemInHand(hand).copy();
            if(stack.hasCustomHoverName())
            {
                String name = stack.getHoverName().getString();
                if(SpaceObjects.get(level.getServer()).spaceObjects.containsKey(name)) {
                    level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                        SpaceObject.Serializable object = SpaceObjects.get(level.getServer()).spaceObjects.get(name);
                        if(object.getGalacticPos() != null)
                            cap.setGalacticPostion(object.getGalacticPos().x, object.getGalacticPos().y, object.getGalacticPos().z);
                        if(object.getOrbitMap() != null) {
                            SpaceObject.Serializable parent = SpaceObjects.get(level.getServer()).spaceObjects.get(object.getOrbitMap().getFirst().location().toString());
                            float distance = object.getOrbitMap().getSecond().get("distance").floatValue();
                            Vector3f solarPos = new Vector3f(distance, 0, 0);
                            cap.setSolarPosition(solarPos.x, solarPos.y, solarPos.z);
                            if (parent.getGalacticPos() != null && !parent.getGalacticPos().equals(cap.getGalacticPosition(), 0.1f))
                                cap.setGalacticPostion(parent.getGalacticPos().x, parent.getGalacticPos().y, parent.getGalacticPos().z);
                        } else cap.setSolarPosition(0f, 0f, 0f);
                    });
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
