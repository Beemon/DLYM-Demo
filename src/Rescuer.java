import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class Rescuer extends Sprite
{
	public static final int STATE_STOP = 0;
	public static final int STATE_MOVE_UP = 1;
	public static final int STATE_MOVE_RIGHT = 2;
	public static final int STATE_MOVE_DOWN = 3;
	public static final int STATE_MOVE_LEFT = 4;

	private int[] seq_roll_up = { 0, 1, 2, 3 };
	private int[] seq_roll_right = { 4, 5, 6, 7 };
	private int[] seq_roll_down = { 8, 9, 10, 11 };
	private int[] seq_roll_left = { 12, 13, 14, 15 };

	private GameModule module;
	private Board board;
	private int state;
	private int board_x;
	private int board_y;
	private int timer;
	private int x;
	private int y;
	private int lastKey;

	public Rescuer(Image image, Board board, GameModule module)
	{
		super(image, 12, 12);
		setFrameSequence(seq_roll_down);
		setFrame(0);
		this.module = module;
		this.board = board;
		board_x = board.getX();
		board_y = board.getY();
		moveToCell(
			board.getEntranceX(),
			board.getEntranceY()
		);
	}

	public void moveToCell(int x, int y)
	{
		setPosition(
			board_x + (this.x = x * 12),
			board_y + (this.y = y * 12)
		);
		board.setObject(x, y, 255);
	}

	private void clearPosition()
	{
		int cx = x / 12;
		int cy = y / 12;
		board.setObject(cx, cy, 0);
	}

	private boolean canMoveInto(int x, int y)
	{
		if (x < 0 || x >= 120) return false;
		if (y < 0 || y >= 120) return false;

		int cx = x / 12;
		int cy = y / 12;
		if(!board.isEmptyForRescuer(cx, cy)) return false;
		board.setObject(cx, cy, 255);
		return true;
	}

	private void moveUp()
	{
		int ny = y - 12;
		if (canMoveInto(x, ny))
		{
			clearPosition();
			state = STATE_MOVE_UP;
			setFrameSequence(seq_roll_up);
			setFrame(0);
		}
	}

	private void moveDown()
	{
		int ny = y + 12;
		if (canMoveInto(x, ny))
		{
			clearPosition();
			state = STATE_MOVE_DOWN;
			setFrameSequence(seq_roll_down);
			setFrame(0);
		}
	}

	private void moveLeft()
	{
		int nx = x - 12;
		if (canMoveInto(nx, y))
		{
			clearPosition();
			state = STATE_MOVE_LEFT;
			setFrameSequence(seq_roll_left);
			setFrame(0);
		}
	}

	private void moveRight()
	{
		int nx = x + 12;
		if (canMoveInto(nx, y))
		{
			clearPosition();
			state = STATE_MOVE_RIGHT;
			setFrameSequence(seq_roll_right);
			setFrame(0);
		}
	}

	private void putMarker()
	{
		int old_count = board.getMarkerCount();
		int cx = x / 12;
		int cy = y / 12;
		board.putMarker(cx, cy);
		if (old_count == 0 && board.getMarkerCount() == 0)
			module.freeRelease();
		state = STATE_STOP;
	}

	public void move(int key)
	{
		if(state == STATE_STOP)
		{
			if (key == GameCanvas.UP_PRESSED) moveUp();
			else if (key == GameCanvas.RIGHT_PRESSED) moveRight();
			else if (key == GameCanvas.DOWN_PRESSED) moveDown();
			else if (key == GameCanvas.LEFT_PRESSED) moveLeft();
			else if (key == GameCanvas.FIRE_PRESSED) putMarker();
			if(state != STATE_STOP) timer = 1;
		}
		lastKey = key;
	}

	public void stop()
	{
		lastKey = 0;
	}

	public void update()
	{
		if(state == STATE_STOP) return;
		if (--timer > 0) return;
		timer = 8;

		int nx = x;
		int ny = y;
		if (state == STATE_MOVE_UP) ny = ny - 3;
		else if (state == STATE_MOVE_RIGHT) nx = nx + 3;
		else if (state == STATE_MOVE_DOWN) ny = ny + 3;
		else if (state == STATE_MOVE_LEFT) nx = nx - 3;
		setPosition(board_x + (x = nx), board_y + (y = ny));
		int frame = getFrame() + 1;
		if (frame == 4)
		{
			frame = 0;
			state = STATE_STOP;
			if(lastKey != 0) move(lastKey);
			if(state == STATE_STOP) setFrameSequence(seq_roll_down);
			return;
		}
		setFrame(frame);
	}
}
