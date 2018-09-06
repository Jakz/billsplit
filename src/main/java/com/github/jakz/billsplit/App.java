package com.github.jakz.billsplit;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.MultiAmount;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.github.jakz.billsplit.json.CategoryAdapter;
import com.github.jakz.billsplit.json.ExpenseAmountsAdapter;
import com.github.jakz.billsplit.json.ExpenseDeserializer;
import com.github.jakz.billsplit.json.ExpenseSetDeserializer;
import com.github.jakz.billsplit.json.TimestampAdapter;
import com.github.jakz.billsplit.json.WeightedGroupAdapter;
import com.github.jakz.billsplit.ui.ExpenseTablePanel;
import com.github.jakz.billsplit.ui.Mediator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;
import com.pixbits.lib.ui.charts.BarChartPanel;
import com.pixbits.lib.ui.charts.ChartPanel;
import com.pixbits.lib.ui.charts.Measurable;
import com.pixbits.lib.ui.charts.PieChartPanel;
import com.pixbits.lib.ui.charts.events.PieChartMouseListener;

public class App 
{
  static MyMediator mediator = new MyMediator();
  static WrapperFrame<ExpenseTablePanel> expensesFrame;
  
  
  static Environment environment;
  
  static class MyMediator implements Mediator
  {
    ExpenseSet expenses;
    
    @Override public ExpenseSet expenses() { return expenses; }

    @Override
    public void onExpensesLoaded(ExpenseSet expenses)
    {
      this.expenses = expenses;
      expensesFrame.panel().setData(expenses);
    }
  }
  
  public static ExpenseSet loadData()
  {
    environment = new Environment(null);
    
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Timestamp.class, new TimestampAdapter());
    builder.registerTypeAdapter(Category.class, new CategoryAdapter());
    builder.registerTypeAdapter(ExpenseAmounts.class, new ExpenseAmountsAdapter(environment));
    builder.registerTypeAdapter(WeightedGroup.class, new WeightedGroupAdapter(environment));
    builder.registerTypeAdapter(ExpenseSet.class, new ExpenseSetDeserializer(environment));
    builder.registerTypeAdapter(Expense.class, new ExpenseDeserializer(environment));

    builder.setPrettyPrinting();
    
    Gson gson = builder.create();
    
    try (BufferedReader rdr = Files.newBufferedReader(Paths.get("expenses.json")))
    {
      return gson.fromJson(rdr, ExpenseSet.class);
    } 
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }  
  }
  
  public static void buildUI()
  {
    UIUtils.setNimbusLNF();
    
    expensesFrame = UIUtils.buildFrame(new ExpenseTablePanel(mediator), "Expenses");
    expensesFrame.exitOnClose();
  }

  public static void main( String[] args )
  {
    buildUI();
    expensesFrame.centerOnScreen();
    expensesFrame.setVisible(true);

    
    ExpenseSet expenses = loadData();
    System.out.printf("Loaded %d expenses for %s\n", expenses.size(), expenses.amounts().map(e -> e.total(Currency.EUR)).reduce(Amount.zero(), (a1,a2) -> a1.add(a2)).toString());
    mediator.onExpensesLoaded(expenses);
    
    ChartPanel<Measurable<Amount>> panel = new BarChartPanel<Measurable<Amount>>(new Dimension(600,600));
    
    List<Pair<Timestamp, List<Expense>>> byDay = expenses.byDay();
    
    List<Measurable<Amount>> byDayAmounts = byDay.stream()
        .map(p -> Measurable.of(
            Expense.amount(p.second, Currency.EUR).chartValue(), 
            p.first.toString(), 
            Amount.zero())
        )
        .collect(Collectors.toList());
    
    panel.add(byDayAmounts);
    panel.refresh();
    
    WrapperFrame<?> frame = UIUtils.buildFrame(panel, "Expenses By Day");
    frame.exitOnClose();
    frame.setVisible(true);

    if (true)
      return;

    /*PieChartPanel<MultiAmount> panel = new PieChartPanel<>(new Dimension(600,600));
    panel.addListener(new PieChartMouseListener() {
      @Override public void enteredPie() { System.out.println("Entered Pie"); }
      @Override public void exitedPie() { System.out.println("Exited Pie"); }
      
      
      @Override
      public void enteredArc(Measurable measurable) { }

      @Override
      public void exitedArc(Measurable measurable) { }
      
    });
    panel.add(expenses.amounts());
    panel.refresh();
    
    WrapperFrame<?> frame = UIUtils.buildFrame(panel, "Expenses");
    frame.exitOnClose();
    frame.setVisible(true);
    
    Amount camount = expenses.total(Currency.EUR);
    
    System.out.println(camount);*/
  }
}
