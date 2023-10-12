package spacebuilder2020.remotecommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.*;
import spacebuilder2020.remotecommands.util.Util;
import spacebuilder2020.remotecommands.util.Util.ListenThread;

@Mod(modid = RemoteCommands.MODID,name=RemoteCommands.MODNAME, acceptableRemoteVersions = "*", version = RemoteCommands.VERSION)
public class RemoteCommands {
	public static final String MODID = "remotecommands";
	public static final String MODNAME = "remotecommands";
	public static final String VERSION = "1.0.2";
	public ArrayList<String> commandQue = new ArrayList<String>();
	
	private ListenThread listenThread;
	private String listenAddress;
	
	@Instance
    public static RemoteCommands instance = new RemoteCommands();
	
	@SidedProxy(clientSide="spacebuilder2020.remotecommands.ClientProxy", serverSide="spacebuilder2020.remotecommands.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit(e);
		
    }
        
    @EventHandler
    public void init(FMLInitializationEvent e) {
    	proxy.init(e);       
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    	proxy.postInit(e);
    }
    
    @EventHandler
    public void serverLoad(final FMLServerStartingEvent event)
    {
    	Util.LogMessage("Loading Remote Commands");
    	event.registerServerCommand(new BasicCommand("remoterun",new String[]{"rrun"},"rrun [<server1:server2:etc>|all] <command>","rc.rrun",true,new BasicCommand.CommandProcesser() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] astring) {
				String cstring = Util.combineStrings(astring, 1);
				if (astring.length > 1)
				{
				    if (astring[0].equals("all"))
				    {			    	
					    Iterator<String> it = ConfigHandler.instance.destinationAddresses.keySet().iterator();
				    	while (it.hasNext())	
							Util.SendMessage(cstring,it.next(), sender);			    
					    Util.ChatMessage("Sent '" + cstring + "' to all remote hosts",sender);
					    String cout = Util.RunCommand(event.getServer(),cstring);
					    Util.ChatMessage("Command Output (local): \n" + cout,sender);
					    
				    }
				    else
				    {
				    	String tmp1[] = astring[0].split(":");
				    	ArrayList<String> tmp2 = new ArrayList<String>();
				    	for (int i = 0; i < tmp1.length; i++)
				    	{			    	
				    		if (!tmp2.contains(tmp1[i]))
				    		{
				    			tmp2.add(tmp1[i]);
					    		//String address = ConfigHandler.instance.destinationAddresses.get(tmp1[i]);
					    		if (ConfigHandler.instance.destinationAddresses.containsKey(tmp1[i]))
					    		{			    			
					    			Util.SendMessage(cstring, tmp1[i], sender);
					    			Util.ChatMessage("Sent: '" + cstring + "' To " + tmp1[i],sender);
					    		}
					    		else
					    		{
					    			if (tmp1[i].equals(ConfigHandler.instance.name) || tmp1[i].equals("local"))
					    			{
					    				Util.ChatMessage("Ran: '" + cstring + "' on Local Machine",sender);
					    				String cout = Util.RunCommand(event.getServer(),cstring);
									    Util.ChatMessage("Command Output (local): \n" + cout,sender);
					    			}
					    			else
					    				Util.ChatMessage("Error: " + tmp1[i] + " Does not map to an address",sender);
					    		}
				    		}
				    	}
				 
				    }
				}
				else
				{
					Util.ChatMessage("rrun " + astring[0] + " <command>",sender);
				}
			}
		}));
    	event.registerServerCommand(new BasicCommand("globalrun",new String[]{"grun"},"grun <command> or rrun all <command>","rc.rrun.all",true,new BasicCommand.CommandProcesser() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] astring) {
				String cstring = Util.combineStrings(astring);			    
			    Util.RunCommand(event.getServer(),"rrun all " + cstring, sender);
			}
		}));
    	event.registerServerCommand(new BasicCommand("remotecommandsadmin",new String[]{"rca","rcadmin"},"rca reload|reset|value","rc.config.reload",true,new BasicCommand.CommandProcesser() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] astring) {
				if (astring[0].equals("reload"))
				{
					ConfigHandler.instance.LoadConfigs();
					Util.ChatMessage("Configs Reloaded",sender);
				}
				else if (astring[0].equals("reset"))
				{
					ConfigHandler.instance.ResettoDefaults();
					Util.ChatMessage("Configs Reset",sender);
				}
				else if (astring[0].equals("value"))
				{	
					if (astring.length > 1)
					{
						boolean changed = false;
						if (astring[1].equals("boundaddress"))
						{
							if (astring.length > 2)
							{
								Util.ChatMessage("Value 'boundaddress' set",sender);
								ConfigHandler.instance.boundaddress = astring[2];
								changed = true;
							}
							else
								Util.ChatMessage("rca value boundaddress <newvalue>", sender);
							Util.ChatMessage("boundaddress = " + ConfigHandler.instance.boundaddress, sender);
						}
						else if (astring[1].equals("name"))
						{
							if (astring.length > 2)
							{
								Util.ChatMessage("Value 'name' set",sender);
								ConfigHandler.instance.name = astring[2];
								changed = true;
							}
							else
								Util.ChatMessage("rca value name <newvalue>", sender);									
							Util.ChatMessage("name = " + ConfigHandler.instance.name, sender);
						}
						else if (astring[1].equals("destinationAddresses"))
						{
							if (astring.length > 2)
							{
								if (astring[2].equals("add"))
								{
									if (astring.length > 4)
									{
										if (ConfigHandler.instance.destinationAddresses.containsValue(astring[3]))
										{
											Util.ChatMessage("Destination Address: '" + astring[3] + "' is already in the database\nPlease use rca value destinationAddresses change <ip> <name>", sender);	
										}
										else if (ConfigHandler.instance.destinationAddresses.containsKey(astring[4]))
										{
											Util.ChatMessage("Destination Address: '" + astring[4] + "' is already in the database under a different ip\nPlease use rca value destinationAddresses change <ip> <name>", sender);
										}
										else
										{
											ConfigHandler.instance.destinationAddresses.put(astring[4], astring[3]);
											changed = true;
										}
									}
									else
									{
										Util.ChatMessage("rca value destinationAddresses add <ip> <name>", sender);		
									}
								}
								else if (astring[2].equals("remove"))
								{
									if (astring.length > 3)
									{
										if (ConfigHandler.instance.destinationAddresses.containsKey(astring[3]))
										{
											ConfigHandler.instance.destinationAddresses.remove(astring[3]);
											changed = true;
										}
										else if (ConfigHandler.instance.destinationAddresses.containsValue(astring[3]))
										{										
											ConfigHandler.instance.destinationAddresses.values().remove(astring[3]);
											changed = true;											
										}
										else
										{
											Util.ChatMessage("Ip or key does not exist in database", sender);
										}
									}
									else
									{
										Util.ChatMessage("rca value destinationAddresses remove <ip>|<name>", sender);		
										
									}
								}
								else if (astring[2].equals("change"))
								{
									if (astring.length > 4)
									{
										ConfigHandler.instance.destinationAddresses.remove(astring[4]);
										ConfigHandler.instance.destinationAddresses.values().remove(astring[3]);
										ConfigHandler.instance.destinationAddresses.put(astring[4], astring[3]);
										changed = true;
									}
									else
									{
										Util.ChatMessage("rca value destinationAddresses change <ip> <name>", sender);		
									}
								}
								else
								{
									Util.ChatMessage("Invalid Option",sender);
								}								
							}
							else
								Util.ChatMessage("rca value destinationAddresses add|remove|change <newvalue>", sender);
							StringBuilder sb = new StringBuilder();
							sb.append("destinationAddresses = \n");
							Iterator<String> it = ConfigHandler.instance.destinationAddresses.keySet().iterator();
							String s;
							while (it.hasNext())								
								sb.append(ConfigHandler.instance.destinationAddresses.get(s = it.next()) + " (" + s + ")\n");
							
							Util.ChatMessage(sb.toString(), sender);
						}
						else
						{
							Util.ChatMessage("Invalid Option",sender);
						}
						if (changed)
						{
							Util.ChatMessage("Changes updated",sender);
							ConfigHandler.instance.SaveConfigs();
						}
					}
					else
					{
						Util.ChatMessage("rca value boundaddress|name|destinationAddresses",sender);
					}
				}				
				else
				{
					
					Util.ChatMessage("Invalid Option",sender);
				}
				if (!listenAddress.equals(ConfigHandler.instance.boundaddress))
				{
					Util.ChatMessage("Rebinding Listener",sender);
					listenThread.CloseSocket();
					while (listenThread.isAlive())
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					listenThread = Util.Listen(event.getServer(),listenAddress = ConfigHandler.instance.boundaddress);
					Util.ChatMessage("Rebound Listener",sender);
				}
			}
		}));
    	listenThread = Util.Listen(event.getServer(),listenAddress = ConfigHandler.instance.boundaddress);
    }
    
    
    
    
}
