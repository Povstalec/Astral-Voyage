package net.povstalec.astralvoyage.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.init.BlockInit;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.DimensionHelper;
import org.joml.Vector3f;

public class SpaceshipMovementBlock extends Block {

    private Direction.Axis direction;
    private boolean movementType;

    public SpaceshipMovementBlock(Properties prop, Direction.Axis direction, boolean type) {
        super(prop);
        this.movementType = type;
        this.direction = direction;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!level.isClientSide()) {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                if(player.isHolding(Item.byBlock(BlockInit.SPACESHIP_GENERATOR_BLOCK.get())))
                    if(movementType)
                        cap.moveGalacticPosition(movementDirection().mul(-1));
                    else cap.moveSolarPosition(movementDirection().mul(-1));
                else if(movementType)
                    cap.moveGalacticPosition(movementDirection());
                else cap.moveSolarPosition(movementDirection());
                player.sendSystemMessage(Component.literal(cap.getSolarPosition().toString()));
            });
        }
        return InteractionResult.SUCCESS;
    }

    private Vector3f movementDirection(){
        int x = direction == Direction.Axis.X ? 10000 : 0;
        int y = direction == Direction.Axis.Y ? 100 : 0;
        int z = direction == Direction.Axis.Z ? 100 : 0;

        return new Vector3f(x, y, z);
    }
}
