/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class AutomatonComponent {
    protected Automaton automaton;

    private class DeleteItem extends JMenuItem 
            implements ActionListener {
        public DeleteItem() {
            super("Delete");
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent evt) {
            AutomatonComponent.this.expose(automaton.getCanvas().getGraphics());
            AutomatonComponent.this.remove();
            automaton.getCanvas().commitTransaction(true);
        }
    }

    public AutomatonComponent(Automaton automaton) {
        this.automaton = automaton;
    }
    
    JMenuItem createDeleteItem() {
        return new DeleteItem();
    }

    public void createMenu(JPopupMenu menu) {
        menu.add(new DeleteItem());
    }

    Automaton getAutomaton() {
        return automaton;
    }

    public abstract Rectangle getBounds(Rectangle rect, Graphics g);
    public abstract boolean isIn(int x, int y, Graphics g);
    public abstract void remove();
    public abstract void draw(Graphics g);
    public abstract void showMenu(int x, int y);

    public void expose(Graphics g) {
        Rectangle rect = new Rectangle();
        automaton.getCanvas().expose(getBounds(rect, g));
    }
}