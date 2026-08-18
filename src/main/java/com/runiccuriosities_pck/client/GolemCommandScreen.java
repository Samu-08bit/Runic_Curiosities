package com.runiccuriosities_pck.client;

import com.runiccuriosities_pck.GolemCommandPacket;
import com.runiccuriosities_pck.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GolemCommandScreen extends Screen {
    private final int entityId;
    private final boolean isSitting;
    private final boolean isStaying;

    public GolemCommandScreen(int entityId, boolean isSitting, boolean isStaying) {
        super(Component.literal("Saviritium Golem Commands"));
        this.entityId = entityId;
        this.isSitting = isSitting;
        this.isStaying = isStaying;
    }

    @Override
    protected void init() {
        super.init();
        int btnWidth = 120;
        int btnHeight = 20;
        int spacing = 24;

        int startY = this.height / 2 - (spacing * 3) / 2;

        Button followBtn = Button.builder(Component.literal("Follow Me"), b -> {
            PacketHandler.INSTANCE.sendToServer(new GolemCommandPacket(this.entityId, 0));
            this.onClose();
        }).bounds(this.width / 2 - btnWidth / 2, startY, btnWidth, btnHeight).build();
        followBtn.active = !this.isSitting;
        this.addRenderableWidget(followBtn);

        Button stayBtn = Button.builder(Component.literal("Stay"), b -> {
            PacketHandler.INSTANCE.sendToServer(new GolemCommandPacket(this.entityId, 1));
            this.onClose();
        }).bounds(this.width / 2 - btnWidth / 2, startY + spacing, btnWidth, btnHeight).build();
        stayBtn.active = !this.isSitting;
        this.addRenderableWidget(stayBtn);

        this.addRenderableWidget(Button.builder(Component.literal("Sit / Stand Up"), b -> {
            PacketHandler.INSTANCE.sendToServer(new GolemCommandPacket(this.entityId, 2));
            this.onClose();
        }).bounds(this.width / 2 - btnWidth / 2, startY + spacing * 2, btnWidth, btnHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);

        int btnWidth = 120;
        int spacing = 24;
        int startY = this.height / 2 - (spacing * 3) / 2;
        int statusX = this.width / 2 + btnWidth / 2 + 10; // Posizione X per le scritte ON/OFF

        // Determiniamo logicamente quale stato è attivo
        boolean isFollowActive = !this.isSitting && !this.isStaying;
        boolean isStayActive = !this.isSitting && this.isStaying;
        boolean isSitActive = this.isSitting;

        // Disegna lo Status del Follow (ON Verde / OFF Rosso)
        if (isFollowActive) {
            guiGraphics.drawString(this.font, "ON", statusX, startY + 6, 0x00FF00, false);
        } else {
            guiGraphics.drawString(this.font, "OFF", statusX, startY + 6, 0xFF0000, false);
        }

        // Disegna lo Status dello Stay (ON Verde / OFF Rosso)
        if (isStayActive) {
            guiGraphics.drawString(this.font, "ON", statusX, startY + spacing + 6, 0x00FF00, false);
        } else {
            guiGraphics.drawString(this.font, "OFF", statusX, startY + spacing + 6, 0xFF0000, false);
        }

        // Disegna lo Status del Sit (ON Verde / OFF Rosso)
        if (isSitActive) {
            guiGraphics.drawString(this.font, "ON", statusX, startY + spacing * 2 + 6, 0x00FF00, false);
        } else {
            guiGraphics.drawString(this.font, "OFF", statusX, startY + spacing * 2 + 6, 0xFF0000, false);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}