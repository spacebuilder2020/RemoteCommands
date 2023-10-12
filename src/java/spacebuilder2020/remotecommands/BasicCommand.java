package spacebuilder2020.remotecommands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class BasicCommand implements ICommand {

	public interface CommandProcesser
	{
		public void processCommand(ICommandSender icommandsender, String[] astring);
	}
	 private ArrayList<String> aliases;
	 private String name;
	 private String usage;
	 private String perm;
	 boolean wildcardperm;
	 private CommandProcesser cp;
	 public BasicCommand(String name,String[] aliases, String usage, String perm, boolean wildcardperm, CommandProcesser cp)
	 {
		this.name = name;
		this.aliases = new ArrayList<String>();
		for (int i = 0; i < aliases.length; i++)
			this.aliases.add(aliases[i]);
		this.usage = usage;
		this.perm = perm;
		this.wildcardperm = wildcardperm;
		this.cp = cp;
	 }

	@Override
	public String getCommandName() 
	{
		return name;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) 
	{
		return usage;
	}

	@Override
	public List getCommandAliases() 
	{
		return this.aliases;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) 
	{
		return false;
	}
	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
		// TODO Auto-generated method stub				
		if(astring.length == 0)
	    {
	    	icommandsender.addChatMessage(new TextComponentString(getCommandUsage(icommandsender)));
	    	return;
	    }
	    cp.processCommand(icommandsender, astring);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO Auto-generated method stub
		return sender.canCommandSenderUseCommand(4, getCommandName());
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			net.minecraft.util.math.BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

}
