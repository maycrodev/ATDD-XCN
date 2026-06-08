/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-064
// External ID: 145
// Suite: Modulo 7 - Docente
// Integrante: Alan Florez
//
// Resumen:
//   Verificar que el docente no puede acceder a las rutas del modulo Admin.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Usuario con rol Docente sin permisos de admin
//     (cuenta sembrada: docente.test@ucb.edu.bo)
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion como Docente
//           -> Autenticacion exitosa
//   PASO 2. Intentar navegar a /admin/cursos
//           -> Sistema evalua permisos
//   PASO 3. Observar el comportamiento
//           -> Sistema redirige a la pantalla Home
//   PASO 4. Iniciar Sesion como docente
//           -> Autenticacion exitosa
//   PASO 5. Navegar a /docente
//           -> Acceso al modulo de docente funciona correctamente
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=AlanFlorez_TC064_DocenteNoAccedeAdminTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AlanFlorez_TC064_DocenteNoAccedeAdminTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL_DOCENTE = "docente.test@ucb.edu.bo";
    private static final String PASSWORD_DOCENTE = "Docente#2026!ok";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc064_docenteNoAccedeAdminTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // PASO 1: Iniciar sesion como Docente
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL_DOCENTE);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD_DOCENTE);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        // PASO 2: Intentar navegar a /admin/cursos
        driver.get(BASE_URL + "admin/cursos");
        TimeUnit.SECONDS.sleep(3);

        // PASO 3: Observar el comportamiento
        String urlTrasAdmin = driver.getCurrentUrl();
        String bodyTrasAdmin = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean noAccedioAdmin =
                !urlTrasAdmin.endsWith("/admin/cursos") ||
                !bodyTrasAdmin.contains("administrar cursos");

        // PASO 4: Iniciar Sesion como docente (segun el TestLink esta indicacion
        // se repite — interpretamos que la sesion sigue activa).
        // Se omite re-login porque la sesion no se cerro en PASO 3.

        // PASO 5: Navegar a /docente
        driver.get(BASE_URL + "docente");
        TimeUnit.SECONDS.sleep(5);

        String urlTrasDocente = driver.getCurrentUrl();
        String bodyTrasDocente = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean accesoModuloDocente =
                urlTrasDocente.contains("/docente") &&
                (bodyTrasDocente.contains("docente") ||
                 bodyTrasDocente.contains("mis cursos"));

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 3: el sistema redirige fuera del panel admin
        Assert.assertEquals(noAccedioAdmin, true,
                "PASO 3: el sistema debio redirigir al Home y no mostrar el panel admin");
        // Resultado esperado PASO 5: acceso al modulo Docente funciona
        Assert.assertEquals(accesoModuloDocente, true,
                "PASO 5: el docente debe poder acceder a su propio modulo /docente");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
