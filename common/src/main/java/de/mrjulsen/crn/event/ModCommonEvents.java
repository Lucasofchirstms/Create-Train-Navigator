package de.mrjulsen.crn.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.display.AdvancedDisplayTarget;
import de.mrjulsen.crn.cmd.DebugCommand;
import de.mrjulsen.crn.config.ModCommonConfig;
import de.mrjulsen.crn.data.storage.GlobalSettings;
import de.mrjulsen.crn.data.train.TrainData;
import de.mrjulsen.crn.data.train.TrainListener;
import de.mrjulsen.crn.event.events.CreateTrainPredictionEvent;
import de.mrjulsen.crn.event.events.GlobalTrainDisplayDataRefreshEventPost;
import de.mrjulsen.crn.event.events.GlobalTrainDisplayDataRefreshEventPre;
import de.mrjulsen.crn.event.events.ScheduleResetEvent;
import de.mrjulsen.crn.event.events.StartWebserverEvent;
import de.mrjulsen.crn.event.events.SubmitTrainPredictionsEvent;
import de.mrjulsen.crn.event.events.TotalDurationTimeChangedEvent;
import de.mrjulsen.crn.event.events.TrainArrivalAndDepartureEvent;
import de.mrjulsen.crn.event.events.TrainDestinationChangedEvent;
import de.mrjulsen.crn.registry.ModExtras;
import de.mrjulsen.crn.util.ModUtils;
import de.mrjulsen.crn.web.DLRestServer;
import de.mrjulsen.crn.web.WebsitePreparableReloadListener;
import de.mrjulsen.crn.web.DLRestServer.DLRestManager;
import de.mrjulsen.crn.web.handlers.PingHandler;
import de.mrjulsen.mcdragonlib.internal.ClientWrapper;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;

public class ModCommonEvents {

    private static long lastTicks = 0;
    private static MinecraftServer currentServer;


    public static void init() {
        
        ReloadListenerRegistry.register(PackType.SERVER_DATA, ModUtils.setWebsiteResourceManager(new WebsitePreparableReloadListener()));
        
        LifecycleEvent.SETUP.register(() -> {
            CreateRailwaysNavigator.LOGGER.info("Welcome to the CREATE RAILWAYS NAVIGATOR mod by MRJULSEN.");
        });

        LifecycleEvent.SERVER_LEVEL_LOAD.register((level) -> {
            ModExtras.init();
        });        

        LifecycleEvent.SERVER_STARTED.register((server) -> {
            currentServer = server;
            // Register Events
            CRNEventsManager.registerEvent(GlobalTrainDisplayDataRefreshEventPost::new);
            CRNEventsManager.registerEvent(GlobalTrainDisplayDataRefreshEventPre::new);
            CRNEventsManager.registerEvent(TrainDestinationChangedEvent::new);
            CRNEventsManager.registerEvent(TrainArrivalAndDepartureEvent::new);
            CRNEventsManager.registerEvent(SubmitTrainPredictionsEvent::new);
            CRNEventsManager.registerEvent(CreateTrainPredictionEvent::new);
            CRNEventsManager.registerEvent(ScheduleResetEvent::new);
            CRNEventsManager.registerEvent(TotalDurationTimeChangedEvent::new);
            CRNEventsManager.registerEvent(StartWebserverEvent::new);

            CRNEventsManager.getEvent(CRNCommonEventsRegistryEvent.class).run();

            TrainListener.start();
            AdvancedDisplayTarget.start();

            CRNEventsManager.getEventOptional(StartWebserverEvent.class).ifPresent(x -> x.register(CreateRailwaysNavigator.MOD_ID, () -> List.of(
                new PingHandler()
            )));

            if (ModCommonConfig.USE_WEBSERVER.get()) {
                List<DLRestManager> handlers = new ArrayList<>();
                if (CRNEventsManager.isRegistered(StartWebserverEvent.class)) {
                    handlers.addAll(CRNEventsManager.getEvent(StartWebserverEvent.class).run());
                }
                DLRestServer.start(ModCommonConfig.PORT.get(), handlers);
            }
        });

        LifecycleEvent.SERVER_STOPPING.register((server) -> {
            DLRestServer.stop();
            GlobalSettings.clearInstance();
            
            TrainListener.stop();
            AdvancedDisplayTarget.stop();
            CRNEventsManager.clearEvents();
        });

        LifecycleEvent.SERVER_STOPPED.register((server) -> {
            currentServer = null;
        });

        TickEvent.SERVER_POST.register((server) -> {
            if (ModCommonEvents.hasServer()) {
                long currentTicks = ModCommonEvents.getPhysicalLevel().dayTime();
                long diff = currentTicks - lastTicks;
                if (Math.abs(diff) > 1) {
                    for (TrainData data : TrainListener.data.values()) {
                        data.shiftTime(diff);
                    }
                    if (ModCommonConfig.ADVANCED_LOGGING.get()) CreateRailwaysNavigator.LOGGER.info("All times have been corrected: " + (diff) + " Ticks");
                }
                lastTicks = currentTicks;
            }

            TrainListener.tick();
        });

        CommandRegistrationEvent.EVENT.register((dispatcher, selection) -> {
            DebugCommand.register(dispatcher, selection);
        });

        LifecycleEvent.SERVER_LEVEL_SAVE.register((server) -> {
            if (!getCurrentServer().isPresent()) return;
            if (server != getCurrentServer().get().overworld()) return;
            
            TrainListener.save();
            if (GlobalSettings.hasInstance()) GlobalSettings.getInstance().save();
        });
    }

    public static boolean hasServer() {
        return currentServer != null;
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(currentServer);
    }

    public static Level getPhysicalLevel() {
        return hasServer() ? getCurrentServer().get().overworld() : ClientWrapper.getClientLevel();
    }
}


