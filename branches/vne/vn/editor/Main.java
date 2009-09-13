/*
 * Copyright (c) 2006, Carl Burch.
 * 
 * This file is part of the Automaton Simulator source code. The latest
 * version is available at http://www.cburch.com/proj/autosim/.
 *
 * Automaton Simulator is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * Automaton Simulator is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Automaton Simulator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301  USA
 */

/*
  The editor of the Automaton Simulator has been modified for
  use in the software package VN - Visualization of Nondeterminism.
  The modification are Copyright 2009 by Mordechai (Moti) Ben-Ari.
 
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
package vn.editor;

import java.io.File;
import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        Automaton automaton = null;
        File f = null;
        if(args.length > 0) {
            try {
                f = new File(args[0]);
                automaton = Automaton.read(f);
            } catch(IOException e) {
                System.err.println(e.getMessage());
                return;
            }
        }
        MainFrame win = new MainFrame(automaton, f);
        win.setVisible(true);
    }
}
