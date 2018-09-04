package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.charts.Measurable;

public class Expense implements Measurable
{
  private final Category category;
  private final Optional<Timestamp> timestamp;
  private final ExpenseAmounts amounts;
  private final WeightedGroup sharers;
  
  private final String title;
  
  public Expense(ExpenseAmounts amounts, Timestamp timestamp, WeightedGroup sharers, Category category, String title)
  {
    this.amounts = amounts;
    this.timestamp = Optional.of(timestamp);
    this.sharers = sharers;
    this.category = category;
    this.title = title;
  }
  
  public Expense(Amount amount, Person owner, Timestamp timestamp, Group sharers, Category category, String title)
  {
    this(
      new ExpenseAmounts(Collections.singletonList(new Share<>(owner, amount))),
      timestamp,
      new WeightedGroup(sharers, sharers.size()),
      category,
      title
    );
  }
  
  public static Expense of(Amount amount, Person owner, Group sharers, Category category, String title)
  {
    return new Expense(amount, owner, null, sharers, category, title);
  }
  
  public static Expense of(Amount amount, Person owner, Timestamp timestamp, Category category, String title)
  {
    return new Expense(amount, owner, timestamp, owner.group(), category, title);
  }
  
  public void add(Amount amount, Person owner)
  {
    amounts.add(new Share<>(owner, amount));
  }
  
  public Category category() { return category; }
  public Optional<Timestamp> timestamp() { return timestamp; }
  
  public Amount amount() { return amount(ExchangeRates.Provider.rates().baseCurrency()); }
  public Amount amount(Currency currency) { return amounts.amount(currency); }
  
  @Override
  public float chartValue() { return amount(ExchangeRates.Provider.rates().baseCurrency()).chartValue(); }
  
  @Override
  public String chartLabel() { return title; }
  
  public MultiAmount multiAmount()
  {    
    Map<Currency, Amount> amounts = this.amounts.stream().collect(
        Collectors.toMap(
            p -> p.value.currency(), 
            p -> p.value,
            (i, j) -> i.add(j))
   );
    
   return new MultiAmount(amounts.values());
  }
}
