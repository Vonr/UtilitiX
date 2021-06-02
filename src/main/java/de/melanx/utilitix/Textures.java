package de.melanx.utilitix;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

public class Textures {

    public static final ResourceLocation GRAY_BELL_TEXTURE = new ResourceLocation(UtilitiX.getInstance().modid, "special/gray_bell");
    public static final ResourceLocation GLUE_OVERLAY_TEXTURE = new ResourceLocation(UtilitiX.getInstance().modid, "special/glue_ball_overlay");
    
    public static void registerTextures(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
            event.addSprite(GRAY_BELL_TEXTURE);
            event.addSprite(GLUE_OVERLAY_TEXTURE);
        }
    }
}
