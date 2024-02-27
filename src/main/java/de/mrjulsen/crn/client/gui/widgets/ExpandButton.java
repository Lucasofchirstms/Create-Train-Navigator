package de.mrjulsen.crn.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import de.mrjulsen.crn.ModMain;
import de.mrjulsen.mcdragonlib.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;

public class ExpandButton extends Button {

    public static final int WIDTH = 200;
    public static final int HEIGHT = 48;
    
    private final Font font;

    // Data
    private boolean expanded;

    private static final String expandText = "▼ " + Utils.translate("gui." + ModMain.MOD_ID + ".common.expand").getString();
    private static final String collapseText = "▲ " + Utils.translate("gui." + ModMain.MOD_ID + ".common.collapse").getString();
    

    public ExpandButton(Font font, int pX, int pY, boolean initialState, OnPress pOnPress) {
        super(pX, pY, 20, 20, Utils.emptyText(), pOnPress);
        this.font = font;
        this.expanded = initialState;
        
        int w1 = font.width(expandText) + 10;
        int w2 = font.width(collapseText) + 10;
        int h = font.lineHeight + 6;

        width = w1 > w2 ? w1 : w2;
        height = h;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isMouseOver(pMouseX, pMouseY)) {
            expanded = !expanded;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (isMouseOver(pMouseX, pMouseY)) {
            fill(pPoseStack, x, y, x + width, y + height, 0x1AFFFFFF);
            drawCenteredString(pPoseStack, font, expanded ? Utils.text(collapseText).withStyle(ChatFormatting.UNDERLINE).getVisualOrderText() : Utils.text(expandText).withStyle(ChatFormatting.UNDERLINE).getVisualOrderText(), x + width / 2, y + height / 2 - font.lineHeight / 2, 0xFFFFFF);
        } else {            
            drawCenteredString(pPoseStack, font, expanded ? collapseText : expandText, x + width / 2, y + height / 2 - font.lineHeight / 2, 0xFFFFFF);
        }        
    }

    public boolean isExpanded() {
        return expanded;
    }
    
}
