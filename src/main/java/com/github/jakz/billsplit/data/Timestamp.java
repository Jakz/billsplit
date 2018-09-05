package com.github.jakz.billsplit.data;

import java.time.LocalDate;

public class Timestamp
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
  
  public int hashCode() { return date.hashCode(); }
  public boolean equals(Object object) { return object instanceof Timestamp && ((Timestamp)object).date.equals(date); }
  
  public String toString() { return String.format("%04d-%02d-%02d", year(), month(), day()); }
}
