/**
 * Copyright (C) 2026 aoqia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see https://www.gnu.org/licenses/.
 *
 * This license and file are additionally subjected to the exceptions described in LICENSE_EXCEPTION.md.
 *
 * The exceptions permit independent software to use this project as a dependency under terms of its own choosing, but
 * does not permit the copying or incorporation of covered source code into another work outside the terms of the AGPL.
 */

package dev.aoqia.leaf.mods.practical_optimisation.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.aoqia.leaf.mods.practical_optimisation.Main;

import zombie.debug.DebugType;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.worldgen.WorldGenChunk;

@Mixin(WorldGenChunk.class)
public class WorldGenChunkMixin {
    /**
     * @author aoqia
     * @reason Wasteful use of string matching via regex instead of using string ops.
     */
    @Overwrite
    public void cleanChunk(IsoChunk chunk, String material, String filter) {
        if (Main.DEVELOPMENT && (material.contains(".") || material.contains("?"))) {
            DebugType.Mod.warn("cleanChunk called with unexpected regex: (" + material + ")");
        }

        int cleaned = 0;
        material = material.replace("*", "");

        List<IsoObject> toRemove = new ArrayList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                IsoGridSquare square = chunk.getGridSquare(x, y, 0);
                if (square != null) {
                    IsoObject floor = square.getFloor();
                    if (floor != null) {
                        String floorMaterial = floor.getSprite().getProperties().get("FloorMaterial");
                        if (floorMaterial != null && floorMaterial.startsWith(material)) {
                            for (int i = 0; i < square.getObjects().size(); i++) {
                                IsoObject object = square.getObjects().get(i);
                                if (object.getSprite().getName().startsWith(filter)) {
                                    toRemove.add(object);
                                    cleaned++;
                                }
                            }
                        }

                        for (IsoObject object : toRemove) {
                            square.DeleteTileObject(object);
                        }
                    }
                }
            }
        }

        if (cleaned > 0) {
            DebugType.WorldGen.debugln("%s | %s > Cleaned %s tiles", material, filter, cleaned);
        }
    }

    /**
     * @author aoqia
     * @reason Uses a ton of memory by doing regex matching instead of basic string ops.
     */
    @Overwrite
    public boolean isProtected(final List<String> protectedTiles, final String tile) {
        for (String protectedTile : protectedTiles) {
            if (Main.DEVELOPMENT && (protectedTile.contains(".") || protectedTile.contains("?"))) {
                DebugType.Mod.warn("isProtected called with unexpected regex: (" + protectedTile + ")");
            }

            String protectedTileSanitized = protectedTile.replace("*", "");
            if (tile.startsWith(protectedTileSanitized)) {
                return true;
            }
        }

        return false;
    }
}
