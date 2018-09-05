package com.github.jakz.billsplit.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.ExpenseSet;
import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Timestamp;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.DefaultTableAndListRenderer;

public class ExpenseTablePanel extends JPanel
{
  private final Mediator mediator;
  DataSource<Expense> data;
  JTable table;
  TableModel<Expense> model;
  
  public ExpenseTablePanel(Mediator mediator)
  {
    this.mediator = mediator;
    table = new JTable();
    table.setAutoCreateRowSorter(true);
    model = new Model(table);
    
    ColumnSpec<Expense, ?> ordinalColumn = new ColumnSpec<>("#", Integer.class, (i,e) -> i);
    ordinalColumn.setWidth(60);
    model.addColumn(ordinalColumn);
    
    ColumnSpec<Expense, ?> dateColumn = new ColumnSpec<>("Date", Timestamp.class, e -> e.timestamp());
    dateColumn.setWidth(100);
    model.addColumn(dateColumn);
    
    model.addColumn(new ColumnSpec<>("Amount", Amount.class, e -> e.amount()));
    
    
    ColumnSpec<Expense, ExpenseAmounts> amountsColumn = new ColumnSpec<>("Amounts", ExpenseAmounts.class, e -> e.amounts());
    DefaultTableAndListRenderer<ExpenseAmounts> amountsRenderer = new DefaultTableAndListRenderer<>()
    {
      @Override
      public void decorate(JLabel label, JComponent source, ExpenseAmounts value, int index, boolean isSelected, boolean hasFocus)
      {
        if (value.isMultiple())
        {
          String text = value.stream().map(s -> String.format("%s: %s", s.person.nickname(), s.value.toString())).collect(Collectors.joining(", "/*, "[ ", " ]"*/));
          label.setText(text);
        }
        else
        {
          label.setText(String.format("%s: %s", value.get(0).person.nickname(), value.get(0).value.toString()));
        }
      }
    };
    amountsColumn.setRenderer(amountsRenderer);
    model.addColumn(amountsColumn);
    
    model.addColumn(new ColumnSpec<>("Type", String.class, e -> e.category().toString()));
    model.addColumn(new ColumnSpec<>("Desc", String.class, e -> e.title()));

    
    model.fireTableStructureChanged();
    
    setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(1200, 800));
    add(scrollPane, BorderLayout.CENTER);
  }
  
  public void setData(ExpenseSet expenses)
  {
    this.data = expenses;
    model.setData(expenses);
    model.fireTableDataChanged();
  }
  
  private class Model extends TableModel<Expense>
  {
    public Model(JTable table)
    {
      super(table, DataSource.empty());
    }
  }
}
