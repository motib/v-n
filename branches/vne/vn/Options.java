// Copyright 2006-9 by Mordechai (Moti) Ben-Ari. See VN.java. */
package vn;
import javax.swing.*;
import java.awt.event.*;

class Options extends JFrame  implements ActionListener {
    private JRadioButton small;
    private JRadioButton medium;
    private JRadioButton large;
    private ButtonGroup  sizeGroup;

    private JRadioButton color;
    private JRadioButton bold;
    private ButtonGroup  highlightGroup = new ButtonGroup();

    private JButton ok          = new JButton(Config.OK);
    private JButton cancel      = new JButton(Config.CANCEL);
    
    private int size;  // 0 = small, 1 = medium, 2 = large
    private int high;  // 0 = color, 1 = bold

    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == ok) {
    		Config.setIntProperty("GRAPH_SIZE",  size);
    		Config.setIntProperty("HIGHLIGHT",   high);
        if (VN.file != null)
    		  Config.setStringProperty("SOURCE_DIRECTORY", 
    			  VN.file.getParentFile().toString());
    		Config.saveFile();
        dispose();
    	} else if (e.getSource() == cancel)
    		dispose();
      else if (e.getSource() == small)  size = 0;
      else if (e.getSource() == medium) size = 1;
      else if (e.getSource() == large)  size = 2;
      else if (e.getSource() == color)  high = 0;
      else if (e.getSource() == bold)   high = 1;
      else
        dispose();
    }
    
    void setItem(AbstractButton item, ButtonGroup b, JPanel p, int mn) {
    	item.setMnemonic(mn);
        item.addActionListener(this);
        if (b != null) b.add(item);
        p.add(item);
    }
    
    Options() {
    	size = Config.getIntProperty("GRAPH_SIZE");
    	high = Config.getIntProperty("HIGHLIGHT");
    	
      small  = new JRadioButton(Config.SMALL,  size == 0);
      medium = new JRadioButton(Config.MEDIUM, size == 1);
      large  = new JRadioButton(Config.LARGE,  size == 2);
      sizeGroup = new ButtonGroup();
        
      color  = new JRadioButton(Config.COLOR,  high == 0);
      bold   = new JRadioButton(Config.BOLD,   high == 1);
      highlightGroup = new ButtonGroup();
        
      JPanel sizePanel = new JPanel();
      sizePanel.setLayout(new java.awt.GridLayout(1,4));
      sizePanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.blue));
      sizePanel.add(new JLabel("  " + Config.SIZE));
      setItem(small, sizeGroup, sizePanel, Config.SMALLMN);
      setItem(medium, sizeGroup, sizePanel, Config.MEDIUMMN);
      setItem(large, sizeGroup, sizePanel, Config.LARGEMN);

      JPanel highlightPanel = new JPanel();
      highlightPanel.setLayout(new java.awt.GridLayout(1,4));
      highlightPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.blue));
      highlightPanel.add(new JLabel("  " + Config.HIGHLIGHT));
      setItem(color, highlightGroup, highlightPanel, Config.COLORMN);
      setItem(bold, highlightGroup, highlightPanel, Config.BOLDMN);
      highlightPanel.add(new JLabel());

      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new java.awt.GridLayout(1,2));
      buttonPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.blue));
      setItem(ok, null, buttonPanel, Config.OKMN);
      setItem(cancel, null, buttonPanel, Config.CANCELMN);
        
      getContentPane().setLayout(new java.awt.GridLayout(3,1));
      getContentPane().add(sizePanel);
      getContentPane().add(highlightPanel);
      getContentPane().add(buttonPanel);

      getRootPane().setDefaultButton(ok);
      getRootPane().registerKeyboardAction(this,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      setFont(new java.awt.Font(Config.FONT_FAMILY, Config.FONT_STYLE, Config.FONT_SIZE));
      setTitle(Config.OPTIONS);
      setSize(400, 150);
      setLocationRelativeTo(null); 
      setVisible(true);
    }
}
