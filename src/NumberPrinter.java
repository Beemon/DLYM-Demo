import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class NumberPrinter
{
	private Image fontImage;

	public NumberPrinter(Image fontImage)
	{
		this.fontImage = fontImage;
	}

	public void print(Graphics g, int x, int y, int value, int color, int width)
	{
		int digit;
		int fy = color * 6;
		x = x + (width - 1) * 6;
		g.setColor(0);
		g.fillRect(x, y, width * 6, 6);
		while (width > 0)
		{
			digit = value % 10;
			value = value / 10;
			g.drawRegion(fontImage, digit * 6, fy, 6, 6,
				Sprite.TRANS_NONE, x, y,
				Graphics.TOP |
				Graphics.LEFT);
			x = x - 6;
			width = width - 1;
		}
	}

}