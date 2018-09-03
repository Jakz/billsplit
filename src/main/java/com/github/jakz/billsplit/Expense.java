package com.github.jakz.billsplit;

import java.util.ArrayList;
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
  private final List<Pair<Person, Amount>> amounts;
  private final Group sharers;
  
  private final String title;
  private final String description;
  
  public Expense(Amount amount, Person owner, Timestamp timestamp, Group sharers, Category category, String title, String description)
  {
    this.category = category;
    this.timestamp = Optional.ofNullable(timestamp);
    this.title = title;
    this.description = description;
    
    this.amounts = new ArrayList<>();
    this.amounts.add(new Pair<>(owner, amount));

    this.sharers = sharers;
  }
  
  public static Expense of(Amount amount, Person owner, Group sharers, Category category, String title, String description)
  {
    return new Expense(amount, owner, null, sharers, category, title, description);
  }
  
  public static Expense of(Amount amount, Person owner, Timestamp timestamp, Category category, String title, String description)
  {
    return new Expense(amount, owner, timestamp, owner.group(), category, title, description);
  }
  
  public static Expense of(Amount amount, Person owner, Timestamp timestamp, Category category, String title)
  {
    return new Expense(amount, owner, timestamp, owner.group(), category, title, "");
  }
  
  public void add(Amount amount, Person owner)
  {
    amounts.add(new Pair<>(owner, amount));
  }
  
  public Category category() { return category; }
  public Optional<Timestamp> timestamp() { return timestamp; }
  public Amount amount()
  { 
    return amount(amounts.get(0).second.currency());
  }
  
  public Amount amount(Currency currency)
  {
    return amounts.stream().reduce(
        Amount.of(0.0f, currency),
        (a, p) -> a.add(p.second.convert(currency)),
        (a1, a2) -> a1.add(a2).convert(currency)
    );
  }
  
  @Override
  public float chartValue() { return amount(ExchangeRates.Provider.rates().baseCurrency()).chartValue(); }
  
  @Override
  public String chartLabel() { return title; }
  
  public MultiAmount multiAmount()
  {    
    Map<Currency, Amount> amounts = this.amounts.stream().collect(
        Collectors.toMap(
            p -> p.second.currency(), 
            p -> p.second,
            (i, j) -> i.add(j))
   );
    
   return new MultiAmount(amounts.values());
  }
}
