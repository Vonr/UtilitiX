package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.crudefurnace.BlockCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.TileCrudeFurnace;
import de.melanx.utilitix.content.wireless.BlockLinkedRepeater;
import de.melanx.utilitix.content.wireless.TileLinkedRepeater;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.resources.ResourceLocation;

@WailaPlugin
public class UtilJade implements IWailaPlugin {

    public static final ResourceLocation CRUDE_FURNACE = UtilitiX.getInstance().resource("crude_furnace");
    public static final ResourceLocation LINKED_REPEATER = UtilitiX.getInstance().resource("linked_repeater");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(CrudeFurnaceProvider.INSTANCE, TooltipPosition.BODY, BlockCrudeFurnace.class);
        registrar.registerBlockDataProvider(CrudeFurnaceProvider.INSTANCE, TileCrudeFurnace.class);
        registrar.addConfig(CRUDE_FURNACE, true);

        registrar.registerComponentProvider(LinkedRepeaterProvider.INSTANCE, TooltipPosition.BODY, BlockLinkedRepeater.class);
        registrar.registerBlockDataProvider(LinkedRepeaterProvider.INSTANCE, TileLinkedRepeater.class);
        registrar.addConfig(LINKED_REPEATER, true);
    }
}