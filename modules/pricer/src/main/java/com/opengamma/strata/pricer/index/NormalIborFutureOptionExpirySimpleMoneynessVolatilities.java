/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.index;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.date.DayCount;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.param.CurrencyParameterSensitivities;
import com.opengamma.strata.market.param.CurrencyParameterSensitivity;
import com.opengamma.strata.market.param.ParameterMetadata;
import com.opengamma.strata.market.param.ParameterPerturbation;
import com.opengamma.strata.market.param.UnitParameterSensitivity;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivity;
import com.opengamma.strata.market.surface.InterpolatedNodalSurface;
import com.opengamma.strata.market.surface.Surface;
import com.opengamma.strata.market.surface.SurfaceInfoType;
import com.opengamma.strata.market.surface.Surfaces;

/**
 * Data provider of volatility for Ibor future options in the normal or Bachelier model. 
 * <p>
 * The volatility is represented by a surface on the expiry and simple moneyness. 
 * The expiry is measured in number of days (not time) according to a day-count convention.
 * The simple moneyness can be on the price or on the rate (1-price).
 */
@BeanDefinition
public final class NormalIborFutureOptionExpirySimpleMoneynessVolatilities
    implements NormalIborFutureOptionVolatilities, ImmutableBean {

  /**
   * The normal volatility surface.
   * <p>
   * The x-value of the surface is the expiry, as a year fraction.
   * The y-value of the surface is the simple moneyness.
   */
  @PropertyDefinition(validate = "notNull")
  private final Surface surface;
  /** 
   * The valuation date-time.
   * <p>
   * The volatilities are calibrated for this date-time. 
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final ZonedDateTime valuationDateTime;
  /**
   * The index of the underlying future.
   */
  private final IborIndex index;  // cached, not a property
  /**
   * Whether the moneyness is on the price (true) or on the rate (false).
   */
  private final boolean moneynessOnPrice;  // cached, not a property
  /**
   * The day count convention of the surface.
   */
  private final DayCount dayCount;  // cached, not a property

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance from the volatility surface and the date-time for which it is valid.
   * <p>
   * The surface is specified by an instance of {@link Surface}, such as {@link InterpolatedNodalSurface}.
   * The surface must contain the correct metadata:
   * <ul>
   * <li>The x-value type must be {@link ValueType#YEAR_FRACTION}
   * <li>The y-value type must be {@link ValueType#SIMPLE_MONEYNESS}
   * <li>The z-value type must be {@link ValueType#NORMAL_VOLATILITY}
   * <li>The day count must be set in the additional information using {@link SurfaceInfoType#DAY_COUNT}
   * <li>The underlying index must be set in the additional information using {@link SurfaceInfoType#IBOR_INDEX}
   * </ul>
   * Suitable surface metadata can be created using
   * {@link Surfaces#iborFutureOptionNormalExpirySimpleMoneyness(String, DayCount, IborIndex)}.
   * 
   * @param surface  the implied volatility surface
   * @param valuationDateTime  the valuation date-time
   * @return the volatilities
   */
  public static NormalIborFutureOptionExpirySimpleMoneynessVolatilities of(
      Surface surface,
      ZonedDateTime valuationDateTime) {

    return new NormalIborFutureOptionExpirySimpleMoneynessVolatilities(surface, valuationDateTime);
  }

  @ImmutableConstructor
  private NormalIborFutureOptionExpirySimpleMoneynessVolatilities(
      Surface surface,
      ZonedDateTime valuationDateTime) {

    ArgChecker.notNull(surface, "surface");
    ArgChecker.notNull(valuationDateTime, "valuationDateTime");
    surface.getMetadata().getXValueType().checkEquals(
        ValueType.YEAR_FRACTION, "Incorrect x-value type for Black volatilities");
    surface.getMetadata().getYValueType().checkEquals(
        ValueType.YEAR_FRACTION, "Incorrect y-value type for Black volatilities");
    surface.getMetadata().getZValueType().checkEquals(
        ValueType.NORMAL_VOLATILITY, "Incorrect z-value type for Black volatilities");
    IborIndex index = surface.getMetadata().findInfo(SurfaceInfoType.IBOR_INDEX)
        .orElseThrow(() -> new IllegalArgumentException("Incorrect surface metadata, missing index"));
    DayCount dayCount = surface.getMetadata().findInfo(SurfaceInfoType.DAY_COUNT)
        .orElseThrow(() -> new IllegalArgumentException("Incorrect surface metadata, missing DayCount"));

    this.surface = surface;
    this.valuationDateTime = valuationDateTime;
    this.moneynessOnPrice = false;
    this.index = index;
    this.dayCount = dayCount;
  }

  //-------------------------------------------------------------------------
  @Override
  public IborFutureOptionVolatilitiesName getName() {
    return IborFutureOptionVolatilitiesName.of(surface.getName().getName());
  }

  @Override
  public IborIndex getIndex() {
    return index;
  }

  @Override
  public int getParameterCount() {
    return surface.getParameterCount();
  }

  @Override
  public double getParameter(int parameterIndex) {
    return surface.getParameter(parameterIndex);
  }

  @Override
  public ParameterMetadata getParameterMetadata(int parameterIndex) {
    return surface.getParameterMetadata(parameterIndex);
  }

  @Override
  public NormalIborFutureOptionExpirySimpleMoneynessVolatilities withParameter(int parameterIndex, double newValue) {
    return new NormalIborFutureOptionExpirySimpleMoneynessVolatilities(
        surface.withParameter(parameterIndex, newValue), valuationDateTime);
  }

  @Override
  public NormalIborFutureOptionExpirySimpleMoneynessVolatilities withPerturbation(ParameterPerturbation perturbation) {
    return new NormalIborFutureOptionExpirySimpleMoneynessVolatilities(
        surface.withPerturbation(perturbation), valuationDateTime);
  }

  //-------------------------------------------------------------------------
  @Override
  public double volatility(double expiry, LocalDate fixingDate, double strikePrice, double futurePrice) {
    double simpleMoneyness = moneynessOnPrice ? strikePrice - futurePrice : futurePrice - strikePrice;
    return surface.zValue(expiry, simpleMoneyness);
  }

  @Override
  public CurrencyParameterSensitivities parameterSensitivity(PointSensitivities pointSensitivities) {
    CurrencyParameterSensitivities sens = CurrencyParameterSensitivities.empty();
    for (PointSensitivity point : pointSensitivities.getSensitivities()) {
      if (point instanceof IborFutureOptionSensitivity) {
        IborFutureOptionSensitivity pt = (IborFutureOptionSensitivity) point;
        sens = sens.combinedWith(parameterSensitivity(pt));
      }
    }
    return sens;
  }

  private CurrencyParameterSensitivity parameterSensitivity(IborFutureOptionSensitivity point) {
    ArgChecker.isTrue(point.getIndex().equals(index),
        "Index of volatilities must be the same as index of sensitivity");
    double simpleMoneyness = moneynessOnPrice ?
        point.getStrikePrice() - point.getFuturePrice() : point.getFuturePrice() - point.getStrikePrice();
    UnitParameterSensitivity unitSens = surface.zValueParameterSensitivity(point.getExpiry(), simpleMoneyness);
    return unitSens.multipliedBy(point.getCurrency(), point.getSensitivity());
  }

  //-------------------------------------------------------------------------
  @Override
  public double relativeTime(ZonedDateTime zonedDateTime) {
    ArgChecker.notNull(zonedDateTime, "date");
    return dayCount.relativeYearFraction(valuationDateTime.toLocalDate(), zonedDateTime.toLocalDate());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code NormalIborFutureOptionExpirySimpleMoneynessVolatilities}.
   * @return the meta-bean, not null
   */
  public static NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Meta meta() {
    return NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Builder builder() {
    return new NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Builder();
  }

  @Override
  public NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Meta metaBean() {
    return NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Meta.INSTANCE;
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
   * Gets the normal volatility surface.
   * <p>
   * The x-value of the surface is the expiry, as a year fraction.
   * The y-value of the surface is the simple moneyness.
   * @return the value of the property, not null
   */
  public Surface getSurface() {
    return surface;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valuation date-time.
   * <p>
   * The volatilities are calibrated for this date-time.
   * @return the value of the property, not null
   */
  @Override
  public ZonedDateTime getValuationDateTime() {
    return valuationDateTime;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      NormalIborFutureOptionExpirySimpleMoneynessVolatilities other = (NormalIborFutureOptionExpirySimpleMoneynessVolatilities) obj;
      return JodaBeanUtils.equal(surface, other.surface) &&
          JodaBeanUtils.equal(valuationDateTime, other.valuationDateTime);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(surface);
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDateTime);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("NormalIborFutureOptionExpirySimpleMoneynessVolatilities{");
    buf.append("surface").append('=').append(surface).append(',').append(' ');
    buf.append("valuationDateTime").append('=').append(JodaBeanUtils.toString(valuationDateTime));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code NormalIborFutureOptionExpirySimpleMoneynessVolatilities}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code surface} property.
     */
    private final MetaProperty<Surface> surface = DirectMetaProperty.ofImmutable(
        this, "surface", NormalIborFutureOptionExpirySimpleMoneynessVolatilities.class, Surface.class);
    /**
     * The meta-property for the {@code valuationDateTime} property.
     */
    private final MetaProperty<ZonedDateTime> valuationDateTime = DirectMetaProperty.ofImmutable(
        this, "valuationDateTime", NormalIborFutureOptionExpirySimpleMoneynessVolatilities.class, ZonedDateTime.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "surface",
        "valuationDateTime");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1853231955:  // surface
          return surface;
        case -949589828:  // valuationDateTime
          return valuationDateTime;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Builder builder() {
      return new NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Builder();
    }

    @Override
    public Class<? extends NormalIborFutureOptionExpirySimpleMoneynessVolatilities> beanType() {
      return NormalIborFutureOptionExpirySimpleMoneynessVolatilities.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code surface} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Surface> surface() {
      return surface;
    }

    /**
     * The meta-property for the {@code valuationDateTime} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ZonedDateTime> valuationDateTime() {
      return valuationDateTime;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1853231955:  // surface
          return ((NormalIborFutureOptionExpirySimpleMoneynessVolatilities) bean).getSurface();
        case -949589828:  // valuationDateTime
          return ((NormalIborFutureOptionExpirySimpleMoneynessVolatilities) bean).getValuationDateTime();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code NormalIborFutureOptionExpirySimpleMoneynessVolatilities}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<NormalIborFutureOptionExpirySimpleMoneynessVolatilities> {

    private Surface surface;
    private ZonedDateTime valuationDateTime;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(NormalIborFutureOptionExpirySimpleMoneynessVolatilities beanToCopy) {
      this.surface = beanToCopy.getSurface();
      this.valuationDateTime = beanToCopy.getValuationDateTime();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1853231955:  // surface
          return surface;
        case -949589828:  // valuationDateTime
          return valuationDateTime;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1853231955:  // surface
          this.surface = (Surface) newValue;
          break;
        case -949589828:  // valuationDateTime
          this.valuationDateTime = (ZonedDateTime) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public NormalIborFutureOptionExpirySimpleMoneynessVolatilities build() {
      return new NormalIborFutureOptionExpirySimpleMoneynessVolatilities(
          surface,
          valuationDateTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the normal volatility surface.
     * <p>
     * The x-value of the surface is the expiry, as a year fraction.
     * The y-value of the surface is the simple moneyness.
     * @param surface  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder surface(Surface surface) {
      JodaBeanUtils.notNull(surface, "surface");
      this.surface = surface;
      return this;
    }

    /**
     * Sets the valuation date-time.
     * <p>
     * The volatilities are calibrated for this date-time.
     * @param valuationDateTime  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder valuationDateTime(ZonedDateTime valuationDateTime) {
      JodaBeanUtils.notNull(valuationDateTime, "valuationDateTime");
      this.valuationDateTime = valuationDateTime;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("NormalIborFutureOptionExpirySimpleMoneynessVolatilities.Builder{");
      buf.append("surface").append('=').append(JodaBeanUtils.toString(surface)).append(',').append(' ');
      buf.append("valuationDateTime").append('=').append(JodaBeanUtils.toString(valuationDateTime));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
