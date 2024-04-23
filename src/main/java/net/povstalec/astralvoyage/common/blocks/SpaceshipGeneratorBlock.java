package net.povstalec.astralvoyage.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.util.DimensionHelper;

public class SpaceshipGeneratorBlock extends Block {

    public SpaceshipGeneratorBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!level.isClientSide()) {
            level.getServer().execute(() -> DimensionHelper.createSpaceship(level.getServer()));
        }
        return InteractionResult.SUCCESS;
    }
}
