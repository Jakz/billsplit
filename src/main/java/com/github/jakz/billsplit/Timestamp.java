package com.github.jakz.billsplit;

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
}
