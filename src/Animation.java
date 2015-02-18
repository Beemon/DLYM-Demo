public class Animation
{
	private boolean completed;
	private Board board;
	private int x;
	private int y;
	private int [] frames;
	private int old_value;
	private int notify;
	private int index;
	private int timer;

	public Animation(Board board, int x, int y, int [] frames, int notify)
	{
		this.board = board;
		this.x = x;
		this.y = y;
		this.frames = frames;
		this.notify = notify;
		completed = true;
	}

	public void start()
	{
		if (completed)
		{
			old_value = board.getCell(x, y);
			completed = false;
		}
		timer = 1;
		index = 0;
	}

	public void start(int x, int y)
	{
		if (!completed)
		{
			board.setCell(this.x, this.y, old_value);
		}
		old_value = board.getCell(x, y);
		completed = false;
		this.x = x;
		this.y = y;
		timer = 1;
		index = 0;
	}

	public void update()
	{
		if(!completed)
		{
			if (--timer > 0) return;
			timer = 15;
			int value = frames[index++];
			if(value != 0) board.setCell(x, y, value); else
			{
				board.setCell(x, y, old_value);
				if(notify > 0) board.notify(notify);
				completed = true;
			}
		}
	}
}
