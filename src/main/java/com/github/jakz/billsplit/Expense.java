package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pixbits.lib.lang.Pair;

public class Expense
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
  
  public Category category() { return category; }
  public Optional<Timestamp> timestamp() { return timestamp; }
  public Amount amount()
  { 
    if (amounts.size() > 1)
    {
      return amounts.stream().reduce(
          Amount.of(0.0f, amounts.get(0).second.currency()),
          (a, p) -> a.add(p.second),
          (a1, a2) -> a1.add(a2)
      );
    }
    else
      return amounts.get(0).second; 
  }
}
