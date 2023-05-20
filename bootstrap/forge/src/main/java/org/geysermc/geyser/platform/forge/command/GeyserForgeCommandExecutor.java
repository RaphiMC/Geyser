/*
 * Copyright (c) 2019-2023 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.platform.forge.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.command.GeyserCommand;
import org.geysermc.geyser.command.GeyserCommandExecutor;
import org.geysermc.geyser.platform.forge.GeyserForgeMod;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.text.ChatColor;
import org.geysermc.geyser.text.GeyserLocale;

import java.util.Collections;

public class GeyserForgeCommandExecutor extends GeyserCommandExecutor implements Command<CommandSourceStack> {
    private final GeyserForgeMod mod;
    private final GeyserCommand command;

    public GeyserForgeCommandExecutor(GeyserForgeMod mod, GeyserImpl connector, GeyserCommand command) {
        super(connector, Collections.singletonMap(command.name(), command));
        this.mod = mod;
        this.command = command;
    }

    public boolean testPermission(CommandSourceStack source) {
        return true; // TODO: Use new Forge permission API
    }

    @Override
    public int run(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();
        ForgeCommandSender sender = new ForgeCommandSender(source);
        GeyserSession session = getGeyserSession(sender);
        if (!testPermission(source)) {
            sender.sendMessage(ChatColor.RED + GeyserLocale.getPlayerLocaleString("geyser.bootstrap.command.permission_fail", sender.locale()));
            return 0;
        }
        if (this.command.name().equals("reload")) {
            this.mod.setReloading(true);
        }

        if (command.isBedrockOnly() && session == null) {
            sender.sendMessage(ChatColor.RED + GeyserLocale.getPlayerLocaleString("geyser.bootstrap.command.bedrock_only", sender.locale()));
            return 0;
        }
        command.execute(session, sender, new String[0]);
        return 0;
    }
}