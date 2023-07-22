package dev.turtywurty.shulkercolours;

import dev.turtywurty.shulkercolours.network.PacketManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ShulkerColours.MODID)
public class ShulkerColours {
    public static final String MODID = "shulkercolours";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ShulkerColours() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketManager::init);
    }
}
