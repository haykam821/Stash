package io.github.haykam821.stash.component;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import io.github.haykam821.stash.Main;
import net.minecraft.util.Identifier;

public class StashComponentInitializer implements EntityComponentInitializer {
	private static final Identifier STASH_ID = Main.id("stash");
	public static final ComponentKey<StashComponent> STASH = ComponentRegistryV3.INSTANCE.getOrCreate(STASH_ID, StashComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(STASH, StashComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}