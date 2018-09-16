package com.github.jakz.billsplit.ui;

import com.github.jakz.billsplit.data.Amount;

public final class SummaryEntry<T> implements Comparable<SummaryEntry<T>>
{
  public final String title;
  public final Amount amount;
  public final T data;
  
  private SummaryEntry(T data, String title, Amount amount)
  {
    this.data = data;
    this.title = title;
    this.amount = amount;
  }
  
  public static SummaryEntry<?> of(String title, Amount amount)
  {
    return new SummaryEntry<>(null, title, amount);
  }
  
  public static <U> SummaryEntry<U> of(U data, String title, Amount amount)
  {
    return new SummaryEntry<>(data, title, amount);
  }
  
  public SummaryEntry<?> combine(SummaryEntry<?> o)
  {
    if (data != o.data)
      throw new IllegalArgumentException(String.format("Can't combine SummaryEntry with different data: %s != %s", data, o.data));
    return of(data, "", amount.add(o.amount));
  }

  @Override
  public int compareTo(SummaryEntry<T> o)
  {
    return amount.compareTo(o.amount);
  }
}
