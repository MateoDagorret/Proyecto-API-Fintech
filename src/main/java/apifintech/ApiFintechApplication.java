package apifintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ApiFintechApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiFintechApplication.class, args);

        System.out.println();
        System.out.println("===========================================================");
        System.out.println("  API Fintech - Gestion de usuarios y cuentas");
        System.out.println("  App:      http://localhost:8080/cuentas");
        System.out.println("            http://localhost:8080/usuarios");
        System.out.println("  Swagger:  http://localhost:8080/swagger-ui/index.html");
        System.out.println("  MySQL:    jdbc:mysql://localhost:3306/api_fintech");
        System.out.println("===========================================================");
        System.out.println();
    }
}
