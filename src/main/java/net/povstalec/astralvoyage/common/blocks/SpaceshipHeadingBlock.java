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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.block_entities.SpaceshipHeadingBlockEntity;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.init.BlockEntityInit;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.Rotation;
import net.povstalec.astralvoyage.common.util.SphericalCoords;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SpaceshipHeadingBlock extends BaseEntityBlock {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public SpaceshipHeadingBlock(Properties prop) {
        super(prop);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            ItemStack stack = player.getItemInHand(hand).copy();
            SpaceshipHeadingBlockEntity blockEntity = (SpaceshipHeadingBlockEntity) level.getBlockEntity(pos);
            if(stack.hasCustomHoverName())
            {
                String name = stack.getHoverName().getString();
                if(SpaceObjects.get(level.getServer()).spaceObjects.containsKey(name) && blockEntity != null) {
                    level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                        SpaceObject.Serializable object = SpaceObjects.create(level.getServer()).spaceObjects.get(name);
                        object.getOrbitMap().ifPresentOrElse(orbitMap -> {
                            float distance = orbitMap.getSecond().get("distance").floatValue();
                            Vector3f solarPos = new Vector3f(distance, 0 ,0);

                            if(object.getGalacticPos().isPresent() && cap.getGalacticPosition().equals(object.getGalacticPos().get(), 0.1f))
                                blockEntity.setTarget(Rotation.sphericalToRotation(SphericalCoords.cartesianToSpherical(solarPos)));
                        }, () -> {
                            if(object.getGeneration().isPresent())
                                blockEntity.setTarget(Rotation.sphericalToRotation(SphericalCoords.cartesianToSpherical(object.getGalacticPos().get())));
                        });
                    });
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockNeighbour, BlockPos neighbourPos, boolean bool) {
        if(!level.isClientSide())
        {
            boolean powered = state.getValue(POWERED);
            if(powered != level.hasNeighborSignal(pos))
                if(powered)
                    level.scheduleTick(pos, this, 4);
                else
                    level.setBlock(pos, state.cycle(POWERED), 2);
        }

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(state.getValue(POWERED) && !level.hasNeighborSignal(pos))
            level.setBlock(pos, state.cycle(POWERED), 2);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceshipHeadingBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntityInit.HEADING_BLOCK.get() ? SpaceshipHeadingBlockEntity::tick : null;
    }
}
