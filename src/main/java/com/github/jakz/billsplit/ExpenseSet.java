package com.github.jakz.billsplit;

import java.util.ArrayList;
import java.util.List;

public class ExpenseSet
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
}
