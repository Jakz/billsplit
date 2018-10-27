package com.github.jakz.billsplit.data;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  
  public static Timestamp today()
  {
    return new Timestamp(LocalDate.now());
  }
  
  private final static Pattern timestampPattern = Pattern.compile("([0-9]{4})\\-([01]*[0-9])\\-([0-3]*[0-9])");
  public static Timestamp of(String format)
  {
    Matcher matcher = timestampPattern.matcher(format);
    
    if (matcher.matches())
    {
      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2));
      int day = Integer.parseInt(matcher.group(3));
      
      return Timestamp.of(year, month, day);
    }
    else
      return null;
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
