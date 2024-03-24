package online.ettarp.ettadiseases.listeners;

import online.ettarp.ettadiseases.EttaDiseases;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.sql.*;
import java.util.Random;

public class PlayerAttackInfected implements Listener {
    private final EttaDiseases plugin;
    private final Connection connection;
    private final Random random;

    public PlayerAttackInfected(EttaDiseases plugin) {
        this.plugin = plugin;
        random = new Random();

        try {
            connection = plugin.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player infected = (Player) event.getEntity();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM infected WHERE nickname = ?");
                    preparedStatement.setString(1, infected.getName());
                    ResultSet resultSet = preparedStatement.executeQuery();

                    PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM infected WHERE nickname = ?");
                    preparedStatement2.setString(1, attacker.getName());
                    ResultSet resultSet2 = preparedStatement2.executeQuery();

                    if (resultSet.next() && !resultSet2.next()) {
                        int chance = random.nextInt(100);
                        int transferChance = plugin.getConfig().getInt("diseases." + resultSet.getString("disease") + ".transfer-chance");

                        if (chance < transferChance) {
                            PreparedStatement statement = connection.prepareStatement("INSERT INTO infected(nickname, disease, incubation) VALUES (?, ?, ?)");

                            statement.setString(1, attacker.getName());
                            statement.setString(2, resultSet.getString("disease"));
                            statement.setInt(3, plugin.getConfig().getInt("diseases." + resultSet.getString("disease") + ".incubation-time"));

                            statement.execute();
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
