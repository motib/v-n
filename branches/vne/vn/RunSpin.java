// Copyright 2006-9 by Mordechai (Moti) Ben-Ari. See VN.java. */

package vn;
import java.io.*;
import javax.swing.JOptionPane;

/*
 * RunSpin
 *     Forks a process to run Spin
 */

class RunSpin {
    static private String[] spinProgram = new String[Config.STATEMENTS];
    static private OutputStreamWriter output;

    static void readPromelaProgram() {
        BufferedReader sourceReader;
        String s = "";
        if (Config.VERBOSE) 
        	VN.progress(Config.SPIN_READ);
        try {  
            sourceReader = new BufferedReader(
                new FileReader(VN.fileRoot + Config.PromelaExt));
            int l = 0;
            while (true) {
                s = sourceReader.readLine();
                if (s == null) break;
                spinProgram[l++] = s;
            }
        } catch (IOException e) { VN.fileError(Config.PromelaExt); }
    }
    
    static void out(String s) {
        try {
            output.write(s);
            output.flush();
        }
        catch (IOException e) { 
        	e.printStackTrace();
        	System.exit(1);
        }
    }

    static void runSpin(VN.SpinMode spinMode) {
    	boolean allTrails = spinMode == VN.SpinMode.ALL_TRAILS;
    	if (allTrails) spinMode = VN.SpinMode.TRAIL;
        String inputString = "";
        readPromelaProgram();
        VN.pathArea.setText("");
        PrintWriter pathWriter = null;
        if (spinMode != VN.SpinMode.VERIFY)
	        try {
	            pathWriter = new PrintWriter(
	                new FileWriter(VN.fileRoot + Config.pathExt)); 
	        } catch (IOException e) { VN.fileError(Config.pathExt); }
        
        if (Config.VERBOSE) 
        	VN.progress(Config.RUN_ERIGONE + ": " + spinMode);
        Process p;
        ProcessBuilder pb = null;
        try {
            // Use ProcessBuilder to run Spin, redirecting ErrorStream
            if (spinMode == VN.SpinMode.RANDOM)
            	pb = new ProcessBuilder(
            		Config.getStringProperty("ERIGONE_COMMAND"), 
                  "-r", VN.fileName + Config.PromelaExt);
            else if (spinMode == VN.SpinMode.INTERACTIVE)
            	pb = new ProcessBuilder(
            		Config.getStringProperty("ERIGONE_COMMAND"), 
                  "-i", VN.fileName + Config.PromelaExt);
            else if (spinMode == VN.SpinMode.VERIFY)
            	pb = new ProcessBuilder(
                Config.getStringProperty("ERIGONE_COMMAND"),
            		"-s", "-m" + VN.pathNumber, VN.fileName + Config.PromelaExt);
            else if (spinMode == VN.SpinMode.TRAIL)
            	pb = new ProcessBuilder(Config.getStringProperty("ERIGONE_COMMAND"),
            		"-g" + (allTrails ? VN.pathNumber: ""),
                VN.fileName + Config.PromelaExt);
            // System.out.println(pb.command());
            File pf = new File(VN.fileRoot + Config.PromelaExt).getParentFile();
            if (pf != null) pb.directory(pf.getCanonicalFile());
            pb.redirectErrorStream(true);
            p = pb.start();
            // Connect to I/O streams
            InputStream istream = p.getInputStream();
            BufferedReader input =
                new BufferedReader(new InputStreamReader(istream));
            OutputStream ostream = p.getOutputStream();
            output = new OutputStreamWriter(ostream);
            // Process Spin output line by line
            boolean isState = true;
            String lastState = "";
            String[] choices = new String[Config.CHOICES];
            int numChoices = 0;
            String s = "";
            inputString = "";
            while (true) {
                s = input.readLine();
                // System.out.println(s);
                if (s == null) 
                    break;
                else if (s.startsWith("**")) {
                    s = s.substring(2);
                    pathWriter.println(s);
                    if (s.startsWith(Config.RESULT_ACCEPT) || 
                        s.startsWith(Config.RESULT_REJECT)) {
                        VN.pathArea.append(" : " + s);
                    }
                    else if (isState) {
                        VN.pathArea.append(s);
                        lastState = new String(s);
                    }
                    else {
                        VN.pathArea.append(" -" + s + "-> ");
                        if ((spinMode == VN.SpinMode.TRAIL) && !s.equals("L")) 
                        	inputString = inputString + s;
                    }
                    isState = !isState;
                }
                else if (s.startsWith("verification terminated=successfully,")) {
                  VN.pathArea.append(Config.NO_ACCEPT + (VN.pathNumber-1));
                  VN.pathNumber = 0;  // Signal to stop
                }
                else if ((spinMode != VN.SpinMode.INTERACTIVE)) {
                }
                else if (s.startsWith("executable transitions=")) {
                  numChoices = 0;
                }
                else if (s.startsWith("process=")) {
                  choices[numChoices++] = s;
                }
                else if (s.startsWith("choose from=")) {
                  pathWriter.flush();
                  VN.showGraph();
                  int choice = selectChoice(choices, numChoices, lastState);
                  if (choice == numChoices+1) {
                    VN.pathArea.append(" : " + Config.QUIT);
                    out("q\n");
                    break;
                  }
                  else
                    out(choice + "\n");
                  s = input.readLine();
                }
            }
            // Wait for Spin process to end
            p.waitFor();
        }
        catch (InterruptedException e) {  }
        catch (IOException e) { VN.fileError(Config.PromelaExt); }
        if (spinMode != VN.SpinMode.VERIFY)
        	pathWriter.close();
        if (spinMode == VN.SpinMode.TRAIL) 
        	if (!VN.inputs.contains(inputString)) 
        		VN.inputs.add(inputString);
    }
    
    static void printTokens(String[] tokens) {
    	for (int j = 0; j < tokens.length; j++) 
    		System.out.print("#"+tokens[j]);
    	System.out.println("#");
    }

    // Extract value from named association: "name=value,"
    public static String extract(String s, String pattern) {
      int i = s.indexOf(pattern);
      if (i == -1) return "";
      i = i + pattern.length();
      return s.substring(i, s.indexOf(",", i+1));
    }

    // Extract numeric value from named association: "name=value,"
    public static int extractNum(String s, String pattern) {
      int i = s.indexOf(pattern);
      if (i == -1) return -1;
      i = i + pattern.length();
      String t = s.substring(i, s.indexOf(",", i+1));
      try {
        return Integer.parseInt(t);
      }
      catch(NumberFormatException e) {
        return -1;
      }
    }

    static int selectChoice(String[] choices, int numChoices, String lastState) {
        String[] selections = new String[numChoices+1];
        String[] tokens;
        String source, target, message;
        int lineIndex, last, n;
        char letter = ' ';
        for (int i = 0; i < numChoices; i++) {
            lineIndex = extractNum(choices[i], "line=");
            source = spinProgram[lineIndex-1];
            tokens = source.split("\\s");
            printTokens(tokens);
            if (tokens[3].equals("true")) letter = 'L';
            else if (tokens[3].equals("i[h]")) letter = tokens[5].charAt(1);
            last = tokens.length-1;
            target = tokens[last].substring(0,tokens[last].length());
            if (letter == '.') selections[i] = Config.ACCEPT;
            selections[i] = "-" + letter + "->" + target;
        }
        choices[numChoices] = Config.QUIT;
        selections[numChoices] = Config.QUIT;
        message = Config.SELECT + lastState;
        n = JOptionPane.showOptionDialog(
            VN.messageArea, message, null, 
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            selections, selections[0]);
        if (n == JOptionPane.CLOSED_OPTION) return numChoices + 1;
        return n;
    }
}
