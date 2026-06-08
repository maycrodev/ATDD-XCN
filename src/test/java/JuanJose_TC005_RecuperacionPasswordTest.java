/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-005
// External ID: 86
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Juan Jose Cordeiro
//
// **** AUTOMATIZACION PARCIAL ****
// Los PASOS 5-6 requieren acceder a un buzon de correo real (Gmail/etc),
// abrir un email y hacer clic en un enlace de recuperacion. Esto NO es
// automatizable de forma confiable en una prueba ATDD sin agregar
// dependencias a Gmail API / IMAP. Este test automatiza PASOS 1 a 4 y
// asegura el resultado esperado del PASO 4 (correo enviado + mensaje).
//
// Resumen:
//   Verificar que la recuperacion de contrasena por correo funciona.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion (frontend y backend)
//   - Cuenta registrada con correo accesible
//     (cuenta sembrada: estudiante.test@ucb.edu.bo)
//
// Pasos del TestLink:
//   PASO 1. Abrir el navegador y navegar al sistema
//           -> Sistema carga correctamente
//   PASO 2. En el formulario de login hacer clic en '¿Olvidaste tu contrasena?'
//           -> Se muestra el formulario de recuperacion
//   PASO 3. Ingresar el correo electronico registrado
//           -> Campo acepta el correo
//   PASO 4. Hacer clic en 'Enviar enlace de recuperacion'
//           -> El sistema envia el correo y muestra mensaje de confirmacion
//   PASO 5. [MANUAL] Abrir el enlace recibido en el correo (/reset-password)
//           -> El sistema muestra el formulario para ingresar nueva contrasena
//   PASO 6. [MANUAL] Ingresar y confirmar la nueva contrasena, luego enviar
//           -> El sistema actualiza la contrasena y muestra mensaje de exito
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=JuanJose_TC005_RecuperacionPasswordTest

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

public class JuanJose_TC005_RecuperacionPasswordTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL_REGISTRADO = "estudiante.test@ucb.edu.bo";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc005_recuperacionPasswordTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        // PASO 1: Abrir el navegador y navegar al sistema
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // (Abrir modal de login para acceder al link "olvidaste tu contrasena")
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);

        // PASO 2: En el formulario de login hacer clic en '¿Olvidaste tu contrasena?'
        WebElement linkOlvido = driver.findElement(By.cssSelector("button.forgot-password"));
        linkOlvido.click();
        TimeUnit.SECONDS.sleep(2);

        // Resultado esperado PASO 2: se muestra el formulario de recuperacion
        String textoTrasLink = driver.findElement(By.cssSelector(".login-container"))
                .getText().toLowerCase();
        boolean formularioRecuperacionVisible =
                textoTrasLink.contains("recuperaci") ||
                textoTrasLink.contains("olvidaste") ||
                textoTrasLink.contains("codigo");

        // PASO 3: Ingresar el correo electronico registrado
        WebElement inputEmailReset = driver.findElement(By.cssSelector("input[type='email']"));
        inputEmailReset.sendKeys(EMAIL_REGISTRADO);
        TimeUnit.SECONDS.sleep(1);

        // PASO 4: Hacer clic en 'Enviar enlace de recuperacion'
        WebElement btnEnviar = driver.findElement(By.cssSelector("button.submit-btn[type='submit']"));
        btnEnviar.click();
        TimeUnit.SECONDS.sleep(5);

        // PASO 5 y PASO 6: NO AUTOMATIZADOS — requieren acceder al buzon de
        // correo real, abrir email, hacer click en link. Verificacion manual.

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 2: formulario de recuperacion visible
        Assert.assertEquals(formularioRecuperacionVisible, true,
                "PASO 2: debio mostrarse el formulario de recuperacion de contrasena");

        // Resultado esperado PASO 4: sistema envia el correo y muestra mensaje
        // de confirmacion. En esta SPA el siguiente paso es la pantalla del
        // codigo de 6 digitos (OTP), por lo que verificamos que aparece.
        String textoTrasEnvio = driver.findElement(By.cssSelector(".login-container"))
                .getText().toLowerCase();
        boolean confirmacionEnvio =
                textoTrasEnvio.contains("codigo") ||
                textoTrasEnvio.contains("código") ||
                textoTrasEnvio.contains("enviado") ||
                textoTrasEnvio.contains("revisa");
        Assert.assertEquals(confirmacionEnvio, true,
                "PASO 4: debio mostrarse confirmacion de envio o pantalla del codigo");

        // Verificar que aparecen 6 inputs OTP (la SPA implementa el reset
        // ingresando codigo de 6 digitos, equivalente al enlace del email)
        List<WebElement> inputsOtp = driver.findElements(
                By.cssSelector("input[inputmode='numeric']"));
        Assert.assertEquals(inputsOtp.size() >= 6, true,
                "PASO 4: la SPA debe permitir continuar el flujo de recuperacion");

        // PASOS 5-6 ([MANUAL]): el tester debe abrir el correo electronico,
        // obtener el codigo de 6 digitos, ingresarlo en la SPA y completar
        // el formulario de nueva contrasena.
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
