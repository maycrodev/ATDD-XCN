/****************************************************************/
// Historia de Usuario: Como sistema, quiero impedir que un nuevo
// estudiante se registre con una contrasena debil que no cumple las
// politicas de seguridad (>=12 chars, mayuscula, minuscula, numero,
// caracter especial) para reducir el riesgo de cuentas comprometidas.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-02:
// Validar que en el formulario de REGISTRO, al escribir una contrasena
// debil (ej. "abc123"), el panel de "reglas de contrasena" marca como
// pendientes las reglas no cumplidas y la fuerza queda en "Muy debil".
//
// PASO 1. Abrir la SPA y abrir modal de login.
// PASO 2. Cambiar a modo "Registro" (boton "Registrate aqui").
// PASO 3. Escribir una contrasena debil en el campo password.
// PASO 4. Inspeccionar el panel de fuerza/reglas.
// Resultado Esperado: la etiqueta de fuerza dice "Muy debil" y la lista
// de reglas muestra varias entradas como pendientes (icono "o").
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=JuanJose_RegistroPasswordDebilTest

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

public class JuanJose_RegistroPasswordDebilTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void registroPasswordDebilMuestraReglasIncumplidasTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // Abrir modal de login
        WebElement botonAbrirLogin = driver.findElement(By.cssSelector("button.btn-login"));
        botonAbrirLogin.click();
        TimeUnit.SECONDS.sleep(2);

        // Cambiar a modo registro
        WebElement botonToggleRegistro = driver.findElement(By.cssSelector("button.toggle-btn"));
        botonToggleRegistro.click();
        TimeUnit.SECONDS.sleep(2);

        // Escribir password debil
        WebElement inputPassword = driver.findElement(By.cssSelector("input[name='password']"));
        inputPassword.sendKeys("abc123");
        TimeUnit.SECONDS.sleep(2);

        /*********** Verificacion - Assert ***********/
        WebElement etiquetaFuerza = driver.findElement(By.cssSelector(".password-strength-label"));
        String textoFuerza = etiquetaFuerza.getText().toLowerCase();
        boolean esDebil = textoFuerza.contains("debil");
        Assert.assertEquals(true, esDebil);

        // Verificar que existen reglas pendientes en el listado
        int pendientes = driver.findElements(By.cssSelector(".password-rule-item.pending")).size();
        Assert.assertEquals(true, pendientes >= 1);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
