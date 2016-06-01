/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.sensitivity;

import static com.opengamma.strata.basics.currency.Currency.GBP;
import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.index.IborIndices.GBP_LIBOR_3M;
import static com.opengamma.strata.collect.TestHelper.assertSerialization;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static com.opengamma.strata.collect.TestHelper.date;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.CurrencyPair;
import com.opengamma.strata.basics.currency.FxMatrix;
import com.opengamma.strata.basics.index.IborIndexObservation;
import com.opengamma.strata.market.value.LegalEntityGroup;

/**
 * Test {@link IssuerCurveZeroRateSensitivity}.
 */
@Test
public class IssuerCurveZeroRateSensitivityTest {

  private static final ReferenceData REF_DATA = ReferenceData.standard();
  private static final LocalDate DATE = date(2015, 8, 27);
  private static final double VALUE = 32d;
  private static final Currency CURRENCY = USD;
  private static final LegalEntityGroup GROUP = LegalEntityGroup.of("ISSUER1");

  public void test_of_withSensitivityCurrency() {
    Currency sensiCurrency = GBP;
    IssuerCurveZeroRateSensitivity test = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, sensiCurrency, GROUP, VALUE);
    assertEquals(test.getLegalEntityGroup(), GROUP);
    assertEquals(test.getCurveCurrency(), CURRENCY);
    assertEquals(test.getCurrency(), sensiCurrency);
    assertEquals(test.getDate(), DATE);
    assertEquals(test.getSensitivity(), VALUE);
  }

  public void test_of_withoutSensitivityCurrency() {
    IssuerCurveZeroRateSensitivity test = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    assertEquals(test.getLegalEntityGroup(), GROUP);
    assertEquals(test.getCurveCurrency(), CURRENCY);
    assertEquals(test.getCurrency(), CURRENCY);
    assertEquals(test.getDate(), DATE);
    assertEquals(test.getSensitivity(), VALUE);
  }

  public void test_of_zeroRateSensitivity() {
    Currency sensiCurrency = GBP;
    ZeroRateSensitivity zeroSensi = ZeroRateSensitivity.of(CURRENCY, DATE, sensiCurrency, VALUE);
    IssuerCurveZeroRateSensitivity test = IssuerCurveZeroRateSensitivity.of(zeroSensi, GROUP);
    assertEquals(test.getLegalEntityGroup(), GROUP);
    assertEquals(test.getCurveCurrency(), CURRENCY);
    assertEquals(test.getCurrency(), sensiCurrency);
    assertEquals(test.getDate(), DATE);
    assertEquals(test.getSensitivity(), VALUE);
  }

  //-------------------------------------------------------------------------
  public void test_withCurrency() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    IssuerCurveZeroRateSensitivity test = base.withCurrency(GBP);
    assertEquals(test.getLegalEntityGroup(), GROUP);
    assertEquals(test.getCurveCurrency(), CURRENCY);
    assertEquals(test.getCurrency(), GBP);
    assertEquals(test.getDate(), DATE);
    assertEquals(test.getSensitivity(), VALUE);
  }

  public void test_withSensitivity() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    double newValue = 53d;
    IssuerCurveZeroRateSensitivity test = base.withSensitivity(newValue);
    assertEquals(test.getLegalEntityGroup(), GROUP);
    assertEquals(test.getCurveCurrency(), CURRENCY);
    assertEquals(test.getCurrency(), CURRENCY);
    assertEquals(test.getDate(), DATE);
    assertEquals(test.getSensitivity(), newValue);
  }

  public void test_compareKey() {
    IssuerCurveZeroRateSensitivity a1 = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    IssuerCurveZeroRateSensitivity a2 = IssuerCurveZeroRateSensitivity.of(CURRENCY, date(2015, 8, 27), GROUP, VALUE);
    IssuerCurveZeroRateSensitivity b = IssuerCurveZeroRateSensitivity.of(GBP, DATE, GROUP, VALUE);
    IssuerCurveZeroRateSensitivity c = IssuerCurveZeroRateSensitivity.of(CURRENCY, date(2015, 9, 27), GROUP, VALUE);
    IssuerCurveZeroRateSensitivity d =
        IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, LegalEntityGroup.of("ISSUER2"), VALUE);
    IborRateSensitivity other = IborRateSensitivity.of(
        IborIndexObservation.of(GBP_LIBOR_3M, date(2015, 8, 27), REF_DATA), 32d);
    assertEquals(a1.compareKey(a2), 0);
    assertEquals(a1.compareKey(b) > 0, true);
    assertEquals(b.compareKey(a1) < 0, true);
    assertEquals(a1.compareKey(c) < 0, true);
    assertEquals(c.compareKey(a1) > 0, true);
    assertEquals(a1.compareKey(d) < 0, true);
    assertEquals(d.compareKey(a1) > 0, true);
    assertEquals(a1.compareKey(other) > 0, true);
    assertEquals(other.compareKey(a1) < 0, true);
  }

  public void test_convertedTo() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    double rate = 1.5d;
    FxMatrix matrix = FxMatrix.of(CurrencyPair.of(GBP, USD), rate);
    IssuerCurveZeroRateSensitivity test1 = base.convertedTo(USD, matrix);
    assertEquals(test1, base);
    IssuerCurveZeroRateSensitivity test2 = base.convertedTo(GBP, matrix);
    IssuerCurveZeroRateSensitivity expected = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GBP, GROUP, VALUE / rate);
    assertEquals(test2, expected);
  }

  //-------------------------------------------------------------------------
  public void test_multipliedBy() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    double rate = 2.4d;
    IssuerCurveZeroRateSensitivity test = base.multipliedBy(rate);
    IssuerCurveZeroRateSensitivity expected = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE * rate);
    assertEquals(test, expected);
  }

  public void test_mapSensitivity() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    IssuerCurveZeroRateSensitivity expected = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, 1d / VALUE);
    IssuerCurveZeroRateSensitivity test = base.mapSensitivity(s -> 1d / s);
    assertEquals(test, expected);
  }

  public void test_normalize() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    IssuerCurveZeroRateSensitivity test = base.normalize();
    assertEquals(test, base);
  }

  public void test_buildInto() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    MutablePointSensitivities combo = new MutablePointSensitivities();
    MutablePointSensitivities test = base.buildInto(combo);
    assertSame(test, combo);
    assertEquals(test.getSensitivities(), ImmutableList.of(base));
  }

  public void test_cloned() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    IssuerCurveZeroRateSensitivity test = base.cloned();
    assertEquals(test, base);
  }

  public void test_createZeroRateSensitivity() {
    IssuerCurveZeroRateSensitivity base = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GBP, GROUP, VALUE);
    ZeroRateSensitivity expected = ZeroRateSensitivity.of(CURRENCY, DATE, GBP, VALUE);
    ZeroRateSensitivity test = base.createZeroRateSensitivity();
    assertEquals(test, expected);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    IssuerCurveZeroRateSensitivity test1 = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    coverImmutableBean(test1);
    IssuerCurveZeroRateSensitivity test2 =
        IssuerCurveZeroRateSensitivity.of(GBP, date(2014, 3, 24), LegalEntityGroup.of("ISSUER1"), 12d);
    coverBeanEquals(test1, test2);
  }

  public void test_serialization() {
    IssuerCurveZeroRateSensitivity test = IssuerCurveZeroRateSensitivity.of(CURRENCY, DATE, GROUP, VALUE);
    assertSerialization(test);
  }

}
