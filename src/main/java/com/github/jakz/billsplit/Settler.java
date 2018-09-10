package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.Debt;
import com.github.jakz.billsplit.data.Person;
import com.pixbits.lib.lang.CommutablePair;
import com.pixbits.lib.lang.Pair;

public class Settler
{
  public List<Debt> generateDebts(Expense expense)
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
        Amount amount = share.value.multiply(quote.value);
        
        debts.add(new Debt(debtor, creditor, amount));
      }
    }
    
    return debts;  
  }
  
  public List<Debt> pruneSelfOwedDebts(List<Debt> debts)
  {
    return debts.stream().filter(debt -> debt.debtor != debt.creditor).collect(Collectors.toList());
  }
  
  public void positivize(List<Debt> debts)
  {
    debts.forEach(Debt::positivize);
  }
  
  public List<Debt> simplify(List<Debt> debts)
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
}
