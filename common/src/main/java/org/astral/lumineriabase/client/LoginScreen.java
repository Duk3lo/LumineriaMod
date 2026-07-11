package org.astral.lumineriabase.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.astral.lumineriabase.Constants;
import org.astral.lumineriabase.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class LoginScreen extends Screen {
    private static final ResourceLocation BG_IMAGE = ResourceLocation.tryParse(Constants.MODID + ":textures/gui/auth_bg.png");

    private final boolean isRegistered;
    private EditBox passwordField;
    private EditBox confirmField;
    private String error;
    private boolean showPassword = false;
    private final Consumer<String> onLoginAttempt;

    public LoginScreen(boolean isRegistered, String message, Consumer<String> onLoginAttempt) {
        super(Component.literal("Autenticación"));
        this.isRegistered = isRegistered;
        this.error = message != null ? message : "";
        this.onLoginAttempt = onLoginAttempt;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.passwordField = new EditBox(this.font, centerX - 100, centerY - 20, 175, 20, Component.literal("Pass"));
        this.passwordField.setMaxLength(32);
        this.addRenderableWidget(this.passwordField);

        if (!isRegistered) {
            this.confirmField = new EditBox(this.font, centerX - 100, centerY + 15, 175, 20, Component.literal("Confirm"));
            this.confirmField.setMaxLength(32);
            this.addRenderableWidget(this.confirmField);
        }

        this.addRenderableWidget(Button.builder(Component.literal("👁"), b -> {
            showPassword = !showPassword;
            updatePasswordVisibility();
        }).bounds(centerX + 80, centerY - 20, 20, 20).build());

        updatePasswordVisibility();

        int btnY = isRegistered ? centerY + 15 : centerY + 45;
        this.addRenderableWidget(Button.builder(Component.literal(isRegistered ? "Iniciar Sesión" : "Registrarse"), b -> {
            String p = passwordField.getValue();
            if (p.isEmpty()) {
                error = "La contraseña no puede estar vacía.";
                return;
            }
            if (!isRegistered && !p.equals(confirmField.getValue())) {
                error = "Las contraseñas no coinciden.";
                return;
            }
            onLoginAttempt.accept(p);

            ClientActionExecutor.clearPendingLogin();
            this.onClose();
        }).bounds(centerX - 60, btnY, 120, 20).build());
    }

    private void updatePasswordVisibility() {
        java.util.function.BiFunction<String, Integer, FormattedCharSequence> formatter =
                showPassword ? (s, i) -> FormattedCharSequence.forward(s, Style.EMPTY)
                        : (s, i) -> FormattedCharSequence.forward("*".repeat(s.length()), Style.EMPTY);

        if (this.passwordField != null) this.passwordField.setFormatter(formatter);
        if (this.confirmField != null) this.confirmField.setFormatter(formatter);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        Services.PLATFORM.renderBackground(graphics, mouseX, mouseY, partial);
        if (BG_IMAGE != null) {
            graphics.blit(BG_IMAGE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
        }
        graphics.fill(width/2 - 120, height/2 - 60, width/2 + 120, height/2 + (isRegistered ? 50 : 80), 0x99000000);
        super.render(graphics, mouseX, mouseY, partial);
        graphics.drawCenteredString(font, isRegistered ? "§lINICIAR SESIÓN" : "§lREGISTRO", width/2, height/2 - 50, 0xFFFFFF);
        graphics.drawCenteredString(font, "§e" + (isRegistered ? "Ingresa tu contraseña" : "Crea una contraseña"), width/2, height/2 - 35, 0xFFFFFF);
        if (!error.isEmpty()) {
            graphics.drawCenteredString(font, "§c" + error, width/2, height/2 - 75, 0xFFFFFF);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}