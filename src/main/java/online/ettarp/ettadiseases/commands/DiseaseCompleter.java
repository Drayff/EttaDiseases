package online.ettarp.ettadiseases.commands;

import online.ettarp.ettadiseases.EttaDiseases;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DiseaseCompleter implements TabCompleter {
    private final EttaDiseases plugin;

    public DiseaseCompleter(EttaDiseases plugin){
        this.plugin = plugin;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        if(args.length == 1) {
            completions.add("infect");
            completions.add("cure");
        } else if(args.length == 2) {
            completions = null;
        } else if(args.length == 3) {
            if(args[0].equals("infect")) {
                completions.addAll(plugin.getConfig().getConfigurationSection("diseases").getKeys(false));
            }
        }

        return completions;
    }
}
