package me.savvy.api.builders;

import java.util.Collection;
import java.util.Map;
import me.savvy.ApexCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageBuilder {

  private String message, prefix;

  private MessageBuilder(String string) {
    this.message = translate(string);
    this.prefix = ApexCore.getInstance().getConfig().getString("prefix");
  }

  public MessageBuilder append(String[] strings) {
    return this.append(strings, " ");
  }

  public MessageBuilder append(String[] strings, CharSequence delimiter) {
    String s = translate(String.join(delimiter, strings));
    if (this.message == null || this.message.isEmpty()) {
      this.message = s;
    } else {
      this.message = this.message.concat(" ").concat(s);
    }
    return this;
  }

  public MessageBuilder withPrefix() {
    this.message = translate(this.prefix + " " + message);
    return this;
  }

  public MessageBuilder setPlaceholders(Map<String, String> placeholders) {
    placeholders.forEach((o, o2) -> this.message = this.message.replace(o, o2));
    return this;
  }

  public MessageBuilder send(Collection<? extends CommandSender> commandSenders) {
    commandSenders.forEach(this::send);
    return this;
  }

  public MessageBuilder broadcast() {
    Bukkit.broadcastMessage(this.message);
    return this;
  }

  public MessageBuilder broadcast(String permission) {
    Bukkit.broadcast(permission, this.message);
    return this;
  }

  public MessageBuilder kick(Collection<? extends Player> players) {
    players.forEach(this::kick);
    return this;
  }

  public void kick(Player player) {
    player.kickPlayer(this.message);
  }

  public void send(CommandSender commandSender) {
    commandSender.sendMessage(this.message);
  }

  public static MessageBuilder create(String string) {
    return new MessageBuilder(string);
  }

  private String translate(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
  }
