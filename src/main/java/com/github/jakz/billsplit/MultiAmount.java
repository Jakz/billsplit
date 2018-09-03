package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.pixbits.lib.ui.charts.Measurable;

public class MultiAmount implements Iterable<Amount>, Measurable
{
  private List<Amount> amounts;
  
  public MultiAmount(Collection<Amount> amounts)
  {
    this.amounts = new ArrayList<>(amounts);
  }
  
  public MultiAmount()
  {
    this.amounts = new ArrayList<>();
  }
 
  public Amount collapse(Currency currency)
  {
    return amounts.stream().reduce(
        Amount.of(0.0f, currency),
        (a, p) -> a.add(p.convert(currency)),
        (a1, a2) -> a1.add(a2).convert(currency)
    );
  }
  
  public Amount collapse() { return amounts.isEmpty() ? Amount.zero() : collapse(amounts.get(0).currency()); }
  
  public Stream<Amount> stream() { return amounts.stream(); }
  public Iterator<Amount> iterator() { return amounts.iterator(); }
  public Amount[] array() { return amounts.toArray(new Amount[amounts.size()]); }

  @Override
  public float chartValue() { return collapse().chartValue(); }
}
