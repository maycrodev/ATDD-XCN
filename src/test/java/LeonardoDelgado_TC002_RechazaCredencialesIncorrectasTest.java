/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-002
// External ID: 83
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Leonardo Delgado
//
// Resumen:
//   Verificar que el sistema rechaza credenciales incorrectas.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Tener una cuenta registrada
//
// Pasos del TestLink:
//   PASO 1. Abrir el navegador web
//           -> Navegador abierto correctamente
//   PASO 2. Navegar a la URL del sistema
//           -> La pagina de inicio carga correctamente
//   PASO 3. Hacer clic en 'Iniciar Sesion'
//           -> Se muestra el formulario de inicio de sesion
//   PASO 4. Ingresar un correo valido pero contrasena incorrecta
//           -> Los campos aceptan el texto ingresado
//   PASO 5. Hacer clic en el boton 'Ingresar'
//           -> El sistema muestra un mensaje de error indicando
//              credenciales invalidas y no permite el acceso
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=LeonardoDelgado_TC002_RechazaCredencialesIncorrectasTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class LeonardoDelgado_TC002_RechazaCredencialesIncorrectasTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc002_rechazaCredencialesIncorrectasTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        // PASO 1: Abrir el navegador web (hecho en @BeforeTest)
        // PASO 2: Navegar a la URL del sistema
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // PASO 3: Hacer clic en 'Iniciar Sesion'
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        int modalesAbiertos = driver.findElements(By.cssSelector(".login-container")).size();
        boolean formularioVisible = modalesAbiertos == 1;

        // PASO 4: Ingresar un correo valido pero contrasena incorrecta
        driver.findElement(By.cssSelector("input[name='email']"))
                .sendKeys("estudiante.test@ucb.edu.bo");
        driver.findElement(By.cssSelector("input[name='password']"))
                .sendKeys("PasswordDefinitivamenteIncorrecta#9999");
        TimeUnit.SECONDS.sleep(1);

        // PASO 5: Hacer clic en el boton 'Ingresar'
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(4);

        // Resultado esperado PASO 5: mensaje de error + no permite acceso.
        // El JWT exitoso se guardaria en la cookie "auth_token" — debe seguir vacia.
        Object token = ((JavascriptExecutor) driver).executeScript(
                "var m = document.cookie.match(/(?:^|; )auth_token=([^;]*)/);"
                + "return m ? decodeURIComponent(m[1]) : null;");
        boolean noAutenticado = token == null || token.toString().isEmpty();

        int modalesTrasIntento = driver.findElements(By.cssSelector(".login-container")).size();
        boolean modalSigueAbierto = modalesTrasIntento == 1;

        String textoModal = driver.findElement(By.cssSelector(".login-container"))
                .getText().toLowerCase();
        boolean mensajeErrorVisible =
                textoModal.contains("credenciales") ||
                textoModal.contains("incorrect") ||
                textoModal.contains("invalid") ||
                textoModal.contains("error");

        /*********** Verificacion - Assert ***********/
        Assert.assertEquals(formularioVisible, true,
                "PASO 3: el formulario de login debio aparecer");
        Assert.assertEquals(noAutenticado, true,
                "PASO 5: el sistema NO debio autenticar (no debe haber token JWT)");
        Assert.assertEquals(modalSigueAbierto, true,
                "PASO 5: el modal de login debio permanecer abierto");
        Assert.assertEquals(mensajeErrorVisible, true,
                "PASO 5: el sistema debio mostrar un mensaje de error de credenciales");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
