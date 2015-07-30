package calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"calculator", "services.*", "domain"})
public class SalaryCalculatorApplication extends SpringBootServletInitializer {

    private static Class<SalaryCalculatorApplication> applicationClass = SalaryCalculatorApplication.class;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    public static void main(String[] args) {
        SpringApplication.run(applicationClass, args);
    }
}
