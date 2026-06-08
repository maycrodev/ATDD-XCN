/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-009
// External ID: 90
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Juan Jose Cordeiro
//
// Resumen:
//   Verificar que el formulario de login no permite campos vacios.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Conexion a Internet estable
//   - El sistema debe estar en ejecucion (frontend y backend)
//
// Pasos del TestLink:
//   PASO 1. Abrir el navegador y navegar al sistema
//           -> Sistema carga correctamente
//   PASO 2. Hacer clic en el boton de 'Iniciar Sesion'
//           -> Formulario de login visible
//   PASO 3. Dejar el campo de correo vacio y escribir una contrasena
//           -> Solo contrasena ingresada
//   PASO 4. Hacer clic en el boton 'Ingresar'
//           -> El sistema muestra validacion de campo requerido en el correo
//   PASO 5. Ingresar correo y dejar contrasena vacia
//           -> Solo correo ingresado
//   PASO 6. Hacer clic en el boton 'Ingresar'
//           -> El sistema muestra validacion de campo requerido en la contrasena
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=JuanJose_TC009_LoginCamposVaciosTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class JuanJose_TC009_LoginCamposVaciosTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc009_loginCamposVaciosTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        // PASO 1: Abrir el navegador y navegar al sistema
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // PASO 2: Hacer clic en el boton de 'Iniciar Sesion'
        WebElement btnIniciarSesion = driver.findElement(By.cssSelector("button.btn-login"));
        btnIniciarSesion.click();
        TimeUnit.SECONDS.sleep(2);

        WebElement inputEmail = driver.findElement(By.cssSelector("input[name='email']"));
        WebElement inputPassword = driver.findElement(By.cssSelector("input[name='password']"));
        WebElement btnSubmit = driver.findElement(By.cssSelector("button.submit-btn[type='submit']"));

        // PASO 3: Dejar el campo de correo vacio y escribir una contrasena
        inputEmail.clear();
        inputPassword.sendKeys("AlgunPassword#2026");

        // PASO 4: Hacer clic en el boton 'Ingresar' (con correo vacio)
        btnSubmit.click();
        TimeUnit.SECONDS.sleep(2);

        // Verificar que el modal sigue abierto (validacion impidio el envio)
        int modalesTrasIntento1 = driver.findElements(By.cssSelector(".login-container")).size();

        // PASO 5: Ingresar correo y dejar contrasena vacia
        inputEmail = driver.findElement(By.cssSelector("input[name='email']"));
        inputPassword = driver.findElement(By.cssSelector("input[name='password']"));
        inputEmail.sendKeys("estudiante.test@ucb.edu.bo");
        inputPassword.clear();

        // PASO 6: Hacer clic en el boton 'Ingresar' (con contrasena vacia)
        btnSubmit = driver.findElement(By.cssSelector("button.submit-btn[type='submit']"));
        btnSubmit.click();
        TimeUnit.SECONDS.sleep(2);

        int modalesTrasIntento2 = driver.findElements(By.cssSelector(".login-container")).size();

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 4: validacion de campo requerido en el correo
        // (el modal sigue abierto, no se autentico)
        Assert.assertEquals(modalesTrasIntento1, 1,
                "PASO 4: el sistema debio impedir el envio con correo vacio");

        // Resultado esperado PASO 6: validacion de campo requerido en la contrasena
        // (el modal sigue abierto, no se autentico)
        Assert.assertEquals(modalesTrasIntento2, 1,
                "PASO 6: el sistema debio impedir el envio con contrasena vacia");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
