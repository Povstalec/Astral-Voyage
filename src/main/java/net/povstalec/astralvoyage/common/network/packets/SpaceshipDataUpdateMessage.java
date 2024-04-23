package net.povstalec.astralvoyage.common.network.packets;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;
import net.povstalec.astralvoyage.common.network.packets.spaceship.SpaceshipData;

import java.util.function.Function;
import java.util.function.Supplier;

public class SpaceshipDataUpdateMessage {

    public static final Int2ObjectArrayMap<Function<Integer, SpaceshipData>> DATAS = new Int2ObjectArrayMap<>();


    static{
        //register(UPDATE_LOC, UpdateDataLocation::new);
    }

    public static void register(int id, Function<Integer, SpaceshipData> data){
        DATAS.put(id, data);
    }

    public final SpaceshipData data;


    public SpaceshipDataUpdateMessage(SpaceshipData data){
        this.data = data;
    }

    public static void encode(SpaceshipDataUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeInt(mes.data.getId());
        mes.data.serialize(buf);
    }

    public static SpaceshipDataUpdateMessage decode(FriendlyByteBuf buf){
        int id = buf.readInt();
        SpaceshipData data = DATAS.get(id).apply(id);
        data.deserialize(buf);
        return new SpaceshipDataUpdateMessage(data);
    }

    public static void handle(SpaceshipDataUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ClientPacketHandler.handleTardisUpdatePacket(mes);
        });
        context.get().setPacketHandled(true);
    }


}
