import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class GameModule extends GameCanvas implements Runnable
{
	private static final int RELEASE_TIMER = 1;
	private static final int ENDGAME_TIMER = 2;
	private static final int ENDTHIS_LEVEL = 3;
	private Thread thread;
	private boolean running;
	private boolean pausing;
	private Graphics g;
	private int keys;
	private int w;
	private int h;

	private NumberPrinter printer;
	private Board board;
	private Rescuer rescuer;
	private RescueeList rescueeList;
	private Timer gameTimer;
	private int state;
	private int releaseTimer;
	private int releaseSubTimer;
	private int endGameTimer;
	private int releaseCount;
	private int lifeCount;
	private int saveCount;
	private int deadCount;
	private int myScore;

	public GameModule()
	{
		super(false);
		setFullScreenMode(true);
		w = getWidth();
		h = getHeight();
		g = getGraphics();
	}

	public void start()
	{
		if (!running)
		{
			thread = new Thread(this);
			running = true;
			thread.start();
		}
		else pausing = false;
	}

	public void pause()
	{
		pausing = true;
	}

	public void stop()
	{
		running = false;
	}

	private Image getImage(String name)
	{
		try
		{
			return Image.createImage(name);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public void clear()
	{
		g.setColor(0);
		g.fillRect(0, 0, w, h);
	}

	public void startTimer()
	{
		state = RELEASE_TIMER;
		releaseTimer =  60;
		endGameTimer = 120;

		gameTimer = new Timer();
		gameTimer.schedule(new GameTimer(this), 1000, 1000);
	}

	public void onRescueeCreate()
	{
		rescueeList.addRescuee();
		releaseCount = releaseCount - 1;
		lifeCount = lifeCount + 1;
	}

	public void releaseRescuee()
	{
		if (board.isEmpty(
			board.getEntranceX(),
			board.getEntranceY()))
		{
			board.animateRescueeEntering();
		}
	}

	public void tick()
	{
		if (state == RELEASE_TIMER)
		{
			releaseTimer = releaseTimer - 1;
			if (releaseTimer == 0)
			{
				state = ENDGAME_TIMER;
				releaseSubTimer = 1;
			}
		}

		if(state == ENDGAME_TIMER)
		{
			endGameTimer = endGameTimer - 1;
			if(endGameTimer == 0)
			{
				state = ENDTHIS_LEVEL;
				running = false;
				return;
			}
		}

		if (releaseCount > 0)
		{
			releaseSubTimer = releaseSubTimer - 1;
			if (releaseSubTimer == 0)
			{
				releaseSubTimer = 2;
				releaseRescuee();
			}
		}
	}

	public void paintScore()
	{
		printer.print(g,
			board.getX(),
			board.getY() - 7,
			myScore,
			0, 4);
	}

	public void paintTimer()
	{
		if (state == RELEASE_TIMER)
		{
			printer.print(g,
				board.getX() + 60 - 6,
				board.getY() - 7, releaseTimer,
				2, 2);
			return;
		}

		if (state == ENDGAME_TIMER)
		{
			printer.print(g,
				board.getX() + 60 - 12,
				board.getY() - 7, endGameTimer,
				3, 4);
		}
	}

	public void addToScore(int value)
	{
		myScore = myScore + value;
	}

	public void addToSaveCount(Rescuee obj)
	{
		addToScore(10);
		saveCount = saveCount + 1;
		lifeCount = lifeCount - 1;
		if (lifeCount + releaseCount == 0)
		{
			state = ENDTHIS_LEVEL;
			running = false;
		}
/*
		try
		{
		    Manager.playTone(60, 100, 100);
		}
		catch (Exception e) { }
*/
		board.animateRescueeExiting();
	}

	public void addToDeadCount(Rescuee obj)
	{
		deadCount = deadCount + 1;
		lifeCount = lifeCount - 1;
		if (lifeCount + releaseCount == 0)
		{
			state = ENDTHIS_LEVEL;
			running = false;
		}
/*
		try
		{
		    Manager.playTone(10, 100, 100);
		}
		catch (Exception e) { }
*/
		int cx = obj.getVirtualX() / 12;
		int cy = obj.getVirtualY() / 12;
		board.animateRescueeDying(cx, cy);
	}

	public void run()
	{
		clear();

		myScore = 0;

		printer = new NumberPrinter(getImage("/images/F0.png"));

		board = new Board(getImage("/images/T0.png"),
			getImage("/images/F1.png"), this);
		board.setPosition((getWidth() - 120) / 2,
			(getHeight() - 128) / 2 + 7);
		board.loadLevel(0);

		releaseCount = 4;

		rescuer = new Rescuer(
			getImage("/images/S0.png"),
			board, this);

		rescueeList = new RescueeList(
			getImage("/images/S1.png"),
			board, this);

		startTimer();

		while (running)
		{
			board.paint(g);
			board.paintMarkers(g);
			rescueeList.paint(g);
			rescuer.paint(g);
			paintScore();
			paintTimer();
			flushGraphics();
//			keys = getKeyStates();
			rescuer.update();
			rescueeList.update();
			board.update();
		}

		clear();
		g.setColor(255, 255, 0);
		g.drawString("END OF LEVEL", 2, 2,
			Graphics.TOP | Graphics.LEFT);
		flushGraphics();
		while (getKeyStates() > 0) ;
		while (getKeyStates() == 0) ;
	}

	public void keyPressed(int keyCode)
	{
		int move = 0;
		if(keyCode == -1) move = GameCanvas.UP_PRESSED; else
		if(keyCode == -2) move = GameCanvas.DOWN_PRESSED; else
		if(keyCode == -3) move = GameCanvas.LEFT_PRESSED; else
		if(keyCode == -4) move = GameCanvas.RIGHT_PRESSED; else
		if(keyCode == -5) move = GameCanvas.FIRE_PRESSED;
		rescuer.move(move);
	}

	public void keyReleased(int keyCode)
	{
		rescuer.stop();
	}

	public void freeRelease()
	{
		if (releaseTimer > 1)
			releaseTimer = 1;
	}
}

