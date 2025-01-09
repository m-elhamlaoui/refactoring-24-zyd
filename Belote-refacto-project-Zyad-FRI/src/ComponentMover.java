import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentMover extends MouseAdapter {
    private Component target;
    private Point startDrag;
    private Point startLocation;

    public void registerComponent(Component component) {
        target = component;
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startDrag = e.getPoint();
        startLocation = target.getLocation();
    }
  

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startDrag != null && startLocation != null) {
            int x = startLocation.x + e.getX() - startDrag.x;
            int y = startLocation.y + e.getY() - startDrag.y;
            target.setLocation(x, y);
        }
    }
}
