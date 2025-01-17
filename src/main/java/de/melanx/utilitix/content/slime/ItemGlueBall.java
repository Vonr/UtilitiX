package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.compat.zeta.ZetaCompat;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fml.ModList;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemGlueBall extends ItemBase {

    public ItemGlueBall(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        LevelChunk chunk = context.getLevel().getChunkAt(context.getClickedPos());
        //noinspection ConstantConditions
        StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
        //noinspection ConstantConditions
        if (glue != null) {
            int x = context.getClickedPos().getX() & 0xF;
            int y = context.getClickedPos().getY();
            int z = context.getClickedPos().getZ() & 0xF;
            Direction face = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? context.getClickedFace().getOpposite() : context.getClickedFace();
            if (!glue.get(x, y, z, face) && SlimyCapability.canGlue(context.getLevel(), context.getClickedPos(), face)) {
                if (!context.getLevel().isClientSide) {
                    glue.set(x, y, z, face, true);
                    chunk.setUnsaved(true);
                    if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                    ((ServerLevel) context.getLevel()).sendParticles(ParticleTypes.ITEM_SLIME, context.getClickedPos().getX() + 0.5 + (0.55 * face.getStepX()), context.getClickedPos().getY() + 0.5 + (0.55 * face.getStepY()), context.getClickedPos().getZ() + 0.5 + (0.55 * face.getStepZ()), 10, 0, 0, 0, 0.1);
                }
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
            }
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag isAdvanced) {
        if (ModList.get().isLoaded("zeta")) {
            Component warning = ZetaCompat.warningForGlue();
            if (warning != null) {
                tooltips.add(warning);
            }
        }
    }
}
