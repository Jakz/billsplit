package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.github.jakz.billsplit.data.Group;
import com.github.jakz.billsplit.data.Person;

public class WeightedGroupTests
{   
  public boolean checkValidity(WeightedGroup group)
  {
    float sum = group.stream().reduce(0.0f, (v,s) -> v + s.value, (i,j) -> i+j);
    return sum == 1.0f;
  }
  
  @Test
  public void testSinglePersonGroup()
  {
    WeightedGroup wg = new WeightedGroup(new Person("foo"));
    assertEquals(wg.size(), 1);
    assertTrue(checkValidity(wg));
  }
  
  @Test
  public void testTwoPersonEvenGroup()
  {
    WeightedGroup wg = new WeightedGroup(new Person("foo"), new Person("bar"));
    assertTrue(checkValidity(wg));
    assertEquals(wg.size(), 2);
    assertEquals(wg.get(0), new Share<Float>(new Person("foo"), 0.5f));
    assertEquals(wg.get(1), new Share<Float>(new Person("bar"), 0.5f));
  }
  
  @Test
  public void testThreePersonEvenGroup()
  {
    Group group = TestsSuite.group("foo", "bar", "baz");
    WeightedGroup wg = new WeightedGroup(group, group.size());
    assertTrue(checkValidity(wg));
    assertEquals(wg.size(), 3);
    assertEquals(wg.weight("foo"), 1.0f / 3.0f, 0.0001f);
    assertEquals(wg.weight("bar"), 1.0f / 3.0f, 0.0001f);
    assertEquals(wg.weight("baz"), 1.0f / 3.0f, 0.0001f);
  }
}
