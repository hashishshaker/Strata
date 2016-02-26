/**
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.capfloor;

import static com.opengamma.strata.basics.PayReceive.PAY;
import static com.opengamma.strata.basics.PayReceive.RECEIVE;
import static com.opengamma.strata.basics.PutCall.CALL;
import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.index.IborIndices.EUR_EURIBOR_6M;
import static com.opengamma.strata.collect.TestHelper.date;
import static com.opengamma.strata.collect.TestHelper.dateUtc;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.basics.currency.Payment;
import com.opengamma.strata.basics.value.ValueSchedule;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.pricer.DiscountingPaymentPricer;
import com.opengamma.strata.pricer.impl.capfloor.IborCapletFloorletDataSet;
import com.opengamma.strata.pricer.rate.ImmutableRatesProvider;
import com.opengamma.strata.product.TradeInfo;
import com.opengamma.strata.product.capfloor.IborCapFloor;
import com.opengamma.strata.product.capfloor.IborCapFloorLeg;
import com.opengamma.strata.product.capfloor.IborCapFloorTrade;
import com.opengamma.strata.product.swap.SwapLeg;

/**
 * Test {@link NormalIborCapFloorTradePricer}.
 */
@Test
public class NormalIborCapFloorTradePricerTest {

  private static final double NOTIONAL_VALUE = 1.0e6;
  private static final ValueSchedule NOTIONAL = ValueSchedule.of(NOTIONAL_VALUE);
  private static final LocalDate START = LocalDate.of(2015, 10, 21);
  private static final LocalDate END = LocalDate.of(2020, 10, 21);
  private static final double STRIKE_VALUE = 0.0105;
  private static final ValueSchedule STRIKE = ValueSchedule.of(STRIKE_VALUE);
  private static final IborCapFloorLeg CAP_LEG =
      IborCapFloorDataSet.createCapFloorLeg(EUR_EURIBOR_6M, START, END, STRIKE, NOTIONAL, CALL, RECEIVE);
  private static final SwapLeg PAY_LEG =
      IborCapFloorDataSet.createFixedPayLeg(EUR_EURIBOR_6M, START, END, 0.0395, NOTIONAL_VALUE, PAY);
  private static final IborCapFloor CAP_TWO_LEGS = IborCapFloor.of(CAP_LEG, PAY_LEG);
  private static final IborCapFloor CAP_ONE_LEG = IborCapFloor.of(CAP_LEG);

  // valuation before start
  private static final ZonedDateTime VALUATION = dateUtc(2015, 8, 20);
  private static final ImmutableRatesProvider RATES =
      IborCapletFloorletDataSet.createRatesProvider(VALUATION.toLocalDate());
  private static final NormalIborCapletFloorletExpiryStrikeVolatilities VOLS = IborCapletFloorletDataSet
      .createNormalVolatilitiesProvider(VALUATION, EUR_EURIBOR_6M);
  private static final TradeInfo TRADE_INFO = TradeInfo.builder().tradeDate(VALUATION.toLocalDate()).build();
  private static final Payment PREMIUM = Payment.of(EUR, -NOTIONAL_VALUE * 0.19, VALUATION.toLocalDate());
  private static final IborCapFloorTrade TRADE_PAYLEG = IborCapFloorTrade.builder()
      .product(CAP_TWO_LEGS)
      .tradeInfo(TRADE_INFO)
      .build();
  private static final IborCapFloorTrade TRADE_PREMIUM = IborCapFloorTrade.builder()
      .product(CAP_ONE_LEG)
      .premium(PREMIUM)
      .tradeInfo(TradeInfo.EMPTY)
      .build();

  //   valuation at payment of 1st period
  private static final double OBS_INDEX_1 = 0.013;
  private static final double OBS_INDEX_2 = 0.0135;
  private static final LocalDateDoubleTimeSeries TIME_SERIES = LocalDateDoubleTimeSeries.builder()
      .put(date(2015, 10, 19), OBS_INDEX_1)
      .put(date(2016, 4, 19), OBS_INDEX_2)
      .build();
  private static final ZonedDateTime VALUATION_PAY = dateUtc(2016, 4, 21);
  private static final ImmutableRatesProvider RATES_PAY =
      IborCapletFloorletDataSet.createRatesProvider(VALUATION_PAY.toLocalDate(), EUR_EURIBOR_6M, TIME_SERIES);
  private static final NormalIborCapletFloorletExpiryStrikeVolatilities VOLS_PAY = IborCapletFloorletDataSet
      .createNormalVolatilitiesProvider(VALUATION_PAY, EUR_EURIBOR_6M);

  private static final double TOL = 1.0e-13;
  private static final NormalIborCapFloorTradePricer PRICER = NormalIborCapFloorTradePricer.DEFAULT;
  private static final NormalIborCapFloorProductPricer PRICER_PRODUCT = NormalIborCapFloorProductPricer.DEFAULT;
  private static final DiscountingPaymentPricer PRICER_PREMIUM = DiscountingPaymentPricer.DEFAULT;

  public void test_presentValue() {
    MultiCurrencyAmount computedWithPayLeg = PRICER.presentValue(TRADE_PAYLEG, RATES, VOLS);
    MultiCurrencyAmount computedWithPremium = PRICER.presentValue(TRADE_PREMIUM, RATES, VOLS);
    MultiCurrencyAmount pvOneLeg = PRICER_PRODUCT.presentValue(CAP_ONE_LEG, RATES, VOLS);
    MultiCurrencyAmount pvTwoLegs = PRICER_PRODUCT.presentValue(CAP_TWO_LEGS, RATES, VOLS);
    CurrencyAmount pvPrem = PRICER_PREMIUM.presentValue(PREMIUM, RATES);
    assertEquals(computedWithPayLeg, pvTwoLegs);
    assertEquals(computedWithPremium, pvOneLeg.plus(pvPrem));
  }

  public void test_presentValueSensitivity() {
    PointSensitivityBuilder computedWithPayLeg = PRICER.presentValueSensitivity(TRADE_PAYLEG, RATES, VOLS);
    PointSensitivityBuilder computedWithPremium = PRICER.presentValueSensitivity(TRADE_PREMIUM, RATES, VOLS);
    PointSensitivityBuilder pvOneLeg = PRICER_PRODUCT.presentValueSensitivity(CAP_ONE_LEG, RATES, VOLS);
    PointSensitivityBuilder pvTwoLegs = PRICER_PRODUCT.presentValueSensitivity(CAP_TWO_LEGS, RATES, VOLS);
    PointSensitivityBuilder pvPrem = PRICER_PREMIUM.presentValueSensitivity(PREMIUM, RATES);
    assertEquals(computedWithPayLeg, pvTwoLegs);
    assertEquals(computedWithPremium, pvOneLeg.combinedWith(pvPrem));
  }

  public void test_currencyExposure() {
    MultiCurrencyAmount computedWithPayLeg = PRICER.currencyExposure(TRADE_PAYLEG, RATES, VOLS);
    MultiCurrencyAmount computedWithPremium = PRICER.currencyExposure(TRADE_PREMIUM, RATES, VOLS);
    MultiCurrencyAmount pvWithPayLeg = PRICER.presentValue(TRADE_PAYLEG, RATES, VOLS);
    MultiCurrencyAmount pvWithPremium = PRICER.presentValue(TRADE_PREMIUM, RATES, VOLS);
    PointSensitivityBuilder pointWithPayLeg = PRICER.presentValueSensitivity(TRADE_PAYLEG, RATES, VOLS);
    PointSensitivityBuilder pointWithPremium = PRICER.presentValueSensitivity(TRADE_PREMIUM, RATES, VOLS);
    MultiCurrencyAmount expectedWithPayLeg = RATES.currencyExposure(pointWithPayLeg.build()).plus(pvWithPayLeg);
    MultiCurrencyAmount expectedWithPremium = RATES.currencyExposure(pointWithPremium.build()).plus(pvWithPremium);
    assertEquals(computedWithPayLeg.getAmount(EUR).getAmount(),
        expectedWithPayLeg.getAmount(EUR).getAmount(), NOTIONAL_VALUE * TOL);
    assertEquals(computedWithPremium.getAmount(EUR).getAmount(),
        expectedWithPremium.getAmount(EUR).getAmount(), NOTIONAL_VALUE * TOL);
  }

  public void test_currentCash() {
    MultiCurrencyAmount computedWithPayLeg = PRICER.currentCash(TRADE_PAYLEG, RATES, VOLS);
    MultiCurrencyAmount computedWithPremium = PRICER.currentCash(TRADE_PREMIUM, RATES, VOLS);
    assertEquals(computedWithPayLeg, MultiCurrencyAmount.of(CurrencyAmount.zero(EUR)));
    assertEquals(computedWithPremium, MultiCurrencyAmount.of(PREMIUM.getValue()));
  }

  public void test_currentCash_onPay() {
    MultiCurrencyAmount computedWithPayLeg = PRICER.currentCash(TRADE_PAYLEG, RATES_PAY, VOLS_PAY);
    MultiCurrencyAmount computedWithPremium = PRICER.currentCash(TRADE_PREMIUM, RATES_PAY, VOLS_PAY);
    MultiCurrencyAmount expectedWithPayLeg = PRICER_PRODUCT.currentCash(CAP_TWO_LEGS, RATES_PAY, VOLS_PAY);
    MultiCurrencyAmount expectedWithPremium = PRICER_PRODUCT.currentCash(CAP_ONE_LEG, RATES_PAY, VOLS_PAY);
    assertEquals(computedWithPayLeg, expectedWithPayLeg);
    assertEquals(computedWithPremium, expectedWithPremium);
  }

}