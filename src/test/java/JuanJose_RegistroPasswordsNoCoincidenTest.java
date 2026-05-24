/****************************************************************/
// Historia de Usuario: Como nuevo estudiante quiero recibir una alerta
// inmediata si escribo distinto la contrasena en el campo "Confirmar"
// para evitar registrarme con una contrasena que no recuerdo.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-03:
// Validar que en el formulario de REGISTRO, cuando la contrasena y la
// confirmacion NO coinciden, la SPA muestra el mensaje
// "Las contrasenas no coinciden".
//
// PASO 1. Abrir la SPA y abrir modal de login en modo registro.
// PASO 2. Escribir una contrasena valida en el campo "password".
// PASO 3. Escribir un valor diferente en "confirmPassword".
// PASO 4. Inspeccionar el mensaje de validacion.
// Resultado Esperado: aparece el texto "Las contrasenas no coinciden".
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=JuanJose_RegistroPasswordsNoCoincidenTest

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

public class JuanJose_RegistroPasswordsNoCoincidenTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void registroPasswordsNoCoincidenMuestraErrorTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // Abrir modal y cambiar a registro
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("button.toggle-btn")).click();
        TimeUnit.SECONDS.sleep(2);

        // Escribir password y confirmacion distintos
        WebElement inputPassword = driver.findElement(By.cssSelector("input[name='password']"));
        WebElement inputConfirm = driver.findElement(By.cssSelector("input[name='confirmPassword']"));
        inputPassword.sendKeys("PasswordSegura#2026");
        inputConfirm.sendKeys("OtraDistinta#2026");
        TimeUnit.SECONDS.sleep(2);

        /*********** Verificacion - Assert ***********/
        WebElement contenedor = driver.findElement(By.cssSelector(".login-container"));
        String texto = contenedor.getText().toLowerCase();
        boolean mensajePresente =
                texto.contains("no coinciden") ||
                texto.contains("contrasenas no coinciden") ||
                texto.contains("contraseñas no coinciden");
        Assert.assertEquals(true, mensajePresente);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
