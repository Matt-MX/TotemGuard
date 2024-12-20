/*
 * This file is part of TotemGuard - https://github.com/Bram1903/TotemGuard
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.totemguard.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import io.ebean.annotation.Platform;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public final class Settings {

    @Comment("Prefix: Sets the command prefix for the plugin.")
    private String prefix = "&6&lTG &8» ";

    @Comment("\nDebug: Enables debug mode (Advanced Users Only).")
    private boolean debug = false;

    @Comment("\nAlert Client Brand: Notifies players with the alert permission, what client brand a player is using.")
    private boolean alertClientBrand = false;

    @Comment({
            "",
            "Supported Placeholders for alerts and punishments:",
            "%prefix% - Prefix of the Plugin",
            "%uuid% - UUID of the Player",
            "%player% - Name of the Player",
            "%check% - Name of the Check",
            "%description% - Description of the Check",
            "%ping% - Player's Ping",
            "%tps% - Server's TPS",
            "%punishable% - If the check is punishable",
            "%violations% - Amount of Violations",
            "%max_violations% - Maximum Violations",
            "",
            "Alert Format: The format of the alert message."})
    private String alertFormat = "%prefix%&e%player%&7 failed &6%check%&f &7VL[&6%violations%/%max_violations%&7]";

    @Comment("\nAlerts Enabled: Message when enabling alerts.")
    private String alertsEnabled = "%prefix%&aAlerts enabled";

    @Comment("\nAlerts Disabled: Message when disabling alerts.")
    private String alertsDisabled = "%prefix%&cAlerts disabled";

    @Comment("\nProxy Alert Settings:")
    private ProxyAlerts proxyAlerts = new ProxyAlerts();

    @Comment("\nColor Scheme Settings:")
    private ColorScheme colorScheme = new ColorScheme();

    @Comment("\nThe time in minutes at which the plugin should reset the violations.")
    private int resetViolationsInterval = 30;

    @Comment("\nUpdate Checker Settings:")
    private UpdateChecker updateChecker = new UpdateChecker();

    @Comment("\nDetermines when the plugin should stop for checking a player.")
    private Determine determine = new Determine();

    @Comment("\nDatabase settings:")
    private Database database = new Database();

    @Comment("\nWebhook settings:")
    private Webhook webhook = new Webhook();

    @Comment("\nChecks")
    private Checks checks = new Checks();

    @Configuration
    @Getter
    public static class ProxyAlerts {
        @Comment({
            "Proxy messaging method",
            "How should be send and receive messages from sibling servers?",
            "Options:",
            " - plugin-messaging (Will use plugin messaging through player connections.)",
            " - redis (Requires further configuration in the 'redis' section below.)"
        })
        private String method = "plugin-messaging";

        @Comment("When enabled, the plugin will send alerts to other servers connected to the proxy.")
        private boolean send = true;

        @Comment("\nWhen enabled, the plugin will receive alerts from other servers connected to the proxy.")
        private boolean receive = true;


        private RedisConfiguration redis = new RedisConfiguration();

        @Configuration
        @Getter
        public static class RedisConfiguration {
            private String host = "localhost";
            private int port = 0;
            private String username = "null";
            private String password = "1234";
            private String channel = "totemguard";
        }
    }

    @Configuration
    @Getter
    public static class ColorScheme {
        @Comment("Primary Color: The primary color of the plugin.")
        private String primaryColor = "&6";

        @Comment("\nSecondary Color: The secondary color of the plugin.")
        private String secondaryColor = "&7";
    }

    @Configuration
    @Getter
    public static class Database {
        @Comment("Database Type: The type of database to use. (SQLite, MYSQL)")
        private String type = String.valueOf(Platform.SQLITE);

        @Comment("\nDatabase Host: The host of the database.")
        private String host = "localhost";

        @Comment("\nDatabase Port: The port of the database.")
        private int port = 3306;

        @Comment("\nDatabase Name: The name of the database.")
        private String name = "TotemGuard";

        @Comment("\nDatabase Username: The username of the database.")
        private String username = "root";

        @Comment("\nDatabase Password: The password of the database.")
        private String password = "password";
    }

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
        private int maxPing = 400;
    }

    @Configuration
    @Getter
    public static class Checks {
        @Comment("When enabled, players with the bypass permission will not be flagged.")
        private boolean bypass = false;

        @Comment({"", "AutoTotemA Settings"})
        private AutoTotemA autoTotemA = new AutoTotemA();

        @Comment("\nAutoTotemB Settings")
        private AutoTotemB autoTotemB = new AutoTotemB();

        @Comment("\nAutoTotemC Settings")
        private AutoTotemC autoTotemC = new AutoTotemC();

        @Comment("\nAutoTotemD Settings")
        private AutoTotemD autoTotemD = new AutoTotemD();

        @Comment("\nAutoTotemE Settings")
        private AutoTotemE autoTotemE = new AutoTotemE();

        @Comment("\nAutoTotemF Settings")
        private AutoTotemF autoTotemF = new AutoTotemF();

        @Comment("\nBadPacketA Settings")
        private BadPacketsA badPacketsA = new BadPacketsA();

        @Comment("\nBadPacketB Settings")
        private BadPacketsB badPacketsB = new BadPacketsB();

        @Comment("\nManualTotemA Settings")
        private ManualTotemA manualTotemA = new ManualTotemA();

        @Configuration
        @Getter
        public abstract static class CheckSettings {
            private boolean enabled = true;
            private boolean punishable;
            private int punishmentDelayInSeconds = 0;
            private int maxViolations;
            private List<String> punishmentCommands = List.of(
                    "ban %player% 1d [TotemGuard] Unfair Advantage"
            );

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
            private int normalCheckTimeMs = 1500;

            @Comment("\nClick Time Difference: The value (in ms) which anything below will trigger the flag.")
            private int clickTimeDifference = 75;

            public AutoTotemA() {
                super(true, 2);
            }
        }

        @Configuration
        @Getter
        public static class AutoTotemB extends CheckSettings {
            @Comment("\nStandard Deviation Threshold: The threshold for the standard deviation.")
            private double standardDeviationThreshold = 30.0;

            @Comment("\nMean Threshold: The threshold for the mean.")
            private double meanThreshold = 500.0;

            @Comment("\nConsecutive Low SD Count: The amount of consecutive low standard deviations before flagging.")
            private int consecutiveLowSDCount = 3;

            public AutoTotemB() {
                super(true, 6);
            }
        }

        @Configuration
        @Getter
        public static class AutoTotemC extends CheckSettings {
            @Comment("\nConsistent SD Range: The range for the standard average deviation.")
            private double consistentSDRange = 1.0;

            @Comment("\nConsecutive Violations: The amount of consecutive violations before flagging.")
            private int consecutiveViolations = 3;

            public AutoTotemC() {
                super(true, 3);
            }
        }

        @Configuration
        @Getter
        public static class AutoTotemD extends CheckSettings {
            @Comment("\nTotal Sequence: The total sequence timing under which the player will be flagged.")
            private int totalSequence = 160;

            @Comment("\nTime average Difference between packets: The time difference between packets.")
            private int baseTimeDifference = 50;

            @Comment("\nTime Tolerance: The tolerance for the time difference.")
            private int tolerance = 5;

            public AutoTotemD() {
                super(true, 2);
            }
        }

        @Configuration
        @Getter
        public static class AutoTotemE extends CheckSettings {
            @Comment("\nStandard Deviation Threshold: The threshold for the standard deviation.")
            private double standardDeviationThreshold = 10.0;

            @Comment("\nAverage Standard Deviation Threshold: The threshold for the average standard deviation.")
            private double averageStDeviationThreshold = 10.0;

            public AutoTotemE() {
                super(true, 4);
            }
        }

        @Configuration
        @Getter
        public static class AutoTotemF extends CheckSettings {
            @Comment("\nTime Difference: The time difference between closing the inventory and the last click.")
            private int timeDifference = 1500;

            public AutoTotemF() {
                super(false, 5);
            }
        }

        @Configuration
        @Getter
        public static class BadPacketsA extends CheckSettings {
            public BadPacketsA() {
                super(true, 30, 1);
            }
        }

        @Configuration
        @Getter
        public static class BadPacketsB extends CheckSettings {
            @Comment("\nBanned Client Brands: The list of client brands to flag.")
            private List<String> bannedClientBrands = List.of(
                    "autototem"
            );

            public BadPacketsB() {
                super(true, 30, 1);
            }
        }

        @Configuration
        @Getter
        public static class ManualTotemA extends CheckSettings {
            @Comment("\nCheck Time: Amount of time the check command waits for a retotem. (in ms)")
            private int checkTime = 250;

            public ManualTotemA() {
                super(false, 2);
            }
        }
    }
}