package com.github.jakz.billsplit.ui;

import java.awt.Color;

import com.github.jakz.billsplit.data.Person;
import com.pixbits.lib.ui.color.ColorCache;
import com.pixbits.lib.ui.color.HashColorCache;
import com.pixbits.lib.ui.color.PastelColorGenerator;

public class Colors
{
  private static final ColorCache<Person> personColors = new HashColorCache<>(new PastelColorGenerator());
  
  public static Color colorForPerson(Person person) { return personColors.get(person); }
}
