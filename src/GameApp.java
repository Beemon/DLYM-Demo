import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class GameApp extends MIDlet
{
	private Display screen;
	private GameModule module;

	protected void startApp()
	{
		if (screen == null) screen = Display.getDisplay(this);
		if (module == null) module = new GameModule();
		screen.setCurrent(module);
		module.start();
	}

	protected void pauseApp()
	{
		module.pause();
		notifyPaused();
	}

	protected void destroyApp(boolean unconditional)
	{
		module.stop();
		notifyDestroyed();
	}
}
