/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.credit;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.StandardId;

/**
 * Reference data for a CDS index.
 * <p>
 * The elements define the index in the context of finding suitable market data for pricing.
 */
@BeanDefinition
public final class IndexReferenceInformation
    implements ReferenceInformation, ImmutableBean, Serializable {

  /**
   * The CDS index identifier, such as a RED pair code.
   */
  @PropertyDefinition(validate = "notNull")
  private final StandardId indexId;
  /**
   * The CDS index series identifier.
   */
  @PropertyDefinition
  private final int indexSeries;
  /**
   * The CDS index series version identifier.
   * Used to identify the version of the index over time.
   */
  @PropertyDefinition
  private final int indexAnnexVersion;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   * 
   * @param indexId  the identifier of the index that protection applies to
   * @param indexSeries  the series of the index
   * @param indexAnnexVersion  the version of the index
   * @return the reference
   */
  public static IndexReferenceInformation of(StandardId indexId, int indexSeries, int indexAnnexVersion) {
    return new IndexReferenceInformation(indexId, indexSeries, indexAnnexVersion);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the type of the reference.
   * 
   * @return the type
   */
  @Override
  public ReferenceInformationType getType() {
    return ReferenceInformationType.INDEX;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code IndexReferenceInformation}.
   * @return the meta-bean, not null
   */
  public static IndexReferenceInformation.Meta meta() {
    return IndexReferenceInformation.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(IndexReferenceInformation.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static IndexReferenceInformation.Builder builder() {
    return new IndexReferenceInformation.Builder();
  }

  private IndexReferenceInformation(
      StandardId indexId,
      int indexSeries,
      int indexAnnexVersion) {
    JodaBeanUtils.notNull(indexId, "indexId");
    this.indexId = indexId;
    this.indexSeries = indexSeries;
    this.indexAnnexVersion = indexAnnexVersion;
  }

  @Override
  public IndexReferenceInformation.Meta metaBean() {
    return IndexReferenceInformation.Meta.INSTANCE;
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
   * Gets the CDS index identifier, such as a RED pair code.
   * @return the value of the property, not null
   */
  public StandardId getIndexId() {
    return indexId;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the CDS index series identifier.
   * @return the value of the property
   */
  public int getIndexSeries() {
    return indexSeries;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the CDS index series version identifier.
   * Used to identify the version of the index over time.
   * @return the value of the property
   */
  public int getIndexAnnexVersion() {
    return indexAnnexVersion;
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
      IndexReferenceInformation other = (IndexReferenceInformation) obj;
      return JodaBeanUtils.equal(indexId, other.indexId) &&
          (indexSeries == other.indexSeries) &&
          (indexAnnexVersion == other.indexAnnexVersion);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(indexId);
    hash = hash * 31 + JodaBeanUtils.hashCode(indexSeries);
    hash = hash * 31 + JodaBeanUtils.hashCode(indexAnnexVersion);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("IndexReferenceInformation{");
    buf.append("indexId").append('=').append(indexId).append(',').append(' ');
    buf.append("indexSeries").append('=').append(indexSeries).append(',').append(' ');
    buf.append("indexAnnexVersion").append('=').append(JodaBeanUtils.toString(indexAnnexVersion));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code IndexReferenceInformation}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code indexId} property.
     */
    private final MetaProperty<StandardId> indexId = DirectMetaProperty.ofImmutable(
        this, "indexId", IndexReferenceInformation.class, StandardId.class);
    /**
     * The meta-property for the {@code indexSeries} property.
     */
    private final MetaProperty<Integer> indexSeries = DirectMetaProperty.ofImmutable(
        this, "indexSeries", IndexReferenceInformation.class, Integer.TYPE);
    /**
     * The meta-property for the {@code indexAnnexVersion} property.
     */
    private final MetaProperty<Integer> indexAnnexVersion = DirectMetaProperty.ofImmutable(
        this, "indexAnnexVersion", IndexReferenceInformation.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "indexId",
        "indexSeries",
        "indexAnnexVersion");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          return indexId;
        case 1329638889:  // indexSeries
          return indexSeries;
        case -1801228842:  // indexAnnexVersion
          return indexAnnexVersion;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public IndexReferenceInformation.Builder builder() {
      return new IndexReferenceInformation.Builder();
    }

    @Override
    public Class<? extends IndexReferenceInformation> beanType() {
      return IndexReferenceInformation.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code indexId} property.
     * @return the meta-property, not null
     */
    public MetaProperty<StandardId> indexId() {
      return indexId;
    }

    /**
     * The meta-property for the {@code indexSeries} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> indexSeries() {
      return indexSeries;
    }

    /**
     * The meta-property for the {@code indexAnnexVersion} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Integer> indexAnnexVersion() {
      return indexAnnexVersion;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          return ((IndexReferenceInformation) bean).getIndexId();
        case 1329638889:  // indexSeries
          return ((IndexReferenceInformation) bean).getIndexSeries();
        case -1801228842:  // indexAnnexVersion
          return ((IndexReferenceInformation) bean).getIndexAnnexVersion();
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
   * The bean-builder for {@code IndexReferenceInformation}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<IndexReferenceInformation> {

    private StandardId indexId;
    private int indexSeries;
    private int indexAnnexVersion;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(IndexReferenceInformation beanToCopy) {
      this.indexId = beanToCopy.getIndexId();
      this.indexSeries = beanToCopy.getIndexSeries();
      this.indexAnnexVersion = beanToCopy.getIndexAnnexVersion();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          return indexId;
        case 1329638889:  // indexSeries
          return indexSeries;
        case -1801228842:  // indexAnnexVersion
          return indexAnnexVersion;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1943291277:  // indexId
          this.indexId = (StandardId) newValue;
          break;
        case 1329638889:  // indexSeries
          this.indexSeries = (Integer) newValue;
          break;
        case -1801228842:  // indexAnnexVersion
          this.indexAnnexVersion = (Integer) newValue;
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
    public IndexReferenceInformation build() {
      return new IndexReferenceInformation(
          indexId,
          indexSeries,
          indexAnnexVersion);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the CDS index identifier, such as a RED pair code.
     * @param indexId  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder indexId(StandardId indexId) {
      JodaBeanUtils.notNull(indexId, "indexId");
      this.indexId = indexId;
      return this;
    }

    /**
     * Sets the CDS index series identifier.
     * @param indexSeries  the new value
     * @return this, for chaining, not null
     */
    public Builder indexSeries(int indexSeries) {
      this.indexSeries = indexSeries;
      return this;
    }

    /**
     * Sets the CDS index series version identifier.
     * Used to identify the version of the index over time.
     * @param indexAnnexVersion  the new value
     * @return this, for chaining, not null
     */
    public Builder indexAnnexVersion(int indexAnnexVersion) {
      this.indexAnnexVersion = indexAnnexVersion;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("IndexReferenceInformation.Builder{");
      buf.append("indexId").append('=').append(JodaBeanUtils.toString(indexId)).append(',').append(' ');
      buf.append("indexSeries").append('=').append(JodaBeanUtils.toString(indexSeries)).append(',').append(' ');
      buf.append("indexAnnexVersion").append('=').append(JodaBeanUtils.toString(indexAnnexVersion));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
