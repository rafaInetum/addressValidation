package org.correos.app.addressvalidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AddressValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AddressValidationApplication.class, args);
    }

}
