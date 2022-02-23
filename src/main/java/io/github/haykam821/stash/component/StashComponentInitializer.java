package io.github.haykam821.stash.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.haykam821.stash.Main;
import net.minecraft.util.Identifier;

public class StashComponentInitializer implements EntityComponentInitializer {
	private static final Identifier STASH_ID = new Identifier(Main.MOD_ID, "stash");
	public static final ComponentKey<StashComponent> STASH = ComponentRegistryV3.INSTANCE.getOrCreate(STASH_ID, StashComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(STASH, StashComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}