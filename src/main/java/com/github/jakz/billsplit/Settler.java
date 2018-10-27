package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Debt;
import com.github.jakz.billsplit.data.Person;
import com.pixbits.lib.algorithm.graphs.DirectedGraph;
import com.pixbits.lib.algorithm.graphs.TarjanSCC;
import com.pixbits.lib.algorithm.graphs.Vertex;
import com.pixbits.lib.lang.CommutablePair;
import com.pixbits.lib.lang.Pair;

public class Settler
{
  public List<Debt> generateDebts(ExpenseSet expenses, final Currency currency)
  {
    return expenses.stream()
        .map(e -> generateDebts(e, currency))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
  
  public List<Debt> generateDebts(Expense expense) { return generateDebts(expense, null); }
  public List<Debt> generateDebts(Expense expense, final Currency currency)
  {
    /* for each person which had benefit from the expense generate a debt for each contributor */
    WeightedGroup quotas = expense.quotas();
    ExpenseAmounts amounts = expense.amounts();
    
    List<Debt> debts = new ArrayList<>();
    
    for (Share<Float> quote : quotas)
    {      
      for (Share<Amount> share : amounts)
      {
        Person debtor = quote.person;
        Person creditor = share.person;
        Amount amount = (currency == null ? share.value : share.value.convert(currency)).multiply(quote.value);
                
        debts.add(new Debt(debtor, creditor, amount));
      }
    }
    
    return debts;  
  }
  
  public List<Debt> pruneSelfOwedDebts(List<Debt> debts)
  {
    return debts.stream().filter(debt -> debt.debtor != debt.creditor).collect(Collectors.toList());
  }
  
  public List<Debt> positivize(List<Debt> debts)
  {
    return debts.stream().map(Debt::positivize).collect(Collectors.toList());
  }
  
  public List<Debt> simplifyDirectDebts(List<Debt> debts)
  {
    Map<Pair<Person, Person>, Map<Currency, Amount>> amounts = new HashMap<>();

    for (Debt debt : debts)
    {
      /* we enforce an order to manage symmetrical debts (A owes to B an amount X is equivalent to B owes to A an amount of -X) */
      boolean inverted = debt.creditor.nickname().compareTo(debt.debtor.nickname()) > 0;
      
      Pair<Person, Person> key;
      Amount amount = debt.amount;
      
      if (inverted)
      {
        amount = debt.amount.negate();
        key = new Pair<>(debt.creditor, debt.debtor);
      }
      else
        key = new Pair<>(debt.debtor, debt.creditor);
      
      
      Map<Currency, Amount> camounts = amounts.computeIfAbsent(key, k -> new HashMap<>());
      
      camounts.merge(amount.currency(), amount, (o,n) -> o.add(n));
    }

    List<Debt> simplified = new ArrayList<>();
    
    for (Map.Entry<Pair<Person, Person>, Map<Currency, Amount>> entry : amounts.entrySet())
    {
      for (Map.Entry<Currency, Amount> amount : entry.getValue().entrySet())
      {
        simplified.add(new Debt(entry.getKey().first, entry.getKey().second, amount.getValue()));
      }
    }
    
    simplified.removeIf(d -> d.amount.isZero());
    
    return simplified;
  }
  
  public List<Debt> settle(List<Debt> debts)
  {
    debts = simplifyDirectDebts(debts);
    debts = positivize(debts);
    debts = pruneSelfOwedDebts(debts);
    settleCycles(debts);
    
    return debts;
  }
  
  public List<Debt> settleCycles(List<Debt> debts)
  {
    DirectedGraph<Person, Debt> graph = DirectedGraph.of(
        debts,
        d -> new Pair<>(d.debtor, d.creditor),
        d -> d);
    
    TarjanSCC<Person, Debt> tarjan = new TarjanSCC<>(graph);
    
    Set<TarjanSCC.SCC<Person,Debt>> cycles = tarjan.compute();
    
    /* remove 1 vertex only SCCs */
    cycles = cycles.stream()
      .filter(s -> s.size() > 1)
      .collect(Collectors.toSet());
        
    cycles.forEach(cycle -> {
      List<Debt> involvedDebts = cycle.stream()
        .map(s -> s.origin())
        .map(e -> e.data())
        .collect(Collectors.toList());
      
      if (involvedDebts.stream().map(Debt::amount).map(Amount::currency).distinct().count() > 1)
        throw new IllegalArgumentException("Can't settle a cycle made of multiple currencies");
      
      Amount commonValue = involvedDebts.stream().map(Debt::amount).min(Comparator.naturalOrder()).get();
      System.out.println("Cycles: "+commonValue);
      System.out.println("Involved: "+involvedDebts.stream().map(Object::toString).collect(Collectors.joining(",")));

      
      involvedDebts.forEach(debt -> debt.amount.subtract(commonValue));
    });
    

    
    debts.removeIf(d -> d.amount.isZero());
    
    return debts;
  }
}
