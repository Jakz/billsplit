package com.github.jakz.billsplit.ui;

import com.github.jakz.billsplit.ExpenseSet;

public interface Mediator
{
  public ExpenseSet expenses();

  public void onExpensesLoaded(ExpenseSet expenses);
}
