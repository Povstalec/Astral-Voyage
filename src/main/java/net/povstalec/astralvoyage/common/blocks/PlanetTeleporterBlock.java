package net.povstalec.astralvoyage.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.DimensionHelper;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlanetTeleporterBlock extends Block {
    public PlanetTeleporterBlock(Properties prop) {
        super(prop);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if(pLevel.isClientSide())
            return InteractionResult.FAIL;

        pLevel.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(
        cap -> {
            List<Map.Entry<String, SpaceObject.Serializable>> objectList = SpaceObjects.get(pLevel.getServer()).spaceObjects.entrySet().stream().filter(
                    thing -> {
                        Vector3f vector = new Vector3f(0);
                        if (thing.getValue().getOrbitMap().isPresent())
                            vector = new Vector3f(thing.getValue().getOrbitMap().get().getSecond().get("distance").floatValue(), 0, 0);
                        return vector.distance(cap.getSolarPosition()) < 500000;
                    }).toList();
            objectList.get(0).getValue().getDimension().ifPresent(present ->
                    pPlayer.teleportTo(
                            pLevel.getServer().levelKeys().stream().toList().contains(present)
                                    ? pLevel.getServer().getLevel(present) : (ServerLevel) pLevel,
                            pPlayer.getOnPos().getX(), pPlayer.getOnPos().getY(),
                            pPlayer.getOnPos().getZ(), RelativeMovement.ALL,
                            pPlayer.getYRot(), pPlayer.getXRot())
            );
            if (objectList.get(0).getValue().getSurface().isPresent() && objectList.get(0).getValue().getDimension().isEmpty()) {
                ServerLevel planetLevel = DimensionHelper.createPlanet(pLevel.getServer(), objectList.get(0));
                planetLevel.getCapability(CapabilitiesInit.PLANET).ifPresent(planet -> {
                    planet.setKey(SpaceObject.stringToSpaceObjectKey(objectList.get(0).getKey()));
                    if (objectList.get(0).getValue().getOrbitMap().isPresent()) {
                        planet.setParent(Optional.of(objectList.get(0).getValue().getOrbitMap().get().getFirst()));
                        planet.setSolarPosition(objectList.get(0).getValue().getOrbitMap().get().getSecond().get("distance").floatValue(), 0, 0);
                        SpaceObject.Serializable parentObject = SpaceObjects.get(pLevel.getServer()).spaceObjects.get(objectList.get(0).getValue().getOrbitMap().get().getFirst().location().toString());
                        if (parentObject.getGalacticPos().isPresent())
                            planet.setGalacticPostion(parentObject.getGalacticPos().get().x, parentObject.getGalacticPos().get().y, parentObject.getGalacticPos().get().z);
                    }
                });
                pPlayer.teleportTo(planetLevel,
                        pPlayer.getOnPos().getX(), pPlayer.getOnPos().getY(),
                        pPlayer.getOnPos().getZ(), RelativeMovement.ALL,
                        pPlayer.getYRot(), pPlayer.getXRot());
            }
            else pPlayer.displayClientMessage(Component.translatable("astralvoyage.planet_teleporter.no_settings_or_dimension").append(objectList.get(0).getKey()), true);
        });
        return InteractionResult.PASS;
    }
}
