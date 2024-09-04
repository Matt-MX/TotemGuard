package com.deathmotion.totemguard.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public final class Settings {

    @Comment("Prefix: Sets the command prefix for the plugin.")
    private String prefix = "&6⚡ ";

    @Comment("\nThe time in minutes at which the plugin should reset the violations.")
    private int resetViolationsInterval = 30;

    @Comment("\nUpdate Checker Settings:")
    private UpdateChecker updateChecker = new UpdateChecker();

    @Comment("\nDetermines when the plugin should stop for checking a player.")
    private Determine determine = new Determine();

    @Comment("\nWebhook settings:")
    private Webhook webhook = new Webhook();

    @Comment("\nChecks")
    private Checks checks = new Checks();

    @Configuration
    @Getter
    public static class Webhook {
        @Comment("Webhook Alert Settings")
        private AlertSettings alert = new AlertSettings();

        @Comment("\nWebhook Punishment Settings")
        private PunishmentSettings punishment = new PunishmentSettings();

        @Configuration
        @Getter
        public abstract static class WebhookSettings {
            @Comment("Enable and/or disable the webhook implementation.")
            private boolean enabled = false;

            @Comment("\nWebhook URL: The URL of the webhook to send notifications to.")
            private String url = "https://discord.com/api/webhooks/your_webhook_url";

            @Comment("\nClient Name: Name of the client.")
            private String name = "TotemGuard";

            @Comment("\nWebhook Embed color: Color of the webhook embed (in hex).")
            private String color;

            @Comment("\nWebhook Title: Brief description about what the webhook is about. (Like Alert, Punishment, etc.)")
            private String title;

            @Comment("\nWebhook Profile Image: Sets the image of the embed's profile.")
            private String profileImage = "https://i.imgur.com/hqaGO5H.png";

            @Comment("\nWebhook Timestamp: Displays the time that this embed was sent at.")
            private boolean timestamp = true;

            public WebhookSettings(String title, String color) {
                this.title = title;
                this.color = color;
            }
        }

        @Configuration
        @Getter
        public static class AlertSettings extends WebhookSettings {
            public AlertSettings() {
                super("TotemGuard Alert", "#d9b61a");
            }
        }

        @Configuration
        @Getter
        public static class PunishmentSettings extends WebhookSettings {
            public PunishmentSettings() {
                super("TotemGuard Punishment", "#d60010");
            }
        }
    }

    @Configuration
    @Getter
    public static class UpdateChecker {
        @Comment("Enable and/or disable the update checker.")
        private boolean enabled = true;

        @Comment("\nPrint to Console: Prints the update message to the console.")
        private boolean printToConsole = true;

        @Comment("\nNotify In-Game: Notifies players with the permission in-game.")
        private boolean notifyInGame = true;
    }

    @Configuration
    @Getter
    public static class Determine {
        @Comment("Minimum TPS.")
        private double minTps = 15.0;

        @Comment("\nMaximum Ping.")
        private int maxPing = 500;
    }

    @Configuration
    @Getter
    public static class Checks {
        @Comment("When enabled, players with the bypass permission will not be flagged.")
        private boolean bypass = false;

        @Comment("\nAutoTotemA Settings")
        private AutoTotemA autoTotemA = new AutoTotemA();

        @Comment("\nManualTotemA Settings")
        private ManualTotemA manualTotemA = new ManualTotemA();

        @Comment("\nBadPacketA Settings")
        private BadPacketsA badPacketsA = new BadPacketsA();

        @Configuration
        @Getter
        public abstract static class CheckSettings {
            private boolean enabled = true;
            private boolean punishable;
            private int punishmentDelayInSeconds = 0;
            private int maxViolations;
            private String[] punishmentCommands = {
                    "ban %player% 1d [TotemGuard] Unfair Advantage"
            };

            public CheckSettings(boolean punishable, int punishmentDelay, int maxViolations) {
                this.punishable = punishable;
                this.punishmentDelayInSeconds = punishmentDelay;
                this.maxViolations = maxViolations;
            }

            public CheckSettings(boolean punishable, int maxViolations) {
                this.punishable = punishable;
                this.maxViolations = maxViolations;
            }
        }

        @Configuration
        @Getter
        public static class AutoTotemA extends CheckSettings {
            @Comment("\nNormal Check Time: Sets the interval (in ms) for normal checks.")
            private int normalCheckTimeMs = 1000;

            @Comment("\nClick Time Difference: The value (in ms) which anything below will trigger the flag.")
            private int clickTimeDifference = 75;

            public AutoTotemA() {
                super(true, 5);
            }
        }

        @Configuration
        @Getter
        public static class ManualTotemA extends CheckSettings {
            @Comment("\nCheck Time: Amount of time the /check command waits for a retotem. (in ms)")
            private int checkTime = 250;

            @Comment("\nDamage on /check: Toggles damage on /check command to ensure a more accurate result.")
            private boolean toggleDamageOnCheck = true;

            public ManualTotemA() {
                super(false, 1);
            }
        }

        @Configuration
        @Getter
        public static class BadPacketsA extends CheckSettings {
            public BadPacketsA() {
                super(true, 60, 1);
            }
        }
    }
}