package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ExpenseAmounts
{
  private final List<Share<Amount>> amounts;
  
  public ExpenseAmounts(List<Share<Amount>> amounts)
  {
    this.amounts = new ArrayList<>(amounts);
  }
  
  void add(Share<Amount> amount)
  {
    this.add(amount);
  }
  
  void add(Person person, Amount amount)
  {
    this.add(new Share<>(person, amount));
  }

  public Amount amount(Currency currency)
  {
    return amounts.stream().reduce(
        Amount.of(0.0f, currency),
        (a, p) -> a.add(p.value.convert(currency)),
        (a1, a2) -> a1.add(a2).convert(currency)
    );
  }
  
  public Stream<Share<Amount>> stream() { return amounts.stream(); }
}
