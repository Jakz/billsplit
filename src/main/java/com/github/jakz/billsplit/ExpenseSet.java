package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.MultiAmount;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.charts.Measurable;
import com.pixbits.lib.ui.table.DataSource;

public class ExpenseSet implements Iterable<Expense>, DataSource<Expense>
{
  private final List<Expense> expenses;
  
  public ExpenseSet()
  {
    this.expenses = new ArrayList<>();
  }
  
  public ExpenseSet(List<Expense> expenses)
  {
    this.expenses = expenses;
  }
  
  void add(Expense expense) { this.expenses.add(expense); }
  
  Amount total(Currency currency)
  {
    return expenses.stream().reduce(
        Amount.of(0.0f, currency),
        (a, p) -> a.add(p.amount(currency)),
        (a1, a2) -> a1.add(a2).convert(currency)
    );
  }

  @Override
  public Iterator<Expense> iterator() { return expenses.iterator(); }
  public Stream<MultiAmount> amounts() { return expenses.stream().map(Expense::multiAmount); }

  @Override public Expense get(int index) { return expenses.get(index); }
  @Override public int size() { return expenses.size(); }
  @Override public int indexOf(Expense object) { return expenses.indexOf(object); }
  
  public <T> List<Pair<T, List<Expense>>> groupBy(Function<Expense, T> classifier)
  {
    Map<T, List<Expense>> byDay = expenses.stream().collect(Collectors.groupingBy(classifier));
    return byDay.entrySet().stream()
      .map(e -> new Pair<>(e.getKey(), e.getValue()))
      .collect(Collectors.toList());
  }
  
  public List<Pair<Person, List<Share<Amount>>>> groupByPersonExpenses()
  {
    Map<Person, List<Share<Amount>>> amountsByPerson = expenses.stream().flatMap(expense -> expense.stream()).collect(Collectors.groupingBy(share -> share.person));
    
    return amountsByPerson.entrySet().stream()
       .map(e -> new Pair<>(e.getKey(), e.getValue()))
       .collect(Collectors.toList());
  }
  
  public List<Pair<Person, List<MultiAmount>>> groupByPersonOwed()
  {
    Map<Person, List<Pair<Person,MultiAmount>>> amountsByPerson = expenses.stream().flatMap(expense -> {
      final MultiAmount amounts = expense.multiAmount();
      return expense.quotas().stream().map(share -> new Pair<Person, MultiAmount>(share.person, amounts.multiply(share.value)));
    }).collect(Collectors.groupingBy(p -> p.first));
    
    return amountsByPerson.entrySet().stream()
        .map(e -> {
          List<MultiAmount> amounts = e.getValue().stream().map(p -> p.second).collect(Collectors.toList());
          Person person = e.getKey();
          return new Pair<>(person, amounts);
        })
        .collect(Collectors.toList());

  }
  
  public List<Pair<Timestamp, List<Expense>>> byDay()
  {
    return groupBy(expense -> expense.timestamp());
  }
  
  public List<Pair<Category, List<Expense>>> byCategory(boolean onlyRoots)
  {
    return groupBy(expense -> onlyRoots ? expense.category().root() : expense.category());
  }
}
