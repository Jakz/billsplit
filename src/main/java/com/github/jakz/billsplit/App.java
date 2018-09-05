package com.github.jakz.billsplit;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;
import com.pixbits.lib.ui.charts.BarChartPanel;
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
    System.out.printf("Loaded %d expenses\n", expenses.size());
    mediator.onExpensesLoaded(expenses);
    
    if (true)
      return;

    PieChartPanel<MultiAmount> panel = new PieChartPanel<>(new Dimension(600,600));
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
    
    System.out.println(camount);
    
    /*List<Sample> samples = new ArrayList<>();
    for (int i = 0; i < 10; ++i)
    {
      samples.add(new Sample(ThreadLocalRandom.current().nextFloat()*50.0f));
    }
    
    PieChartPanel<Sample> canvas = new PieChartPanel<Sample>(new Dimension(800,600));
    canvas.setAutoRebuild(true);
    canvas.add(samples);
    
    WrapperFrame<?> frame = UIUtils.buildFrame(canvas, "Chart");
    frame.exitOnClose();
    frame.setVisible(true);*/
  }
}
