import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class Rescuee extends Sprite
{
	private static final int STATE_MOVE_UP = 1;
	private static final int STATE_MOVE_RIGHT = 2;
	private static final int STATE_MOVE_DOWN = 3;
	private static final int STATE_MOVE_LEFT = 4;

	private static int[] seq_roll_up = { 0, 1, 2, 3 };
	private static int[] seq_roll_right = { 4, 5, 6, 7 };
	private static int[] seq_roll_down = { 8, 9, 10, 11 };
	private static int[] seq_roll_left = { 12, 13, 14, 15 };

	private RescueeList list;
	private boolean moving;
	private Board board;
	private int state;
	private int board_x;
	private int board_y;
	private int timer;
	private int x;
	private int y;
	private boolean disabled;

	public Rescuee(RescueeList list, Image image, Board board)
	{
		super(image, 12, 12);
		setFrameSequence(seq_roll_down);
		setFrame(0);
		this.board = board;
		this.list = list;
		board_x = board.getX();
		board_y = board.getY();
		MoveToCell(
			board.getEntranceX(),
			board.getEntranceY()
		);
		state = STATE_MOVE_DOWN;
		timer = 15;
	}

	public boolean getDisabled()
	{
		return disabled;
	}

	public void MoveToCell(int x, int y)
	{
		setPosition(
			board_x + (this.x = x * 12),
			board_y + (this.y = y * 12)
		);
		board.setObject(x, y, 254);
	}

	public void clearPosition()
	{
		int cx = x / 12;
		int cy = y / 12;
		board.setObject(cx, cy, 0);
	}

	public boolean canMoveInto(int x, int y)
	{
		if (x < 0 || x >= 120) return false;
		if (y < 0 || y >= 120) return false;

		int cx = x / 12;
		int cy = y / 12;
		if (!board.isEmpty(cx, cy)) return false;
		board.setObject(cx, cy, 254);
		return true;
	}

	private void moveUp()
	{
		int ny = y - 12;
		if (canMoveInto(x, ny))
		{
			clearPosition();
			moving = true;
			setFrameSequence(seq_roll_up);
			setFrame(0);
		}
		else state = STATE_MOVE_RIGHT;
	}

	private void moveDown()
	{
		int ny = y + 12;
		if (canMoveInto(x, ny))
		{
			clearPosition();
			moving = true;
			setFrameSequence(seq_roll_down);
			setFrame(0);
		}
		else state = STATE_MOVE_LEFT;
	}

	private void moveLeft()
	{
		int nx = x - 12;
		if (canMoveInto(nx, y))
		{
			clearPosition();
			moving = true;
			setFrameSequence(seq_roll_left);
			setFrame(0);
		}
		else state = STATE_MOVE_UP;
	}

	private void moveRight()
	{
		int nx = x + 12;
		if (canMoveInto(nx, y))
		{
			clearPosition();
			moving = true;
			setFrameSequence(seq_roll_right);
			setFrame(0);
		}
		else state = STATE_MOVE_DOWN;
	}

	public void checkForMovement()
	{
		if (state == STATE_MOVE_UP) moveUp();
		else if (state == STATE_MOVE_RIGHT) moveRight();
		else if (state == STATE_MOVE_DOWN) moveDown();
		else if (state == STATE_MOVE_LEFT) moveLeft();
	}

	public int getVirtualX() { return x; }
	public int getVirtualY() { return y; }

	private void checkUnderlyingTile()
	{
		int cx = x / 12;
		int cy = y / 12;
		int cell = board.getCell(cx, cy);
		switch(cell)
		{
			case 5: state = STATE_MOVE_UP; break;
			case 6: state = STATE_MOVE_RIGHT; break;
			case 7: state = STATE_MOVE_DOWN; break;
			case 8: state = STATE_MOVE_LEFT; break;
			case - 2:
				clearPosition();
				disabled = true;
				list.rescueeExit(this);
				break;
			case - 3:
				clearPosition();
				disabled = true;
				list.rescueeKill(this);
				break;
		}
	}

	public void update()
	{
		if (disabled)
			return;

		if (!moving)
		{
			checkForMovement();
			if (!moving) return;
			timer = 1;
		}

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
			moving = false;
			checkUnderlyingTile();
		}
		setFrame(frame);
 	}
}
