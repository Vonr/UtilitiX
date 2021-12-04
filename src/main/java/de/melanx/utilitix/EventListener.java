package de.melanx.utilitix;

import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import de.melanx.utilitix.network.StickyChunkRequestSerializer;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import java.util.Set;

public class EventListener {

    private static final MutableComponent BLACKLISTED_MOB = new TranslatableComponent("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").withStyle(ChatFormatting.DARK_RED);
    private static final MutableComponent GILDED = new TranslatableComponent("tooltip.utilitix.gilded").withStyle(ChatFormatting.GOLD);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getPlayer();

        if (player.isShiftKeyDown() && player.getItemInHand(event.getHand()).getItem() == ModItems.mobBell && event.getTarget() instanceof LivingEntity target) {
            InteractionHand hand = event.getHand();
            ItemStack stack = player.getItemInHand(hand);
            ResourceLocation entityKey = EntityType.getKey(target.getType());
            if (entityKey.toString().equals(stack.getOrCreateTag().getString("Entity"))) {
                return;
            }

            if (!UtilitiXConfig.HandBells.mobBellEntities.test(entityKey)) {
                player.displayClientMessage(BLACKLISTED_MOB, true);
                return;
            }

            stack.getOrCreateTag().putString("Entity", entityKey.toString());
            player.setItemInHand(hand, stack);
            player.displayClientMessage(ItemMobBell.getCurrentMob(target.getType()), true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    // TODO wait for https://github.com/MinecraftForge/MinecraftForge/pull/7715
//    @SubscribeEvent
//    public void onBowFindAmmo(PlayerFindProjectileEvent event) {
//        if (event.getFoundAmmo().isEmpty()) {
//            PlayerEntity player = event.getPlayer();
//            Stream.concat(Stream.of(player.getHeldItemOffhand()), player.inventory.mainInventory.stream())
//                    .filter(stack -> stack.getItem() == ModItems.quiver)
//                    .filter(stack -> !Quiver.isEmpty(stack))
//                    .findFirst()
//                    .ifPresent(stack -> {
//                        IItemHandlerModifiable inventory = Quiver.getInventory(stack);
//                        assert inventory != null;
//                        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
//                        if (enchantmentLevel >= 1) {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    event.setAmmo(arrow.copy());
//                                    return;
//                                }
//                            }
//                        } else {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    arrow = player.isCreative() ? arrow.copy() : arrow;
//                                    event.setAmmo(arrow);
//                                    return;
//                                }
//                            }
//                        }
//                    });
//        }
//    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof ArmorStand && event.getTarget().getPersistentData().getBoolean("UtilitiXArmorStand")) {
            if (event.getItemStack().getItem() == Items.FLINT && event.getPlayer().isShiftKeyDown()) {
                ArmorStand entity = (ArmorStand) event.getTarget();
                if (UtilitiXConfig.armorStandPoses.size() >= 2) {
                    int newIdx = (entity.getPersistentData().getInt("UtilitiXPoseIdx") + 1) % UtilitiXConfig.armorStandPoses.size();
                    entity.getPersistentData().putInt("UtilitiXPoseIdx", newIdx);
                    UtilitiXConfig.armorStandPoses.get(newIdx).apply(entity);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void loadChunk(ChunkEvent.Load event) {
        if (event.getWorld().isClientSide()) {
            UtilitiX.getNetwork().channel.sendToServer(new StickyChunkRequestSerializer.StickyChunkRequestMessage(event.getChunk().getPos()));
        }
    }

    @SubscribeEvent
    public void neighbourChange(BlockEvent.NeighborNotifyEvent event) {
        if (!event.getWorld().isClientSide() && event.getWorld() instanceof Level level) {
            for (Direction dir : Direction.values()) {
                BlockPos thePos = event.getPos().relative(dir);
                BlockState state = level.getBlockState(thePos);
                if (state.getBlock() == Blocks.MOVING_PISTON && (state.getValue(BlockStateProperties.FACING) == dir || state.getValue(BlockStateProperties.FACING) == dir.getOpposite())) {
                    // Block has been changed because of a piston move.
                    // Glue logic is handled in the piston til
                    // Skip this here
                    return;
                } else if (state.getBlock() == Blocks.PISTON_HEAD && state.getValue(BlockStateProperties.SHORT) && (state.getValue(BlockStateProperties.FACING) == dir || state.getValue(BlockStateProperties.FACING) == dir.getOpposite())) {
                    // Block has been changed because of a piston move.
                    // Glue logic is handled in the piston til
                    // Skip this here
                    // This is sometimes buggy but we can't really do anything about this.
                    return;
                }
            }
            LevelChunk chunk = level.getChunkAt(event.getPos());
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = event.getPos().getX() & 0xF;
                int y = event.getPos().getY();
                int z = event.getPos().getZ() & 0xF;
                for (Direction dir : Direction.values()) {
                    if (glue.get(x, y, z, dir) && !SlimyCapability.canGlue(level, event.getPos(), dir)) {
                        glue.set(x, y, z, dir, false);
                        chunk.setUnsaved(true);
                        BlockPos targetPos = event.getPos().relative(dir);
                        ItemEntity ie = new ItemEntity(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(ModItems.glueBall));
                        ie.setPickUpDelay(20);
                        level.addFreshEntity(ie);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onItemDespawn(ItemExpireEvent event) {
        ItemEntity entity = event.getEntityItem();
        Level level = entity.getCommandSenderWorld();
        if (!level.isClientSide) {
            BlockPos pos = entity.blockPosition();
            ItemStack stack = entity.getItem();
            if (stack.getItem() instanceof BlockItem item && (item.getBlock() instanceof CropBlock || item.getBlock() instanceof SaplingBlock)) {
                if (!UtilitiXConfig.plantsOnDespawn.test(item.getRegistryName())) {
                    return;
                }

                try {
                    DirectionalPlaceContext context = new DirectionalPlaceContext(level, pos, Direction.DOWN, stack, Direction.UP);
                    if (item.place(context) == InteractionResult.SUCCESS) {
                        level.setBlockAndUpdate(pos, item.getBlock().defaultBlockState());
                        return;
                    }

                    context = new DirectionalPlaceContext(level, pos.above(), Direction.DOWN, stack, Direction.UP);
                    if (item.place(context) == InteractionResult.SUCCESS) {
                        level.setBlockAndUpdate(pos.above(), item.getBlock().defaultBlockState());
                    }
                } catch (NullPointerException e) {
                    UtilitiX.getInstance().logger.warn("Tried to place {} but was prevented.", item);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseItem() == Event.Result.ALLOW || event.getUseBlock() == Event.Result.DENY) {
            return;
        }

        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock) && !BlockTags.DOORS.contains(state.getBlock()) || state.getBlock().material == Material.METAL) {
            return;
        }

        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean open = state.getValue(DoorBlock.OPEN);

        BlockPos neighborPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());

        BlockState neighborState = level.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof DoorBlock) && !BlockTags.DOORS.contains(neighborState.getBlock()) || neighborState.getBlock().material == Material.METAL) {
            return;
        }

        if (neighborState.getValue(DoorBlock.HALF) == half && neighborState.getValue(DoorBlock.HINGE) != hinge && neighborState.getValue(DoorBlock.FACING) == facing) {
            ((DoorBlock) neighborState.getBlock()).setOpen(event.getPlayer(), level, neighborState, neighborPos, !open);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (GildingArmorRecipe.isGilded(stack)) {
            event.getToolTip().add(2, GILDED);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplosionStart(ExplosionEvent.Start event) {
        if (event.isCanceled()) {
            return;
        }

        Explosion explosion = event.getExplosion();

        if (explosion.getExploder() instanceof Creeper creeper) {
            float health = creeper.getHealth();
            float maxHealth = creeper.getMaxHealth();

            explosion.radius = explosion.radius * (health / maxHealth);
        }
    }

    private static final Set<ResourceLocation> AIOTBOTANIA_FLATTEN_ALLOWED = Set.of(
            new ResourceLocation("aiotbotania", "livingwood_aiot"),
            new ResourceLocation("aiotbotania", "livingrock_aiot"),
            new ResourceLocation("aiotbotania", "manasteel_aiot"),
            new ResourceLocation("aiotbotania", "elementium_aiot"),
            new ResourceLocation("aiotbotania", "terra_aiot"),
            new ResourceLocation("aiotbotania", "alfsteel_aiot")
    );

    @SubscribeEvent
    public void onBlockToolInteraction(BlockEvent.BlockToolInteractEvent event) {
        if (event.getToolAction() == ToolActions.SHOVEL_FLATTEN && event.getPlayer().isCrouching()
                && (!ModList.get().isLoaded("aiotbotania") || !AIOTBOTANIA_FLATTEN_ALLOWED.contains(event.getHeldItemStack().getItem().getRegistryName()))) {
            event.setCanceled(true);
        }
    }
}
