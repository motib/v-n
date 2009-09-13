/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
//
// DRAWING TOOLS
//
class ToolTransition extends Tool {
    private Transition current = null;
    private State start = null;
    private int cur_x;
    private int cur_y;
    private Rectangle to_expose = null;

    public ToolTransition(Canvas canvas) {
        super(canvas);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void select(Graphics g) {
        super.select(g);
        start = null;
    }

    public void mousePressed(Graphics g, MouseEvent e) {
        start = getCanvas().getAutomaton().findState(e.getX(), e.getY(), g);
        if(start == null) {
            AutomatonComponent found = getCanvas().getAutomaton().find(e.getX(), e.getY(), g);
            if(found instanceof Transition) {
                current = (Transition) found;
            } else {
                current = null;
            }
        }
        mouseDragged(g, e);
    }

    public void mouseDragged(Graphics g, MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if(x < 0 || y < 0) return;
        if(start != null) {
            if(to_expose != null) getCanvas().expose(to_expose);

            cur_x = x;
            cur_y = y;
            draw(g);
            if(to_expose != null) getCanvas().expose(to_expose);
        }
        if(current != null) {
            current.expose(g);
            current.move(x, y);
            current.expose(g);
        }
        getCanvas().commitTransaction(false);
    }

    public void mouseReleased(Graphics g, MouseEvent e) {
        mouseDragged(g, e);
        if(to_expose != null) getCanvas().expose(to_expose);

        if(start != null) {
            State dst = getCanvas().getAutomaton().findState(e.getX(), e.getY(), g);
            if(start == null || dst == null) {
                ; // do nothing
            } else {
                Transition transition
                    = getCanvas().getAutomaton().addTransition(start, dst);
                if(transition != null) {
                    transition.addTransit("L");
                    transition.expose(g);
                    getCanvas().commitTransaction(true);
                }
            }
        }   
        to_expose = null;
        current = null;
        start = null;
    }

    public void draw(Graphics g) {
        if(start == null) return;

        int x = cur_x;
        int y = cur_y;
        State dst = getCanvas().getAutomaton().findState(x, y, g);
        if(start == dst) {
            to_expose = null;
            return;
        }

        if(dst != null) {
            x = dst.getX();
            y = dst.getY();
        }

        double th = Math.atan2(start.getY() - y, start.getX() - x);
        int[] xp = {
            (int) (x + Transition.ARROW_LEN
                        * Math.cos(th + Transition.ARROW_THETA)),
            (int) x,
            (int) (x + Transition.ARROW_LEN
                        * Math.cos(th - Transition.ARROW_THETA)),
        };
        int[] yp = {
            (int) (y + Transition.ARROW_LEN
                        * Math.sin(th + Transition.ARROW_THETA)),
            (int) y,
            (int) (y + Transition.ARROW_LEN
                        * Math.sin(th - Transition.ARROW_THETA)),
        };

        GraphicsUtil.switchToWidth(g, 3);
        g.setColor(Color.black);
        g.drawPolyline(xp, yp, 3);
        g.drawLine(start.getX(), start.getY(), x, y);

        to_expose = new Rectangle(x, y, 0, 0);
        to_expose.add(start.getX(), start.getY());
        to_expose.add(xp[0], yp[0]);
        to_expose.add(xp[2], yp[2]);
        to_expose.grow(3, 3);
    }
}
