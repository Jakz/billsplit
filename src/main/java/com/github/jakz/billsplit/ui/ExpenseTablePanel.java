package com.github.jakz.billsplit.ui;

import java.awt.BorderLayout;
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
    
    model.addColumn(new ColumnSpec<>("Date", Timestamp.class, e -> e.timestamp()));
    model.addColumn(new ColumnSpec<>("Amount", Amount.class, e -> e.amount()));
    
    
    ColumnSpec<Expense, ExpenseAmounts> amountsColumn = new ColumnSpec<>("Amounts", ExpenseAmounts.class, e -> e.amounts());
    DefaultTableAndListRenderer<ExpenseAmounts> amountsRenderer = new DefaultTableAndListRenderer<>()
    {
      @Override
      public void decorate(JLabel label, JComponent source, ExpenseAmounts value, int index, boolean isSelected, boolean hasFocus)
      {
        String text = value.stream().map(s -> String.format("(%s, %s)", s.person.nickname(), s.value.toString())).collect(Collectors.joining(", ", "[ ", " ]"));
        label.setText(text);
      }
    };
    amountsColumn.setRenderer(amountsRenderer);
    model.addColumn(amountsColumn);
    
    model.fireTableStructureChanged();
    
    setLayout(new BorderLayout());
    add(new JScrollPane(table), BorderLayout.CENTER);
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
