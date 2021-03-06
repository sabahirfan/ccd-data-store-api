package uk.gov.hmcts.ccd.domain.model.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "")
public class WizardPage implements Serializable {

    private String id = null;
    private String label = null;
    private Integer order = null;
    private List<WizardPageField> wizardPageFields = new ArrayList<>();
    private String showCondition;

    public WizardPage(String id, String label, Integer order, List<WizardPageField> wizardPageFields, String showCondition) {
        this.id = id;
        this.label = label;
        this.order = order;
        this.wizardPageFields = wizardPageFields;
        this.showCondition = showCondition;
    }

    public WizardPage() {
    }

    @ApiModelProperty(value = "")
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("order")
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("wizard_page_fields")
    public List<WizardPageField> getWizardPageFields() {
        return wizardPageFields;
    }

    public void setWizardPageFields(List<WizardPageField> wizardPageFields) {
        this.wizardPageFields = wizardPageFields;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("show_condition")
    public String getShowCondition() {
        return showCondition;
    }

    public void setShowCondition(String showCondition) {
        this.showCondition = showCondition;
    }
}
