package com.github.jakz.billsplit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.billsplit.Expense;
import com.github.jakz.billsplit.ExpenseAmounts;
import com.github.jakz.billsplit.ExpenseSet;
import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Timestamp;
import com.pixbits.lib.ui.color.ColorCache;
import com.pixbits.lib.ui.color.HashColorCache;
import com.pixbits.lib.ui.color.PastelColorGenerator;
import com.pixbits.lib.ui.color.PleasantColorGenerator;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.DefaultTableAndListRenderer;
import com.pixbits.lib.ui.table.renderers.LambdaListRenderer;

public class ExpenseTablePanel extends JPanel
{
  private final Mediator mediator;
  DataSource<Expense> data;
  JTable table;
  TableModel<Expense> model;
  
  
  private ColumnColorMode colorMode;
  private Currency currency;
  boolean keepNativeCurrencyIfSingle;

  private final HashColorCache<Object> colorCache;

  JComboBox<ColumnColorMode> colorModeComboBox;
  JComboBox<Currency> currencyComboBox;
  JCheckBox keepNativeCurrencyIfSingleComboBox;
  
  enum ColumnColorMode
  {
    NONE("No Color", e -> null),
    BY_CATEGORY("By Category", e -> e.category()),
    BY_SPENDER("By Spender", e -> e.amounts().stream().map(a -> a.person).collect(Collectors.toSet())),
    BY_DAY("By Day", e -> e.timestamp())
    ;
    
    private ColumnColorMode(String caption, Function<Expense, Object> keyMapper)
    {
      this.caption = caption;
      this.keyMapper = keyMapper;
    }
    
    public final Function<Expense, Object> keyMapper;
    public final String caption;
    
    public String toString() { return caption; }
  };
  

  

  
  public ExpenseTablePanel(Mediator mediator)
  {
    this.mediator = mediator;
    
    keepNativeCurrencyIfSingle = false;
    keepNativeCurrencyIfSingleComboBox = new JCheckBox("Keep native if single");
    keepNativeCurrencyIfSingleComboBox.addActionListener(e -> { 
      keepNativeCurrencyIfSingle = keepNativeCurrencyIfSingleComboBox.isSelected();
      model.fireTableDataChanged();
    }); 
    
    colorMode = ColumnColorMode.NONE;
    colorCache = new HashColorCache<>(new PastelColorGenerator());
    colorModeComboBox = new JComboBox<>(ColumnColorMode.values());
    colorModeComboBox.addItemListener(e -> { 
      if (e.getStateChange() == ItemEvent.SELECTED) 
        colorModeChanged();
    });
    
    currency = Currency.NONE;
    List<Currency> currencies = Arrays.asList(Currency.values());
    currencies.sort(Currency.COMPARATOR_BY_NAME);
    currencyComboBox = new JComboBox<>(currencies.toArray(new Currency[currencies.size()]));
    currencyComboBox.setRenderer(LambdaListRenderer.<Currency>of((v, l) -> l.setText(v.longName())));
    currencyComboBox.addItemListener(e -> {
      this.currency = currencyComboBox.getItemAt(currencyComboBox.getSelectedIndex());
      model.fireTableDataChanged();
    });
    
    table = new Table();
    table.setAutoCreateRowSorter(true);
    model = new Model(table);
    
    ColumnSpec<Expense, ?> ordinalColumn = new ColumnSpec<>("#", Integer.class, (i,e) -> i);
    ordinalColumn.setWidth(60);
    model.addColumn(ordinalColumn);
    
    ColumnSpec<Expense, ?> dateColumn = new ColumnSpec<>("Date", Timestamp.class, e -> e.timestamp());
    dateColumn.setWidth(100);
    model.addColumn(dateColumn);
    
    model.addColumn(new ColumnSpec<>("Amount", Amount.class, e -> {
      boolean shouldConvert = currency.isReal() && (!e.amounts().isSingleCurrency() || !keepNativeCurrencyIfSingle);
      
      if (shouldConvert)
        return e.amount(currency);
      else
      {
        Optional<Amount> collapsed = e.amount();
        return collapsed.orElse(Amount.zero());
      }
    }));
    
    
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
    
    JPanel lowerPanel = new JPanel();
    lowerPanel.add(new JLabel("Coloring:"));
    lowerPanel.add(colorModeComboBox);
    lowerPanel.add(new JLabel("Currency:"));
    lowerPanel.add(currencyComboBox);
    lowerPanel.add(keepNativeCurrencyIfSingleComboBox);
    add(lowerPanel, BorderLayout.SOUTH);
  }
  
  private void colorModeChanged()
  {
    SwingUtilities.invokeLater(() -> {
      this.colorMode = colorModeComboBox.getItemAt(colorModeComboBox.getSelectedIndex());
      colorCache.clear();
      table.repaint();
    });
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
  
  private class Table extends JTable
  {
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
      Component component = super.prepareRenderer(renderer, row, column);
      ColumnColorMode mode = ExpenseTablePanel.this.colorMode;
      row = table.convertRowIndexToModel(row);
      Expense expense = model.data().get(row);
      
      if (mode != ColumnColorMode.NONE)
      {
        Color color = colorCache.get(mode.keyMapper.apply(expense));
        component.setBackground(color);
      }
      
      return component;
    }
  }
}
