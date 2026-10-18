# Offline Skins

A NeoForge mod for Minecraft 26.3 that loads player skins from files on the server. It works with offline-mode servers and requires the mod on both the server and every connecting client.

## Run the client

Install a JDK 25, then run from the project directory:

```powershell
.\gradlew.bat runClient
```

To build the mod JAR:

```powershell
.\gradlew.bat build
```

The JAR is written to `build\libs`. NeoForge documentation: <https://docs.neoforged.net/>.

## Add offline skins

Put a PNG skin for each player in the server's `config\offline_skins` directory. Name each file with the player's Minecraft username, for example `Alex.png`. Skins must be 64x64 pixels; use standard Minecraft skin layouts. The filename must use only letters, numbers, and underscores, and usernames are matched without regard to letter case.

The server sends available skins to clients when a player joins. Add or replace skin files and have players reconnect to refresh them.
