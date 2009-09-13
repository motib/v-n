/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

class Canvas extends JPanel implements Scrollable {
    private static final int EXTRA_SPACE = 150;

    private JScrollPane parent = null;
    private Tool cur_tool = null;
    private boolean mouseDown = false;
    private Automaton automaton = null;
    private boolean dirty = false;
    private boolean suppress_repaint = false;

    public Canvas() {
        setBackground(Color.white);
        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseMotionListener());
        addKeyListener(new MyKeyListener());
        setAutomaton(new NFA());
    }

    public void setTool(Tool what) {
        if(cur_tool != null) cur_tool.deselect(getGraphics());
        cur_tool = what;
        cur_tool.select(getGraphics());
    }

    public Automaton getAutomaton() { return automaton; }

    public void setAutomaton(Automaton automaton) {
        automaton.setCanvas(null);
        this.automaton = automaton;
        automaton.setCanvas(this);
    }

    //
    // graphics methods
    //
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        dirty = false;
        automaton.draw(g);
        cur_tool.draw(g);
    }
    public void commitTransaction(boolean dirty) {
        computeSize();
    }
    public void setSuppressRepaint(boolean flag) {
        suppress_repaint = flag;
        if(!flag && dirty) repaint();
    }
    public void exposeAll() {
        expose(0, 0, getWidth(), getHeight());
    }
    public void expose(int x, int y, int width, int height) {
        if(!dirty) {
            dirty = true;
            if(!suppress_repaint) repaint(20);
        }
    }
    public void expose(Rectangle rect) {
        expose(rect.x, rect.y, rect.width, rect.height);
    }
    public void setScrollPane(JScrollPane parent) {
        this.parent = parent;
        computeSize();
    }
    public void computeSize() {
        Dimension ret = automaton.getDimensions(getGraphics());
        ret.setSize(ret.width + EXTRA_SPACE, ret.height + EXTRA_SPACE);
        if(parent != null) dimAdd(ret, parent.getViewport().getSize());
        setPreferredSize(ret);
        revalidate();
    }
    private void dimAdd(Dimension x, Dimension y) {
        if(y.width > x.width) x.setSize(y.width, x.height);
        if(y.height > x.height) x.setSize(x.width, y.height);
    }

    //
    // Scrollable methods
    //
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        if(direction == SwingConstants.VERTICAL) {
            return visibleRect.height / 10 * 10;
        } else {
            return visibleRect.width / 10 * 10;
        }
    }
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }
    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        return 10;
    }

    private class MyMouseMotionListener
            implements MouseMotionListener {
        public void mouseMoved(MouseEvent e) {
            Graphics g = getGraphics();
            cur_tool.mouseMoved(g, e);
        }
        public void mouseDragged(MouseEvent e) {
            if(mouseDown) {
                Graphics g = getGraphics();
                cur_tool.mouseDragged(g, e);
            }
        }
    }
    private class MyMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) {
            Graphics g = getGraphics();
            cur_tool.mouseExited(g, e);
        }
        public void mousePressed(MouseEvent e) {
            int mask = InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK;
            if(e.isPopupTrigger() || (e.getModifiers() & mask) != 0) {
                // mouse-press requests component drop-down menu
                mouseDown = false;
                int x = e.getX();
                int y = e.getY();
                Graphics g = getGraphics();
                AutomatonComponent comp = automaton.find(x, y, g);
                if(comp != null) comp.showMenu(e.getX(), e.getY());
            } else {
                // mouse-press represents tool use
                mouseDown = true;
                cur_tool.mousePressed(getGraphics(), e);
            }
            grabFocus();
        }
        public void mouseReleased(MouseEvent e) {
            if(mouseDown) {
                mouseDown = false;
                cur_tool.mouseReleased(getGraphics(), e);
                grabFocus();
            }
        }
    }
    private class MyKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) { }
        public void keyReleased(KeyEvent e) { }
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if(c != KeyEvent.CHAR_UNDEFINED) {
                Graphics g = getGraphics();
                cur_tool.keyTyped(g, c);
            }
        }
    }
}
