/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.index;

import com.opengamma.strata.market.param.ParameterPerturbation;

/**
 * Volatility for Ibor future options in the normal or Bachelier model.
 */
public interface NormalIborFutureOptionVolatilities
    extends IborFutureOptionVolatilities {

  @Override
  public abstract NormalIborFutureOptionVolatilities withParameter(int parameterIndex, double newValue);

  @Override
  public abstract NormalIborFutureOptionVolatilities withPerturbation(ParameterPerturbation perturbation);

}
