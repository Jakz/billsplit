package com.github.jakz.billsplit;

import com.github.jakz.billsplit.data.Category;

public enum DefaultCategory implements Category
{
  FOOD("Food"),
  TRANSPORTATION("Transport"),
  ATTRACTION("Attraction"),
  GIFT("Gift"),
  MISC("Misc"),

  BREAKFAST("Breakfast", FOOD),
  LUNCH("Lunch", FOOD),
  DINNER("Dinner", FOOD),
  
  SNACKS("Snacks", FOOD),
  
  FRUITS("Fruits", SNACKS),
  WATER("Water", SNACKS),
  
  BEVERAGES("Beverages", FOOD),
  ALCHOOL("Drinks", BEVERAGES),
  COFFEE("Coffee", BEVERAGES),
  
  TAXI("Taxi", TRANSPORTATION),
  BUS("Bus", TRANSPORTATION),
  TRAIN("Train", TRANSPORTATION),
  METRO("Metro", TRAIN),
  AIRPLANE("Plane", TRANSPORTATION),
  CAR_RENT("Car Rent", TRANSPORTATION),
  BIKE("Bike", TRANSPORTATION),
  
  ACCOMODATION("Accomodation"),
  HOTEL("Hotel", ACCOMODATION),
  HOSTEL("Hostel", ACCOMODATION),
  
  GASOLINE("Gasoline", CAR_RENT),
  
  HEALTH("Health", MISC),
  
  TOILET("Toilet", HEALTH),
  
  EXCHANGE("Exchange", MISC),
  COMMISSION("Commission", MISC),
  POOL_DEPOSIT("Pool Deposit", MISC),
  
  TICKET("Ticket", ATTRACTION),
  TOUR("Tour", ATTRACTION),
  
  MUSEUM("Museum", TICKET),
  
  MAGNET("Magnet", GIFT)
  ;
  
  private final DefaultCategory parent;
  private DefaultCategory root;
  private final String caption;
  
  private DefaultCategory(String caption)
  {
    this(caption, null);
  }
  
  private DefaultCategory(String caption, DefaultCategory parent)
  {
    this.caption = caption;
    this.parent = parent;
    this.root = parent;
    
    if (root != null)
    {
      while (root.parent != null)
        root = root.parent;
    }
    else
      root = this;
  }
  
  @Override public String caption() { return caption; }
  @Override public Category parent() { return parent; }
  @Override public Category root() { return root; }
}
