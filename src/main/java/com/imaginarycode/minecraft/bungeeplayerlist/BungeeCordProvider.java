package com.imaginarycode.minecraft.bungeeplayerlist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class BungeeCordProvider implements PlayerProvider {
    private ServerInfo serverInfo = null;

    public BungeeCordProvider() {
    }

    public BungeeCordProvider(ServerInfo info) {
        this.serverInfo = info;
    }

    @Override
    public Set<String> getPlayers() {
        Set<String> players = new HashSet<>();

        if (serverInfo != null) {
            for (ProxiedPlayer player : serverInfo.getPlayers()) {
                players.add(player.getName());
            }
        } else {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                players.add(player.getName());
            }
        }

        return players;
    }
}
