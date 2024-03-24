package online.ettarp.ettadiseases;

import online.ettarp.ettadiseases.commands.DiseaseCommand;
import online.ettarp.ettadiseases.commands.DiseaseCompleter;
import online.ettarp.ettadiseases.db.DBHandler;
import online.ettarp.ettadiseases.listeners.PlayerAttackInfected;
import online.ettarp.ettadiseases.runnable.EffectsGiverTask;
import online.ettarp.ettadiseases.runnable.IncubationTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
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

        new IncubationTask(this).runTaskTimerAsynchronously(this, 0, 60*20);
        new EffectsGiverTask(this).runTaskTimerAsynchronously(this, 0, 60*5*20);

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
