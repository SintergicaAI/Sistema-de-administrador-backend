package com.sintergica.apiv2;

import com.sintergica.apiv2.controlador.Clientes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Apiv2ApplicationTests {

    @Autowired(required = false)
    private Clientes clientes;

    @Test
    void contextLoads() {
        System.out.println("Contexto cargado");
        assertNotNull(clientes, "El bean clientes no puede ser null");
        Assertions.assertEquals(1,1);
    }


}
