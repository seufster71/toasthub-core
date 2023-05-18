package org.toasthub.core.preference.model;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "pref_product")
@JsonInclude(Include.NON_NULL)
public class PrefProduct extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Text title;
	private String productCode;
	private Integer productVersion;
	private String productRegion;
	private String inheritCode;
	private Integer inheritVersion;
	private String inheritRegion;
	
	// Constructor
	public PrefProduct () {
		super();
	}
	
	// Methods
	@JsonView({View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Text.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "text_id")
	public Text getTitle() {
		return title;
	}
	public void setTitle(Text title) {
		this.title = title;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "product_code")
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "product_version")
	public Integer getProductVersion() {
		return productVersion;
	}
	public void setProductVersion(Integer productVersion) {
		this.productVersion = productVersion;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "product_region")
	public String getProductRegion() {
		return productRegion;
	}
	public void setProductRegion(String productRegion) {
		this.productRegion = productRegion;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "inherit_product_code")
	public String getInheritCode() {
		return inheritCode;
	}
	public void setInheritCode(String inheritCode) {
		this.inheritCode = inheritCode;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "inherit_product_version")
	public Integer getInheritVersion() {
		return inheritVersion;
	}
	public void setInheritVersion(Integer inheritVersion) {
		this.inheritVersion = inheritVersion;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "inherit_product_region")
	public String getInheritRegion() {
		return inheritRegion;
	}
	public void setInheritRegion(String inheritRegion) {
		this.inheritRegion = inheritRegion;
	}
}
