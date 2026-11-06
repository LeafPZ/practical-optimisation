package dev.aoqia.leaf.mods.practical_optimisation.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.aoqia.leaf.mods.practical_optimisation.Main;

import zombie.debug.DebugType;
import zombie.iso.worldgen.WorldGenUtils;

@Mixin(WorldGenUtils.class)
public class WorldGenUtilsMixin {
    /**
     * @author aoqia
     * @reason Uses a lot of memory because of regex.
     */
    @Overwrite
    public boolean canPlace(List<String> placement, String floorName) {
        boolean toBePlaced = false;

        for (String floorCheck : placement) {
            boolean check = true;
            if (floorCheck.startsWith("!")) {
                check = false;
                floorCheck = floorCheck.substring(1);
            }

            if (Main.DEVELOPMENT && (floorCheck.contains(".") || floorCheck.contains("?"))) {
                DebugType.Mod.warn("canPlace called with a tile containing regex (" + floorCheck + ")");
            }

            if (floorName.startsWith(floorCheck.replace("*", ""))) {
                toBePlaced = check;
            }
        }

        return toBePlaced;
    }
}
