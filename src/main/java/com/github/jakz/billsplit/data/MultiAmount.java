package com.github.jakz.billsplit.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
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
  
  public MultiAmount multiply(float v)
  {
    List<Amount> scaled = amounts.stream().map(a -> a.multiply(v)).collect(Collectors.toList());
    return new MultiAmount(scaled);
  }
  
  public MultiAmount add(MultiAmount other, boolean keepSimplified)
  {
    if (!keepSimplified)
    {
      MultiAmount result = new MultiAmount();
      result.amounts.addAll(amounts);
      result.amounts.addAll(other.amounts);
      return result;
    }
    else
    {
      Map<Currency, Amount> samounts = new HashMap<>();
            
      this.amounts.stream().forEach(a -> samounts.merge(a.currency(), a, (o, v) -> o.add(v)));
      other.amounts.stream().forEach(a -> samounts.merge(a.currency(), a, (o, v) -> o.add(v)));
      
      return new MultiAmount(samounts.values());
    }
  }
  
  public boolean isCollapsible() { return amounts.size() <= 1 || amounts.stream().map(Amount::currency).distinct().count() == 1; }
  public Amount total(Currency currency) { return collapse(currency); }
  public Optional<Amount> collapse() { return Optional.of(amounts.isEmpty() ? Amount.zero() : collapse(amounts.get(0).currency())); }
  
  public Stream<Amount> stream() { return amounts.stream(); }
  public Iterator<Amount> iterator() { return amounts.iterator(); }
  public Amount[] array() { return amounts.toArray(new Amount[amounts.size()]); }

  @Override
  public float chartValue() { return collapse().get().chartValue(); }
}
