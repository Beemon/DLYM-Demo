import java.util.*;

public class GameTimer extends TimerTask
{
	private GameModule module;

	public GameTimer(GameModule module)
	{
		this.module = module;
	}

	public void run()
	{
		module.tick();
	}
}