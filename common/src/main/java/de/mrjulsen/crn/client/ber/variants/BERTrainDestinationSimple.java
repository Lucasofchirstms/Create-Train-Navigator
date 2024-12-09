package de.mrjulsen.crn.client.ber.variants;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.blockentity.AdvancedDisplayBlockEntity;
import de.mrjulsen.crn.block.blockentity.AdvancedDisplayBlockEntity.EUpdateReason;
import de.mrjulsen.crn.block.display.properties.TrainDestinationCompactSettings;
import de.mrjulsen.crn.client.ber.AdvancedDisplayRenderInstance;
import de.mrjulsen.crn.client.lang.ELanguage;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.BERLabel;
import de.mrjulsen.mcdragonlib.client.ber.BERLabel.BoundsHitReaction;
import de.mrjulsen.mcdragonlib.util.DLUtils;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BERTrainDestinationSimple implements AbstractAdvancedDisplayRenderer<TrainDestinationCompactSettings> {

    
    private final BERLabel outOfServiceLabel = new BERLabel(ELanguage.translate("block." + CreateRailwaysNavigator.MOD_ID + ".advanced_display.ber.not_in_service"))
        .setPos(3, 6)
        .setScale(0.5f, 0.25f)
        .setYScale(0.5f)
        .setCentered(true)
        .setScrollingSpeed(2)
    ;

    private final BERLabel trainLineLabel = new BERLabel()
        .setScale(0.6f, 0.3f)
        .setYScale(0.8f)
    ;
    private final BERLabel destinationLabel = new BERLabel()
        .setScale(0.5f, 0.25f)
        .setYScale(0.5f)
        .setCentered(true)
        .setScrollingSpeed(2)
    ;


    @Override
    public void renderTick(float deltaTime) {
        trainLineLabel.renderTick();
        destinationLabel.renderTick();
        outOfServiceLabel.renderTick();
    }

    @Override
    public void render(BERGraphics<AdvancedDisplayBlockEntity> graphics, float partialTick, AdvancedDisplayRenderInstance parent, int light, boolean backSide) {
        if (graphics.blockEntity().getTrainData() == null || graphics.blockEntity().getTrainData().isEmpty()) {
            outOfServiceLabel.render(graphics, light);
            return;
        }
        DLUtils.doIfNotNull(trainLineLabel, x -> x.render(graphics, light));
        DLUtils.doIfNotNull(destinationLabel, x -> x.render(graphics, light));
    }

    @Override
    public void update(Level level, BlockPos pos, BlockState state, AdvancedDisplayBlockEntity blockEntity, AdvancedDisplayRenderInstance parent, EUpdateReason reason) {
        if (blockEntity.getTrainData() == null || blockEntity.getTrainData().isEmpty()) {
            outOfServiceLabel
                .setMaxWidth(blockEntity.getXSizeScaled() * 16 - 6, BoundsHitReaction.SCALE_SCROLL)
                .setColor((0xFF << 24) | (getDisplaySettings(blockEntity).getFontColor() & 0x00FFFFFF))
            ;
            return;
        }
        updateContent(blockEntity);        
    }

    private void updateContent(AdvancedDisplayBlockEntity blockEntity) {
        TrainDestinationCompactSettings settings = getDisplaySettings(blockEntity);
        int width = settings.getTrainNameWidth();

        trainLineLabel
            .setPos(3, 5)
            .setText(width == 0 ? TextUtils.empty() : TextUtils.text(blockEntity.getTrainData().getTrainData().getName()).withStyle(ChatFormatting.BOLD))
            .setColor((0xFF << 24) | (getDisplaySettings(blockEntity).getFontColor() & 0x00FFFFFF))
            .setMaxWidth(
                settings.isFullTrainNameWidth() ?
                    blockEntity.getXSizeScaled() * 16 - 6 :
                    (settings.isAutoTrainNameWidth() ?
                        12 :
                        Math.min(
                            getDisplaySettings(blockEntity).getTrainNameWidth(),
                            blockEntity.getXSizeScaled() * 16 - 6)                
                ), settings.isAutoTrainNameWidth() ? BoundsHitReaction.IGNORE : BoundsHitReaction.SCALE_SCROLL)
            .setCentered(settings.isFullTrainNameWidth())
        ;
        destinationLabel
            .setPos((settings.isAutoTrainNameWidth() ? trainLineLabel.getTextWidth() : width) + 5, 6)
            .setMaxWidth(blockEntity.getXSizeScaled() * 16 - destinationLabel.getX() - 3, BoundsHitReaction.SCALE_SCROLL)
            .setText(settings.isFullTrainNameWidth() ? TextUtils.empty() : TextUtils.text(blockEntity.getTrainData().getNextStop().isPresent() ? blockEntity.getTrainData().getNextStop().get().getDestination() : ""))
            .setColor((0xFF << 24) | (getDisplaySettings(blockEntity).getFontColor() & 0x00FFFFFF))
        ;
        
    }
}
