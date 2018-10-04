package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.MultiAmount;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.charts.Measurable;

public class Expense implements Measurable
{
  private final Category category;
  private final Timestamp timestamp;
  private final ExpenseAmounts amounts;
  private final WeightedGroup sharers;
  
  private final String title;
  
  public Expense(ExpenseAmounts amounts, Timestamp timestamp, WeightedGroup sharers, Category category, String title)
  {
    this.amounts = amounts;
    this.timestamp = timestamp;
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
  
  public static Expense of(ExpenseAmounts amounts, WeightedGroup sharers)
  {
    return new Expense(amounts, null, sharers, null, null);
  }

  public static Expense of(Amount amount, Person owner, WeightedGroup sharers)
  {
    return new Expense(new ExpenseAmounts(owner, amount), null, sharers, null, null);
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
  
  public String title() { return title; }
  public Category category() { return category; }
  public Timestamp timestamp() { return timestamp; }
  
  public WeightedGroup quotas() { return sharers; }
  public Stream<Share<Amount>> stream() { return amounts.stream(); }
  public ExpenseAmounts amounts() { return amounts; }
  //public Amount amount() { return amount(ExchangeRates.Provider.rates().baseCurrency()); }
  public Amount amount(Currency currency) { return amounts.amount(currency); }
  public Optional<Amount> amount()
  {
    return amounts.isSingleCurrency() ? Optional.of(amount(amounts.get(0).value().currency())) : Optional.empty();
  }
  
  public float quota(Person person) { return sharers.weight(person); }
  
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
