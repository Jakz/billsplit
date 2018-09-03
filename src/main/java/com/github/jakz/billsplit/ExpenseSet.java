package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ExpenseSet implements Iterable<Expense>
{
  private final List<Expense> expenses;
  
  public ExpenseSet()
  {
    this.expenses = new ArrayList<>();
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
}
