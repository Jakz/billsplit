package com.github.jakz.billsplit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Rule;
import org.junit.Test;

import com.github.jakz.billsplit.data.Timestamp;
import com.github.jakz.billsplit.json.TimestampAdapter;
import com.github.jakz.billsplit.support.Repeat;
import com.github.jakz.billsplit.support.RepeatRule;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonTests
{
  @Rule
  public RepeatRule repeatRule = new RepeatRule();
  
  private TimestampAdapter timestampAdapter = new TimestampAdapter();

  @Test
  public void testTimestampSimpleSerialization()
  {
    Timestamp ts = Timestamp.of(1234, 12, 23);
    JsonElement json = timestampAdapter.serialize(ts, null, null);
    
    assertTrue(json.isJsonPrimitive() && json.getAsJsonPrimitive().isString());
    assertEquals(json.getAsString(), "1234-12-23");
  }
  
  @Test
  public void testTimestampPaddingSerialization()
  {
    Timestamp ts = Timestamp.of(1, 2, 3);
    JsonElement json = timestampAdapter.serialize(ts, null, null);
    assertEquals(json.getAsString(), "0001-02-03");
  }
  
  @Test
  @Repeat(times = 100)
  public void testRandomTimestampsSerialization()
  {
    Timestamp ts = TestsSuite.randomTimestamp();
    JsonElement json = timestampAdapter.serialize(ts, null, null);
    String string = String.format("%04d-%02d-%02d", ts.year(), ts.month(), ts.day());
    assertEquals(json.getAsString(), string);
  }
  
  @Test
  public void testTimestampSimpleDeserialization()
  {
    String string = "1234-12-23";
    Timestamp ts = timestampAdapter.deserialize(new JsonPrimitive(string), null, null);
    assertEquals(ts, Timestamp.of(1234, 12, 23));
  }
  
  
  @Test
  @Repeat(times = 100)
  public void testRandomTimestampSerializeAndDeserialize()
  {
    Timestamp ts1 = TestsSuite.randomTimestamp();
    Timestamp ts2 = timestampAdapter.deserialize(timestampAdapter.serialize(ts1, null, null), null, null);
    assertEquals(ts1, ts2);
  }
}
