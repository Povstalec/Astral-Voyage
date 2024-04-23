package net.povstalec.astralvoyage.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record UpdateDimensionsPacket(Set<ResourceKey<Level>> keys, boolean add)
{
    public static UpdateDimensionsPacket read(FriendlyByteBuf buffer)
    {
        Set<ResourceKey<Level>> keys = buffer.readCollection(i->new HashSet<>(), buf->ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation()));
        boolean add = buffer.readBoolean();

        return new UpdateDimensionsPacket(keys,add);
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeCollection(this.keys(), (buf,key)->buf.writeResourceLocation(key.location()));
        buffer.writeBoolean(this.add());
    }

    public void handle(Supplier<NetworkEvent.Context> contextGetter)
    {
        NetworkEvent.Context context = contextGetter.get();
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            context.enqueueWork(() -> ClientHandler.handle(this));
        }
        context.setPacketHandled(true);
    }

    private static class ClientHandler // making client calls in the static class prevents classloading errors
    {
        private static void handle(UpdateDimensionsPacket packet)
        {
            @SuppressWarnings("resource")
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player == null)
                return;

            final Set<ResourceKey<Level>> dimensionList = player.connection.levels();
            if (dimensionList == null)
                return;

            Consumer<ResourceKey<Level>> keyConsumer = packet.add()
                    ? dimensionList::add
                    : dimensionList::remove;

            packet.keys().forEach(keyConsumer);
        }
    }
}