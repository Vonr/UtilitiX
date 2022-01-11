package de.melanx.utilitix.block;

import io.github.noeppi_noeppi.libx.base.BlockBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class DimmableRedstoneLamp extends BlockBase {
    public static final IntegerProperty SIGNAL = BlockStateProperties.POWER;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(SIGNAL);

    public DimmableRedstoneLamp(ModX mod, Properties properties) {
        super(mod, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SIGNAL, 0));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SIGNAL, context.getLevel().getSignal(context.getClickedPos(), context.getClickedFace()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            this.updatePowerStrength(state, level, pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide) {
            this.updatePowerStrength(state, level, pos);
        }
    }

    private void updatePowerStrength(BlockState state, Level level, BlockPos pos) {
        int signal = level.getBestNeighborSignal(pos);
        if (state.getValue(SIGNAL) != signal) {
            level.setBlock(pos, state.setValue(SIGNAL, signal), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL);
    }
}