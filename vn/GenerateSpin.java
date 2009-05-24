// Copyright 2006-9 by Mordechai (Moti) Ben-Ari. See VN.java. */
package vn;
import java.io.*;
/*
 * GenerateSpin
 *     Generates a Promela program for the NDFA
 */
class GenerateSpin {
	static private PrintWriter programWriter;

	static void emit(String s) { programWriter.println(s); }
	
	static void writePromela(String input, int inputLength) {
		if (Config.VERBOSE)
			VN.progress(Config.SPIN_WRITE + VN.fileName + Config.PromelaExt);
        try {
            programWriter = new PrintWriter(
            		new FileWriter(VN.fileRoot + Config.PromelaExt));
        } catch (IOException e) { VN.fileError(Config.PromelaExt); }
        
        Object[] stateObject = VN.states.toArray();
        java.util.Arrays.sort(stateObject);
        Object[] transitionObject = VN.transitions.toArray();
        java.util.Arrays.sort(transitionObject);
        
        int initial = 0;
        for (int i = 0; i < stateObject.length; i++) {
			if ( ((State) stateObject[i]).initial ) 
				{ initial = i; break; }
		}
        
        emit("byte i[" + (inputLength+1) + "];");
        emit("byte h, x;");
        emit("active proctype FA() {");

        if (VN.multiple)
        	for (int i = 0; i < inputLength; i++) {
        		String ifString = "\tif ";
        		for (char c : VN.symbols)
        			ifString = ifString + " :: i["+i+"] = '" + c + "'";
        		ifString = ifString + " fi;";
        		emit(ifString);
        }
        else
          for (int i = 0; i < inputLength; i++)
            emit("  i["+i+"] = '" + input.charAt(i) + "';");

        emit("  i[" + inputLength + "] = '.';"); // Dummy
        emit("  goto q" + initial + ";");

        int t = 0;   // Index of transitions
        int x = 0;   // Value of dummy variable
        for (Object sObject : stateObject) {
        	State st = (State) sObject;
        	emit("q" + st.name + ":");
        	emit("  printf(\"**q" + st.name + "\\n\");"); 
            emit("  if"); 
        	while (true) {
        		if (t == transitionObject.length) break;
        		Transition tr = (Transition) transitionObject[t];
        		if (tr.from.equals(st.name)) {
                	if(tr.letter == 'L') 
                		emit("  :: true -> x=x+" + (x++) + "; printf(\"**L\\n\"); goto q" + tr.to);
                	else 
                		emit("  :: i[h] == '" + tr.letter + 
                    		 "' -> x=x+" + (x++) + "; printf(\"**%c\\n\", i[h]); h++; goto q" + tr.to);
                	t++;
        		}
        		else break;
        	}
        	if (st.finalState) 
        		emit("  :: i[h] == '.' -> x=x+" + (x++) + "; goto accept" );
      		emit("  :: else -> goto reject" );
        	emit("  fi;");
        }
        emit("accept:");
        emit("  printf(\"**" + Config.RESULT_ACCEPT + "\\n\");");
        emit("  assert(false);");
        emit("  goto halt;");
        emit("reject:");
        emit("    printf(\"**" + Config.RESULT_REJECT + "\\n\");");
        emit("halt: skip");
        emit("}");
        programWriter.close();
	}
}
