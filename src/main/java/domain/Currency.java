package domain;

public class Currency {

    private final String name;
    private final Double rate;

    public Currency(String name, Double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public Double getRate() {
        return rate;
    }

}
