package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.MultiAmount;
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
  
  public List<Pair<Timestamp, List<Expense>>> byDay()
  {
    Map<Timestamp, List<Expense>> byDay = expenses.stream().collect(Collectors.groupingBy(expense -> expense.timestamp()));
    return byDay.entrySet().stream()
      .map(e -> new Pair<>(e.getKey(), e.getValue()))
      .collect(Collectors.toList());
  }
}
