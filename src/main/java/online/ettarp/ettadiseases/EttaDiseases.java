package online.ettarp.ettadiseases;

import online.ettarp.ettadiseases.commands.DiseaseCommand;
import online.ettarp.ettadiseases.commands.DiseaseCompleter;
import online.ettarp.ettadiseases.db.DBHandler;
import online.ettarp.ettadiseases.listeners.PlayerAttackInfected;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class EttaDiseases extends JavaPlugin {
    DBHandler handler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        handler = new DBHandler(this);

        try {
            getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `infected` (`nickname` varchar(255) NOT NULL, `disease` varchar(255) NOT NULL, `incubation` int(11) NOT NULL, PRIMARY KEY (`nickname`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        BukkitRunnable incubation = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Statement statement = getConnection().createStatement();

                    for(Player player : getServer().getOnlinePlayers()) {
                        if(statement.executeQuery("SELECT * FROM infected WHERE nickname = '" + player.getName() + "'").next()) {
                            statement.execute("UPDATE infected SET incubation = incubation - 1 WHERE incubation > 0 AND nickname = '" + player.getName() + "';");
                        }
                    }
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        incubation.runTaskTimerAsynchronously(this, 0, 60*20);

        getCommand("disease").setExecutor(new DiseaseCommand(this));
        getCommand("disease").setTabCompleter(new DiseaseCompleter(this));

        getServer().getPluginManager().registerEvents(new PlayerAttackInfected(this), this);
    }

    @Override
    public void onDisable() {
        try {
            handler.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return handler.getConnection();
    }
}
