package com.github.jakz.billsplit;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;
import com.pixbits.lib.ui.charts.BarChartPanel;
import com.pixbits.lib.ui.charts.Measurable;
import com.pixbits.lib.ui.charts.PieChartPanel;

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
    
    Expense expense = Expense.of(Amount.of("78.18 USD"), jack, Timestamp.of(2017, 9, 8), DefaultCategory.AIRPLANE, "Volo Santiago -> Calama");
    expense.add(Amount.of("116.22 EUR"), vicky);
    
    ExchangeRates rates = new ExchangeRates();
    
    Amount camount = rates.convertedValue(expense.amount(), Currency.EUR);
    
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
