/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve.node;

import static com.opengamma.strata.collect.TestHelper.assertSerialization;
import static com.opengamma.strata.collect.TestHelper.assertThrows;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static com.opengamma.strata.collect.TestHelper.date;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.data.ImmutableMarketData;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.MarketDataNotFoundException;
import com.opengamma.strata.data.ObservableId;
import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.curve.CurveNodeDate;
import com.opengamma.strata.market.observable.QuoteId;
import com.opengamma.strata.market.param.DatedParameterMetadata;
import com.opengamma.strata.market.param.ParameterMetadata;
import com.opengamma.strata.market.param.YearMonthDateParameterMetadata;
import com.opengamma.strata.product.SecurityId;
import com.opengamma.strata.product.index.IborFutureTrade;
import com.opengamma.strata.product.index.type.IborFutureConvention;
import com.opengamma.strata.product.index.type.IborFutureConventions;

/**
 * Tests {@link AbsoluteIborFutureCurveNode}.
 */
@Test
public class AbsoluteIborFutureCurveNodeTest {

  private static final ReferenceData REF_DATA = ReferenceData.standard();
  private static final LocalDate VAL_DATE = date(2015, 6, 30);
  private static final IborFutureConvention CONVENTION = IborFutureConventions.USD_LIBOR_3M_QUARTERLY_IMM;
  private static final YearMonth YEAR_MONTH = YearMonth.of(2015, 12);
  private static final StandardId STANDARD_ID = StandardId.of("OG-Ticker", "OG-EDH6");
  private static final QuoteId QUOTE_ID = QuoteId.of(STANDARD_ID);
  private static final double SPREAD = 0.0001;
  private static final String LABEL = "Label";

  private static final double TOLERANCE_RATE = 1.0E-8;

  public void test_builder() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.builder()
        .label(LABEL)
        .convention(CONVENTION)
        .yearMonth(YEAR_MONTH)
        .rateId(QUOTE_ID)
        .additionalSpread(SPREAD)
        .build();
    assertEquals(test.getRateId(), QUOTE_ID);
    assertEquals(test.getAdditionalSpread(), SPREAD);
    assertEquals(test.getConvention(), CONVENTION);
    assertEquals(test.getDate(), CurveNodeDate.END);
  }

  public void test_of_no_spread() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID);
    assertEquals(test.getRateId(), QUOTE_ID);
    assertEquals(test.getAdditionalSpread(), 0.0d);
    assertEquals(test.getConvention(), CONVENTION);
  }

  public void test_of_withSpread() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    assertEquals(test.getRateId(), QUOTE_ID);
    assertEquals(test.getAdditionalSpread(), SPREAD);
    assertEquals(test.getConvention(), CONVENTION);
  }

  public void test_of_withSpreadAndLabel() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD, LABEL);
    assertEquals(test.getRateId(), QUOTE_ID);
    assertEquals(test.getAdditionalSpread(), SPREAD);
    assertEquals(test.getConvention(), CONVENTION);
  }

  public void test_requirements() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    Set<ObservableId> set = test.requirements();
    Iterator<ObservableId> itr = set.iterator();
    assertEquals(itr.next(), QUOTE_ID);
    assertFalse(itr.hasNext());
  }

  public void test_trade() {
    AbsoluteIborFutureCurveNode node = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    double price = 99;
    MarketData marketData = ImmutableMarketData.builder(VAL_DATE).addValue(QUOTE_ID, price).build();
    IborFutureTrade trade = node.trade(1d, marketData, REF_DATA);
    IborFutureTrade expected =
        CONVENTION.createTrade(VAL_DATE, SecurityId.of(STANDARD_ID), YEAR_MONTH, 1L, 1.0, (price / 100) + SPREAD, REF_DATA);
    assertEquals(trade, expected);
  }

  public void test_trade_noMarketData() {
    AbsoluteIborFutureCurveNode node = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    MarketData marketData = MarketData.empty(VAL_DATE);
    assertThrows(() -> node.trade(1d, marketData, REF_DATA), MarketDataNotFoundException.class);
  }

  public void test_initialGuess() {
    AbsoluteIborFutureCurveNode node = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    double price = 99;
    MarketData marketData = ImmutableMarketData.builder(VAL_DATE).addValue(QUOTE_ID, price).build();
    assertEquals(node.initialGuess(marketData, ValueType.ZERO_RATE), 1.0 - (price / 100), TOLERANCE_RATE);
    assertEquals(node.initialGuess(marketData, ValueType.FORWARD_RATE), 1.0 - (price / 100), TOLERANCE_RATE);
    double approximateMaturity = 6d / 12d;
    double df = Math.exp(-approximateMaturity * (1.0 - (price / 100)));
    assertEquals(node.initialGuess(marketData, ValueType.DISCOUNT_FACTOR), df, TOLERANCE_RATE);
    assertEquals(node.initialGuess(marketData, ValueType.UNKNOWN), 0.0d, TOLERANCE_RATE);
  }

  public void test_metadata_end() {
    AbsoluteIborFutureCurveNode node = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD, LABEL);
    LocalDate date = LocalDate.of(2015, 10, 20);
    LocalDate referenceDate = CONVENTION.calculateReferenceDateFromTradeDate(date, YEAR_MONTH, REF_DATA);
    LocalDate maturityDate = CONVENTION.getIndex().calculateMaturityFromEffective(referenceDate, REF_DATA);
    ParameterMetadata metadata = node.metadata(date, REF_DATA);
    assertEquals(metadata.getLabel(), LABEL);
    assertTrue(metadata instanceof YearMonthDateParameterMetadata);
    assertEquals(((YearMonthDateParameterMetadata) metadata).getDate(), maturityDate);
    assertEquals(((YearMonthDateParameterMetadata) metadata).getYearMonth(), YearMonth.from(referenceDate));
  }

  public void test_metadata_fixed() {
    LocalDate nodeDate = VAL_DATE.plusMonths(1);
    AbsoluteIborFutureCurveNode node =
        AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD, LABEL).withDate(CurveNodeDate.of(nodeDate));
    DatedParameterMetadata metadata = node.metadata(VAL_DATE, REF_DATA);
    assertEquals(metadata.getDate(), nodeDate);
    assertEquals(metadata.getLabel(), node.getLabel());
  }

  public void test_metadata_last_fixing() {
    AbsoluteIborFutureCurveNode node =
        AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD, LABEL).withDate(CurveNodeDate.LAST_FIXING);
    ImmutableMarketData marketData = ImmutableMarketData.builder(VAL_DATE).addValue(QUOTE_ID, 0.0d).build();
    IborFutureTrade trade = node.trade(1d, marketData, REF_DATA);
    LocalDate fixingDate = trade.getProduct().getFixingDate();
    DatedParameterMetadata metadata = node.metadata(VAL_DATE, REF_DATA);
    assertEquals(metadata.getDate(), fixingDate);
    LocalDate referenceDate = CONVENTION.calculateReferenceDateFromTradeDate(VAL_DATE, YEAR_MONTH, REF_DATA);
    assertEquals(((YearMonthDateParameterMetadata) metadata).getYearMonth(), YearMonth.from(referenceDate));
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    coverImmutableBean(test);
    AbsoluteIborFutureCurveNode test2 = AbsoluteIborFutureCurveNode.of(
        CONVENTION, YearMonth.of(2016, 3), QuoteId.of(StandardId.of("OG-Ticker", "Unknown")));
    coverBeanEquals(test, test2);
  }

  public void test_serialization() {
    AbsoluteIborFutureCurveNode test = AbsoluteIborFutureCurveNode.of(CONVENTION, YEAR_MONTH, QUOTE_ID, SPREAD);
    assertSerialization(test);
  }

}
