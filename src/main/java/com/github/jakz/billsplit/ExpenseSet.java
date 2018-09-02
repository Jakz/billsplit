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
    return null;
  }
}
