package com.strealex.totemguard.checks.impl.BadPackets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.strealex.totemguard.TotemGuard;
import com.strealex.totemguard.checks.Check;
import com.strealex.totemguard.config.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class BadPacketsA extends Check implements PacketListener {

    private final TotemGuard plugin;

    public BadPacketsA(TotemGuard plugin) {
        super(plugin, "BadPacketsA", "Player is using a suspicious mod!");
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        final Settings.Checks.BadPacketsA settings = plugin.getConfigManager().getSettings().getChecks().getBadPacketsA();
        if (!settings.isEnabled()) {
            return;
        }

        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE && event.getPacketType() != PacketType.Configuration.Client.PLUGIN_MESSAGE) {
            return;
        }

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        String channel = packet.getChannelName();

        if (channel.equalsIgnoreCase("minecraft:using_autototem")) {
            Component checkDetails = Component.text()
                    .append(Component.text("Channel: ", NamedTextColor.GRAY))
                    .append(Component.text(channel, NamedTextColor.GOLD))
                    .build();

            flag((Player) event.getPlayer(), checkDetails, settings);
        }
    }
}