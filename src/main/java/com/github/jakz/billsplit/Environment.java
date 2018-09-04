package com.github.jakz.billsplit;

import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Person;

public class Environment
{
  public final Group group;
  
  public Environment(Group group)
  {
    this.group = group;
  }
  
  public Person person(String nickname)
  {
    return group.forName(nickname);
  }
}
