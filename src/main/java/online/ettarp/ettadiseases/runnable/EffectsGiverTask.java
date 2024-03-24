package online.ettarp.ettadiseases.runnable;

import online.ettarp.ettadiseases.EttaDiseases;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

public class EffectsGiverTask extends BukkitRunnable {
    private final EttaDiseases plugin;

    public EffectsGiverTask(EttaDiseases plugin) {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        try {
            Random random = new Random();
            Statement statement = plugin.getConnection().createStatement();

            ResultSet targets = statement.executeQuery("SELECT * FROM infected WHERE incubation = 0");

            while(targets.next()) {
                Player target = plugin.getServer().getPlayer(targets.getString("nickname"));
                String disease = targets.getString("disease");

                if(plugin.getServer().getOnlinePlayers().contains(target)) {
                    int chance = random.nextInt(100);
                    int effectChance = plugin.getConfig().getInt("diseases." + disease + ".effect-chance");

                    if (chance < effectChance) {
                        List<List<Integer>> effects = (List<List<Integer>>) plugin.getConfig().getList("diseases." + disease + ".effects");

                        for (List<Integer> effect : effects) {
                            int id = effect.get(0);
                            int level = effect.get(1);
                            int duration = effect.get(2) * 20;


                            Bukkit.getScheduler().runTask(plugin, () -> target.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), duration, level - 1)));
                        }
                    }
                }
            }

            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
