package net.js.random;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(RandomnessMod.MOD_ID)
public final class RandomnessMod {
    public static final String MOD_ID = "offline_skins";

    public RandomnessMod(IEventBus modEventBus) {
        modEventBus.addListener(this::registerPayloads);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            modEventBus.addListener(ClientSkinPayloadHandler::registerPayloadHandler);
        }
        NeoForge.EVENT_BUS.addListener(ServerSkinRegistry::onPlayerLoggedIn);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(SkinPayload.TYPE, SkinPayload.STREAM_CODEC);
    }
}
