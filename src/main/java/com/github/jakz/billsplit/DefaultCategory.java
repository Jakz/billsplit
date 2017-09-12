package com.github.jakz.billsplit;

public enum DefaultCategory implements Category
{
  FOOD("Food"),
  TRANSPORTATION("Transport"),
  ATTRACTION("Attraction"),

  BREAKFAST("Breakfast", FOOD),
  LUNCH("Lunch", FOOD),
  DINNER("Dinner", FOOD),
  
  BEVERAGES("Beverages", FOOD),
  
  ALCHOOL("Drinks", BEVERAGES),
  
  TAXI("Taxi", TRANSPORTATION),
  BUS("Bus", TRANSPORTATION),
  TRAIN("Train", TRANSPORTATION),
  AIRPLANE("Plane", TRANSPORTATION),
  CAR_RENT("Car Rent", TRANSPORTATION),
  
  MUSEUM("Museum", ATTRACTION),
  
  GASOLINE("Gasoline", CAR_RENT)
  
  ;
  
  private final DefaultCategory parent;
  private final String caption;
  
  private DefaultCategory(String caption)
  {
    this(caption, null);
  }
  
  private DefaultCategory(String caption, DefaultCategory parent)
  {
    this.caption = caption;
    this.parent = parent;
  }

  @Override public String caption() { return caption; }
  @Override public Category parent() { return parent; }
}
