/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.surface;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.param.ParameterMetadata;

/**
 * Simple parameter metadata containing the x and y values and type.
 */
@BeanDefinition(builderScope = "private")
public final class SimpleSurfaceParameterMetadata
    implements ParameterMetadata, ImmutableBean, Serializable {

  /**
   * The type of the x-value.
   */
  @PropertyDefinition(validate = "notNull")
  private final ValueType xValueType;
  /**
   * The x-value.
   */
  @PropertyDefinition
  private final double xValue;
  /**
   * The type of the y-value.
   */
  @PropertyDefinition(validate = "notNull")
  private final ValueType yValueType;
  /**
   * The y-value.
   */
  @PropertyDefinition
  private final double yValue;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance specifying information about the x-value.
   * 
   * @param xValueType  the x-value type
   * @param xValue  the x-value
   * @param yValueType  the x-value type
   * @param yValue  the x-value
   * @return the parameter metadata based on the date and label
   */
  public static SimpleSurfaceParameterMetadata of(
      ValueType xValueType,
      double xValue,
      ValueType yValueType,
      double yValue) {

    return new SimpleSurfaceParameterMetadata(xValueType, xValue, yValueType, yValue);
  }

  //-------------------------------------------------------------------------
  @Override
  public String getLabel() {
    return xValueType + "=" + xValue + ", " + yValueType + "=" + yValue;
  }

  @Override
  public String getIdentifier() {
    return getLabel();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SimpleSurfaceParameterMetadata}.
   * @return the meta-bean, not null
   */
  public static SimpleSurfaceParameterMetadata.Meta meta() {
    return SimpleSurfaceParameterMetadata.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SimpleSurfaceParameterMetadata.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private SimpleSurfaceParameterMetadata(
      ValueType xValueType,
      double xValue,
      ValueType yValueType,
      double yValue) {
    JodaBeanUtils.notNull(xValueType, "xValueType");
    JodaBeanUtils.notNull(yValueType, "yValueType");
    this.xValueType = xValueType;
    this.xValue = xValue;
    this.yValueType = yValueType;
    this.yValue = yValue;
  }

  @Override
  public SimpleSurfaceParameterMetadata.Meta metaBean() {
    return SimpleSurfaceParameterMetadata.Meta.INSTANCE;
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
   * Gets the type of the x-value.
   * @return the value of the property, not null
   */
  public ValueType getXValueType() {
    return xValueType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the x-value.
   * @return the value of the property
   */
  public double getXValue() {
    return xValue;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of the y-value.
   * @return the value of the property, not null
   */
  public ValueType getYValueType() {
    return yValueType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the y-value.
   * @return the value of the property
   */
  public double getYValue() {
    return yValue;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SimpleSurfaceParameterMetadata other = (SimpleSurfaceParameterMetadata) obj;
      return JodaBeanUtils.equal(xValueType, other.xValueType) &&
          JodaBeanUtils.equal(xValue, other.xValue) &&
          JodaBeanUtils.equal(yValueType, other.yValueType) &&
          JodaBeanUtils.equal(yValue, other.yValue);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(xValueType);
    hash = hash * 31 + JodaBeanUtils.hashCode(xValue);
    hash = hash * 31 + JodaBeanUtils.hashCode(yValueType);
    hash = hash * 31 + JodaBeanUtils.hashCode(yValue);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("SimpleSurfaceParameterMetadata{");
    buf.append("xValueType").append('=').append(xValueType).append(',').append(' ');
    buf.append("xValue").append('=').append(xValue).append(',').append(' ');
    buf.append("yValueType").append('=').append(yValueType).append(',').append(' ');
    buf.append("yValue").append('=').append(JodaBeanUtils.toString(yValue));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SimpleSurfaceParameterMetadata}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code xValueType} property.
     */
    private final MetaProperty<ValueType> xValueType = DirectMetaProperty.ofImmutable(
        this, "xValueType", SimpleSurfaceParameterMetadata.class, ValueType.class);
    /**
     * The meta-property for the {@code xValue} property.
     */
    private final MetaProperty<Double> xValue = DirectMetaProperty.ofImmutable(
        this, "xValue", SimpleSurfaceParameterMetadata.class, Double.TYPE);
    /**
     * The meta-property for the {@code yValueType} property.
     */
    private final MetaProperty<ValueType> yValueType = DirectMetaProperty.ofImmutable(
        this, "yValueType", SimpleSurfaceParameterMetadata.class, ValueType.class);
    /**
     * The meta-property for the {@code yValue} property.
     */
    private final MetaProperty<Double> yValue = DirectMetaProperty.ofImmutable(
        this, "yValue", SimpleSurfaceParameterMetadata.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "xValueType",
        "xValue",
        "yValueType",
        "yValue");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -868509005:  // xValueType
          return xValueType;
        case -777049127:  // xValue
          return xValue;
        case -1065022510:  // yValueType
          return yValueType;
        case -748419976:  // yValue
          return yValue;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SimpleSurfaceParameterMetadata> builder() {
      return new SimpleSurfaceParameterMetadata.Builder();
    }

    @Override
    public Class<? extends SimpleSurfaceParameterMetadata> beanType() {
      return SimpleSurfaceParameterMetadata.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code xValueType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ValueType> xValueType() {
      return xValueType;
    }

    /**
     * The meta-property for the {@code xValue} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> xValue() {
      return xValue;
    }

    /**
     * The meta-property for the {@code yValueType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ValueType> yValueType() {
      return yValueType;
    }

    /**
     * The meta-property for the {@code yValue} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> yValue() {
      return yValue;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -868509005:  // xValueType
          return ((SimpleSurfaceParameterMetadata) bean).getXValueType();
        case -777049127:  // xValue
          return ((SimpleSurfaceParameterMetadata) bean).getXValue();
        case -1065022510:  // yValueType
          return ((SimpleSurfaceParameterMetadata) bean).getYValueType();
        case -748419976:  // yValue
          return ((SimpleSurfaceParameterMetadata) bean).getYValue();
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
   * The bean-builder for {@code SimpleSurfaceParameterMetadata}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<SimpleSurfaceParameterMetadata> {

    private ValueType xValueType;
    private double xValue;
    private ValueType yValueType;
    private double yValue;

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(meta());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -868509005:  // xValueType
          return xValueType;
        case -777049127:  // xValue
          return xValue;
        case -1065022510:  // yValueType
          return yValueType;
        case -748419976:  // yValue
          return yValue;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -868509005:  // xValueType
          this.xValueType = (ValueType) newValue;
          break;
        case -777049127:  // xValue
          this.xValue = (Double) newValue;
          break;
        case -1065022510:  // yValueType
          this.yValueType = (ValueType) newValue;
          break;
        case -748419976:  // yValue
          this.yValue = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public SimpleSurfaceParameterMetadata build() {
      return new SimpleSurfaceParameterMetadata(
          xValueType,
          xValue,
          yValueType,
          yValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("SimpleSurfaceParameterMetadata.Builder{");
      buf.append("xValueType").append('=').append(JodaBeanUtils.toString(xValueType)).append(',').append(' ');
      buf.append("xValue").append('=').append(JodaBeanUtils.toString(xValue)).append(',').append(' ');
      buf.append("yValueType").append('=').append(JodaBeanUtils.toString(yValueType)).append(',').append(' ');
      buf.append("yValue").append('=').append(JodaBeanUtils.toString(yValue));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}