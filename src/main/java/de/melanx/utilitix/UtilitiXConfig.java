package de.melanx.utilitix;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.util.ArmorStandRotation;
import org.moddingx.libx.annotation.config.RegisterConfig;
import org.moddingx.libx.config.Config;
import org.moddingx.libx.config.Group;
import org.moddingx.libx.config.validate.FloatRange;
import org.moddingx.libx.config.validate.IntRange;
import org.moddingx.libx.util.data.ResourceList;

import java.util.List;

@RegisterConfig(value = "common")
public class UtilitiXConfig {

    @Group("Config values for the two bells, mob bell and hand bell")
    public static class HandBells {

        @Config("Entity denylist for mob bell")
        public static ResourceList mobBellEntities = ResourceList.DENY_LIST;

        @Config("The time in ticks how long you have to ring the hand bell to let the mobs glow")
        public static int ringTime = 40;

        @Config("The time in ticks how long a mob should glow")
        public static int glowTime = 60;

        @Config("The radius in which entities will glow")
        public static int glowRadius = 36;

        @Config("The radius in which entities get notified that you rung")
        public static int notifyRadius = 24;
    }

    @Group
    public static class Backpack {

        @Config("The initial size of a backpack")
        @IntRange(min = 1, max = 154)
        public static int slotSize = 9;

        @Config({"The maximum size of a backpack when merging with other backpacks",
                "Beyond this number, backpacks cannot be merged"})
        @IntRange(min = 1, max = 154)
        public static int maxSize = 54;
    }

    @Config({
            "A list of armor stand rotations for armor stands with arms.",
            "You can cycle through these with a piece of flint."
    })
    public static List<ArmorStandRotation> armorStandPoses = ImmutableList.of(
            ArmorStandRotation.defaultRotation(),
            ArmorStandRotation.create(3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -10.0f, 0.0f, -10.0f, -15.0f, 0.0f, 10.0f, 25.0f, 0.0f, -1.0f, -25.0f, 0.0f, 1.0f),
            ArmorStandRotation.create(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -20.0f, 0.0f, -10.0f, -85.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f),
            ArmorStandRotation.create(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -50.0f, 0.0f, 60.0f, -60.0f, -40.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f),
            ArmorStandRotation.create(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -10.0f, 0.0f, -110.0f, -15.0f, 0.0f, 110.0f, -1.0f, 0.0f, -15.0f, 1.0f, 0.0f, 15.0f),
            ArmorStandRotation.create(70.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -10.0f, 0.0f, 5.0f, -15.0f, 0.0f, -5.0f, 3.0f, 0.0f, -1.0f, 3.0f, 0.0f, 1.0f),
            ArmorStandRotation.create(0.0f, -35.0f, -5.0f, 0.0f, 0.0f, 0.0f, -10.0f, 0.0f, -10.0f, -15.0f, 0.0f, 10.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f),
            ArmorStandRotation.create(0.0f, 35.0f, 5.0f, 0.0f, 0.0f, 0.0f, -10.0f, 0.0f, -10.0f, -15.0f, 0.0f, 10.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f),
            ArmorStandRotation.create(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -10.0f, 0.0f, -10.0f, -40.0f, 0.0f, 55.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f)
    );

    @Config("Items in world which have mending collect xp orbs to get repaired")
    public static boolean betterMending = true;

    @Config("Illusioners will appear in raids")
    public static boolean illusionerInRaid = true;

    @Group("Config options for rails and minecarts")
    public static class Track {

        @Config("The maximum hardness of blocks, the stonecutter cart can mine.")
        @FloatRange(min = 0)
        public static float stonecutterMaxHardness = 5;
    }

    @Group("Config options for experience crystal")
    public static class ExperienceCrystal {

        @Config("Should the experience crystal pull xp orbs automatically?")
        public static boolean pullOrbs = true;

        @Config("Maximum experience which can be stored")
        @IntRange(min = 0)
        public static int maxXp = Integer.MAX_VALUE;
    }

    @Config("List of items which are allowed to be planted when despawn on correct soil")
    public static ResourceList plantsOnDespawn = ResourceList.DENY_LIST;

    @Config("Prevents waterlogging when holding the sneak key")
    public static boolean crouchNoWaterlog = true;

    @Config({"The time in ticks which will be added to the despawn delay of a wandering trader on each trade",
            "This way, the wandering trader remains in the world longer."})
    @IntRange(min = 0)
    public static int wanderingTraderExtraTime = 400;

    @Config("Entity denylist for mob yoinker")
    public static ResourceList mobYoinkerEntities = ResourceList.DENY_LIST;

    @Config({"Size scale for exporting maps", "1 = 128x128px", "2 = 256x256px", "3 = 384x384px", "And so on, you got the pattern I hope"})
    @IntRange(min = 1)
    public static int mapScale = 3;

    @Config("Both doors open at the same time if connected")
    public static boolean doubleDoor = true;
}
