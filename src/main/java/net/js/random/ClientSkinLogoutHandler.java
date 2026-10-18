package net.js.random;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = RandomnessMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSkinLogoutHandler {
    private ClientSkinLogoutHandler() {
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        OfflineSkinClient.clear();
    }
}
