package front.jnp2.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class exchangeModel {

    // currency full name
    private String currency;

    // currency code
    private String code;

    // given API has rates as list
    private List<ratesModel> rates;

    // getters and setters
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ratesModel> getRates() {
        return rates;
    }

    public void setRates(List<ratesModel> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "exchangeModel{" +
                "currency='" + currency + '\'' +
                ", code='" + code + '\'' +
                ", rates=" + rates +
                '}';
    }
}
