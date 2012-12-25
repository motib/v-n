// Copyright 2006-9 by Mordechai (Moti) Ben-Ari. See VN.java. */
package vn;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Properties;

/*
 * Config - configuration:
 *   Directorie and file names and extensions
 *   Fonts, colors and sizes
 *   Button names and mnemonics
 *   Texts for messages and errors
 */

public class Config {
    static final String 	VERSION = " V3.2.2";
    static final String 	TITLE =
        "VN - Visualization of Nondeterminism" + VERSION;

	static private Properties properties = new Properties();
	static private final String FILE_NAME = "config.cfg";

	// Verbose output in progress pane
	static final boolean    VERBOSE = false;
	
	private static void setDefaultProperties() {
		properties.put("SOURCE_DIRECTORY", "examples");
    properties.put("HELP_FILE_NAME",	 "txt\\help.txt");
	  properties.put("ABOUT_FILE_NAME",	 "txt\\copyright.txt");
	  properties.put("ERIGONE_COMMAND",  "bin\\erigone.exe");
	  properties.put("DOT_COMMAND",		   "bin\\dot.exe");

		properties.put("HIGHLIGHT",  Integer.toString(0));  // Color
		properties.put("GRAPH_SIZE", Integer.toString(2));  // Large
    properties.put("WIDTH", 		 Integer.toString(1000));
    properties.put("HEIGHT", 		 Integer.toString(720));
    properties.put("LEFT_WIDTH", Integer.toString(500));
    properties.put("TB_DIVIDER", Integer.toString(530));
	}

    public
    static final String jflapExt   = ".jff";
    static final String dotExt     = ".dot";
    static final String graphExt   = ".png";
    static final String pathExt    = ".pth";
    static final String PromelaExt = ".pml";

    static final int 		STATES = 100;
    static final int 		TRANSITIONS = 200;
    static final int    STATEMENTS = 250;
    static final int    CHOICES = 10;
	  static final int 		DELTA = 100;  // Dummy node name offset

    static final int    BUTTON_WIDTH = 70;
    static final int    BUTTON_HEIGHT = 40;
    static final int    TEXT_WIDTH = 150;
    static final int    COLUMNS = 100;
    static final int		PATH_HEIGHT = 42;

    static final String FONT_FAMILY = "Lucida Sans Typewriter";
    static final int 		FONT_STYLE = java.awt.Font.PLAIN;
    static final int 		FONT_SIZE = 14;
    
    static final String INPUT    = "String or length: ";
    static final String OPEN     = "Open";
    static final int 		OPENMN   = KeyEvent.VK_O;
    static final String EDIT     = "Edit";
    static final int 		EDITMN   = KeyEvent.VK_E;
    static final String RANDOM   = "Random";
    static final int 		RANDOMMN = KeyEvent.VK_R;
    static final String CREATE   = "Create";
    static final int    CREATEMN = KeyEvent.VK_C;
    static final String FIND     = "Find";
    static final int    FINDMN   = KeyEvent.VK_F;
    static final String NEXT     = "Next";
    static final int    NEXTMN   = KeyEvent.VK_N;
    static final String GENERATE = "Generate";
    static final int 		GENMN    = KeyEvent.VK_G;
    static final String DFA      = "DFA";
    static final int 		DFAMN    = KeyEvent.VK_D;
    static final String HELP     = "Help";
    static final int 		HELPMN   = KeyEvent.VK_H;
    static final String ABOUT    = "About";
    static final int 		ABOUTMN  = KeyEvent.VK_A;
    static final String EXIT     = "Exit";
    static final int 		EXITMN   = KeyEvent.VK_X;

    static final int[]    GRAPH_FONT  = { 9, 12, 14 };
    static final float[]  GRAPH_WIDTH = { 0.2f, 0.3f, 0.4f };
    static final String[] HIGHLIGHT_TYPE = { " color=red", " style=bold" };

    static final String OPTIONS  = "Options";
    static final int 		OPTIONSMN= KeyEvent.VK_P;
    static final String OK       = "OK";
    static final int 		OKMN     = KeyEvent.VK_O;
    static final String CANCEL   = "Cancel";
    static final int 		CANCELMN = KeyEvent.VK_A;
	  static final String	SIZE	   = "Size";
	  static final String SMALL 	 = "Small";
    static final int 		SMALLMN  = KeyEvent.VK_S;
    static final String MEDIUM	 = "Medium";
    static final int 		MEDIUMMN = KeyEvent.VK_M;
    static final String LARGE	   = "Large";
    static final int 		LARGEMN  = KeyEvent.VK_L;
    static final String	HIGHLIGHT= "Highlight";
    static final String COLOR	   = "Color";
    static final int 		COLORMN  = KeyEvent.VK_C;
    static final String	BOLD	   = "Bold";
    static final int 		BOLDMN   = KeyEvent.VK_B;
    
    static final String	ENTER_NUMBER = "Enter a positive integer and select Generate";
    static final String FILE_ERROR   = "File error ";
    static final String	NO_INPUT     = "Enter a nonempty string or positive integer and select Generate";
    static final String	NO_JFF_FILE  = "Open a \"jff\" file with the automaton";
    static final String	NO_MULTIPLE  = "Enter a nonempty string and select Generate";
    
    static final String	GRAPH_WRITE  = "Writing graph ";
    static final String	READ_PATH    = "Reading path file";
    static final String READING      = "Reading ";
    static final String	RUN_DOT      = "Running dot";
    static final String	RUN_ERIGONE  = "Running Erigone";
    static final String	SPIN_WRITE   = "Generating program ";
    static final String SPIN_READ    = "Reading Promela program ";
    
    static final String   ACCEPT       = "Accept";
    static final String		ACCEPTS_ON	 = ", inputs: ";
    static final String		FINAL_STATE  = "Inputs accepted by final state q";
    static final String		GENERATED    = "Program generated";
    static final String   NO_ACCEPT    = "Number of accepting computations: ";
    static final String   QUIT         = "Quit";
    static final String   RESULT_ACCEPT= "Accepted!";
    static final String   RESULT_REJECT= "Rejected ...";
    static final String 	SELECT  	   = "Select a transition leaving ";
    static final int      SELECT_BUTTON= 120; 
    static final int      SELECT_HEIGHT= 70;

	// Initialize configuration file
    static void init() {
        setDefaultProperties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(FILE_NAME);
        } catch (FileNotFoundException e1) {
            System.out.println("Cannot open VN file, creating new file");
            try {
                saveFile();
                in = new FileInputStream(FILE_NAME);
            } catch (IOException e2) {
                System.err.println("Cannot write VN file");
            }
        }
        try {
            properties.load(in);
            in.close();
        } catch (IOException e3) {
            System.err.println("Cannot read VN file");
        }
    }

	// Save configuration file
    static void saveFile() {
        try {
            FileOutputStream out = new FileOutputStream(FILE_NAME);
            properties.store(out, "VN configuration file");
            out.close();
            System.out.println("Saved VN file " + FILE_NAME);
        } catch (IOException e2) {
            System.err.println("Cannot write VN file");
        }
    }

	// Interface to get/set propertyies of various types
    public static String getStringProperty(String s) {
        return properties.getProperty(s);
    }

    public static void setStringProperty(String s, String newValue) {
        properties.setProperty(s, newValue);
    }

    public static boolean getBooleanProperty(String s) {
        return Boolean.valueOf(properties.getProperty(s)).booleanValue();
    }

    public static void setBooleanProperty(String s, boolean newValue) {
        properties.setProperty(s, Boolean.toString(newValue));
    }

    public static int getIntProperty(String s) {
        return Integer.valueOf(properties.getProperty(s)).intValue();
    }

    public static void setIntProperty(String s, int newValue) {
        properties.setProperty(s, Integer.toString(newValue));
    }
}
