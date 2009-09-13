/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

class MainFrame extends JFrame
  implements ActionListener {
    private Canvas canvas = new Canvas();
    private JToolBar toolBar = new JToolBar();
    private JButton toolSaveAs = new JButton("Save as");
    private JButton toolQuit = new JButton("Quit");
    private JButton toolExit = new JButton("Exit");
    private JButton toolState = new JButton("State");
    private JButton toolTransition = new JButton("Transition");
    private JFileChooser chooser = new JFileChooser();
    private boolean isStateTool;
    private ToolState stateTool = new ToolState(canvas);
    private ToolTransition transitionTool = new ToolTransition(canvas);
    private String title = "Automata Editor";
    private File file;

    public MainFrame(Automaton initial, File f) {
        setSize(800,600);
        setBackground(Color.white);
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setFloatable(false);
        toolBar.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));
        initToolButton(toolState, KeyEvent.VK_S);
        initToolButton(toolTransition, KeyEvent.VK_T);
        toolBar.addSeparator();
        initToolButton(toolSaveAs, KeyEvent.VK_A);
        initToolButton(toolQuit,   KeyEvent.VK_Q);
        initToolButton(toolExit,   KeyEvent.VK_X);
        isStateTool = true;
        toolState.setEnabled(false);
        toolTransition.setEnabled(true);
        canvas.setTool(stateTool);
        getContentPane().add(toolBar, BorderLayout.NORTH);
        JScrollPane scroll_pane = new JScrollPane(canvas);
        getContentPane().add(scroll_pane, BorderLayout.CENTER);
        canvas.setScrollPane(scroll_pane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        if(initial != null) canvas.setAutomaton(initial);
        file = f;
        computeTitle(f);
        setLocationRelativeTo(null); 
        setVisible(true);
        canvas.commitTransaction(true);
    }

    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();
        if(src == toolSaveAs)
          saveFile();
        else if(src == toolExit) {
          if (doSave()) doQuit();
        }
        else if(src == toolQuit)
          doQuit();
        else if(src == toolState) {
          isStateTool = true;
          toolState.setEnabled(false);
          toolTransition.setEnabled(true);
          canvas.setTool(stateTool);
        }
        else if(src == toolTransition) {
          isStateTool = false;
          toolState.setEnabled(true);
          toolTransition.setEnabled(false);
          canvas.setTool(transitionTool);
        }
    }

    void initToolButton(JButton item, int mnemonic) {
        item.setMaximumSize(new java.awt.Dimension(70, 40));
        toolBar.add(item);
        item.setMnemonic(mnemonic);
        item.addActionListener(this);
    }

    private void computeTitle(File f) {
        if(f == null) setTitle(title);
        else          setTitle(title + " - " + f.getName());
    }

    public void doQuit() {
      dispose();
      vn.VN.editorFile = file;
    }

    private boolean doSave() {
        try {
          if (file == null) { 
            JOptionPane.showMessageDialog(null, "Save file before Exit");
            return false;
          }
          FileOutputStream fwrite = new FileOutputStream(file);
          PrintWriter fout = new PrintWriter(fwrite);
          canvas.getAutomaton().print(fout);
          fout.close();
          computeTitle(file);
          canvas.commitTransaction(true);
          return true;
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "File error");
            return false;
        }
    }

    public void saveFile() {
        JFileChooser chooser;
        if (file == null)
          chooser = new JFileChooser(vn.Config.getStringProperty("SOURCE_DIRECTORY"));
        else
          chooser = new JFileChooser(file);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
          "Automata files", "jff");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        String s = f.getName();
        if (!s.endsWith(vn.Config.jflapExt))
          f = new File(f.getPath() + vn.Config.jflapExt);
        if(f.exists()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Would you like to replace the existing file?",
                null,
                JOptionPane.YES_NO_OPTION);
            if(confirm != JOptionPane.YES_OPTION) return;
        }
        file = f;
        doSave();
    }
}
