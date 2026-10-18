package net.js.random;

import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

public final class ClientSkinPayloadHandler {
    private ClientSkinPayloadHandler() {
    }

    public static void registerPayloadHandler(RegisterClientPayloadHandlersEvent event) {
        event.register(SkinPayload.TYPE, (payload, context) -> OfflineSkinClient.receive(payload));
    }
}
