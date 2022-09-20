package com.github.Sprite233.shoutall;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ShoutCMD extends Command {
    public ShoutCMD(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cCommand can only be used by player!");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            player.sendMessage("§cBlank Message!");
            return;
        }

        Data data = ShoutAll.getInstance().getAllData().get(player);
        long before = data.time;
        long now = System.currentTimeMillis() / 1000;
        int time = 180 - (int) (now - before);
        if (time > 0) {
            player.sendMessage("§cCooldown " + time + "Seconds");
            if (!player.hasPermission("shoutall.bypass"))
                return;
            player.sendMessage("§aYou bypassed the cooldown");
        }

        StringBuilder sb = new StringBuilder("§6[Shout] §e");
        sb.append(player.getDisplayName());
        sb.append("§f: ");

        for (String arg : args) {
            if (sender.hasPermission("shoutall.shout.color"))
                arg = arg.replace('&', '§').replace("§§", "&");
            sb.append(arg);
            sb.append(" ");
        }

        sb.append("§b§l§n(Click to teleport)");

        String token = UUID.randomUUID().toString().replace("-", "");

        TextComponent tc = new TextComponent(sb.toString());
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpserver " + token));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cClick to teleport §e" + player.getServer().getInfo().getName()).create()));

        for (ProxiedPlayer player1 : ShoutAll.getInstance().getProxy().getPlayers()) {
            player1.sendMessage(tc);
        }

        data.token = token;
        data.sender = sender.getName();
        data.server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
        data.time = System.currentTimeMillis() / 1000;
    }
}
