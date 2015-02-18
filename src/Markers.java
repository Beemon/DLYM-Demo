import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class Markers
{
	private Image image;
	private Board board;
	private int [] items;
	private int count;
	private boolean updated;

	public Markers(Image image, Board board)
	{
		this.board = board;
		this.image = image;
		items = new int [12];
		count = 0;
	}

	public int size()
	{
		return count;
	}

	public void add(int value)
	{
		items[count++] = value;
		updated = true;
	}

	public int get()
	{
		int value = items[0];
		for (int index = 0; index < count - 1; index++)
			items[index] = items[index + 1];
		count = count - 1;
		updated = true;
		return value;
	}

	public void paint(Graphics g)
	{
		if (!updated)
			return;

		int tiles = count;
		int bx = board.getX();
		int by = board.getY() - 7;
		int mx = bx + 90;

		g.setColor(0);
		g.fillRect(mx, by, 30, 6);

		if (tiles > 4)
		{
			tiles = 4;
			g.drawRegion(image, 24, 0, 6, 6,
				Sprite.TRANS_NONE, bx + 114, by,
				Graphics.TOP | Graphics.LEFT);
		}
		else
		{
			mx = (bx + 120) - (tiles * 6);
		}

		for (int index = 0; index < tiles; index++)
		{
			int value = items[index];
			g.drawRegion(image, value * 6, 0, 6, 6, 
				Sprite.TRANS_NONE, mx, by,
				Graphics.TOP | Graphics.LEFT);
			mx = mx + 6;
		}

		updated = false;
	}

}