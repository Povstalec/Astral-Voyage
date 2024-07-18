package net.povstalec.astralvoyage.common.init;

import net.minecraft.advancements.CriteriaTriggers;
import net.povstalec.astralvoyage.common.advancements.StarshipEntryCriterion;

public class AdvancementInit
{
	public static void register()
	{
		CriteriaTriggers.register(StarshipEntryCriterion.INSTANCE);
	}
}
