package net.povstalec.astralvoyage.common.network.packets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RenderObjectUpdateMessage {

    public List<ClientSpaceObject> renderObjects;

    public RenderObjectUpdateMessage(List<ClientSpaceObject> renderObjects)
    {
    	this.renderObjects = renderObjects;
    }

    public static void write(RenderObjectUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeCollection(mes.renderObjects, (buffer, object) -> {
            buffer.writeUtf(object.getKey().toString());
            buffer.writeVector3f(object.getSolarPos());
            buffer.writeCollection(object.getTextureLayers(), (buff, layer) -> {
                buff.writeUtf(layer.getFirst().toString());
                buff.writeVarIntArray(layer.getSecond().getFirst().stream().mapToInt(i->i).toArray());
                buff.writeBoolean(layer.getSecond().getSecond());
            });
        });
    }

    public static RenderObjectUpdateMessage read(FriendlyByteBuf buf){
        return new RenderObjectUpdateMessage(buf.readCollection(Lists.newArrayList(), buffer -> new ClientSpaceObject(SpaceObject.stringToSpaceObjectKey(buffer.readUtf()), buffer.readVector3f(), buffer.readCollection(Lists.newArrayList(), buffe -> {
            return new Pair<>(new ResourceLocation(buffe.readUtf()), new Pair<>(Arrays.stream(buffe.readVarIntArray()).boxed().toList(), buffe.readBoolean()));
        }))));
    }

    public static void handle(RenderObjectUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleRenderObjectsUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
