/*
  VN - Visualization of Nondeterminism.
  Copyright 2006-9 by Mordechai (Moti) Ben-Ari.
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA.
*/

package vn;
import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class VN extends JFrame implements ActionListener {
  enum   SpinMode { RANDOM, INTERACTIVE, VERIFY, TRAIL, ALL_TRAILS };

  static File     file;
  static String   fileName;
  static String   fileRoot;
  static int	    pathNumber;			  // Counter of multiple paths
  static boolean  multiple = false;	// Search for multiple inputs
  static String   input;		        // Textfield for input or length
  static int 		  currentState;		  // Current final state for DFA
  static String	  partition;			  // Equivalence classes for DFA
  public static File editorFile;    // File returned from editor

    // Databases of states and transitions for FA and path
	static ArrayList<State> states = 
		new ArrayList<State>(Config.STATES);
	static ArrayList<Transition> transitions = 
        new ArrayList<Transition>(Config.TRANSITIONS);

	static ArrayList<State> pathStates = 
		new ArrayList<State>(Config.STATES);
	static ArrayList<Transition> pathTransitions = 
        new ArrayList<Transition>(Config.TRANSITIONS);
    
	// For nondeterministic generation of input strings:
	//   the symbols that can appear in the input string
	//   and the generated strings
	static ArrayList<Character> symbols = 
        new ArrayList<Character>();
	static ArrayList<String> inputs = 
        new ArrayList<String>();

	// User interface components
    static JTextArea messageArea = new JTextArea();
    static JScrollPane messageScrollPane = new JScrollPane(messageArea);
    static JTextArea pathArea = new JTextArea();
    static JScrollPane pathAreaPane;
    static JTextField inputField = new JTextField(10);
    static JSplitPane topSplitPane;
    static JSplitPane mainSplitPane;

    static java.awt.Font font = new java.awt.Font(
            Config.FONT_FAMILY, Config.FONT_STYLE, Config.FONT_SIZE);

    static JToolBar toolBar = new JToolBar();
    static JButton toolOpen = new JButton(Config.OPEN);
    static JButton toolEdit = new JButton(Config.EDIT);
    static JButton toolRandom = new JButton(Config.RANDOM);
    static JButton toolCreate = new JButton(Config.CREATE);
    static JButton toolGenerate = new JButton(Config.GENERATE);
    static JButton toolFind = new JButton(Config.FIND);
    static JButton toolNext = new JButton(Config.NEXT);
    static JButton toolOptions = new JButton(Config.OPTIONS);
    static JButton toolHelp = new JButton(Config.HELP);
    static JButton toolDFA = new JButton(Config.DFA);
    static JButton toolAbout = new JButton(Config.ABOUT);
    static JButton toolExit = new JButton(Config.EXIT);

    // Display messages
    static void fileError(String ext) {
        progress(Config.FILE_ERROR + fileName + ext);
    }
    
    static void progress(String s) {
    	messageArea.append(s + "\n");
    }
    

    static void readAndShow(String f) {
        new ReadXML().readXML(file);
    	WriteGraph.writeGraph(f, states, transitions);
        displayGraphicsFile(f);
    }
    
    static void setFileName(File f) {
    	file = f;
    	fileName = f.getName();
 		  if (Config.VERBOSE) progress(Config.READING + fileName);
        fileName = f.getName().substring(0, fileName.lastIndexOf('.'));
        try {
            fileRoot = file.getCanonicalPath();
            fileRoot = fileRoot.substring(0, fileRoot.lastIndexOf('.'));
        } catch (IOException e) {}
    }

    static void displayGraphicsFile(String which) {
    	if (Config.VERBOSE)
    		progress("Displaying " + VN.fileName + '-' + which + Config.graphExt);
        JScrollPane s = new JScrollPane(
        	new ImagePanel(VN.fileRoot + '-' + which + Config.graphExt));
       	if (which.equals("path")) topSplitPane.setRightComponent(s);
       	else topSplitPane.setLeftComponent(s);
        topSplitPane.setDividerLocation(Config.getIntProperty("LEFT_WIDTH"));
    }

    static void showGraph() {
    	ReadPath.readPath(true);
    	WriteGraph.writeGraph("path", pathStates, pathTransitions);
    	displayGraphicsFile("path");
    	ReadPath.readPath(false);
    	readAndShow("fa-path");
    }

    private void openFile(File f) {
      messageArea.setText("");
      clearAreas();
      setFileName(f);
      setTitle(Config.TITLE + " - " + fileName);
      readAndShow("fa");
    }

    // Wait until editor is finished and then reload NDFA file
    private class RunThread extends Thread {
      public void run() {
        while (editorFile == null)
          try { Thread.sleep(50); }
          catch (InterruptedException e) {}
        openFile(editorFile);
      }
    }

    private void runAndWait(String name) {
      if (name == null)
        vn.editor.Main.main(new String[]{});
      else
        vn.editor.Main.main(new String[]{name});
      editorFile = null;
      RunThread t = new RunThread();
      t.start();
    }

    // Listener
    public void actionPerformed(ActionEvent e) {
    	int inputLength;
        if ((e.getSource() == toolOpen)) {
            JFileChooser fileChooser =
            	new JFileChooser(
            		Config.getStringProperty("SOURCE_DIRECTORY"));
            if (file != null) fileChooser.setSelectedFile(file);
            fileChooser.setFileFilter(new JFFFileFilter());
            if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) 
            	return;
            else {
              File f = fileChooser.getSelectedFile();
              if (!f.exists()) {
                progress(Config.FILE_ERROR);
                return;
              }
              else
                openFile(f);
            }
        }

        else if ((e.getSource() == toolEdit)) {
         	if (file != null)
         	  runAndWait(file.getAbsolutePath());
          else
         	  runAndWait(null);
        } 

        else if (e.getSource() == toolGenerate) {
        	if (file == null) {	progress(Config.NO_JFF_FILE); return; }
        	input = inputField.getText();
        	VN.multiple = false;
        	try {
        		inputLength = Integer.parseInt(input);
        		VN.multiple = true;
        	}
        	catch (NumberFormatException ex){
        		inputLength = input.length();
        	}
        	symbols.clear();
        	currentState = states.size() - 1;
        	partition = "";
        	clearAreas();
        	readAndShow("fa");
          if (inputLength < 1) { VN.progress(Config.NO_INPUT); return; }
          GenerateSpin.writePromela(input, inputLength);
        	pathArea.setText(Config.GENERATED);
        }
        
        else if ((e.getSource() == toolRandom) || 
       		 	 (e.getSource() == toolCreate) ||
       		 	 (e.getSource() == toolFind) ||
        		 (e.getSource() == toolNext)) {
        	SpinMode spinMode;
        	if (e.getSource() == toolRandom) {
        		if (multiple) { progress(Config.NO_MULTIPLE); return; }
        		spinMode = SpinMode.RANDOM; 
        	}
            else if (e.getSource() == toolCreate) { 
        		if (multiple) { progress(Config.NO_MULTIPLE); return; }
            	spinMode = SpinMode.INTERACTIVE;
            }
        	else if (e.getSource() == toolFind) {
        		inputs.clear();
        		clearAreas();
        		displayGraphicsFile("fa");
        		spinMode = SpinMode.VERIFY;
        		pathNumber = 1;
        	}
        	else if (e.getSource() == toolNext) {
        		spinMode = SpinMode.VERIFY;
        		pathNumber++;
        	}
        	else return;
	        if (!(new File(fileRoot + Config.PromelaExt)).exists()) {
	        	progress(Config.NO_INPUT);
	        	return;
	        }
	        else {
            RunSpin.runSpin(spinMode);
            if (spinMode == SpinMode.VERIFY) {
              if (VN.pathNumber != 0)  // Flag to stop
                RunSpin.runSpin(SpinMode.TRAIL);
              else if (multiple)
                pathArea.append(Config.ACCEPTS_ON + inputs);
            }
	        }
        	showGraph();
        }

        else if (e.getSource() == toolDFA) {
        	if (file == null) { progress(Config.NO_JFF_FILE); return; }
        	input = inputField.getText();
        	VN.multiple = false;
        	try {
        		inputLength = Integer.parseInt(input);
        		VN.multiple = true;
        	}
        	catch (NumberFormatException ex){ 
        		VN.progress(Config.ENTER_NUMBER);
        		return;
        	}
        	if (inputLength < 1) { VN.progress(Config.ENTER_NUMBER); return; }
        	for (State s: states) s.finalState = false;
        	currentState = (currentState + 1) % states.size();
        	states.get(currentState).finalState = true;
        	GenerateSpin.writePromela(input, inputLength);
          pathNumber = 0;
        	RunSpin.runSpin(SpinMode.VERIFY);
          inputs.clear();
          pathNumber = 1;
          do {
            RunSpin.runSpin(SpinMode.ALL_TRAILS);
            pathNumber++;
          } while (pathNumber <= states.size());
          pathArea.setText(Config.FINAL_STATE + currentState + ": "+ inputs);
          partition = partition + "q" + currentState + ": "+ inputs + "\n";
          WriteGraph.writeGraph("fa", states, transitions);
          displayGraphicsFile("fa");
          if (currentState == states.size() - 1) {
            JTextArea p = new JTextArea(partition);
            p.setFont(font);
            topSplitPane.setRightComponent(new JPanel().add(p));
          }
        }
        
        else if (e.getSource() == toolOptions)
            new Options();
        
        else if (e.getSource() == toolHelp)
            new DisplayFile(font, messageArea,
                Config.getStringProperty("HELP_FILE_NAME"), Config.HELP);

      else if (e.getSource() == toolAbout)
            new DisplayFile(font, messageArea,
                Config.getStringProperty("ABOUT_FILE_NAME"), Config.ABOUT);

        else if (e.getSource() == toolExit) {
        	System.exit(0);
        }
    }

    // Clear all areas before opening file or creating new file
    void clearAreas() {
        pathArea.setText("");
        topSplitPane.setLeftComponent(new JPanel());
        topSplitPane.setRightComponent(new JPanel());
        topSplitPane.setDividerLocation(Config.getIntProperty("LEFT_WIDTH"));
        validate();
    }

    void initToolButton(JButton item, int mnemonic) {
        item.setMaximumSize(
            new java.awt.Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
        toolBar.add(item);
        item.setMnemonic(mnemonic);
        item.addActionListener(this);
    }
    
    // Initialize toolbar
    void initToolBar() {
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setFloatable(false);
        toolBar.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));
        initToolButton(toolOpen, Config.OPENMN);
        initToolButton(toolEdit, Config.EDITMN);
        toolBar.addSeparator();
        toolBar.add(new JLabel(Config.INPUT));
        inputField.setMaximumSize(
            new java.awt.Dimension(Config.TEXT_WIDTH, Config.BUTTON_HEIGHT));
        toolBar.add(inputField);
        initToolButton(toolGenerate, Config.GENMN);
        toolBar.addSeparator();
        initToolButton(toolRandom, Config.RANDOMMN);
        initToolButton(toolCreate, Config.CREATEMN);
        toolBar.addSeparator();
        initToolButton(toolFind, Config.FINDMN);
        initToolButton(toolNext, Config.NEXTMN);
        toolBar.addSeparator();
        initToolButton(toolDFA, Config.DFAMN);
        toolBar.addSeparator();
        initToolButton(toolOptions, Config.OPTIONSMN);
        initToolButton(toolHelp, Config.HELPMN);
        initToolButton(toolAbout, Config.ABOUTMN);
        toolBar.addSeparator();
        initToolButton(toolExit, Config.EXITMN);
    }

    void init() {
		// Set properties of text areas
        messageArea.setFont(font);
        pathArea.setFont(font);
        inputField.setFont(font);
		
		// Create menus and toolbar
        initToolBar();

		// Set up frame with panes
        topSplitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, new JPanel(), new JPanel());
        topSplitPane.setOneTouchExpandable(true);
        topSplitPane.setDividerLocation(Config.getIntProperty("LEFT_WIDTH"));

        mainSplitPane = new JSplitPane(
        	JSplitPane.VERTICAL_SPLIT, topSplitPane, messageScrollPane);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setDividerLocation(Config.getIntProperty("TB_DIVIDER"));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new java.awt.BorderLayout());
        topPanel.add(toolBar, java.awt.BorderLayout.NORTH);
        pathArea.setColumns(Config.COLUMNS);
        pathArea.setBorder(BorderFactory.createEmptyBorder(4,4,0,4));
        pathAreaPane = new JScrollPane(pathArea, 
        	JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pathAreaPane.setPreferredSize(
        	new java.awt.Dimension(
            Config.getIntProperty("WIDTH")-50,Config.PATH_HEIGHT));
        topPanel.add(pathAreaPane, java.awt.BorderLayout.SOUTH);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new java.awt.BorderLayout());
        contentPane.add(topPanel, java.awt.BorderLayout.NORTH);
        contentPane.add(mainSplitPane, java.awt.BorderLayout.CENTER);
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Configuration JFrame and make visible
        setFont(font);
        setTitle(Config.TITLE);
        setSize(Config.getIntProperty("WIDTH"), Config.getIntProperty("HEIGHT"));
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    public static void main(java.lang.String[] args) {
        final String s = ((args.length > 0) ? args[0] + Config.jflapExt : "");
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    VN vn = new VN();
                    Config.init();
                    vn.init();
                    if (s != "") {
                    	File f = new File(s);
                    	if (!f.exists()) {
                    		System.err.println(Config.FILE_ERROR + s);
                    		System.exit(1);
                    	}
                    	VN.setFileName(f);
                      vn.setTitle(Config.TITLE + " - " + f);
                    	readAndShow("fa");
                    }
                }
            });
    }
}
