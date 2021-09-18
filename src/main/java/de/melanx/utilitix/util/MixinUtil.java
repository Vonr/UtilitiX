package de.melanx.utilitix.util;

import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import de.melanx.utilitix.mixin.MixinPistonMovingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class MixinUtil {

    public static void afterSetBlockState(Level level, BlockPos pos, Byte glueData) {
        if (level != null && pos != null && glueData != null) {
            LevelChunk chunk = level.getChunkAt(pos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = pos.getX() & 0xF;
                int y = pos.getY();
                int z = pos.getZ() & 0xF;
                glue.setData(x, y, z, glueData);
                chunk.markUnsaved();
            }
        }
    }
}
