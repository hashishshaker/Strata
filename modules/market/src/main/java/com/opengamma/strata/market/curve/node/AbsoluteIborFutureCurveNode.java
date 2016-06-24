/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve.node;

import static java.time.temporal.ChronoUnit.MONTHS;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableDefaults;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.ObservableId;
import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.curve.CurveNode;
import com.opengamma.strata.market.curve.CurveNodeDate;
import com.opengamma.strata.market.observable.QuoteId;
import com.opengamma.strata.market.param.DatedParameterMetadata;
import com.opengamma.strata.market.param.YearMonthDateParameterMetadata;
import com.opengamma.strata.product.index.IborFutureTrade;
import com.opengamma.strata.product.index.ResolvedIborFutureTrade;
import com.opengamma.strata.product.index.type.IborFutureConvention;

/**
 * A curve node whose instrument is an Ibor Future specified absolutely by year-month.
 * <p>
 * The trade produced by the node will be a long for a positive quantity and a short for a negative quantity. 
 * This convention is line with other nodes where a positive quantity is similar to long a bond or deposit.
 */
@BeanDefinition
public final class AbsoluteIborFutureCurveNode
    implements CurveNode, ImmutableBean, Serializable {

  /**
   * The convention for the Ibor Future associated with this node.
   */
  @PropertyDefinition(validate = "notNull")
  private final IborFutureConvention convention;
  /**
   * The year-month of the Ibor Future associated with this node.
   */
  @PropertyDefinition(validate = "notNull")
  private final YearMonth yearMonth;
  /**
   * The identifier of the market data value which provides the price.
   */
  @PropertyDefinition(validate = "notNull")
  private final QuoteId rateId;
  /**
   * The additional spread added to the price.
   */
  @PropertyDefinition
  private final double additionalSpread;
  /**
   * The label to use for the node, may be empty.
   * <p>
   * If empty, a default label will be created when the metadata is built.
   * The default label depends on the valuation date, so cannot be created in the node.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final String label;
  /**
   * The method by which the date of the node is calculated, defaulted to 'End'.
   */
  @PropertyDefinition
  private final CurveNodeDate date;

  //-------------------------------------------------------------------------
  /**
   * Obtains a curve node for an Ibor Future using the specified template and rate key.
   *
   * @param convention  the convention defining the future
   * @param yearMonth  the year-month that the future is defined to be for
   * @param rateId  the identifier of the market rate for the security
   * @return a node whose instrument is built from the template using a market rate
   */
  public static AbsoluteIborFutureCurveNode of(
      IborFutureConvention convention,
      YearMonth yearMonth,
      QuoteId rateId) {

    return of(convention, yearMonth, rateId, 0d);
  }

  /**
   * Obtains a curve node for an Ibor Future using the specified template, rate key and spread.
   *
   * @param convention  the convention defining the future
   * @param yearMonth  the year-month that the future is defined to be for
   * @param rateId  the identifier of the market rate for the security
   * @param additionalSpread  the additional spread amount added to the rate
   * @return a node whose instrument is built from the template using a market rate
   */
  public static AbsoluteIborFutureCurveNode of(
      IborFutureConvention convention,
      YearMonth yearMonth,
      QuoteId rateId,
      double additionalSpread) {

    return of(convention, yearMonth, rateId, additionalSpread, "");
  }

  /**
   * Obtains a curve node for an Ibor Future using the specified template, rate key, spread and label.
   *
   * @param convention  the convention defining the future
   * @param yearMonth  the year-month that the future is defined to be for
   * @param rateId  the identifier of the market rate for the security
   * @param additionalSpread  the additional spread amount added to the rate
   * @param label  the label to use for the node, if empty an appropriate default label will be generated
   * @return a node whose instrument is built from the template using a market rate
   */
  public static AbsoluteIborFutureCurveNode of(
      IborFutureConvention convention,
      YearMonth yearMonth,
      QuoteId rateId,
      double additionalSpread,
      String label) {

    return new AbsoluteIborFutureCurveNode(convention, yearMonth, rateId, additionalSpread, label, CurveNodeDate.END);
  }

  @ImmutableDefaults
  private static void applyDefaults(Builder builder) {
    builder.date = CurveNodeDate.END;
  }

  //-------------------------------------------------------------------------
  @Override
  public Set<ObservableId> requirements() {
    return ImmutableSet.of(rateId);
  }

  @Override
  public DatedParameterMetadata metadata(LocalDate valuationDate, ReferenceData refData) {
    LocalDate referenceDate = convention.calculateReferenceDateFromTradeDate(valuationDate, yearMonth, refData);
    LocalDate nodeDate = date.calculate(
        () -> calculateEnd(referenceDate, refData),
        () -> calculateLastFixingDate(valuationDate, refData));
    if (label.isEmpty()) {
      return YearMonthDateParameterMetadata.of(nodeDate, YearMonth.from(referenceDate));
    }
    return YearMonthDateParameterMetadata.of(nodeDate, YearMonth.from(referenceDate), label);
  }

  // calculate the end date
  private LocalDate calculateEnd(LocalDate referenceDate, ReferenceData refData) {
    return convention.getIndex().calculateMaturityFromEffective(referenceDate, refData);
  }

  // calculate the last fixing date
  private LocalDate calculateLastFixingDate(LocalDate valuationDate, ReferenceData refData) {
    IborFutureTrade trade = convention.createTrade(valuationDate, rateId.toSecurityId(), yearMonth, 1, 1, 0, refData);
    return trade.getProduct().getFixingDate();
  }

  @Override
  public IborFutureTrade trade(double quantity, MarketData marketData, ReferenceData refData) {
    LocalDate valuationDate = marketData.getValuationDate();
    double price = marketPrice(marketData) + additionalSpread;
    return convention.createTrade(valuationDate, rateId.toSecurityId(), yearMonth, quantity, 1d, price, refData);
  }

  @Override
  public ResolvedIborFutureTrade resolvedTrade(double quantity, MarketData marketData, ReferenceData refData) {
    return trade(quantity, marketData, refData).resolve(refData);
  }

  @Override
  public double initialGuess(MarketData marketData, ValueType valueType) {
    if (ValueType.ZERO_RATE.equals(valueType) || ValueType.FORWARD_RATE.equals(valueType)) {
      return 1d - marketPrice(marketData);
    }
    if (ValueType.DISCOUNT_FACTOR.equals(valueType)) {
      double approximateMaturity = MONTHS.between(marketData.getValuationDate(), yearMonth.atEndOfMonth()) / 12d;
      return Math.exp(-approximateMaturity * (1d - marketPrice(marketData)));
    }
    return 0d;
  }

  // scale (100 - percentRate) to (1 - decimalRate)
  private Double marketPrice(MarketData marketData) {
    return marketData.getValue(rateId) / 100;
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a copy of this node with the specified date.
   * 
   * @param date  the date to use
   * @return the node based on this node with the specified date
   */
  public AbsoluteIborFutureCurveNode withDate(CurveNodeDate date) {
    return new AbsoluteIborFutureCurveNode(convention, yearMonth, rateId, additionalSpread, label, date);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code AbsoluteIborFutureCurveNode}.
   * @return the meta-bean, not null
   */
  public static AbsoluteIborFutureCurveNode.Meta meta() {
    return AbsoluteIborFutureCurveNode.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(AbsoluteIborFutureCurveNode.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static AbsoluteIborFutureCurveNode.Builder builder() {
    return new AbsoluteIborFutureCurveNode.Builder();
  }

  private AbsoluteIborFutureCurveNode(
      IborFutureConvention convention,
      YearMonth yearMonth,
      QuoteId rateId,
      double additionalSpread,
      String label,
      CurveNodeDate date) {
    JodaBeanUtils.notNull(convention, "convention");
    JodaBeanUtils.notNull(yearMonth, "yearMonth");
    JodaBeanUtils.notNull(rateId, "rateId");
    JodaBeanUtils.notNull(label, "label");
    this.convention = convention;
    this.yearMonth = yearMonth;
    this.rateId = rateId;
    this.additionalSpread = additionalSpread;
    this.label = label;
    this.date = date;
  }

  @Override
  public AbsoluteIborFutureCurveNode.Meta metaBean() {
    return AbsoluteIborFutureCurveNode.Meta.INSTANCE;
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
   * Gets the convention for the Ibor Future associated with this node.
   * @return the value of the property, not null
   */
  public IborFutureConvention getConvention() {
    return convention;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the year-month of the Ibor Future associated with this node.
   * @return the value of the property, not null
   */
  public YearMonth getYearMonth() {
    return yearMonth;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identifier of the market data value which provides the price.
   * @return the value of the property, not null
   */
  public QuoteId getRateId() {
    return rateId;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the additional spread added to the price.
   * @return the value of the property
   */
  public double getAdditionalSpread() {
    return additionalSpread;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the label to use for the node, may be empty.
   * <p>
   * If empty, a default label will be created when the metadata is built.
   * The default label depends on the valuation date, so cannot be created in the node.
   * @return the value of the property, not null
   */
  @Override
  public String getLabel() {
    return label;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the method by which the date of the node is calculated, defaulted to 'End'.
   * @return the value of the property
   */
  public CurveNodeDate getDate() {
    return date;
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
      AbsoluteIborFutureCurveNode other = (AbsoluteIborFutureCurveNode) obj;
      return JodaBeanUtils.equal(convention, other.convention) &&
          JodaBeanUtils.equal(yearMonth, other.yearMonth) &&
          JodaBeanUtils.equal(rateId, other.rateId) &&
          JodaBeanUtils.equal(additionalSpread, other.additionalSpread) &&
          JodaBeanUtils.equal(label, other.label) &&
          JodaBeanUtils.equal(date, other.date);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(convention);
    hash = hash * 31 + JodaBeanUtils.hashCode(yearMonth);
    hash = hash * 31 + JodaBeanUtils.hashCode(rateId);
    hash = hash * 31 + JodaBeanUtils.hashCode(additionalSpread);
    hash = hash * 31 + JodaBeanUtils.hashCode(label);
    hash = hash * 31 + JodaBeanUtils.hashCode(date);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("AbsoluteIborFutureCurveNode{");
    buf.append("convention").append('=').append(convention).append(',').append(' ');
    buf.append("yearMonth").append('=').append(yearMonth).append(',').append(' ');
    buf.append("rateId").append('=').append(rateId).append(',').append(' ');
    buf.append("additionalSpread").append('=').append(additionalSpread).append(',').append(' ');
    buf.append("label").append('=').append(label).append(',').append(' ');
    buf.append("date").append('=').append(JodaBeanUtils.toString(date));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code AbsoluteIborFutureCurveNode}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code convention} property.
     */
    private final MetaProperty<IborFutureConvention> convention = DirectMetaProperty.ofImmutable(
        this, "convention", AbsoluteIborFutureCurveNode.class, IborFutureConvention.class);
    /**
     * The meta-property for the {@code yearMonth} property.
     */
    private final MetaProperty<YearMonth> yearMonth = DirectMetaProperty.ofImmutable(
        this, "yearMonth", AbsoluteIborFutureCurveNode.class, YearMonth.class);
    /**
     * The meta-property for the {@code rateId} property.
     */
    private final MetaProperty<QuoteId> rateId = DirectMetaProperty.ofImmutable(
        this, "rateId", AbsoluteIborFutureCurveNode.class, QuoteId.class);
    /**
     * The meta-property for the {@code additionalSpread} property.
     */
    private final MetaProperty<Double> additionalSpread = DirectMetaProperty.ofImmutable(
        this, "additionalSpread", AbsoluteIborFutureCurveNode.class, Double.TYPE);
    /**
     * The meta-property for the {@code label} property.
     */
    private final MetaProperty<String> label = DirectMetaProperty.ofImmutable(
        this, "label", AbsoluteIborFutureCurveNode.class, String.class);
    /**
     * The meta-property for the {@code date} property.
     */
    private final MetaProperty<CurveNodeDate> date = DirectMetaProperty.ofImmutable(
        this, "date", AbsoluteIborFutureCurveNode.class, CurveNodeDate.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "convention",
        "yearMonth",
        "rateId",
        "additionalSpread",
        "label",
        "date");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return convention;
        case -496678845:  // yearMonth
          return yearMonth;
        case -938107365:  // rateId
          return rateId;
        case 291232890:  // additionalSpread
          return additionalSpread;
        case 102727412:  // label
          return label;
        case 3076014:  // date
          return date;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public AbsoluteIborFutureCurveNode.Builder builder() {
      return new AbsoluteIborFutureCurveNode.Builder();
    }

    @Override
    public Class<? extends AbsoluteIborFutureCurveNode> beanType() {
      return AbsoluteIborFutureCurveNode.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code convention} property.
     * @return the meta-property, not null
     */
    public MetaProperty<IborFutureConvention> convention() {
      return convention;
    }

    /**
     * The meta-property for the {@code yearMonth} property.
     * @return the meta-property, not null
     */
    public MetaProperty<YearMonth> yearMonth() {
      return yearMonth;
    }

    /**
     * The meta-property for the {@code rateId} property.
     * @return the meta-property, not null
     */
    public MetaProperty<QuoteId> rateId() {
      return rateId;
    }

    /**
     * The meta-property for the {@code additionalSpread} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> additionalSpread() {
      return additionalSpread;
    }

    /**
     * The meta-property for the {@code label} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> label() {
      return label;
    }

    /**
     * The meta-property for the {@code date} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveNodeDate> date() {
      return date;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return ((AbsoluteIborFutureCurveNode) bean).getConvention();
        case -496678845:  // yearMonth
          return ((AbsoluteIborFutureCurveNode) bean).getYearMonth();
        case -938107365:  // rateId
          return ((AbsoluteIborFutureCurveNode) bean).getRateId();
        case 291232890:  // additionalSpread
          return ((AbsoluteIborFutureCurveNode) bean).getAdditionalSpread();
        case 102727412:  // label
          return ((AbsoluteIborFutureCurveNode) bean).getLabel();
        case 3076014:  // date
          return ((AbsoluteIborFutureCurveNode) bean).getDate();
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
   * The bean-builder for {@code AbsoluteIborFutureCurveNode}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<AbsoluteIborFutureCurveNode> {

    private IborFutureConvention convention;
    private YearMonth yearMonth;
    private QuoteId rateId;
    private double additionalSpread;
    private String label;
    private CurveNodeDate date;

    /**
     * Restricted constructor.
     */
    private Builder() {
      applyDefaults(this);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(AbsoluteIborFutureCurveNode beanToCopy) {
      this.convention = beanToCopy.getConvention();
      this.yearMonth = beanToCopy.getYearMonth();
      this.rateId = beanToCopy.getRateId();
      this.additionalSpread = beanToCopy.getAdditionalSpread();
      this.label = beanToCopy.getLabel();
      this.date = beanToCopy.getDate();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return convention;
        case -496678845:  // yearMonth
          return yearMonth;
        case -938107365:  // rateId
          return rateId;
        case 291232890:  // additionalSpread
          return additionalSpread;
        case 102727412:  // label
          return label;
        case 3076014:  // date
          return date;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          this.convention = (IborFutureConvention) newValue;
          break;
        case -496678845:  // yearMonth
          this.yearMonth = (YearMonth) newValue;
          break;
        case -938107365:  // rateId
          this.rateId = (QuoteId) newValue;
          break;
        case 291232890:  // additionalSpread
          this.additionalSpread = (Double) newValue;
          break;
        case 102727412:  // label
          this.label = (String) newValue;
          break;
        case 3076014:  // date
          this.date = (CurveNodeDate) newValue;
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
    public AbsoluteIborFutureCurveNode build() {
      return new AbsoluteIborFutureCurveNode(
          convention,
          yearMonth,
          rateId,
          additionalSpread,
          label,
          date);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the convention for the Ibor Future associated with this node.
     * @param convention  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder convention(IborFutureConvention convention) {
      JodaBeanUtils.notNull(convention, "convention");
      this.convention = convention;
      return this;
    }

    /**
     * Sets the year-month of the Ibor Future associated with this node.
     * @param yearMonth  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder yearMonth(YearMonth yearMonth) {
      JodaBeanUtils.notNull(yearMonth, "yearMonth");
      this.yearMonth = yearMonth;
      return this;
    }

    /**
     * Sets the identifier of the market data value which provides the price.
     * @param rateId  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder rateId(QuoteId rateId) {
      JodaBeanUtils.notNull(rateId, "rateId");
      this.rateId = rateId;
      return this;
    }

    /**
     * Sets the additional spread added to the price.
     * @param additionalSpread  the new value
     * @return this, for chaining, not null
     */
    public Builder additionalSpread(double additionalSpread) {
      this.additionalSpread = additionalSpread;
      return this;
    }

    /**
     * Sets the label to use for the node, may be empty.
     * <p>
     * If empty, a default label will be created when the metadata is built.
     * The default label depends on the valuation date, so cannot be created in the node.
     * @param label  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder label(String label) {
      JodaBeanUtils.notNull(label, "label");
      this.label = label;
      return this;
    }

    /**
     * Sets the method by which the date of the node is calculated, defaulted to 'End'.
     * @param date  the new value
     * @return this, for chaining, not null
     */
    public Builder date(CurveNodeDate date) {
      this.date = date;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(224);
      buf.append("AbsoluteIborFutureCurveNode.Builder{");
      buf.append("convention").append('=').append(JodaBeanUtils.toString(convention)).append(',').append(' ');
      buf.append("yearMonth").append('=').append(JodaBeanUtils.toString(yearMonth)).append(',').append(' ');
      buf.append("rateId").append('=').append(JodaBeanUtils.toString(rateId)).append(',').append(' ');
      buf.append("additionalSpread").append('=').append(JodaBeanUtils.toString(additionalSpread)).append(',').append(' ');
      buf.append("label").append('=').append(JodaBeanUtils.toString(label)).append(',').append(' ');
      buf.append("date").append('=').append(JodaBeanUtils.toString(date));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
