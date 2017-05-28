/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.option;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.opengamma.strata.basics.date.Tenor;

/**
 * Raw data from the volatility market for a set of tenors.
 */
@BeanDefinition(style = "light")
public final class TenorRawOptionData
    implements ImmutableBean, Serializable {

  /**
   * The map of tenor to option data.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSortedMap<Tenor, RawOptionData> data;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance of the raw volatility.
   * <p>
   * The data values can be model parameters (like Black or normal volatilities) or direct option prices.
   * 
   * @param data  the map of data by tenor
   * @return the instance
   */
  public static TenorRawOptionData of(Map<Tenor, RawOptionData> data) {
    return new TenorRawOptionData(ImmutableSortedMap.copyOf(data));
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the set of tenors.
   * 
   * @return the set of tenors
   */
  public ImmutableSet<Tenor> getTenors() {
    return data.keySet();
  }

  /**
   * Gets the raw option data for a given tenor.
   * 
   * @param tenor  the tenor to retrieve
   * @return the raw option data
   */
  public RawOptionData getData(Tenor tenor) {
    RawOptionData result = data.get(tenor);
    if (result == null) {
      throw new IllegalArgumentException("No data found for tenor " + tenor);
    }
    return result;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code TenorRawOptionData}.
   */
  private static final MetaBean META_BEAN = LightMetaBean.of(TenorRawOptionData.class);

  /**
   * The meta-bean for {@code TenorRawOptionData}.
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

  private TenorRawOptionData(
      SortedMap<Tenor, RawOptionData> data) {
    JodaBeanUtils.notNull(data, "data");
    this.data = ImmutableSortedMap.copyOfSorted(data);
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
   * Gets the map of tenor to option data.
   * @return the value of the property, not null
   */
  public ImmutableSortedMap<Tenor, RawOptionData> getData() {
    return data;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      TenorRawOptionData other = (TenorRawOptionData) obj;
      return JodaBeanUtils.equal(data, other.data);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(data);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("TenorRawOptionData{");
    buf.append("data").append('=').append(JodaBeanUtils.toString(data));
    buf.append('}');
    return buf.toString();
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
