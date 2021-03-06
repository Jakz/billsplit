package com.github.jakz.billsplit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Optional;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.pixbits.lib.lang.StringUtils;
import com.pixbits.lib.ui.color.ColorCache;
import com.pixbits.lib.ui.color.HashColorCache;
import com.pixbits.lib.ui.color.PastelColorGenerator;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;

public class SummaryTablePanel<T> extends JPanel
{
  private final Mediator mediator;
  DataSource<SummaryEntry<T>> data;
  JTable table;
  TableModel<SummaryEntry<T>> model;
  BarTableCellRenderer barRenderer;
 
  SummaryBarBehavior<T> behavior;
  
  public SummaryTablePanel(Dimension dimension, Mediator mediator)
  {
    this.mediator = mediator;
    table = new JTable();
    table.setAutoCreateRowSorter(true);
    model = new TableModel<>(table, DataSource.empty());
    
    model.addColumn(new ColumnSpec<>("Label", String.class, se -> se.title));
    model.addColumn(new ColumnSpec<>("Value", Amount.class, se -> se.amount));
    
    barRenderer = new BarTableCellRenderer();
    ColumnSpec<SummaryEntry<T>, ?> barColumn = new ColumnSpec<>("", SummaryEntry.class, se -> se);
    barColumn.setRenderer(barRenderer);
    model.addColumn(barColumn);

    model.fireTableStructureChanged();
    
    setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(dimension);
    add(scrollPane, BorderLayout.CENTER);
  }
  
  public void setColorCache(ColorCache<T> colorCache)
  {
    barRenderer.setColorCache(s -> colorCache.get(s.data));
  }
  
  public void setData(DataSource<SummaryEntry<T>> data)
  {
    this.data = data;
    model.setData(data);
    model.fireTableDataChanged();
  }
  
  public void setBehavior(SummaryBarBehavior<T> behavior)
  {
    this.behavior = behavior;
  }
  
  class BarTableCellRenderer extends DefaultTableCellRenderer
  {
    private ColorCache<SummaryEntry<T>> colorCache;
    
    SummaryEntry<T> entry;
    int textReservedWidth;
    
    public BarTableCellRenderer()
    {
      colorCache = new HashColorCache<>(new PastelColorGenerator());
      
      setOpaque(true);
      
      FontMetrics metrics = getFontMetrics(getFont());
      textReservedWidth = metrics.stringWidth("100.0%") + 10;
    }
    
    public void setColorCache(ColorCache<SummaryEntry<T>> cache)
    {
      this.colorCache = cache;
    }
        
    private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font)
    {
      // Get the FontMetrics
      FontMetrics metrics = g.getFontMetrics(font);
      // Determine the X coordinate for the text
      int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
      // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
      int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
      // Set the font
      g.setFont(font);
      // Draw the String
      g.drawString(text, x, y);
    }

    @Override
    public void paintComponent(Graphics gx)
    {
      super.paintComponent(gx);
      int w = Math.max(getWidth() - textReservedWidth, 1);
      int rw = (int)(behavior.barWidthFor(entry) * w);

      Graphics2D g = (Graphics2D)gx;
      
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      g.setColor(colorCache.get(entry));
      g.fillRect(0, 0, rw, getHeight());
      g.setColor(g.getColor().darker());
      g.drawRect(0, 0, rw, getHeight()-1);

      g.setColor(Color.BLACK);      
      FontMetrics metrics = g.getFontMetrics(g.getFont());
      g.drawString(behavior.barLabelFor(entry), rw + 2, getHeight()/2 - metrics.getHeight()/2 + metrics.getAscent());
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);   
      this.entry = (SummaryEntry<T>)value;
      label.setText("");
      return label;
    }
  }
}
