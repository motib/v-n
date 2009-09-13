/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

abstract class Automaton {
    private LinkedList<State> states = new LinkedList<State>();
    private LinkedList<Transition> transitions = new LinkedList<Transition>();
    private LinkedList<AutomatonComponent> components = new LinkedList<AutomatonComponent>();
    private StateSet current = new StateSet(this);
    private StateSet current_draw = current;
    private Canvas canvas = null;
    private Rectangle bounding = null;

    //
    // ABSTRACT METHODS
    //
    public abstract State createState();
    public abstract Transition createTransition(State src, State dst);

    //
    // ACCESS METHODS
    //
    public Canvas getCanvas() { return canvas; }

    public LinkedList getAutomatonStates() {
      return states;
    }

    public Iterator getStates() {
        return states.iterator();
    }
    
    public StateSet getInitialStates() {
        StateSet ret = new StateSet(this);
        for(Iterator it = getStates(); it.hasNext(); ) {
            State state = (State) it.next();
            if(state.isInitial()) ret.add(state);
        }
        return ret;
    }

    public Iterator getTransitions() {
        return transitions.iterator();
    }

    public Iterator getComponents() {
        return components.iterator();
    }

    public Iterator getAllComponents() {
        return Iterators.join(getTransitions(),
            Iterators.join(getStates(), getComponents()));
    }

    public Iterator getAllComponentsReverse() {
        return Iterators.join(Iterators.reverse(components),
            Iterators.join(Iterators.reverse(states),
                Iterators.reverse(transitions)));
    }

    //
    // CONFIGURATION METHODS
    //
    public void setCanvas(Canvas canvas) { this.canvas = canvas; }

    public void exposeConnections(Graphics g, State what) {
        for(Iterator it = getTransitions(); it.hasNext(); ) {
            Transition transition = (Transition) it.next();
            if(transition.getSource() == what
                    || transition.getDest() == what) {
                transition.expose(g);
            }
        }
    }

    public AutomatonComponent addComponent(AutomatonComponent what) {
        components.add(what);
        invalidateBounds();
        return what;
    }
    public void removeComponent(AutomatonComponent what) {
        components.remove(what);
    }

    public State addState(boolean isNew) {
        State q = createState();
        if(q != null) {
            if (isNew) q.setNewName();
            states.add(q);
            invalidateBounds();
        }
        return q;
    }
    public void removeState(State what) {
        current.remove(what);
        current_draw.remove(what);
        states.remove(what);

        Graphics g = null;
        if(canvas != null) g = canvas.getGraphics();

        LinkedList<Transition> to_remove = new LinkedList<Transition>();
        for(Iterator it = getTransitions(); it.hasNext(); ) {
            Transition transition = (Transition) it.next();
            if(transition.getSource() == what || transition.getDest() == what) {
                to_remove.add(transition);
            }
        }
        for(Iterator it = to_remove.iterator(); it.hasNext(); ) {
            Transition transition = (Transition) it.next();
            if(g != null) transition.expose(g);
            transitions.remove(transition);
        }
    }

    public Transition addTransition(State src, State dst) {
        Transition delta = createTransition(src, dst);
        if(delta != null) {
            transitions.add(delta);
            invalidateBounds();
        }
        return delta;
    }
    public void removeTransition(Transition what) {
        transitions.remove(what);
    }

    public void remove(AutomatonComponent comp) {
        if(comp instanceof State) {
            removeState((State) comp);
        } else if(comp instanceof Transition) {
            removeTransition((Transition) comp);
        } else {
            removeComponent(comp);
        }
    }

    //
    // GUI METHODS
    //
    public AutomatonComponent find(int x, int y, Graphics g) {
        for(Iterator it = getAllComponentsReverse(); it.hasNext(); ) {
            AutomatonComponent comp = (AutomatonComponent) it.next();
            if(comp.isIn(x, y, g)) return comp;
        }
        return null;
    }
    public State findState(int x, int y, Graphics g) {
        for(Iterator it = Iterators.reverse(states); it.hasNext(); ) {
            State state = (State) it.next();
            if(state.isIn(x, y, g)) return state;
        }
        return null;
    }
    public void draw(Graphics g) {
        for(Iterator it = getAllComponents(); it.hasNext(); ) {
            ((AutomatonComponent) it.next()).draw(g);
        }
    }

    //
    // BOUNDING BOX METHODS
    //
    public Dimension getDimensions(Graphics g) {
        if(bounding == null) computeBoundingBox(g);

        int width = bounding.width;
        if(bounding.x > 0) width = bounding.x + bounding.width;
        int height = bounding.height;
        if(bounding.y > 0) height = bounding.y + bounding.height;
        return new Dimension(width, height);
    }
    public Rectangle getBounds(Graphics g) {
        if(bounding == null) computeBoundingBox(g);
        return new Rectangle(bounding);
    }
    public void invalidateBounds() { bounding = null; }
    private void computeBoundingBox(Graphics g) {
        bounding = null;
        Rectangle box = new Rectangle();
        for(Iterator it = getAllComponents(); it.hasNext(); ) {
            AutomatonComponent comp = (AutomatonComponent) it.next();
            comp.getBounds(box, g);
            if(bounding == null) {
                bounding = new Rectangle(box);
            } else {
                bounding.add(box);
            }
        }
        if(bounding == null) bounding = new Rectangle();
        bounding.grow(5, 5);
    }

    //
    // FILE METHODS
    //
    public void print(PrintWriter fout) {
      int id = 0;
      fout.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><structure>&#13;");
      fout.println("<type>fa</type>&#13;<automaton>&#13;&#13;");
      for(Iterator it = getStates(); it.hasNext(); ) {
          State state = (State) it.next();
          fout.println("  <state id=\"" + id + "\" name=\"q" + id + "\">&#13;");
          id++;
          fout.println("    <x>" + state.getX() + "</x>");
          fout.println("    <y>" + state.getY() + "</y>&#13;");
			    if (state.isInitial()) fout.println("    <initial/>&#13;");
			    if (state.isFinal())   fout.println("    <final/>&#13;");
          fout.println("  </state>&#13;");
      }

      for(Iterator it = getTransitions(); it.hasNext(); ) {
          Transition transition = (Transition) it.next();
          int i = states.indexOf(transition.getSource());
          int j = states.indexOf(transition.getDest());
          fout.println("  <transition>&#13;");
          fout.println("    <from>" + i + "</from>&#13;");
          fout.println("    <to>" + j + "</to>&#13;");
          fout.println("    <read>" + transition.getTransits() + "</read>&#13;");
          fout.println("  </transition>&#13;");
      }
      fout.println("</automaton>&#13;");
      fout.println("</structure>");
    }

    public static Automaton read(java.io.File f) throws IOException {
        Automaton a = new NFA();
        new GetXML().getXML(f, a);
        return a;
    }
}
