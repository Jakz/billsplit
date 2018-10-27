package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Person;

public class ExpenseAmounts implements Iterable<Share<Amount>>
{
  private final List<Share<Amount>> amounts;
  
  public ExpenseAmounts()
  {
    this.amounts = new ArrayList<>();
  }
  
  public ExpenseAmounts(List<Share<Amount>> amounts)
  {
    this.amounts = new ArrayList<>(amounts);
  }
  
  public ExpenseAmounts(Person person, Amount amount)
  {
    this.amounts = new ArrayList<>();
    this.amounts.add(new Share<Amount>(person, amount));
  }
  
  void add(Share<Amount> amount)
  {
    amounts.add(amount);
  }
  
  public void add(Person person, Amount amount)
  {
    this.add(new Share<>(person, amount));
  }
  
  public boolean isMultiple() { return amounts.size() > 1; }
  public boolean isSingleCurrency() { return !isMultiple() || stream().map(Share::value).map(Amount::currency).distinct().count() == 1; }
  
  public int size() { return amounts.size(); }
  public Share<Amount> get(int i) { return amounts.get(i); }

  public Amount amount(Currency currency)
  {
    return amounts.stream().reduce(
        Amount.zero(),
        (a, p) -> a.add(p.value.convert(currency)),
        (a1, a2) -> a1.add(a2).convert(currency)
    );
  }

  public Stream<Share<Amount>> stream() { return amounts.stream(); }
  public Iterator<Share<Amount>> iterator() { return amounts.iterator(); }
  
  public String toString()
  {
    return amounts.stream()
      .map(s -> s.toString())
      .collect(Collectors.joining(", ", "[", "]"));
  }
}
