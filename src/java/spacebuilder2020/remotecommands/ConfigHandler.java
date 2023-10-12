package spacebuilder2020.remotecommands;

import java.util.*;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import scala.swing.TextComponent;
import spacebuilder2020.remotecommands.util.Util;

public class ConfigHandler {
	public static final String default_boundaddress = "127.0.0.1";
	public static final String default_name = "Server1";
	public static ConfigHandler instance = new ConfigHandler();
	
	public String boundaddress;
	public String name;
	public HashMap<String,String>  destinationAddresses = new HashMap<String,String>();
	public static final String[] defaultDest = new String[] {"127.0.0.2:Server2","127.0.0.3:Server3","127.0.0.4:Server4"};
	
	private Configuration cf;
	public void LoadConfig(net.minecraftforge.fml.common.event.FMLPreInitializationEvent e)
	{
		cf = new Configuration(e.getSuggestedConfigurationFile());
		LoadConfigs();
		SaveConfigs();
		
	}
	public void ResettoDefaults()
	{
		Util.LogMessage("Resetting Configs for Remote Commands");
		boundaddress = default_boundaddress;
		name = default_name;
		destinationAddresses.clear();
		for (int i = 0; i < defaultDest.length; i++)
		{
			String[] tmp = defaultDest[i].split(":");
			destinationAddresses.put(tmp[1], tmp[0]);
		}
		SaveConfigs();
	}
	public void SaveConfigs()
	{
		Util.LogMessage("Saving Updated Configs for Remote Commands");
		Property ba = cf.get("main", "boundaddress",default_boundaddress);
		ba.set(boundaddress);
		Property n = cf.get("main", "name",default_name);
		n.set(name);
		Property dA = cf.get("main", "destinationAddresses",defaultDest);
		Iterator<String> it = destinationAddresses.keySet().iterator();
		String[] tmp = new String[destinationAddresses.size()];
		int i = 0;
		String key;
		while (it.hasNext())
			tmp[i++] = destinationAddresses.get(key = it.next()) + ":" + key;
		dA.set(tmp);
		cf.save();
	}
	public void LoadConfigs()
	{
		Util.LogMessage("Loading Config for Remote Commands");
		destinationAddresses.clear();
		cf.load();		 
		boundaddress = cf.getString("boundaddress", "main", default_boundaddress, "Address the listener is bound to.");
		name = cf.getString("name", "main", default_name, "User Friendly name for the server.");
		String[] tmp1 = cf.getStringList("destinationAddresses", "main", defaultDest, "Address and name of the senders. This also functions as a whitelist for listeners. Do not whitelist an address you don't control or someone could easily run a command from a whitelisted address.");
		for (int i = 0; i < tmp1.length; i++)
		{
			String[] tmp2 = tmp1[i].split(":");
			String tmp2_1;
			if (tmp2.length > 1)
				tmp2_1 = tmp2[1];
			else
			{
				tmp2_1 = "Server" + (i+2);
				Util.LogMessage("Warning: No server name specified, default value " + tmp2_1 + " used");
			}
			if (!tmp2[0].equals(boundaddress))
				if (!destinationAddresses.containsValue(tmp2[0]))
					destinationAddresses.put(tmp2_1,tmp2[0]);
				else
					Util.LogMessage("Warning: " + tmp2[0] + " is a duplicate value.  It has been removed from the config file.");
			else				
				Util.LogMessage("Warning: " + tmp2[0] + " is equal to the listener address.  It has been removed from the config file.");
		}
	}
}
