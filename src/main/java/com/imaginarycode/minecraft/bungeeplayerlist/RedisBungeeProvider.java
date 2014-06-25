package com.imaginarycode.minecraft.bungeeplayerlist;

import com.google.common.collect.ImmutableSet;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RedisBungeeProvider implements PlayerProvider {
    private ServerInfo serverInfo = null;

    public RedisBungeeProvider() {
    }

    public RedisBungeeProvider(ServerInfo info) {
        this.serverInfo = info;
    }

    @Override
    public Set<String> getPlayers() {
        if (serverInfo == null) {
            return ImmutableSet.copyOf(RedisBungee.getApi().getHumanPlayersOnline());
        } else {
            Set<String> players = new HashSet<>();

            for (UUID uuid : RedisBungee.getApi().getServerToPlayers().get(serverInfo.getName())) {
                players.add(RedisBungee.getApi().getNameFromUuid(uuid));
            }

            return players;
        }
    }
}
