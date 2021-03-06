package io.apexcreations.core.modules.economy.commands.sub;

import io.apexcreations.core.ApexCore;
import io.apexcreations.core.builders.MessageBuilder;
import io.apexcreations.core.commands.SubCommand;
import io.apexcreations.core.exceptions.MaxMoneyException;
import io.apexcreations.core.modules.Module;
import io.apexcreations.core.modules.economy.EconomyModule;
import io.apexcreations.core.players.ApexPlayer;
import io.apexcreations.core.utils.Utils;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyTakeSubCommand extends SubCommand {

  public EconomyTakeSubCommand(ApexCore apexCore, String name, String info, String permission, boolean playerOnly,
      String... aliases) {
    super(apexCore, name, info, permission, playerOnly, aliases);
  }

  @Override
  public void execute(CommandSender commandSender, String[] args) {
    args = Arrays.copyOfRange(args, 1, args.length);

    Player player = Bukkit.getPlayer(args[0]);

    if (player == null) {
      MessageBuilder.create("&c&lERROR &7&l>> &cCould not find player!").withPrefix(PREFIX)
          .send(commandSender);
      return;
    }

    Optional<ApexPlayer> optionalApexPlayer = this.getApexCore().getPlayerCache()
        .get(player.getUniqueId());

    if (!optionalApexPlayer.isPresent()) {
      MessageBuilder.create("&c&lERROR &7&l>> &cCould not find player data!").withPrefix(PREFIX)
          .send(commandSender);
      return;
    }

    if (!Utils.isDouble(args[1])) {
      MessageBuilder.create(String
          .format("&c&lERROR &7&l>> &cIncorrect Usage! Try /%s take <player> <amount>",
              this.getName())).send(commandSender);
      return;
    }

    ApexPlayer apexPlayer = optionalApexPlayer.get();

    if (apexPlayer.getAccount() == null) {
      MessageBuilder.create("&c&lERROR &7&l>> &cCould not find player account!").withPrefix(PREFIX)
          .send(commandSender);
      return;
    }

    double amount = Double.parseDouble(args[1]);
    try {
      apexPlayer.getAccount().removeFromBalance(amount);
    } catch (MaxMoneyException e) {
      MessageBuilder.create("&c&lERROR &7&l>> &c" + e.getMessage());
      return;
    }

    String amt = Utils.formatCurrency(BigDecimal.valueOf(amount));
    Optional<Module> optionalEconomyModule =
            this.getApexCore().getModuleManager().getModuleCache().get("Economy");
    if (!optionalEconomyModule.isPresent()) {
      // Do something
      return;
    }
    EconomyModule economyModule = (EconomyModule) optionalEconomyModule.get();
    String currency = economyModule.getCurrencySymbol();

    MessageBuilder.create(String.
        format("&c&lWITHDRAW &7&l>> &c&l%s%s &7has been withdrawn from your account!", currency, amt
        )).send(player);

    MessageBuilder.create(String
        .format("&c&lWITHDRAW &7&l>> &c&l%s%s &7has been withdrawn from %s's account!", currency,
            amt,
            player.getName())).send(commandSender);
  }
}
