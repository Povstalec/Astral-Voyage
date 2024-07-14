package net.povstalec.astralvoyage.common.network.packets;

import com.mojang.datafixers.util.Pair;
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

public class PlanetRenderUpdateMessage {

    public List<ClientSpaceObject> objects;

    public PlanetRenderUpdateMessage(List<ClientSpaceObject> objects)
    {this.objects = objects;}

    public static void write(PlanetRenderUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeCollection(mes.objects, (buffer, object) -> {
            buffer.writeResourceKey(object.getKey());
            buffer.writeFloat(object.getSize());

            if(object.getOrbitOffset() != null)
            {
                buffer.writeBoolean(true);
                buffer.writeDouble(object.getOrbitOffset());
            } else buffer.writeBoolean(false);

            buffer.writeVector3f(object.getSolarPos());
            if(object.getGalacticPos() != null)
            {
                buffer.writeBoolean(true);
                buffer.writeVector3f(object.getGalacticPos());
            } else buffer.writeBoolean(false);

            buffer.writeCollection(object.getTextureLayers(), (bufferL, layer) -> {
                bufferL.writeResourceLocation(layer.getLayer().getFirst());
                bufferL.writeVarIntArray(layer.getLayer().getSecond().getFirst().stream().mapToInt(Integer::intValue).toArray());
                buffer.writeBoolean(layer.getLayer().getSecond().getSecond());
            });
        });
    }

    public static PlanetRenderUpdateMessage read(FriendlyByteBuf buf){
        List<ClientSpaceObject> list;

        list = buf.readCollection(i -> new ArrayList<>(), buffer -> {
            ResourceKey<SpaceObject> key = buffer.readResourceKey(SpaceObject.REGISTRY_KEY);

            float size = buffer.readFloat();

            Double orbitStart = null;
            if(buffer.readBoolean())
                orbitStart = buffer.readDouble();

            Vector3f solarPos = buffer.readVector3f();

            Vector3f galPos = null;
            if(buffer.readBoolean())
                galPos = buffer.readVector3f();

            List<TextureLayerData> layers;
            layers = buffer.readCollection(i -> new ArrayList<>(), buff -> {

                ResourceLocation id = buff.readResourceLocation();

                List<Integer> rgba = Arrays.stream(buff.readVarIntArray()).boxed().toList();

                Boolean blend = buff.readBoolean();

                return new TextureLayerData(new Pair<>(id, new Pair<>(rgba, blend)));
            });

            return new ClientSpaceObject(key, size, orbitStart, solarPos, galPos, layers);
        });

        return new PlanetRenderUpdateMessage(list);
    }

    public static void handle(PlanetRenderUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handlePlanetRenderUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
