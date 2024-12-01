package de.mrjulsen.crn.client.ber.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.blockentity.AdvancedDisplayBlockEntity;
import de.mrjulsen.crn.block.blockentity.AdvancedDisplayBlockEntity.EUpdateReason;
import de.mrjulsen.crn.block.display.AdvancedDisplaySource.ETimeDisplay;
import de.mrjulsen.crn.client.ber.AdvancedDisplayRenderInstance;
import de.mrjulsen.crn.client.ber.IBERRenderSubtype;
import de.mrjulsen.crn.client.lang.ELanguage;
import de.mrjulsen.crn.config.ModClientConfig;
import de.mrjulsen.crn.data.train.TrainStatus.CompiledTrainStatus;
import de.mrjulsen.crn.data.train.portable.StationDisplayData;
import de.mrjulsen.crn.util.ModUtils;
import de.mrjulsen.mcdragonlib.DragonLib;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.BERLabel;
import de.mrjulsen.mcdragonlib.client.ber.BERLabel.BoundsHitReaction;
import de.mrjulsen.mcdragonlib.client.util.BERUtils;
import de.mrjulsen.mcdragonlib.util.DLUtils;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import de.mrjulsen.mcdragonlib.util.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BERDepartureBoardTable implements IBERRenderSubtype<AdvancedDisplayBlockEntity, AdvancedDisplayRenderInstance, Boolean> {

    private static final String keyDeparture = "gui.createrailwaysnavigator.departure";
    private static final String keyTrain = "gui.createrailwaysnavigator.line";
    private static final String keyDestination = "gui.createrailwaysnavigator.destination";
    private static final String keyPlatform = "gui.createrailwaysnavigator.platform";
    private static final String keyVia = "gui.createrailwaysnavigator.via";
    private static final String keyTooSmall = "gui.createrailwaysnavigator.too_small";

    private static final int MIN_SIZE = 4;
    private static final float LINE_HEIGHT = 5.4f;    
    private static final float Y_OFFSET = LINE_HEIGHT; // Headline    
    private static final float SPACING = 2;
    private static final float TIME_LABEL_MAX_WIDTH = 12;
    private static final float REAL_TIME_LABEL_MAX_WIDTH = 12;
    private static final float TRAIN_LINE_LABEL_MAX_WIDTH = 16;
    private static final byte PLATFORM_LABEL_MAX_WIDTH = 16;

    private boolean showInfoLine = false;
    private MutableComponent infoLineText = TextUtils.empty();
    private int maxLines = 0;
    
    private final BERLabel statusLabel = new BERLabel(TextUtils.empty())
        .setCentered(true)
        .setScale(0.4f, 0.4f)
        .setYScale(0.4f)
        .setColor(0xFF111111)
        .setBackground(0xFFFFFFFF, true)
        .setScrollingSpeed(2)
    ;        
    private final BERLabel tooSmallLabel = new BERLabel(TextUtils.translate(keyTooSmall)) // TODO
        .setCentered(false)
        .setScale(0.4f, 0.4f)
        .setYScale(0.4f)
        .setPos(3, 3)
    ;
    private BERLabel[][] lines = new BERLabel[0][];

    private final BERLabel[] headlines;
    {
        headlines = new BERLabel[LineComponent.values().length];

        headlines[LineComponent.TIME.i()] = new BERLabel()
            .setText(ELanguage.translate(keyDeparture).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC))
            .setYScale(0.4f)
            .setMaxWidth(12.5f, BoundsHitReaction.SCALE_SCROLL)
            .setScale(0.4f, 0.2f)
            .setPos(0, 3)
            .setMaxWidth(0, BoundsHitReaction.CUT_OFF)
        ;
        headlines[LineComponent.TRAIN_NAME.i()] = new BERLabel()
            .setText(ELanguage.translate(keyTrain).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC))
            .setYScale(0.4f)
            .setScrollingSpeed(2)
            .setMaxWidth(14, BoundsHitReaction.SCALE_SCROLL)
            .setScale(0.4f, 0.2f)
            .setPos(0, 3)
            .setMaxWidth(0, BoundsHitReaction.CUT_OFF)
        ;
        
        headlines[LineComponent.PLATFORM.i()] = new BERLabel()
            .setText(ELanguage.translate(keyPlatform).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC))
            .setYScale(0.4f)
            .setScale(0.4f, 0.2f)
            .setPos(0, 3)
            .setMaxWidth(0, BoundsHitReaction.CUT_OFF)
        ;

        headlines[LineComponent.DESTINATION.i()] = new BERLabel()
            .setText(ELanguage.translate(keyDestination).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC))
            .setYScale(0.4f)
            .setScrollingSpeed(2)
            .setScale(0.4f, 0.2f)
            .setPos(0, 3)
            .setMaxWidth(0, BoundsHitReaction.CUT_OFF)
        ;
        headlines[LineComponent.STOPOVERS.i()] = new BERLabel()
            .setText(ELanguage.translate(keyVia).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC))
            .setYScale(0.4f)
            .setScrollingSpeed(2)
            .setScale(0.4f, 0.2f)
            .setPos(0, 3)
            .setMaxWidth(0, BoundsHitReaction.CUT_OFF)
        ;
        headlines[LineComponent.INFO.i()] = new BERLabel()
            .setText(TextUtils.empty())
            .setPos(0, 3)
            .setMaxWidth(0, BoundsHitReaction.CUT_OFF)
        ;
    }

    @Override
    public void renderTick(float deltaTime) {
        statusLabel.renderTick();
        DLUtils.doIfNotNull(lines, x -> {
            for (int i = 0; i < x.length; i++) {
                BERLabel[] line = x[i];
                if (line == null) continue;
                for (int k = 0; k < line.length; k++) {                    
                    DLUtils.doIfNotNull(line[k], y -> y.renderTick());
                }
            }
        });
        for (int k = 0; k < headlines.length; k++) {                    
            DLUtils.doIfNotNull(headlines[k], y -> y.renderTick());
        }
    }
    
    @Override
    public void render(BERGraphics<AdvancedDisplayBlockEntity> graphics, float pPartialTicks, AdvancedDisplayRenderInstance parent, int light, boolean backSide) {

        BERUtils.fillColor(graphics, 2, 1.5f + LINE_HEIGHT, 0.0f, graphics.blockEntity().getXSizeScaled() * 16 - 4, 0.25f, (0xFF << 24) | (graphics.blockEntity().getColor() & 0x00FFFFFF), graphics.blockEntity().getBlockState().getValue(HorizontalDirectionalBlock.FACING), light);
        if (graphics.blockEntity().getXSizeScaled() < MIN_SIZE) {
            tooSmallLabel.render(graphics, light);
            return;
        }

        for (int k = 0; k < headlines.length; k++) {
            DLUtils.doIfNotNull(headlines[k], y -> y.render(graphics, light));
        }

        for (int i = 0; i < lines.length && i < maxLines; i++) {
            graphics.poseStack().pushPose();
            if (i % 2 == 1) {
                BERUtils.fillColor(graphics, 2, 2 + Y_OFFSET + i * LINE_HEIGHT, 0, graphics.blockEntity().getXSizeScaled() * 16 - 4, LINE_HEIGHT, (0x40 << 24) | (graphics.blockEntity().getColor() & 0x00FFFFFF), graphics.blockEntity().getBlockState().getValue(HorizontalDirectionalBlock.FACING));
                graphics.poseStack().translate(0, 0, 0.05f);
            }
            for (int k = 0; k < lines[i].length; k++) {
                DLUtils.doIfNotNull(lines[i][k], x -> x.render(graphics, light));
            }
            graphics.poseStack().popPose();
        }

        if (showInfoLine) {
            statusLabel.render(graphics, light);
        }
    }

    private Collection<Component> getStatusInfo(AdvancedDisplayBlockEntity blockEntity, StationDisplayData data) {
        if (!data.getTrainData().hasStatusInfo() && !data.getStationData().isDepartureDelayed()) {
            return List.of();
        }
        Collection<Component> content = new ArrayList<>();
        if (data.getTrainData().isCancelled()) {
            content.add(ELanguage.translate("block." + CreateRailwaysNavigator.MOD_ID + ".advanced_display.ber.information_about_cancelled", data.getTrainData().getName()));
            return content;
        }
        String delay = blockEntity.getTimeDisplay() == ETimeDisplay.ETA ? ModUtils.timeRemainingString(data.getStationData().getDepartureTimeDeviation()) : String.valueOf(TimeUtils.formatToMinutes(data.getStationData().getDepartureTimeDeviation()));
        content.add(ELanguage.translate("block." + CreateRailwaysNavigator.MOD_ID + ".advanced_display.ber.information_about_delayed", data.getTrainData().getName(), delay));
        for (CompiledTrainStatus status : data.getTrainData().getStatus()) {
            content.add(status.text());
        }
        return content;
    }

    @Override
    public void update(Level level, BlockPos pos, BlockState state, AdvancedDisplayBlockEntity blockEntity, AdvancedDisplayRenderInstance parent, EUpdateReason reason) {
        List<StationDisplayData> preds = blockEntity.getStops().stream().filter(x -> x.getStationData().getScheduledArrivalTime() < DragonLib.getCurrentWorldTime() + ModClientConfig.DISPLAY_LEAD_TIME.get() && (!x.getTrainData().isCancelled() || DragonLib.getCurrentWorldTime() < x.getStationData().getScheduledDepartureTime() + ModClientConfig.DISPLAY_LEAD_TIME.get())).toList();
        
        showInfoLine = !preds.isEmpty() && preds.get(0).getStationData().isDepartureDelayed() && preds.get(0).getTrainData().hasStatusInfo();
        if (showInfoLine) {
            // Update status label
            this.infoLineText = TextUtils.concat(TextUtils.text("  +++  "), preds.stream().limit(maxLines).filter(x -> x.getTrainData().hasStatusInfo() && x.getStationData().isDepartureDelayed()).flatMap(x -> {
                return getStatusInfo(blockEntity, x).stream();
            }).toArray(Component[]::new));
        } else {
            infoLineText = TextUtils.empty();
        }

        int defaultMaxLines = blockEntity.getYSizeScaled() * 3 - 2;
        this.maxLines = defaultMaxLines - (showInfoLine ? 1 : 0);        
        int maxIndices = Math.max(0, Math.min(this.maxLines, preds.size()));
        if (reason == EUpdateReason.LAYOUT_CHANGED || this.lines == null || lines.length != maxIndices) {
            updateLayout(blockEntity, preds, maxIndices);
        }
            
        for (int i = 0; i < this.lines.length && i < preds.size(); i++) {
            StationDisplayData stop = preds.get(i);
            updateContent(blockEntity, stop, i, false, 0, 0);
        }

        statusLabel
            .setText(infoLineText)
            .setPos(3, blockEntity.getYSizeScaled() * 16 - 12 * statusLabel.getYScale() - 2)
            .setMaxWidth(blockEntity.getXSizeScaled() * 16 - 6, BoundsHitReaction.SCALE_SCROLL)
        ;
    }


    private void updateLayout(AdvancedDisplayBlockEntity blockEntity, List<StationDisplayData> preds, int maxIndices) {

        if (blockEntity.getXSizeScaled() < MIN_SIZE) {
            tooSmallLabel
                .setMaxWidth(blockEntity.getXSizeScaled() * 16 - 6, BoundsHitReaction.CUT_OFF)
                .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
            ;
            return;
        }

        boolean hasStopovers = blockEntity.getXSizeScaled() - 4 >= 4;
        boolean hasInfo = blockEntity.getXSizeScaled() - 4 >= 7;
        
        // Init headline
        BERLabel hTimeLabel = headlines[LineComponent.TIME.i()];
        hTimeLabel
            .setPos(3, hTimeLabel.getY())
            .setMaxWidth(TIME_LABEL_MAX_WIDTH + (!isSmall(blockEntity) ? REAL_TIME_LABEL_MAX_WIDTH : 0) + SPACING, BoundsHitReaction.CUT_OFF)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        BERLabel hTrainLabel = headlines[LineComponent.TRAIN_NAME.i()];
        hTrainLabel
            .setPos(hTimeLabel.getX() + hTimeLabel.getMaxWidth() + SPACING, hTrainLabel.getY())
            .setMaxWidth(TRAIN_LINE_LABEL_MAX_WIDTH, BoundsHitReaction.CUT_OFF)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;        
        BERLabel hPlatformLabel = headlines[LineComponent.PLATFORM.i()];
        hPlatformLabel
            .setPos(blockEntity.getXSizeScaled() * 16 - 3 - hPlatformLabel.getTextWidth(), hPlatformLabel.getY())
            .setMaxWidth(PLATFORM_LABEL_MAX_WIDTH, BoundsHitReaction.CUT_OFF)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        
        final float remainingSpace = blockEntity.getXSizeScaled() * 16 - 3 - hTrainLabel.getX() - hTrainLabel.getMaxWidth() - platformWidth(blockEntity) - SPACING;
        final float infoSpace = hasInfo ? remainingSpace / 4 : 0;
        final float sectionSize = hasStopovers ? (remainingSpace - infoSpace) / 2 : remainingSpace;
        final float sectionTextSize = sectionSize - SPACING;

        BERLabel hStopoversLabel = headlines[LineComponent.STOPOVERS.i()];
        hStopoversLabel
            .setPos(hasStopovers ? hTrainLabel.getX() + hTrainLabel.getMaxWidth() + SPACING : 0, hStopoversLabel.getY())
            .setMaxWidth(hasStopovers ? sectionTextSize : 0, BoundsHitReaction.CUT_OFF)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        BERLabel hDestinationLabel = headlines[LineComponent.DESTINATION.i()];
        hDestinationLabel
            .setPos(hasStopovers ? hStopoversLabel.getX() + hStopoversLabel.getMaxWidth() + SPACING : hTrainLabel.getX() + hTrainLabel.getMaxWidth() + SPACING, hDestinationLabel.getY())
            .setMaxWidth(Math.min(hDestinationLabel.getX() + (sectionTextSize), blockEntity.getXSizeScaled() * 16 - 3 - hPlatformLabel.getTextWidth() - SPACING) - hDestinationLabel.getX(), BoundsHitReaction.CUT_OFF)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;        
        BERLabel hInfoLabel = headlines[LineComponent.INFO.i()];
        hInfoLabel
            .setPos(hasInfo ? hDestinationLabel.getX() + hDestinationLabel.getMaxWidth() + SPACING : 0, hDestinationLabel.getY())
            .setMaxWidth(hasInfo ? Math.min(hInfoLabel.getX() + (infoSpace - SPACING), blockEntity.getXSizeScaled() * 16 - 3 - hInfoLabel.getX() - hPlatformLabel.getTextWidth() - SPACING) - hInfoLabel.getX() : 0, BoundsHitReaction.CUT_OFF)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        


        this.lines = new BERLabel[maxIndices][];
        for (int i = 0; i < this.lines.length; i++) {
            StationDisplayData stop = preds.get(i);
            this.lines[i] = createLine(blockEntity, stop, i, hasStopovers, hasInfo);
            updateContent(blockEntity, stop, i, true, sectionTextSize, infoSpace - SPACING);
        }
        statusLabel
            .setBackground((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF), true)
        ;

    }

    private void updateContent(AdvancedDisplayBlockEntity blockEntity, StationDisplayData stop, int index, boolean layoutUpdate, float sectionTextSize, float infoLineWidth) {
        boolean isLast = stop.isLastStop();
        BERLabel[] components = lines[index];

        BERLabel timeLabel = components[LineComponent.TIME.i()]
            .setText(TextUtils.text(ModUtils.formatTime(stop.getScheduledTime(), blockEntity.getTimeDisplay() == ETimeDisplay.ETA)))
        ;
        BERLabel realTimeLabel = components[LineComponent.REAL_TIME.i()]
            .setText(isSmall(blockEntity) ? 
                TextUtils.text(ModUtils.formatTime(stop.getScheduledTime(), blockEntity.getTimeDisplay() == ETimeDisplay.ETA)) :
                TextUtils.text(stop.getTrainData().isCancelled() ?
                    " \u274C " : // X
                    (stop.getStationData().isDepartureDelayed() ?
                        (ModUtils.formatTime(stop.getRealTime(), blockEntity.getTimeDisplay() == ETimeDisplay.ETA)) : 
                        ""))) // Nothing (not delayed)
        ;
        BERLabel trainLabel = components[LineComponent.TRAIN_NAME.i()]
            .setText(TextUtils.text(stop.getTrainData().getName()))
        ;
        BERLabel destinationLabel = components[LineComponent.DESTINATION.i()]
            .setText(isLast ?
                ELanguage.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".schedule_board.train_from", stop.getFirstStopName()) :
                TextUtils.text(stop.getStationData().getDestination()))
        ;
        BERLabel stopoversLabel;
        BERLabel infoLabel;
        boolean hasTransfers = (stopoversLabel = components[LineComponent.STOPOVERS.i()]) != null;
        boolean hasInfo = (infoLabel = components[LineComponent.INFO.i()]) != null;
        if (hasTransfers) {
            stopoversLabel
                .setText(isLast ?
                    TextUtils.empty() :
                    TextUtils.concat(TextUtils.text(" \u25CF "), stop.getStopovers().stream().map(a -> (Component)TextUtils.text(a)).toList())
                )
            ;
        }
        if (hasInfo) {
            infoLabel
                .setText(TextUtils.concat(TextUtils.text("  +++  "), getStatusInfo(blockEntity, stop)))
            ;
        }
        BERLabel platformLabel = components[LineComponent.PLATFORM.i()]
            .setText(blockEntity.isPlatformFixed() ?
                TextUtils.empty() :
                TextUtils.text(stop.getStationData().getStationInfo().platform()))
        ;


        if (layoutUpdate) {
            platformLabel
                .setMaxWidth(platformWidth(blockEntity), BoundsHitReaction.SCALE_SCROLL)
            ;        
            timeLabel
                .setPos(headlines[LineComponent.TIME.i()].getX(), Y_OFFSET + 3 + index * LINE_HEIGHT)
            ;            
            realTimeLabel
                .setPos(timeLabel.getX() + timeLabel.getMaxWidth() + SPACING, Y_OFFSET + 3 + index * LINE_HEIGHT)
            ;        
            trainLabel
                .setPos(headlines[LineComponent.TRAIN_NAME.i()].getX(), Y_OFFSET + 3 + index * LINE_HEIGHT)
            ;
            destinationLabel
                .setPos(headlines[LineComponent.DESTINATION.i()].getX(), Y_OFFSET + 3 + index * LINE_HEIGHT)
                .setMaxWidth(sectionTextSize, BoundsHitReaction.SCALE_SCROLL)
            ;
            platformLabel
                .setPos(blockEntity.getXSizeScaled() * 16 - 3 - platformLabel.getTextWidth(), Y_OFFSET + 3 + index * LINE_HEIGHT)
            ;
            if (hasTransfers) {                
                stopoversLabel
                    .setPos(hasTransfers ? headlines[LineComponent.STOPOVERS.i()].getX() : 0, Y_OFFSET + 3 + index * LINE_HEIGHT + 0.5f)
                    .setMaxWidth(hasTransfers ? sectionTextSize : 0, BoundsHitReaction.SCALE_SCROLL)
                ;
            }
            if (hasInfo) {
                infoLabel
                    .setPos(hasInfo ? headlines[LineComponent.INFO.i()].getX() : 0, Y_OFFSET + 3 + index * LINE_HEIGHT)
                    .setMaxWidth(hasInfo ? infoLineWidth : 0, BoundsHitReaction.SCALE_SCROLL)
                ;
            }
        }
    }    

    private BERLabel[] createLine(AdvancedDisplayBlockEntity blockEntity, StationDisplayData stop, int index, boolean withStopovers, boolean withInfo) {
        BERLabel[] components = new BERLabel[LineComponent.values().length];
        boolean isSmall = isSmall(blockEntity);

        components[LineComponent.TIME.i()] = new BERLabel()
            .setYScale(0.4f)
            .setMaxWidth(isSmall ? -2 : TIME_LABEL_MAX_WIDTH, BoundsHitReaction.SCALE_SCROLL)
            .setScale(0.4f, 0.2f)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        components[LineComponent.REAL_TIME.i()] = new BERLabel()
            .setYScale(0.4f)
            .setMaxWidth(REAL_TIME_LABEL_MAX_WIDTH, BoundsHitReaction.SCALE_SCROLL)
            .setScale(0.4f, 0.2f)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        if (!isSmall) {
            components[LineComponent.REAL_TIME.i()]
                .setBackground((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF), false)
                .setColor(0xFF111111)
            ;
        }
        components[LineComponent.TRAIN_NAME.i()] = new BERLabel()
            .setYScale(0.4f)
            .setScrollingSpeed(2)
            .setMaxWidth(TRAIN_LINE_LABEL_MAX_WIDTH, BoundsHitReaction.SCALE_SCROLL)
            .setScale(0.4f, 0.2f)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;        
        components[LineComponent.PLATFORM.i()] = new BERLabel()
            .setYScale(0.4f)
            .setScale(0.4f, 0.2f)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;
        components[LineComponent.DESTINATION.i()] = new BERLabel()
            .setYScale(0.4f)
            .setScrollingSpeed(2)
            .setScale(0.4f, 0.2f)
            .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
        ;

        if (withStopovers) {
            components[LineComponent.STOPOVERS.i()] = new BERLabel()
                .setYScale(0.3f)
                .setScrollingSpeed(2)
                .setScale(0.3f, 0.2f)
                .setColor((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF))
            ;
        }
        if (withInfo) {
            components[LineComponent.INFO.i()] = new BERLabel()
                .setYScale(0.4f)
                .setScrollingSpeed(2)
                .setScale(0.4f, 0.2f)
                .setColor(0xFF111111)
                .setBackground((0xFF << 24) | (blockEntity.getColor() & 0x00FFFFFF), true)
            ;
        }

        return components;
    }

    private static boolean isSmall(AdvancedDisplayBlockEntity blockEntity) {
        return blockEntity.getXSizeScaled() <= MIN_SIZE;
    }
    
    private static byte platformWidth(AdvancedDisplayBlockEntity blockEntity) {
        byte w = blockEntity.getPlatformWidth();
        return w < 0 ? PLATFORM_LABEL_MAX_WIDTH : w;
    }

    private static enum LineComponent {
        TIME(0),
        REAL_TIME(1),
        TRAIN_NAME(2),
        DESTINATION(3),
        PLATFORM(4),
        STOPOVERS(5),
        INFO(6);

        int index;
        LineComponent(int index) {
            this.index = index;
        }
        public int i() {
            return index;
        }
    }
}
