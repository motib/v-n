/* Copyright (c) 2006, Carl Burch. License information is located in the
 * com.cburch.autosim.Main source code and at www.cburch.com/proj/autosim/. */
/* Changes for VN Copyright (C) 2009 by Moti Ben-Ari. GNU GPL */
package vn.editor;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

class Label {
    private LabelOwner owner;
    private String text = "";
    private static Font font = new Font("SansSerif", Font.BOLD, 16);

    static public Font getFont() { return font; }

    public Label(LabelOwner owner) {
        this.owner = owner;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Rectangle getBounds(Graphics g) {
        Rectangle ret = GraphicsUtil.getTextBounds(g, font, text,
            owner.getLabelX(this), owner.getLabelY(this),
            owner.getLabelHAlign(this), owner.getLabelVAlign(this));
        return ret;
    }
    public Rectangle getBounds(Rectangle bounds, Graphics g) {
        bounds.setBounds(getBounds(g));
        return bounds;
    }
    public Rectangle addToBounds(Rectangle bounds, Graphics g) {
        if(text.length() > 0) bounds.add(getBounds(g));
        return bounds;
    }

    public void draw(Graphics g) {
        if(text.length() == 0) return;
        g.setColor(Color.black);
        GraphicsUtil.drawText(g, font, text,
            owner.getLabelX(this), owner.getLabelY(this),
            owner.getLabelHAlign(this), owner.getLabelVAlign(this));
    }
    public void expose(Canvas canvas, Graphics g) {
        Rectangle rect = getBounds(g);
        canvas.expose(rect);
    }
    public void exposeCursor(Canvas canvas, Graphics g) {
        Rectangle rect = getBounds(g);
        canvas.expose(rect.x + rect.width, rect.y, 2, rect.height);
    }
    public void drawCursor(Graphics g) {
        Rectangle rect = getBounds(g);
        int x_pos = rect.x + rect.width + 1;
        int y_pos = rect.y;
        GraphicsUtil.switchToWidth(g, 1);
        g.setColor(Color.black);
        g.drawLine(x_pos, y_pos, x_pos, y_pos + g.getFontMetrics().getAscent());
    }
}
