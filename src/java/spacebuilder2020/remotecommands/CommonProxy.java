package spacebuilder2020.remotecommands;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.event.*;

public class CommonProxy {

	public Logger modLog;
    public void preInit(FMLPreInitializationEvent e) {
    	modLog = e.getModLog();
    	ConfigHandler.instance.LoadConfig(e);
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
