package com.github.jakz.billsplit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.jakz.billsplit.data.Amount;
import com.github.jakz.billsplit.data.Category;
import com.github.jakz.billsplit.data.Currency;
import com.github.jakz.billsplit.data.MultiAmount;
import com.github.jakz.billsplit.data.Person;
import com.github.jakz.billsplit.data.Timestamp;
import com.github.jakz.billsplit.ui.SummaryEntry;
import com.pixbits.lib.lang.Pair;

public class Summarizers
{
  public static MultiAmount multiAmountSum(Iterable<MultiAmount> i)
  {
    return StreamSupport.stream(i.spliterator(), false)
      .reduce(new MultiAmount(), (v,e) -> v.add(e, true), (k,j) -> k.add(j, true));
  }
  
  public static Amount expenseSum(Iterable<Expense> i, Currency currency)
  {
    return StreamSupport.stream(i.spliterator(), false)
      .reduce(Amount.zero(), (v,e) -> v.add(e.amount(currency)), (k,j) -> k.add(j));
  }
  
  public static Amount shareSum(Iterable<Share<Amount>> i, Currency currency)
  {
    return StreamSupport.stream(i.spliterator(), false)
      .reduce(Amount.zero(), (v,e) -> v.add(e.value.convert(currency)), (k,j) -> k.add(j));
  }
  
  public static List<SummaryEntry> byDay(ExpenseSet expenses, Currency currency)
  {
    List<Pair<Timestamp, List<Expense>>> byDay = expenses.byDay();
    Collections.sort(byDay, Comparator.comparing(p -> p.first));

    List<SummaryEntry> entries = byDay.stream()
        .map(p -> SummaryEntry.of(p.first.toString(), expenseSum(p.second, currency)))
        .collect(Collectors.toList());
    
    return entries;
  }
  
  public static List<SummaryEntry> byRootCategory(ExpenseSet expenses, Currency currency)
  {
    List<Pair<Category, List<Expense>>> grouped = expenses.byCategory(true);

    List<SummaryEntry> entries = grouped.stream()
        .map(p -> SummaryEntry.of(p.first.toString(), expenseSum(p.second, currency)))
        .collect(Collectors.toList());

    return entries;
  }
  
  public static List<SummaryEntry> spentByPerson(ExpenseSet expenses, Optional<Currency> currency)
  {
    List<Pair<Person, List<Share<Amount>>>> grouped = expenses.groupByPersonExpenses();

    List<SummaryEntry> entries = grouped.stream()
        .map(p -> SummaryEntry.of(p.first.nickname(), shareSum(p.second, currency)))
        .collect(Collectors.toList());

    return entries;
  }
  
  public static List<SummaryEntry> owedByPerson(ExpenseSet expenses, Optional<Currency> currency)
  {
    List<Pair<Person, List<MultiAmount>>> grouped = expenses.groupByPersonOwed();

    List<SummaryEntry> entries = grouped.stream()
        .map(p -> SummaryEntry.of(p.first.nickname(), multiAmountSum(p.second).collapse(currency)))
        .collect(Collectors.toList());

    return entries;
  }
}
