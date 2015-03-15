VN is a tool for studying the behavior of nondeterministic finite automata (NDFA). It takes a description of an NDFA and generates a nondeterministic program; the program can then be executed randomly or guided interactively. VN can find one or moe accepting computation in an NDFA if they exist. The NDFA and the execution path are graphically displayed.

VN is based on other software tools that are included in the distribution:

The nondeterministic program generated from the NDFA is written in Promela, the language of the Spin model checker. VN uses the model checker, which is compatible with Spin and intended for educational use.

The graphical description of the NDFA and path are created in the dot language and layed out by the dot tool. Graphs in PNG format are created and are then displayed within VN. dot is part of Graphviz.

VN was designed with the help of Michal Armoni.

<b>Note:</b> If you wish to obtain the source code from the Subversion repository,
use the branch <tt>vne</tt>, because the trunk contains an older version
that is no longer supported.