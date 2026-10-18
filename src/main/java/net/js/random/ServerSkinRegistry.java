package net.js.random;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerSkinRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSkinRegistry.class);
    private static final Path SKINS_DIRECTORY = FMLPaths.CONFIGDIR.get().resolve("offline_skins");
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};

    private ServerSkinRegistry() {
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Map<String, SkinFile> skins = loadSkins();
        for (SkinFile skin : skins.values()) {
            PacketDistributor.sendToPlayer(player, new SkinPayload(skin.username(), skin.image()));
        }

        SkinFile joiningPlayerSkin = skins.get(normalize(player.getGameProfile().name()));
        if (joiningPlayerSkin != null) {
            PacketDistributor.sendToAllPlayers(new SkinPayload(joiningPlayerSkin.username(), joiningPlayerSkin.image()));
        }
    }

    private static Map<String, SkinFile> loadSkins() {
        try {
            Files.createDirectories(SKINS_DIRECTORY);
            Map<String, SkinFile> skins = new HashMap<>();
            try (var paths = Files.list(SKINS_DIRECTORY)) {
                for (Path file : paths.filter(path -> path.getFileName().toString().endsWith(".png"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList()) {
                    try {
                        String fileName = file.getFileName().toString();
                        String username = fileName.substring(0, fileName.length() - 4);
                        if (!username.matches("[A-Za-z0-9_]{1,16}")) {
                            LOGGER.warn("Ignoring offline skin file with invalid username in its name: {}", file);
                            continue;
                        }

                        long size = Files.size(file);
                        if (size == 0 || size > SkinPayload.MAX_IMAGE_SIZE) {
                            LOGGER.warn("Ignoring offline skin file outside the allowed size limit: {}", file);
                            continue;
                        }

                        byte[] image = Files.readAllBytes(file);
                        if (!isValidSkinImage(image)) {
                            LOGGER.warn("Ignoring offline skin file that is not a valid 64x64 PNG: {}", file);
                            continue;
                        }

                        String key = normalize(username);
                        if (skins.containsKey(key)) {
                            LOGGER.warn("Ignoring duplicate offline skin username: {}", username);
                            continue;
                        }
                        skins.put(key, new SkinFile(username, image));
                    } catch (IOException exception) {
                        LOGGER.warn("Failed to read offline skin file {}", file, exception);
                    }
                }
            }

            return skins;
        } catch (IOException exception) {
            LOGGER.error("Failed to load offline skins from {}", SKINS_DIRECTORY, exception);
            return Map.of();
        }
    }

    private static boolean isValidSkinImage(byte[] image) throws IOException {
        if (image.length < PNG_SIGNATURE.length) {
            return false;
        }
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            if (image[i] != PNG_SIGNATURE[i]) {
                return false;
            }
        }

        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(image))) {
            if (input == null) {
                return false;
            }

            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                return false;
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                if (reader.getWidth(0) != 64 || reader.getHeight(0) != 64) {
                    return false;
                }
                BufferedImage decoded = reader.read(0);
                return decoded != null && decoded.getWidth() == 64 && decoded.getHeight() == 64;
            } finally {
                reader.dispose();
            }
        }
    }

    private static String normalize(String username) {
        return username.toLowerCase(Locale.ROOT);
    }

    private record SkinFile(String username, byte[] image) {
    }
}
