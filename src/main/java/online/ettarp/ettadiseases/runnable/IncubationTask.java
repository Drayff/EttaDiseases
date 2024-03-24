package online.ettarp.ettadiseases.runnable;

import online.ettarp.ettadiseases.EttaDiseases;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.sql.Statement;

public class IncubationTask extends BukkitRunnable {
    private final EttaDiseases plugin;

    public IncubationTask(EttaDiseases plugin) {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        try {
            Statement statement = plugin.getConnection().createStatement();

            for(Player player : plugin.getServer().getOnlinePlayers()) {
                if(statement.executeQuery("SELECT * FROM infected WHERE nickname = '" + player.getName() + "'").next()) {
                    statement.execute("UPDATE infected SET incubation = incubation - 1 WHERE incubation > 0 AND nickname = '" + player.getName() + "';");
                }
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
