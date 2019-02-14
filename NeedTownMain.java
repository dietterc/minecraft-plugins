package punchcraft.needtown;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;




public class NeedTownMain extends JavaPlugin {
	
	private String broadcastPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Town Ad" +
			ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	private HashMap<CommandSender,Long> cooldown1;	//needtown
	private HashMap<CommandSender,Long> cooldown2;	//neednation
	private HashMap<CommandSender,Long> cooldown3;	//townad
	private HashMap<CommandSender,Long> cooldown4;	//nationad
	private static final int COOLDOWN_TIME_MS = 600000;
	
	 @Override
	 public void onEnable() {
	       
		 cooldown1 = new HashMap<>();	
		 cooldown2 = new HashMap<>();	
		 cooldown3 = new HashMap<>();	
		 cooldown4 = new HashMap<>();
		 
	 }
	   
	 @Override
	 public void onDisable() {
	       
		 cooldown1.clear();
		 cooldown2.clear();
		 cooldown3.clear();
		 cooldown4.clear();
		 
	 }
	 
	 public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		 
		 String commandName = command.getName().toLowerCase();
		 boolean retVal = false;
		 Resident resident;
		 
		 try {
			resident = TownyUniverse.getDataSource().getResident(sender.getName());
		} catch (NotRegisteredException e) {
			resident = null;
		}

		 
		 
		 if(resident!=null) {
			 switch (commandName){
		 		case "needtown":
		 			retVal = true;
		 			needtown(sender, resident); //sender and resident are the same person, both are sent so its easier 
		 		break;
			
		 		case "neednation":
		 			retVal = true;
		 			neednation(sender, resident);
		 		break;
		 	
		 		case "townad":
		 			retVal = true;
		 			townad(sender, resident);
		 		break;
		 	
		 		case "nationad":
		 			retVal = true;
		 			nationad(sender, resident);
		 		break;
		 	}
		 }
		 
		 return retVal;
	 }
	
	 private void needtown(CommandSender sender, Resident resident) {
		 
		 if(!resident.hasTown()) {
			 
			 if(checkCooldown(cooldown1,sender)) {
				 org.bukkit.Bukkit.getServer().broadcastMessage(broadcastPrefix + resident.getName() 
			 		+ " is looking for a town! Invite them now!");
			 }
			 
		 }
		 else {
		 	sender.sendMessage(ChatColor.GRAY + "You cannot use this command if you are already in a town.");
		 }
	 }
	 
	 private void neednation(CommandSender sender, Resident resident) {
		 
		 if(resident.hasTown()) {
			 
		 	Town town;
		 	try {
		 		town = resident.getTown(); //get the town
		 	} catch (NotRegisteredException e) {
		 		town = null;
		 	}	
			
		 	if(!town.hasNation()) {
		 		if(town.hasAssistant(resident) || resident.isMayor()) {
		 			if(checkCooldown(cooldown2,sender)) {
		 				org.bukkit.Bukkit.getServer().broadcastMessage(broadcastPrefix + "The town " 
		 						+ town.getName() + " needs a nation!");
		 			}
		 		}
		 		else {	
		 			sender.sendMessage(ChatColor.GRAY + "You must be a mayor or assistant of a town to use this command.");
		 		}
		 	}
		 	else {
		 		sender.sendMessage(ChatColor.GRAY + "You cannot use this command if you are already in a nation.");
		 	}
		 }
		 else {
		 	sender.sendMessage(ChatColor.GRAY + "You must be in a town to use this command.");
		} 
	 }
	 
	 private void townad(CommandSender sender, Resident resident) {
		 
		 if(resident.hasTown()) {
			 
		 	Town town;
		 	try {
		 		town = resident.getTown(); //get the town
		 	} catch (NotRegisteredException e) {
		 		town = null;
		 	}	
		
		 	if(town.hasAssistant(resident) || resident.isMayor()) {
		 		if(checkCooldown(cooldown3,sender)) {
		 			org.bukkit.Bukkit.getServer().broadcastMessage(broadcastPrefix + town.getName() 
		 				+ " is looking for members! Ask to join now!");
		 		}
		 	}
		 	else {	
		 		sender.sendMessage(ChatColor.GRAY + "You must be a mayor or assistant of a town to use this command.");
		 	}
		 }
		 else {
		 	sender.sendMessage(ChatColor.GRAY + "You must be in a town to use this command.");
		 } 
	 }
	 
	 private void nationad(CommandSender sender, Resident resident) {
		 
		 if(resident.hasNation()) {
			 
		 	Nation nation;
		 	try {
		 		nation = resident.getTown().getNation();
		 	} catch (NotRegisteredException e) {
		 		nation = null;
		 	}
			 
		 	if(nation.hasAssistant(resident) || resident.isKing()) {
		 		if(checkCooldown(cooldown4,sender)) {
		 			org.bukkit.Bukkit.getServer().broadcastMessage(broadcastPrefix + "The nation " + nation.getName() 
		 				+ " is looking for towns! Join them now!");
		 		}
		 	}
		 	else {
		 		sender.sendMessage(ChatColor.GRAY + "You must be a leader or assistant of a nation to use this command.");
		 	}
		 }
		else {
			sender.sendMessage(ChatColor.GRAY + "You must be in a nation to use this command.");
		}
	 }
	 
	 private boolean checkCooldown(HashMap<CommandSender,Long> cooldown, CommandSender sender) {
		 boolean retVal = false;
		 
		 if(cooldown.containsKey(sender)) {
			 
			 long timeDiff = System.currentTimeMillis() - cooldown.get(sender);
			 if(timeDiff >= COOLDOWN_TIME_MS) {
				 retVal = true;
				 cooldown.put(sender, System.currentTimeMillis());
			 }
			 else {
				 sender.sendMessage(ChatColor.GRAY + "Please wait before using this command again (" + (int)(((COOLDOWN_TIME_MS - timeDiff) / 60000) + 1) + "m)");
			 } 
			 
		 }
		 else {
			 cooldown.put(sender, System.currentTimeMillis());
			 retVal = true;
		 }
		 
		 return retVal;
	 }
	 
}


























