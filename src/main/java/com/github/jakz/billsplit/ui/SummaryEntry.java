package com.github.jakz.billsplit.ui;

import com.github.jakz.billsplit.data.Amount;

public class SummaryEntry implements Comparable<SummaryEntry>
{
  public final String title;
  public final Amount amount;
  
  private SummaryEntry(String title, Amount amount)
  {
    this.title = title;
    this.amount = amount;
  }
  
  public static SummaryEntry of(String title, Amount amount)
  {
    return new SummaryEntry(title, amount);
  }
  
  public SummaryEntry combine(SummaryEntry o)
  {
    return of("", amount.add(o.amount));
  }

  @Override
  public int compareTo(SummaryEntry o)
  {
    return amount.compareTo(o.amount);
  }
}
