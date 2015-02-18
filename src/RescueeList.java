import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class RescueeList
{
	private GameModule module;
	private Board board;
	private Image image;
	private Vector vector;
	private int rescueCount;

	public RescueeList(Image image, Board board, GameModule module)
	{
		this.module = module;
		this.board = board;
		this.image = image;
		vector = new Vector();
	}

	public void addRescuee()
	{
		Rescuee obj = new Rescuee(this, image, board);
		vector.addElement(obj);
	}

	public void paint(Graphics g)
	{
		int count = vector.size();
		for (int index = 0; index < count; index++)
		{
			Rescuee obj = (Rescuee)vector.elementAt(index);
			if(!obj.getDisabled()) obj.paint(g);
		}
	}

	public void update()
	{
		int count = vector.size();
		for (int index = 0; index < count; index++)
		{
			Rescuee obj = (Rescuee)vector.elementAt(index);
			obj.update();
		}
	}

	public void rescueeExit(Rescuee obj)
	{
		module.addToSaveCount(obj);
	}

	public void rescueeKill(Rescuee obj)
	{
		module.addToDeadCount(obj);
	}
}
