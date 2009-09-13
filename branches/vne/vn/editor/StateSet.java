/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
import java.util.Iterator;
import java.util.LinkedList;
class StateSet {
    private Automaton automaton;
    private LinkedList<State> states = new LinkedList<State>();

    public StateSet(Automaton automaton) {
        this.automaton = automaton;
    }
    
    public int size() {
        return states.size();
    }
    public Iterator iterator() {
        return states.iterator();
    }
    public boolean contains(State what) {
        return states.contains(what);
    }
    public void remove(State what) {
        states.remove(what);
    }
    public void add(State state) {
        if(!states.contains(state)) {
            states.add(state);
        }
    }
}
