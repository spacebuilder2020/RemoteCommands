package spacebuilder2020.remotecommands.util;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import spacebuilder2020.remotecommands.BasicCommandSender;
import spacebuilder2020.remotecommands.ConfigHandler;
import spacebuilder2020.remotecommands.RemoteCommands;

public class Util {
	public static String combineStrings(String[] as)
	{
		return combineStrings(as,0);
	}
	public static String combineStrings(String[] as, int start)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < as.length; i++)
		{
			sb.append(as[i]);
			if (i < as.length -1)
				sb.append(' ');
		}
		return sb.toString();
	}
	public static String RunCommand(MinecraftServer server, String command)
    {
		
		BasicCommandSender basic = new BasicCommandSender();
		RunCommand(server, command, basic);// MinecraftServer.getServer());
		return basic.getOutput();
    }
	public static void RunCommand(MinecraftServer server, String command, ICommandSender ics)
    {
		if (server.getServer().isServerRunning())
		{			
			server.getServer().getCommandManager().executeCommand(ics, command);
			LogMessage("Ran command: " + command);
		}
		else
		{
			LogMessage("Server not fully started!");
		}
    }
	
	public static void LogMessage(String message)
	{
		RemoteCommands.proxy.modLog.info(message);		
		try {
			Writer w = new FileWriter(new File("logs/remotecommands-log.txt"),true);
			w.write(message);
			w.write('\n');
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void SendMessage(final String message, final String name, final ICommandSender sender)
	{
		Thread sendThread = new Thread(new Runnable(){

			@Override
			public void run() {
				String address = ConfigHandler.instance.destinationAddresses.get(name);
				try {
					if (address == null)
						throw new IOException("Unable to name to address");
					Socket s = new Socket(InetAddress.getByName(address),8560,InetAddress.getByName(ConfigHandler.instance.boundaddress),0);
					Writer w = new OutputStreamWriter(s.getOutputStream());
					w.write(message);
					w.write('\n');
					w.flush();
					StringBuilder sb = new StringBuilder();
					while (true)
					{
						int c = s.getInputStream().read();						
						if (c <= 0)
							break;
						sb.append((char)c);
					}
					ChatMessage("Command Output (" + name + "): \n" + sb.toString(), sender);
					s.close();
					LogMessage("Sent Command: " + message + " to " + name + " ("+ address + ")");
				} catch (IOException e) {
					Util.LogMessage("Unable to Send Command: " + message + " to " + name + " ("+ address + ")");
				}
				
			}
		});
		sendThread.start();
		
	}
	public static void ChatMessage(String message, ICommandSender sender)
	{
		String tmp[] = message.split("\n");
		for (int i = 0; i < tmp.length; i++)
			sender.addChatMessage(new TextComponentString(tmp[i]));
		LogMessage("[ChatMsg] " + message);
	}
	public static class ListenThread extends Thread
	{
		protected ServerSocket ss = null;
		public void CloseSocket()
		{
			if (ss != null)
			{
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ss = null;
			}
		}
		
	}
	public static ListenThread Listen(final MinecraftServer server, final String address)
	{
		ListenThread listenThread = new ListenThread()
		{
			@Override
			public void run() 
			{
				
				try {
					LogMessage("Binding address: " + address + " to port 8560");
					ss = new ServerSocket(8560,0,InetAddress.getByName(address));
					
					while (true)
					{
						Socket s = ss.accept();
						try {											
							String address2 = s.getInetAddress().getHostAddress();
							if (ConfigHandler.instance.destinationAddresses.containsValue(address2))
							{
								Reader r = new InputStreamReader(s.getInputStream());
								StringBuilder sb = new StringBuilder();
								while (true)
								{
									char c = (char) r.read();
									if (c == '\n')
										break;		
									sb.append(c);
								}
								String out = sb.toString();
								LogMessage(address + ": Received " + out + " from " + address2 + "\n");
								
								String cout = RunCommand(server,out);
								for (int i = 0; i < cout.length(); i++)
								{
									int c = cout.charAt(i);
									s.getOutputStream().write(c);
								}
							}
							else
							{
								LogMessage("Input Address is not whitelisted: " + address2);
							}
							s.getOutputStream().write(0);
							s.close();
						} catch (IOException e)
						{
							if (!e.getMessage().equals("socket closed"))
									e.printStackTrace();
						}											
					}					
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (ss != null)
						try {
							ss.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
			}			
		};
		listenThread.setName("Server thread");
		listenThread.start();
		return listenThread;
	}
	
}
