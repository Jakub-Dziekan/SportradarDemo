package com.jd.sportradar.demo;

import lombok.Getter;
import java.math.BigInteger;

@Getter
public class Event {

  private static BigInteger arrivalIndexSequence = BigInteger.ZERO;
  private final BigInteger arrivalIndex;

  public Event(BigInteger arrivalIndex) {
    if (BigInteger.ZERO.equals(arrivalIndex)) {
      this.arrivalIndex = arrivalIndexSequence = arrivalIndexSequence.add(BigInteger.ONE);
    } else {
      this.arrivalIndex = arrivalIndex;
    }
  }
}
