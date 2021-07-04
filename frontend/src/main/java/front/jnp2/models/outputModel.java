package front.jnp2.models;

public class outputModel {

    // currency code
    private String code;
    // full currency name
    private String name;
    // current currency rate
    private double rate;
    // user input in PLN changed to this currency
    private double output;

    // getters and setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "outputModel{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                ", output=" + output +
                '}';
    }
}
