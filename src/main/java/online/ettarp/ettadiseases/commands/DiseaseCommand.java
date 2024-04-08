package online.ettarp.ettadiseases.commands;

import online.ettarp.ettadiseases.EttaDiseases;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class DiseaseCommand implements CommandExecutor {
    private final EttaDiseases plugin;
    private final Connection connection;

    public DiseaseCommand(EttaDiseases plugin) {
        this.plugin = plugin;

        try {
            connection = plugin.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("disease.disease")) {
            sender.sendMessage("У вас нет прав на выполнение этой команды.");
            return true;
        }

        Statement statement;
        String target;

        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (args == null || args.length == 0) {
            sender.sendMessage("Использование: /disease [infect, cure] <Target> <Disease Name>.");
            return true;
        }

        switch (args[0]) {
            case "infect":
                String[] diseases = plugin.getConfig().getConfigurationSection("diseases").getKeys(false).toArray(new String[0]);
                String disease = args[2];
                target = args[1];

                if (args.length != 3) {
                    sender.sendMessage("Использование: /disease infect <Target> <Disease Name>.");
                    return true;
                }

                if (!Arrays.asList(diseases).contains(disease)) {
                    sender.sendMessage("Такой болезни не существует.");
                    return true;
                }

                if (Bukkit.getPlayer(target) == null) {
                    sender.sendMessage("Игрок не онлайн.");
                    return true;
                }


                String finalTarget = target;
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        if (!statement.executeQuery("SELECT * FROM infected WHERE nickname = '" + finalTarget + "'").next()) {
                            PreparedStatement ps = connection.prepareStatement("INSERT INTO infected(nickname, disease, incubation) VALUES (?, ?, ?)");

                            ps.setString(1, finalTarget);
                            ps.setString(2, disease);
                            ps.setInt(3, plugin.getConfig().getInt("diseases." + disease + ".incubation-time"));

                            ps.execute();

                            sender.sendMessage("Игрок успешно заражён.");
                        } else {
                            sender.sendMessage("Игрок уже заражён.");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                break;

            case "cure":
                target = args[1];

                if (args.length != 2) {
                    sender.sendMessage("Использование: /disease cure <Target>.");
                    return true;
                }

                if (Bukkit.getPlayer(target) == null) {
                    sender.sendMessage("Игрок не онлайн.");
                    return true;
                }


                String finalTarget1 = target;
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        PreparedStatement ps = connection.prepareStatement("DELETE FROM infected WHERE nickname = ?");

                        ps.setString(1, finalTarget1);

                        ps.execute();

                        sender.sendMessage("Игрок успешно вылечен.");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                break;
        }

        return true;
    }
}