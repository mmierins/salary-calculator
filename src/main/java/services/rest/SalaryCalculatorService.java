package services.rest;

import services.spring.SalaryCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SalaryCalculatorService {

    @Autowired
    private SalaryCalculator salaryCalculator;

    @RequestMapping("/calculator")
    public Map<String, Object> calculate(
            @RequestParam(value="bruto", defaultValue="0") double bruto,
            @RequestParam(value="persons", defaultValue="0") int persons,
            @RequestParam(value="currency", defaultValue="EUR") String currency) {

        salaryCalculator.setBruto(bruto);
        salaryCalculator.setPersons(persons);
        salaryCalculator.setCurrency(currency);

        Map<String, Object> retValues = new HashMap<>();
        retValues.put("neto", salaryCalculator.getNeto());
        retValues.put("employerFullPayment", salaryCalculator.getEmployerFullPayment());
        retValues.put("currency", salaryCalculator.getCurrency());

        return retValues;
    }

}
