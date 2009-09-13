/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
interface LabelOwner {
    int getLabelX(Label which);
    int getLabelY(Label which);
    int getLabelHAlign(Label which);
    int getLabelVAlign(Label which);
}
