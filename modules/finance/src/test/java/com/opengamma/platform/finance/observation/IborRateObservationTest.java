/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.finance.observation;

import static com.opengamma.basics.index.IborIndices.GBP_LIBOR_1M;
import static com.opengamma.basics.index.IborIndices.GBP_LIBOR_3M;
import static com.opengamma.collect.TestHelper.assertSerialization;
import static com.opengamma.collect.TestHelper.assertThrowsIllegalArg;
import static com.opengamma.collect.TestHelper.coverBeanEquals;
import static com.opengamma.collect.TestHelper.coverImmutableBean;
import static com.opengamma.collect.TestHelper.date;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class IborRateObservationTest {

  public void test_of() {
    IborRateObservation test = IborRateObservation.of(GBP_LIBOR_3M, date(2014, 6, 30));
    IborRateObservation expected = IborRateObservation.builder()
        .index(GBP_LIBOR_3M)
        .fixingDate(date(2014, 6, 30))
        .build();
    assertEquals(test, expected);
  }

  public void test_of_null() {
    assertThrowsIllegalArg(() -> IborRateObservation.of(null, date(2014, 6, 30)));
    assertThrowsIllegalArg(() -> IborRateObservation.of(GBP_LIBOR_3M, null));
    assertThrowsIllegalArg(() -> IborRateObservation.of(null, null));
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    IborRateObservation test = IborRateObservation.of(GBP_LIBOR_3M, date(2014, 6, 30));
    coverImmutableBean(test);
    IborRateObservation test2 = IborRateObservation.of(GBP_LIBOR_1M, date(2014, 7, 30));
    coverBeanEquals(test, test2);
  }

  public void test_serialization() {
    IborRateObservation test = IborRateObservation.of(GBP_LIBOR_3M, date(2014, 6, 30));
    assertSerialization(test);
  }

}