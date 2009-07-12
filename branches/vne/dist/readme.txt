
            VN - Visualization of Nondeterminism
          
          Copyright 2007-9 by Mordechai (Moti) Ben-Ari
	           under the GNU Public License
              See copyright.txt and gpl.txt
           
            http://stwww.weizmann.ac.il/g-cs/benari/

For a given input string, a nondeterministic finite automata (NDFA)
can have more than one execution sequence. Sources of nondeterminism
include multiple transitions for the same letter from a state,
as well as lambda-transitions. VN nondeterministically executes a
finite automaton and displays the path in the graph for the automaton. 

A description of the automaton is created using the JFLAP package 
(http://jflap.org/).

VN (from version 3.0) uses the Erigone model checker
(http://code.google.com/p/erigone/) to run a nondeterministic program
and dot (http://www.graphviz.org/) to create graphics files.

1. Make sure that a JRE for Java 5 is installed.

2a. Run the Windows installer vn-N.exe.

or

2b. Unzip the archives vn-N.zip and vn-bin-N.zip into a clean directory
such as c:\vn.

Note that the source/jar archive vn-N.zip and the binary archive
vn-bin-N.zip may not be updated at the same time. Use the latest
versions of each one.

3. Run the jar file: javaw -jar vn.jar. The batch file run.bat contains
this command. The name of a JFLAP file (without the jff extension) can 
be given as a command line parameter. See the Help screen for
instructions on using VN.

4. To build, compile all Java programs and create the jar file.
The batch file build.bat is provided. Labels, messages, sizes,
commands, font and so on are in Config.java for easy modification.
