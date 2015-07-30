package services.spring;

import domain.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Service
@Scope("singleton")
public class SalaryCalculator {
    /* all absolute values given in EUR */
    private static final double NON_TAXABLE_MIN = 75.0;
    private static final double ENTREPRENEURSHIP_RISK = 0.36;
    private static final double PERSON_DEDUCTION = 165.00;

    private static final double SOCIAL_TAX_EMPLOYEE_PERC = 10.50;
    private static final double SOCIAL_TAX_EMPLOYER_PERC = 23.59;
    private static final double IIN_TAX_PERC = 23.0;

    private static final String DEFAULT_CURRENCY = "EUR";

    private double bruto = 360.0;
    private int persons = 0;
    private String currency = DEFAULT_CURRENCY;

    private Map<String, Double> currencies = Collections.emptyMap();

    @Autowired
    public void setCurrenciesRetriever(CurrenciesRetriever currenciesRetriever) {
        currencies = new TreeMap<String, Double>(currenciesRetriever.getCurrencies());
        currencies.put("EUR", 1.00);
    }

    public void setBruto(double bruto) {
        this.bruto = bruto;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public void setCurrency(String currency) {
        if (!currencies.containsKey(currency)) {
            this.currency = DEFAULT_CURRENCY;
        } else {
            this.currency = currency;
        }
    }

    private double calcPerc(double sum, double perc) {
        return sum * (perc/100);
    }

    private double calcSocialTaxForEmployee(double bruto) {
        return calcPerc(bruto, SOCIAL_TAX_EMPLOYEE_PERC);
    }

    private double calcSocialTaxForEmployer(double bruto) {
        return calcPerc(bruto, SOCIAL_TAX_EMPLOYER_PERC);
    }

    private double calcIIN(double bruto, int persons) {
        double notPerc = bruto - calcSocialTaxForEmployee(bruto) -
                NON_TAXABLE_MIN - (persons * PERSON_DEDUCTION);
        return calcPerc(notPerc, IIN_TAX_PERC);
    }

    private double calcNetoSalary(double bruto, int persons) {
        return bruto - calcSocialTaxForEmployee(bruto) - calcIIN(bruto, persons);
    }

    private double convertCurrency(double euros, double rate) {
        return euros * rate;
    }

    public double getNeto() {
        if (bruto > 0.0) {
            double euros = calcNetoSalary(bruto, persons);
            return convertCurrency(euros, currencies.get(currency));
        } else {
            return 0;
        }
    }

    public double getEmployerFullPayment() {
        if (bruto > 0.0) {
            double euros = bruto + calcSocialTaxForEmployer(bruto) + ENTREPRENEURSHIP_RISK;
            return convertCurrency(euros, currencies.get(currency));
        } else {
            return 0;
        }
    }

    public Currency getCurrency() {
        return new Currency(currency, currencies.get(currency));
    }

}
