import java.awt.Color;
import java.awt.Point;

public interface PaintCom {
	Point getMousePosition();
	void pintar(Point p,Color c, float s);
}
