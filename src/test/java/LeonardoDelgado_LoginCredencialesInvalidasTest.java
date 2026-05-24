/****************************************************************/
// Historia de Usuario: Como sistema, quiero rechazar el login cuando
// la contrasena ingresada no coincide con la del usuario, mostrando un
// mensaje generico (sin filtrar si el email existe o no) para mitigar
// ataques de enumeracion.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-08:
// Validar que un login con email institucional valido pero password
// incorrecta muestra un banner de error y NO cierra el modal.
//
// PASO 1. Abrir la SPA y abrir modal de login.
// PASO 2. Ingresar email institucional valido y password incorrecta.
// PASO 3. Click "Iniciar Sesion".
// Resultado Esperado: el modal sigue abierto y se muestra el banner
// de error con texto relacionado a credenciales.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=LeonardoDelgado_LoginCredencialesInvalidasTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class LeonardoDelgado_LoginCredencialesInvalidasTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void loginConPasswordIncorrectaMuestraErrorTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']"))
                .sendKeys("estudiante.test@ucb.edu.bo");
        driver.findElement(By.cssSelector("input[name='password']"))
                .sendKeys("PassDefinitivamenteIncorrecta#999");
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(4);

        /*********** Verificacion - Assert ***********/
        // El modal sigue abierto
        int modales = driver.findElements(By.cssSelector(".login-container")).size();
        Assert.assertEquals(true, modales == 1);

        // Hay un mensaje de error visible
        String texto = driver.findElement(By.cssSelector(".login-container"))
                .getText().toLowerCase();
        boolean errorMostrado =
                texto.contains("credenciales") ||
                texto.contains("incorrect") ||
                texto.contains("invalid") ||
                texto.contains("error");
        Assert.assertEquals(true, errorMostrado);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
