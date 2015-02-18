import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class Board extends TiledLayer
{
	private static final int NOTIFY_NONE = 0;
	private static final int NOTIFY_CREATE_RESCUEE = 1;

	private int[][] map;
	private Markers markers;
	private Image markerImage;
	private Animation anim_entering;
	private Animation anim_exiting;
	private Animation anim_dying;
	private GameModule module;
	private int[] seq_entrance = { 9, 10, 11, 12, 12, 12, 12, 12, 12, 12, 12, 11, 10, 9, 9, 9, 9, 9, 9, 9, 0 };
	private int[] seq_exit = { 13, 14, 15, 16, 15,14, 0 };
	private int[] seq_death_trap = { 25, 26, 27, 28, 27, 26, 25, 25, 25, 0 };
	private int[] seq_rescuee_sleeping = { 33, 34, 35, 36, 36, 36, 36, 35, 34, 33, 33, 33, 0 };
	private int[] seq_rescuer_entering = { 17, 18, 19, 20, 0 };
	private int[] seq_rescuee_entering = { 21, 22, 23, 24, 0 };
	private int[] seq_rescuee_exiting = { 24, 23, 22, 21, 0 };
	private int[] seq_rescuee_dying = { 29, 30, 31, 32, 0 };
	private int a_entrance;
	private int a_exit;
	private int a_death_trap;
	private int a_rescuee_sleeping;
	private int a_timer;
	private int enter_x;
	private int enter_y;
	private int exit_x;
	private int exit_y;

	public Board(Image tileImage, Image markerImage, GameModule module)
	{
		super(10, 10, tileImage, 12, 12);
		createAnimatedTile(seq_entrance[0]);
		createAnimatedTile(seq_exit[0]);
		createAnimatedTile(seq_death_trap[0]);
		createAnimatedTile(seq_rescuee_sleeping[0]);
		this.markerImage = markerImage;
		this.module = module;
		map = new int[10][10];
		a_timer = 15;
	}

	private void setEntrance(int x, int y)
	{
		enter_x = x;
		enter_y = y;
	}

	private void setExit(int x, int y)
	{
		exit_x = x;
		exit_y = y;
	}

	public boolean isEmpty(int x, int y)
	{
		int cell = getCell(x, y);
		return map[x][y] == 0 && cell < 33;
	}

	public boolean isEmptyForRescuer(int x, int y)
	{
		int cell = getCell(x, y);
		return map[x][y] == 0 && cell < 33 && cell >= 0;
	}

	public void setObject(int x, int y, int index)
	{
		map[x][y] = index;
	}

	public int getMarkerCount()
	{
		return markers.size();
	}

	public int getEntranceX() { return enter_x; }
	public int getEntranceY() { return enter_y; }

	public void loadLevel(int level)
	{
		int[] map = 
		{
			 37, 62, 39, -3, -3, -3, -3, 37, 62, 39,
			 48, -1, 48,  1,  1,  1,  1, 48, -2, 48,
			 48,  1, 48,  1,  1,  1,  1, 48,  1, 48,
			 48,  1, 48,  1,  1,  1,  1, 48,  1, 48,
			 48,  6, 56,  1,  1,  1,  1, 48,  1, 48,
			 48,  1,  1,  1,  1,  1, -3, 48,  1, 48,
			 48,  1, 40,  1, 64,  1,  1, 48,  1, 48,
			 48,  1, 48,  1,  1,  6,  1,  1,  5, 48,
			 48, -3, 48, -3,  1,  1,  1,  1, -3, 48,
			 53, 62, 55,  1, -3, -3, -3, 61, 62, 55
		};

		markers = new Markers(markerImage, this);

		int index = 0;
		for (int y = 0; y < 10; y++)
		for (int x = 0; x < 10; x++)
		{
			int value = map[index++];
			if (value == -1) setEntrance(x, y);	else
			if (value == -2) setExit(x, y); else 
			if (value >= 5 && value <= 8)
			{
				markers.add(value - 5);
				value = 1;
			}
  			setCell(x, y, value);
		}

		anim_entering = new Animation(this, enter_x, enter_y,
			seq_rescuee_entering, NOTIFY_CREATE_RESCUEE);
		anim_exiting = new Animation(this, exit_x, exit_y,
			seq_rescuee_exiting, NOTIFY_NONE);
		anim_dying = new Animation(this, -1, -1,
			seq_rescuee_dying, NOTIFY_NONE);

		a_entrance = 0;
		a_exit = 0;
		a_death_trap = 0;
		a_rescuee_sleeping = 0;
	}

	public void animateRescueeEntering()
	{
		anim_entering.start();
	}

	public void animateRescueeExiting()
	{
		anim_exiting.start();
	}

	public void animateRescueeDying(int x, int y)
	{
		anim_dying.start(x, y);
	}

	public void notify(int value)
	{
		if (value == NOTIFY_CREATE_RESCUEE) module.onRescueeCreate();
	}


	private void animateEntrance()
	{
		int frame = seq_entrance[a_entrance++];
		if (frame == 0) frame = seq_entrance[a_entrance = 0];
		setAnimatedTile(-1, frame);
	}

	private void animateExit()
	{
		int frame = seq_exit[a_exit++];
		if (frame == 0) frame = seq_exit[a_exit = 0];
		setAnimatedTile(-2, frame);
	}

	private void animateDeathTrap()
	{
		int frame = seq_death_trap[a_death_trap++];
		if (frame == 0) frame = seq_death_trap[a_death_trap = 0];
		setAnimatedTile(-3, frame);
	}

	private void animateRescueeSleeping()
	{
		int frame = seq_rescuee_sleeping[a_rescuee_sleeping++];
		if (frame == 0) frame = seq_rescuee_sleeping[a_rescuee_sleeping = 0];
		setAnimatedTile(-4, frame);
	}

	public void paintMarkers(Graphics g)
	{
		markers.paint(g);
	}

	public void putMarker(int cx, int cy)
	{
		int cell = getCell(cx, cy);
		if (cell == 1)
		{
			if (markers.size() > 0)
				setCell(cx, cy, markers.get() + 5);
			return;
		}
		if (cell >= 5 && cell <= 8)
		{
			markers.add(cell - 5);
			setCell(cx, cy, 1);
		}
	}

	public void update()
	{
		a_timer = a_timer - 1;
		anim_entering.update();
		anim_exiting.update();
		anim_dying.update();
		if(a_timer == 15) animateEntrance(); else
		if(a_timer == 11) animateExit(); else
		if(a_timer ==  7) animateDeathTrap(); else
		if(a_timer ==  4) animateRescueeSleeping(); else
		if(a_timer ==  0) a_timer = 15;
	}
}
