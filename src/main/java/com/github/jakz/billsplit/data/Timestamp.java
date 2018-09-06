package com.github.jakz.billsplit.data;

import java.time.LocalDate;

public class Timestamp implements Comparable<Timestamp>
{
  private LocalDate date;
  
  public Timestamp(LocalDate date)
  {
    this.date = date;
  }
  
  public static Timestamp of(int year, int month, int day)
  {
    return new Timestamp(LocalDate.of(year, month, day));
  }
  
  public int year() { return date.getYear(); }
  public int month() { return date.getMonthValue(); }
  public int day() { return date.getDayOfMonth(); }
  
  @Override public int hashCode() { return date.hashCode(); }
  @Override public boolean equals(Object object) { return object instanceof Timestamp && ((Timestamp)object).date.equals(date); }
  
  @Override public String toString() { return String.format("%04d-%02d-%02d", year(), month(), day()); }

  @Override
  public int compareTo(Timestamp o)
  {
    return date.compareTo(o.date);
  }
}
