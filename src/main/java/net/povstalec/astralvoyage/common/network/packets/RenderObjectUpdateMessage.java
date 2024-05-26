package net.povstalec.astralvoyage.common.network.packets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;
import org.apache.commons.compress.utils.Lists;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RenderObjectUpdateMessage {

    public List<ClientSpaceObject> renderObjects;

    public RenderObjectUpdateMessage(List<ClientSpaceObject> renderObjects)
    {
    	this.renderObjects = renderObjects;
    }

    public static void write(RenderObjectUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeCollection(mes.renderObjects, (buffer, object) -> {
            buffer.writeResourceKey(object.getKey());
            buffer.writeFloat(object.getSize());
            buffer.writeVector3f(object.getSolarPos());
            if(object.getGalacticPos().isPresent())
            {
                buffer.writeBoolean(true);
                buffer.writeVector3f(object.getGalacticPos().get());
            } else buffer.writeBoolean(false);
            buffer.writeCollection(object.getTextureLayers(), (buff, layer) -> {
                buff.writeResourceLocation(layer.getLayer().getFirst());
                buff.writeVarIntArray(layer.getLayer().getSecond().getFirst().stream().mapToInt(i->i).toArray());
                buff.writeBoolean(layer.getLayer().getSecond().getSecond());
            });
        });
    }

    public static RenderObjectUpdateMessage read(FriendlyByteBuf buf){
        List<ClientSpaceObject> list = buf.readList(buffer -> {
            ResourceKey<SpaceObject> key = buffer.readResourceKey(SpaceObject.REGISTRY_KEY);
            float size = buffer.readFloat();
            Vector3f solarPos = buffer.readVector3f();
            Optional<Vector3f> galPos = Optional.empty();
            if(buffer.readBoolean())
                galPos = Optional.of(buffer.readVector3f());
            List<TextureLayerData> layers = buffer.readList(buffe -> {
                ResourceLocation location = buffe.readResourceLocation();
                int[] rgba = buffe.readVarIntArray();
                Boolean blend = buffe.readBoolean();
                Pair<ResourceLocation, Pair<List<Integer>, Boolean>> settings = new Pair<>(location, new Pair<>(Arrays.stream(rgba).boxed().collect(Collectors.toList()), blend));

                return new TextureLayerData(settings);
            });
            return new ClientSpaceObject(key, size, solarPos, galPos, layers);
        });
        return new RenderObjectUpdateMessage(list);
    }

    public static void handle(RenderObjectUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleRenderObjectsUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
