package net.povstalec.astralvoyage.common.network.packets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;
import net.povstalec.astralvoyage.common.util.TextureLayerData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class RenderObjectUpdateMessage {

    public List<ClientSpaceObject> objects;

    public RenderObjectUpdateMessage(List<ClientSpaceObject> objects)
    {this.objects = objects;}

    public static void write(RenderObjectUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeCollection(mes.objects, (buffer, object) -> {
            buffer.writeResourceKey(object.getKey());
            buffer.writeFloat(object.getSize());

            object.getOrbitOffset().ifPresentOrElse(offset -> {
                buffer.writeBoolean(true);
                buffer.writeDouble(offset);
            }, () -> buffer.writeBoolean(false));

            buffer.writeVector3f(object.getSolarPos());
            object.getGalacticPos().ifPresentOrElse(galacticPos -> {
                buffer.writeBoolean(true);
                buffer.writeVector3f(galacticPos);
            }, () -> buffer.writeBoolean(false));

            buffer.writeCollection(object.getTextureLayers(), (bufferL, layer) -> {
                bufferL.writeResourceLocation(layer.getLayer().getFirst());
                bufferL.writeVarIntArray(layer.getLayer().getSecond().getFirst().stream().mapToInt(Integer::intValue).toArray());
                buffer.writeBoolean(layer.getLayer().getSecond().getSecond());
            });
        });
    }

    public static RenderObjectUpdateMessage read(FriendlyByteBuf buf){
        List<ClientSpaceObject> list = new ArrayList<>();

        list = buf.readCollection(i -> new ArrayList<>(), buffer -> {
            ResourceKey<SpaceObject> key = buffer.readResourceKey(SpaceObject.REGISTRY_KEY);

            float size = buffer.readFloat();

            Optional<Double> orbitStart = Optional.empty();
            if(buffer.readBoolean())
                orbitStart = Optional.of(buffer.readDouble());

            Vector3f solarPos = buffer.readVector3f();

            Optional<Vector3f> galPos = Optional.empty();
            if(buffer.readBoolean())
                galPos = Optional.of(buffer.readVector3f());

            List<TextureLayerData> layers = new ArrayList<>();
            layers = buffer.readCollection(i -> new ArrayList<>(), buff -> {

                ResourceLocation id = buff.readResourceLocation();

                List<Integer> rgba = Arrays.stream(buff.readVarIntArray()).boxed().toList();

                Boolean blend = buff.readBoolean();

                return new TextureLayerData(new Pair<>(id, new Pair<>(rgba, blend)));
            });

            return new ClientSpaceObject(key, size, orbitStart, solarPos, galPos, layers);
        });

        return new RenderObjectUpdateMessage(list);
    }

    public static void handle(RenderObjectUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleRenderObjectUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
