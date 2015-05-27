/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance;

import org.joda.beans.ImmutableBean;

import com.opengamma.strata.basics.CalculationTarget;

/**
 * A single trade.
 * <p>
 * A trade is a transaction that occurred on a specific date between two counterparties.
 * For example, an interest rate swap trade agreed on a particular date for
 * cash-flows in the future.
 * <p>
 * Implementations of this interface must be immutable beans.
 */
public interface Trade
    extends CalculationTarget, ImmutableBean {

  /**
   * The additional trade information.
   * <p>
   * This allows additional information to be attached to the trade.
   * 
   * @return the additional trade info
   */
  public abstract TradeInfo getTradeInfo();

}
