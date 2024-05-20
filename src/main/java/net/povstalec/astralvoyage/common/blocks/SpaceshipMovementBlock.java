package net.povstalec.astralvoyage.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.DimensionHelper;
import org.joml.Vector3f;

public class SpaceshipMovementBlock extends Block {

    private Direction.Axis direction;

    public SpaceshipMovementBlock(Properties prop, Direction.Axis direction) {
        super(prop);
        this.direction = direction;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!level.isClientSide()) {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                if(player.isCrouching())
                    cap.moveGalacticPosition(movementDirection().mul(-1));
                cap.moveGalacticPosition(movementDirection());
            });
        }
        return InteractionResult.SUCCESS;
    }

    private Vector3f movementDirection(){
        int x = direction == Direction.Axis.X ? 1 : 0;
        int y = direction == Direction.Axis.Y ? 1 : 0;
        int z = direction == Direction.Axis.Z ? 1 : 0;

        return new Vector3f(x, y, z);
    }
}
