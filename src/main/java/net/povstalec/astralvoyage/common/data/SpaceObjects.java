package net.povstalec.astralvoyage.common.data;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.astralvoyage.AstralVoyage;

public class SpaceObjects extends SavedData
{
	private static final String FILE_NAME = AstralVoyage.MODID + "-space_objects";
	
	private MinecraftServer server;
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		//TODO
		
		return tag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		
	}
	
	//============================================================================================
	//********************************************Data********************************************
	//============================================================================================
	
	public SpaceObjects(MinecraftServer server)
	{
		this.server = server;
	}
	
	public static SpaceObjects create(MinecraftServer server)
	{
		return new SpaceObjects(server);
	}
	
	public static SpaceObjects load(MinecraftServer server, CompoundTag tag)
	{
		SpaceObjects data = create(server);

		data.server = server;
		data.deserialize(tag);
		
		return data;
	}
	
	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
		return tag;
	}
	
	@Nonnull
	public static SpaceObjects get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return SpaceObjects.get(level.getServer());
	}
	
	@Nonnull
	public static SpaceObjects get(MinecraftServer server)
	{
		DimensionDataStorage storage = server.overworld().getDataStorage();
		
		return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
	}
}
