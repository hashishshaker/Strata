/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.rate;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.FxRateProvider;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.MarketDataFxRateProvider;
import com.opengamma.strata.data.ObservableSource;

/**
 * The default FX rate lookup.
 */
@BeanDefinition(style = "light", constructorScope = "package")
final class DefaultFxRateLookup
    implements FxRateLookup, ImmutableBean, Serializable {

  /**
   * The singleton instance.
   */
  static final DefaultFxRateLookup DEFAULT = new DefaultFxRateLookup(null, ObservableSource.NONE);

  /**
   * The triangulation currency.
   */
  @PropertyDefinition(get = "optional")
  private final Currency currency;
  /**
   * The source of observable market data.
   */
  @PropertyDefinition(validate = "notNull")
  private final ObservableSource observableSource;

  // creates an instance
  DefaultFxRateLookup(ObservableSource observableSource) {
    this(null, observableSource);
  }

  //-------------------------------------------------------------------------
  @Override
  public FxRateProvider fxRateProvider(MarketData marketData) {
    if (currency == null) {
      return MarketDataFxRateProvider.of(marketData, observableSource);
    }
    return MarketDataFxRateProvider.of(marketData, observableSource, currency);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DefaultFxRateLookup}.
   */
  private static final MetaBean META_BEAN = LightMetaBean.of(DefaultFxRateLookup.class);

  /**
   * The meta-bean for {@code DefaultFxRateLookup}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  static {
    JodaBeanUtils.registerMetaBean(META_BEAN);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   * @param currency  the value of the property
   * @param observableSource  the value of the property, not null
   */
  DefaultFxRateLookup(
      Currency currency,
      ObservableSource observableSource) {
    JodaBeanUtils.notNull(observableSource, "observableSource");
    this.currency = currency;
    this.observableSource = observableSource;
  }

  @Override
  public MetaBean metaBean() {
    return META_BEAN;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the triangulation currency.
   * @return the optional value of the property, not null
   */
  public Optional<Currency> getCurrency() {
    return Optional.ofNullable(currency);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the source of observable market data.
   * @return the value of the property, not null
   */
  public ObservableSource getObservableSource() {
    return observableSource;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DefaultFxRateLookup other = (DefaultFxRateLookup) obj;
      return JodaBeanUtils.equal(currency, other.currency) &&
          JodaBeanUtils.equal(observableSource, other.observableSource);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(currency);
    hash = hash * 31 + JodaBeanUtils.hashCode(observableSource);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("DefaultFxRateLookup{");
    buf.append("currency").append('=').append(currency).append(',').append(' ');
    buf.append("observableSource").append('=').append(JodaBeanUtils.toString(observableSource));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}