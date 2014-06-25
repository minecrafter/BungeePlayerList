package com.imaginarycode.minecraft.bungeeplayerlist;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class BungeePlayerList extends Plugin implements Listener {
    private PlayerProvider provider;

    @Override
    public void onEnable() {
        // Configuration magic
        Configuration configuration;
        try {
            if (!getDataFolder().exists())
                getDataFolder().mkdir();

            File file = new File(getDataFolder(), "config.yml");

            if (!file.exists()) {
                Files.copy(getResourceAsStream("config.yml"), file.toPath());
            }

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load configuration", e);
            return;
        }

        // Do we have server info?
        ServerInfo info = null;
        if (configuration.get("display-list-of-server") != null) {
            info = getProxy().getServerInfo(configuration.getString("display-list-of-server"));
        }

        // Find out our PlayerProvider
        if (getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
            if (info == null) {
                provider = new RedisBungeeProvider();
            } else {
                provider = new RedisBungeeProvider(info);
            }
            getLogger().info("Providing player list via RedisBungee.");
        } else {
            if (info == null) {
                provider = new BungeeCordProvider();
            } else {
                provider = new BungeeCordProvider(info);
            }
            getLogger().info("Providing player list via BungeeCord.");
        }

        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing original = event.getResponse();

        List<ServerPing.PlayerInfo> currentInfo = new ArrayList<>();

        if (original.getPlayers().getSample() != null) {
            Collections.addAll(currentInfo, original.getPlayers().getSample());
        }

        for (String player : provider.getPlayers()) {
            currentInfo.add(new ServerPing.PlayerInfo(player, (String)null));
        }

        ServerPing.PlayerInfo[] array = currentInfo.toArray(new ServerPing.PlayerInfo[currentInfo.size()]);

        event.setResponse(new ServerPing(original.getVersion(), new ServerPing.Players(original.getPlayers().getMax(), original.getPlayers().getOnline(), array), original.getDescription(), original.getFaviconObject()));
    }
}
