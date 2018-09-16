package com.github.jakz.billsplit.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.github.jakz.billsplit.ExpenseSet;
import com.github.jakz.billsplit.Summarizers;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Person;
import com.pixbits.lib.lang.StringUtils;
import com.pixbits.lib.ui.color.ColorCache;
import com.pixbits.lib.ui.color.HashColorCache;
import com.pixbits.lib.ui.color.PastelColorGenerator;
import com.pixbits.lib.ui.table.DataSource;

public class TotalsPanel extends JPanel
{
  private SummaryTablePanel<Person> totalsOwed;
  private SummaryTablePanel<Person> totalsSpent;
  
  private SummaryTablePanel<Category> totalsByCategory;
  
  ColorCache<Person> colorCachePersons;
  
  public TotalsPanel()
  {
    totalsOwed = new SummaryTablePanel<>(new Dimension(300,200), null);
    totalsSpent = new SummaryTablePanel<>(new Dimension(300,200), null);
    totalsByCategory = new SummaryTablePanel<>(new Dimension(300,200), null);
    
    totalsOwed.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Amount Owed"));
    totalsSpent.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Amount Spent"));
    totalsByCategory.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Expenses by type"));

    colorCachePersons = new HashColorCache<>(new PastelColorGenerator());
    
    totalsOwed.setColorCache(colorCachePersons);
    totalsSpent.setColorCache(colorCachePersons);
    
    this.setLayout(new BorderLayout());
    
    JPanel tables = new JPanel(new GridLayout(2, 2));
    tables.add(totalsOwed);
    tables.add(totalsByCategory);
    tables.add(totalsSpent);
    
    this.add(tables, BorderLayout.CENTER);    
  }
  
  public void setData(ExpenseSet expenses)
  {
    List<SummaryEntry<Person>> totalsOwed = Summarizers.owedByPerson(expenses, Optional.of(Currency.EUR));
    this.totalsOwed.setBehavior(SummaryBarBehavior.ofAveraging(totalsOwed, t -> t.amount.convert(Currency.EUR).unprecise(), (t,f) -> StringUtils.toPercent(f, 2)+"%"));
    this.totalsOwed.setData(DataSource.of(totalsOwed));
    
    List<SummaryEntry<Person>> totalsSpent = Summarizers.spentByPerson(expenses, Optional.of(Currency.EUR));
    this.totalsSpent.setBehavior(SummaryBarBehavior.ofAveraging(totalsSpent, t -> t.amount.convert(Currency.EUR).unprecise(), (t,f) -> StringUtils.toPercent(f, 2)+"%"));
    this.totalsSpent.setData(DataSource.of(totalsSpent));
    
    List<SummaryEntry<Category>> totalsByCategory = Summarizers.byRootCategory(expenses, Currency.EUR);
    this.totalsByCategory.setBehavior(SummaryBarBehavior.ofAveraging(totalsByCategory, t -> t.amount.convert(Currency.EUR).unprecise(), (t,f) -> StringUtils.toPercent(f, 2)+"%"));
    this.totalsByCategory.setData(DataSource.of(totalsByCategory));


  }
}
