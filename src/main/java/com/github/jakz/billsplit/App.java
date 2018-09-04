package com.github.jakz.billsplit;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.MultiAmount;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;
import com.pixbits.lib.ui.charts.BarChartPanel;
import com.pixbits.lib.ui.charts.Measurable;
import com.pixbits.lib.ui.charts.PieChartPanel;
import com.pixbits.lib.ui.charts.events.PieChartMouseListener;

public class App 
{
  static class Sample implements Measurable
  {
    final float v;
    public Sample(float v) { this.v = v; }
    @Override public float chartValue() { return v; }
  }
  
  public static void main( String[] args )
  {
    Person jack = new Person("Jack");
    Person vicky = new Person("Vicky");
    
    Group group = new Group(jack, vicky);
    group.stream().forEach(p -> p.group(group));
    
    Expense expense = Expense.of(Amount.of("78.18 USD"), jack, Timestamp.of(2019, 8, 9), DefaultCategory.AIRPLANE, "Volo Santiago -- Calama");
    expense.add(Amount.of("116.22 EUR"), vicky);
    
    ExpenseSet expenses = new ExpenseSet();
    expenses.add(expense);
    expenses.add(Expense.of(Amount.of("127.50 USD"), vicky, Timestamp.of(2019, 8, 9), DefaultCategory.AIRPLANE, "Volo Iquique -- Santiago"));
    expenses.add(Expense.of(Amount.of("30 EUR"), vicky, Timestamp.of(2019, 8, 13), DefaultCategory.TAXI, "Taxi Casa -- Aeroporto"));
    expenses.add(Expense.of(Amount.of("10 EUR"), jack, Timestamp.of(2019, 8, 13), DefaultCategory.HEALTH, "Condoms"));
    expenses.add(Expense.of(Amount.of("13.70 EUR"), vicky, Timestamp.of(2019, 8, 13), DefaultCategory.DINNER, "Cena Fiumicino"));

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
