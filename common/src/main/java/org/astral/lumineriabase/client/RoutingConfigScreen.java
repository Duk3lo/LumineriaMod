package org.astral.lumineriabase.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.astral.lumineriabase.platform.Services;
import org.jetbrains.annotations.NotNull;

public class RoutingConfigScreen extends Screen {
    private final Screen previousScreen;
    private EditBox keyInputField;

    public RoutingConfigScreen(Screen previousScreen) {
        super(Component.literal("Configuración de Enrutamiento"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.keyInputField = new EditBox(this.font, centerX - 100, centerY - 20, 200, 20, Component.literal("Routing Key"));
        this.keyInputField.setValue(Services.PLATFORM.getCurrentRoutingKey());
        this.addRenderableWidget(this.keyInputField);

        this.addRenderableWidget(Button.builder(Component.literal("§aAceptar"), b -> {
            Services.PLATFORM.saveRoutingKey(this.keyInputField.getValue().trim());
            this.onClose();
        }).bounds(centerX - 105, centerY + 20, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("§cCancelar"), b -> this.onClose())
                .bounds(centerX + 5, centerY + 20, 100, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        Services.PLATFORM.renderBackground(graphics, mouseX, mouseY, partial);
        graphics.drawCenteredString(this.font, "§lCONFIGURAR RUTA", this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partial);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.previousScreen);
        }
    }
}