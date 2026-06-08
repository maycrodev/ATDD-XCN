/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-012
// External ID: 93
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Juan Jose Cordeiro
//
// Resumen:
//   Verificar que el formulario de cambio de contrasena valida la
//   coincidencia de contrasenas.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion (frontend y backend)
//   - Usuario autenticado con flag debe_cambiar_password = true
//     (cuenta sembrada: docente.cambio2@ucb.edu.bo / TempCambio2#2026)
//     Nota: usamos una cuenta DIFERENTE a TC-003 porque ese test consume
//     el flag debe_cambiar_password al cambiar exitosamente la contrasena.
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion con cuenta que requiere cambio de contrasena
//           -> Sistema redirige a /cambiar-password
//   PASO 2. Ingresar la nueva contrasena en el primer campo
//           -> Campo acepta el texto
//   PASO 3. Ingresar una contrasena diferente en el campo de confirmacion
//           -> Campo acepta el texto
//   PASO 4. Hacer clic en el boton de confirmar cambio
//           -> El sistema muestra error indicando que las contrasenas no coinciden
//   PASO 5. Ingresar contrasenas iguales y hacer clic en confirmar
//           -> El sistema actualiza la contrasena exitosamente
//
// Historia de Usuario:
//   Como usuario que esta cambiando su contrasena, quiero que el sistema
//   valide que ambos campos de nueva contrasena sean identicos, para evitar
//   guardar una contrasena diferente a la que intente establecer.
//
// Resultado Esperado:
//   El sistema muestra un mensaje de error indicando que las contrasenas
//   no coinciden cuando los campos nueva_password y confirmar_password
//   tienen valores distintos, impidiendo el guardado hasta que ambos
//   campos sean identicos.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=JuanJose_TC012_CambioPasswordCoincidenciaTest

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

public class JuanJose_TC012_CambioPasswordCoincidenciaTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL_CAMBIO = "docente.cambio2@ucb.edu.bo";
    private static final String PASSWORD_TEMP = "TempCambio2#2026";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc012_cambioPasswordCoincidenciaTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // PASO 1: Iniciar sesion con cuenta que requiere cambio de contrasena
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL_CAMBIO);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD_TEMP);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        // Resultado esperado PASO 1: redirige a /cambiar-password
        String urlTrasLogin = driver.getCurrentUrl();
        boolean estaEnCambioPassword = urlTrasLogin.contains("/cambiar-password");

        // PASO 2: Ingresar la nueva contrasena en el primer campo
        // PASO 3: Ingresar una contrasena diferente en el campo de confirmacion
        // El form de /cambiar-password tiene 3 inputs: password_actual,
        // nueva_password, confirmar_password (ver CambioContrasena.jsx).
        WebElement inputActual = driver.findElement(By.cssSelector("input[name='password_actual']"));
        WebElement inputNueva = driver.findElement(By.cssSelector("input[name='nueva_password']"));
        WebElement inputConfirmar = driver.findElement(By.cssSelector("input[name='confirmar_password']"));
        inputActual.sendKeys(PASSWORD_TEMP);
        inputNueva.sendKeys("NuevaPassword#2026");
        inputConfirmar.sendKeys("DiferentePassword#2026"); // <-- distinta a proposito
        TimeUnit.SECONDS.sleep(1);

        // PASO 4: Hacer clic en el boton de confirmar cambio
        WebElement btnConfirmar = driver.findElement(By.cssSelector("button.submit-password-btn"));
        btnConfirmar.click();
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 1: redirigio a /cambiar-password
        Assert.assertEquals(estaEnCambioPassword, true,
                "PASO 1: el sistema debio redirigir a /cambiar-password");

        // Resultado esperado PASO 4: error indicando que las contrasenas no coinciden
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean mensajeNoCoinciden =
                bodyText.contains("no coinciden") ||
                bodyText.contains("coinciden");
        Assert.assertEquals(mensajeNoCoinciden, true,
                "PASO 4: debio mostrar mensaje de contrasenas no coinciden");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
