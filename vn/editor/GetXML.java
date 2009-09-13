/* Copyright (C) 2009 by Moti Ben-Ari. GNU GPL. See VN.java */
package vn.editor;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
public class GetXML extends DefaultHandler {
  	Automaton automaton;
	  State state = null;
    Transition transition = null;
    boolean initial = false;
    boolean finalState = false;
    int stateName = 0;
    String from = "";
    String to = "";
    char letter = ' ';
    String symbols;
    String current = null;
    String x, y;
    static public int maxName = -1;

    public void startDocument() throws SAXException {}
    public void endDocument() throws SAXException {}

    public void startElement(
    	String nURI, String l, String qName, Attributes attrs) 
    		throws SAXException {
      if (qName.equals("state")) {
         stateName = Integer.valueOf(attrs.getValue("id"));
        if (stateName > maxName) maxName = stateName;
      }
    	else if (qName.equals("initial"))
    		initial = true;
    	else if (qName.equals("final"))
    		finalState = true;
    	else if (qName.equals("from") || qName.equals("to") || 
    	         qName.equals("x")    || qName.equals("y") || 
               qName.equals("read")) {
    		current = qName;
    	}
    }

    public void characters(char buf[], int offset, int len)
    		throws SAXException {
    	if (current == null) return;
    	if (current.equals("from"))
    		from = new String(buf, offset, len);
    	else if (current.equals("to")) 
    		to = new String(buf, offset, len);
    	else if (current.equals("read")) {
    	  if (offset == 0) 
    	    symbols = "L";
    	  else
          symbols = new String(buf, offset, len);
      }
    	else if (current.equals("x"))
    	  x = new String(buf, offset, len);
    	else if (current.equals("y"))
    	  y = new String(buf, offset, len);
    	current = null;
    }

    public void endElement(
    	String nURI, String s, String qName) 
    		throws SAXException {
    	if (qName.equals("state")) {
        state = automaton.addState(false);
    	  state.setInitial(initial);
    	  state.setFinal(finalState);
    	  state.setName(stateName);
    	  state.setCoord(Float.valueOf(x), Float.valueOf(y));
    		initial = false; finalState = false;
    	}
    	else if (qName.equals("transition")) {
        transition = automaton.addTransition(
          (State) automaton.getAutomatonStates().get(Integer.valueOf(from)),
          (State) automaton.getAutomatonStates().get(Integer.valueOf(to))); 
        transition.addTransit(symbols);
        transition.setLabelText();
    	}                                                                         
    }

	public void getXML(File file, Automaton a) {
      automaton = a;
      maxName = -1;
      DefaultHandler handler = this;
      SAXParserFactory factory = SAXParserFactory.newInstance();
      try {
          SAXParser saxParser = factory.newSAXParser();
          saxParser.parse(file, handler);
      } catch (Throwable t) { t.printStackTrace(); }
  }
}
