/****************************************************************/
// Historia de Usuario: Como estudiante que olvido su contrasena
// quiero solicitar un codigo de recuperacion por email para poder
// restablecerla sin contactar a soporte.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-09:
// Validar que el flujo de "Olvidaste tu contrasena?" avanza del paso
// "email" al paso "codigo" cuando se envia un email cualquiera (la
// API responde de forma resistente a enumeracion: siempre 200).
//
// PASO 1. Abrir la SPA y abrir modal de login.
// PASO 2. Click en "Olvidaste tu contrasena?".
// PASO 3. Escribir un email institucional valido.
// PASO 4. Click "Enviar codigo de verificacion".
// Resultado Esperado: la SPA muestra la pantalla de ingreso de los
// 6 digitos del codigo (titulo "Ingresa el codigo").
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=LeonardoDelgado_OlvidoPasswordEnviaCodigoTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LeonardoDelgado_OlvidoPasswordEnviaCodigoTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void olvidoPasswordAvanzaAPantallaCodigoTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // Abrir modal de login
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);

        // Click "Olvidaste tu contrasena?"
        WebElement linkOlvido = driver.findElement(By.cssSelector("button.forgot-password"));
        linkOlvido.click();
        TimeUnit.SECONDS.sleep(2);

        // Escribir email institucional valido
        WebElement inputEmailReset = driver.findElement(By.cssSelector("input[type='email']"));
        inputEmailReset.sendKeys("estudiante.test@ucb.edu.bo");

        // Enviar
        WebElement botonEnviar = driver.findElement(By.cssSelector("button.submit-btn[type='submit']"));
        botonEnviar.click();
        TimeUnit.SECONDS.sleep(5);

        /*********** Verificacion - Assert ***********/
        String texto = driver.findElement(By.cssSelector(".login-container"))
                .getText().toLowerCase();
        boolean enPasoCodigo =
                texto.contains("ingresa el codigo") ||
                texto.contains("ingresa el código") ||
                texto.contains("codigo de 6 dig") ||
                texto.contains("código de 6 dig");
        Assert.assertEquals(true, enPasoCodigo);

        // Debe existir el grupo de 6 inputs de 1 digito (OTP)
        List<WebElement> inputsOtp = driver.findElements(
                By.cssSelector("input[inputmode='numeric']"));
        Assert.assertEquals(true, inputsOtp.size() == 6);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
