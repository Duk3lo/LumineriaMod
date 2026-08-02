package org.astral.lumineriabase.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;


public final class LumineriaCommands {
    private LumineriaCommands() {}

    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lumineria")
                .then(Commands.literal("migrar")
                        .then(Commands.literal("conservar").executes(ctx -> {
                            if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
                                NameConflictManager.resolveKeepInPremium(player);
                            }
                            return 1;
                        }))
                        .then(Commands.literal("mudar")
                                .then(Commands.argument("nick", StringArgumentType.word())
                                        .executes(ctx -> {
                                            if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
                                                String nick = StringArgumentType.getString(ctx, "nick");
                                                NameConflictManager.resolveMoveToNewNick(player, nick);
                                            }
                                            return 1;
                                        })))));
    }
}
