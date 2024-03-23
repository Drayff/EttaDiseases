package online.ettarp.ettadiseases;

import online.ettarp.ettadiseases.db.DBHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class EttaDiseases extends JavaPlugin {
    DBHandler handler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        handler = new DBHandler(this);
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
