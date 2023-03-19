package link.harryw.deathpenalty.events;

import link.harryw.deathpenalty.DeathPenalty;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnEvent implements Listener {

    private final Economy econ;
    private final DeathPenalty plugin;

    public RespawnEvent(DeathPenalty plugin) {
        this.plugin = plugin;
        this.econ = DeathPenalty.econ;
    }

    @EventHandler
    public void respawnEvent(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (!p.isDead()) return;
        if (p.hasPermission("deathpenalty.bypass")) return;

        double currentBalance = econ.getBalance(p);
        double withdrawAmount = 0.0f;

        if (currentBalance >= config.getInt("minimumBalance")) {
            if (config.getString("lossType").equalsIgnoreCase("percentage")) {
                int percentageAmount = config.getInt("percentageLost");
                withdrawAmount = currentBalance * (percentageAmount / 100.0f);
            } else if (config.getString("lossType").equalsIgnoreCase("fixed")) {
                withdrawAmount = config.getInt("amountLost");
            }

            econ.withdrawPlayer(p, withdrawAmount);

            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix")) + ChatColor.translateAlternateColorCodes('&', config.getString("deathMessage")).replace("%amount%", String.valueOf((int) withdrawAmount)));
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix")) + ChatColor.translateAlternateColorCodes('&', config.getString("belowMinimumBalance")));
        }
    }
}
