package com.github.jakz.billsplit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  
  public static List<Amount> shareSum(Iterable<Share<Amount>> i)
  {
    Map<Currency, List<Share<Amount>>> amounts = StreamSupport.stream(i.spliterator(), false)
        .collect(Collectors.groupingBy(s -> s.value.currency()));
    
    return amounts.values().stream().map(list -> {
      return list.stream().reduce(Amount.zero(), (v, s) -> v.add(s.value), (v1, v2) -> v1.add(v2));
    }).collect(Collectors.toList());
  }
  
  public static List<SummaryEntry<Timestamp>> byDay(ExpenseSet expenses, Currency currency)
  {
    List<Pair<Timestamp, List<Expense>>> byDay = expenses.byDay();
    Collections.sort(byDay, Comparator.comparing(p -> p.first));

    List<SummaryEntry<Timestamp>> entries = byDay.stream()
        .map(p -> SummaryEntry.of(p.first, p.first.toString(), expenseSum(p.second, currency)))
        .collect(Collectors.toList());
    
    return entries;
  }
  
  public static List<SummaryEntry<Category>> byRootCategory(ExpenseSet expenses, Currency currency)
  {
    List<Pair<Category, List<Expense>>> grouped = expenses.byCategory(true);

    List<SummaryEntry<Category>> entries = grouped.stream()
        .map(p -> SummaryEntry.of(p.first, p.first.toString(), expenseSum(p.second, currency)))
        .collect(Collectors.toList());

    return entries;
  }
  
  public static List<SummaryEntry<Person>> spentByPerson(ExpenseSet expenses, Optional<Currency> currency)
  {
    List<Pair<Person, List<Share<Amount>>>> grouped = expenses.groupByPersonExpenses();

    List<SummaryEntry<Person>> entries = grouped.stream()
        .map(p -> {
          List<Amount> converted = currency.isPresent() ? 
            Collections.singletonList(shareSum(p.second, currency.get())) :
            shareSum(p.second);
          
          return converted.stream().map(e -> SummaryEntry.of(p.first, p.first.nickname(), e));
        })
        .flatMap(s -> s).collect(Collectors.toList());

    return entries;
  }
  
  public static List<SummaryEntry<Person>> owedByPerson(ExpenseSet expenses, Optional<Currency> currency)
  {
    List<Pair<Person, List<MultiAmount>>> grouped = expenses.groupByPersonOwed();

    List<SummaryEntry<Person>> entries = grouped.stream()
        .map(p -> {
          Stream<Amount> converted = currency.isPresent() ? 
              Collections.singleton(multiAmountSum(p.second).collapse(currency.get())).stream() :
              multiAmountSum(p.second).stream();
              
           return converted.map(e -> SummaryEntry.of(p.first, p.first.nickname(), e));
        }).flatMap(s -> s).collect(Collectors.toList());

    return entries;
  }
}
