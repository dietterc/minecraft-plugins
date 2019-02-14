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
	
	//format prefix for the broadcast
	private String broadcastPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Town Ad" +
			ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;

	//cooldown hashmaps
	private HashMap<CommandSender,Long> cooldown1;	//needtown
	private HashMap<CommandSender,Long> cooldown2;	//neednation
	private HashMap<CommandSender,Long> cooldown3;	//townad
	private HashMap<CommandSender,Long> cooldown4;	//nationad
	
	private static final int COOLDOWN_TIME_MS = 600000; //10 minutes
	
	 @Override
	 public void onEnable() {
	     //create new empty hashmaps
		 cooldown1 = new HashMap<>();	
		 cooldown2 = new HashMap<>();	
		 cooldown3 = new HashMap<>();	
		 cooldown4 = new HashMap<>();
		 
	 }
	   
	 @Override
	 public void onDisable() {
	     
		 //clear all hashmaps
		 cooldown1.clear();
		 cooldown2.clear();
		 cooldown3.clear();
		 cooldown4.clear();
		 
	 }
	 
	 public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		 
		 String commandName = command.getName().toLowerCase(); //ignore caps for the command
		 boolean retVal = false;
		 Resident resident;		
		 
		 try {
			resident = TownyUniverse.getDataSource().getResident(sender.getName()); //get the resident from towny
		} catch (NotRegisteredException e) {
			resident = null;
		}
		 
		 //check what command was sent, call the appropriate method 
		 //sender and resident are the same person, both are sent each command so its easier
		 if(resident!=null) {
			 switch (commandName){
		 		case "needtown":
		 			retVal = true;
		 			needtown(sender, resident); 
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
	 
	/**
	 * needtown
	 * 
	 * Broadcasts that the command sender is looking for a town only if they dont
	 *  have a town already
	 */
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
	 
	 /**
	  * neednation
	  * 
	  * Broadcast that the town of the command sender is looking for a nation
	  * sender must be in a town and a mayor or assistant
	  * town cannot already be in a nation
	  */
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
	 
	 /**
	  * townad
	  * 
	  * Broadcasts that the command senders town is looking for members
	  * sender must be a mayor or assistant
	  */
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
	 
	 /**
	  * nationad
	  * 
	  * broadcasts that the commandsender's nation is looking for towns 
	  * must be leader or assistant of the nation
	  * can only be run if they have a nation
	  */
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
	 
	 /**
	  * checkCooldown
	  * 
	  * Given a hashmap and a commandsender, checks whether or not enough
	  * 	time has passed before they can run a command again
	  * 
	  * The hashmap stores the time that the command was last run at, at the location of 
	  * 	commandsender(hashed). To check the cooldown, I compare the time stored with 
	  * 	the current time whenever the command is run.
	  *  
	  */
	 private boolean checkCooldown(HashMap<CommandSender,Long> cooldown, CommandSender sender) {
		 boolean retVal = false;
		 
		//check if they are in the hashmap, if they arent, then there is no cooldown and they can run the command
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