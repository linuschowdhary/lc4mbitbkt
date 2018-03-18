package hsbc.core.pojo;

public class LoanCalculatorConfigCSVtoJSON {
    private boolean property_showCurrencyInput;
    private String value_labelSample;

    public LoanCalculatorConfigCSVtoJSON(boolean property_showCurrencyInput, String value_labelSample) {
        this.property_showCurrencyInput = property_showCurrencyInput;
        this.value_labelSample = value_labelSample;
    }

    public boolean isProperty_showCurrencyInput() {
        return property_showCurrencyInput;
    }

    public void setProperty_showCurrencyInput(boolean property_showCurrencyInput) {
        this.property_showCurrencyInput = property_showCurrencyInput;
    }

    public String getValue_labelSample() {
        return value_labelSample;
    }

    public void setValue_labelSample(String value_labelSample) {
        this.value_labelSample = value_labelSample;
    }
}
