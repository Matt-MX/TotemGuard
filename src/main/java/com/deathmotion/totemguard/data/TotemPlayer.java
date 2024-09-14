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

package com.deathmotion.totemguard.data;

import com.deathmotion.totemguard.util.datastructure.TotemData;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class TotemPlayer {
    @Getter
    private final TotemData totemData = new TotemData();
    @Setter
    private UUID uuid;
    @Setter
    private String username;
    @Setter
    private String clientBrandName;
    @Setter
    private ClientVersion clientVersion;
    @Setter
    private boolean bedrockPlayer;
}
