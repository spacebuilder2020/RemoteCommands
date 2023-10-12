package spacebuilder2020.remotecommands;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicCommandSender extends CommandBlockBaseLogic {

	private StringBuilder output = new StringBuilder();
	
	public String getOutput()
	{
		return output.toString();
	}

	@Override
	public void addChatMessage(ITextComponent message) {
		String ftext = message.getUnformattedText();
		output.append(ftext);
		output.append('\n');
	}

	@Override
	public boolean canCommandSenderUseCommand(int i, String command) {
		return true;
	}

	@Override
	public World getEntityWorld() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int func_145751_f() {
		// TODO Auto-generated method stub
		return 0;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void func_145757_a(ByteBuf p_145757_1_) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Entity getCommandSenderEntity() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void updateCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BlockPos getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3d getPositionVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MinecraftServer getServer() {
		// TODO Auto-generated method stub
		return null;
	}

}
