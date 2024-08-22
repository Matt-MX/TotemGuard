package net.strealex.totemguard.manager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.strealex.totemguard.TotemGuard;
import net.strealex.totemguard.data.CheckDetails;
import net.strealex.totemguard.data.TotemPlayer;
import net.strealex.totemguard.interfaces.IWebhookSettings;

import java.awt.*;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordManager {
    private static final Pattern WEBHOOK_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?\\w+\\.\\w+/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?");
    private final TotemGuard plugin;

    public DiscordManager(TotemGuard plugin) {
        this.plugin = plugin;
    }

    public void sendAlert(TotemPlayer totemPlayer, CheckDetails checkDetails) {
        final IWebhookSettings settings = plugin.getConfigManager().getSettings().getWebhook().getAlert();
        sendWebhook(totemPlayer, checkDetails, settings, false);
    }

    public void sendPunishment(TotemPlayer totemPlayer, CheckDetails checkDetails) {
        final IWebhookSettings settings = plugin.getConfigManager().getSettings().getWebhook().getPunishment();
        sendWebhook(totemPlayer, checkDetails, settings, true);
    }

    private void sendWebhook(TotemPlayer totemPlayer, CheckDetails checkDetails, IWebhookSettings settings, boolean isPunishment) {
        if (!settings.isEnabled()) {
            return;
        }

        Matcher matcher = WEBHOOK_PATTERN.matcher(settings.getUrl());
        if (!matcher.matches()) {
            plugin.getLogger().warning("Invalid webhook URL! Please check your configuration.");
            return;
        }

        WebhookClient client = WebhookClient.withId(Long.parseLong(matcher.group(1)), matcher.group(2));
        client.setTimeout(15000);

        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder()
                .setUsername(settings.getName())
                .setAvatarUrl(settings.getProfileImage());

        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder()
                .setThumbnailUrl("http://cravatar.eu/avatar/" + totemPlayer.getUsername() + "/64.png")
                .setColor(Color.decode(settings.getColor()).getRGB())
                .setTitle(new WebhookEmbed.EmbedTitle(settings.getTitle(), null))
                .addField(new WebhookEmbed.EmbedField(true, "**Player**", totemPlayer.getUsername()))
                .addField(new WebhookEmbed.EmbedField(true, "**Check**", checkDetails.getCheckName()));

        if (!isPunishment) {
            embedBuilder
                    .addField(new WebhookEmbed.EmbedField(true, "**Violations**", String.valueOf(checkDetails.getViolations())))
                    .addField(new WebhookEmbed.EmbedField(true, "**Client Brand**", totemPlayer.getClientBrandName()))
                    .addField(new WebhookEmbed.EmbedField(true, "**Client Version**", totemPlayer.getClientVersion().getReleaseName()))
                    .addField(new WebhookEmbed.EmbedField(true, "**Ping**", String.valueOf(checkDetails.getPing())))
                    .addField(new WebhookEmbed.EmbedField(true, "**TPS**", String.valueOf(checkDetails.getTps())))
                    .addField(new WebhookEmbed.EmbedField(false, "**Details**", "```" + PlainTextComponentSerializer.plainText().serialize(checkDetails.getDetails()) + "```"));
        }

        if (settings.isTimestamp()) {
            embedBuilder.setTimestamp(Instant.now());
        }

        messageBuilder.addEmbeds(embedBuilder.build());
        WebhookMessage message = messageBuilder.build();

        try {
            client.send(message);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send webhook message!\n" + message.getBody());
        }
    }
}