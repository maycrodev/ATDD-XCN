/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-008
// External ID: 89
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Alan Florez
//
// Resumen:
//   Verificar que el header cambia dinamicamente segun el rol del usuario.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Cuentas con roles distintos: Estudiante, Docente y Administrador
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion como Estudiante
//           -> Autenticacion exitosa
//   PASO 2. Observar el header del sistema
//           -> Se muestra el HeaderEstudiante con opciones correspondientes
//   PASO 3. Cerrar sesion e iniciar como Docente
//           -> Autenticacion exitosa
//   PASO 4. Observar el header del sistema
//           -> Se muestra el HeaderDocente con opciones correspondientes
//   PASO 5. Cerrar sesion e iniciar como Administrador
//           -> Autenticacion exitosa
//   PASO 6. Observar el header del sistema
//           -> Se muestra el UserHeaderDynamic con opciones de administrador
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=AlanFlorez_TC008_HeaderCambiaSegunRolTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlanFlorez_TC008_HeaderCambiaSegunRolTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    // Tres cuentas con roles distintos (pre-condicion del TC)
    private static final String EMAIL_ESTUDIANTE = "estudiante.test@ucb.edu.bo";
    private static final String PASSWORD_ESTUDIANTE = "Estudiante#2026!ok";
    private static final String EMAIL_DOCENTE = "docente.test@ucb.edu.bo";
    private static final String PASSWORD_DOCENTE = "Docente#2026!ok";
    private static final String EMAIL_ADMIN = "admin.test@ucb.edu.bo";
    private static final String PASSWORD_ADMIN = "Admin#2026!ok";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc008_headerCambiaSegunRolTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // PASO 1: Iniciar sesion como Estudiante
        loginConCredenciales(EMAIL_ESTUDIANTE, PASSWORD_ESTUDIANTE);

        // PASO 2: Observar el header del sistema (HeaderEstudiante)
        String bodyEstudiante = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean headerEstudianteVisible =
                bodyEstudiante.contains("mi perfil") ||
                bodyEstudiante.contains("catalogo") ||
                bodyEstudiante.contains("mis cursos");

        // PASO 3: Cerrar sesion e iniciar como Docente
        cerrarSesion();
        loginConCredenciales(EMAIL_DOCENTE, PASSWORD_DOCENTE);

        // PASO 4: Observar el header del sistema (HeaderDocente)
        String bodyDocente = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean headerDocenteVisible =
                bodyDocente.contains("mi panel") ||
                bodyDocente.contains("panel docente") ||
                bodyDocente.contains("mis cursos asignados");

        // PASO 5: Cerrar sesion e iniciar como Administrador
        cerrarSesion();
        loginConCredenciales(EMAIL_ADMIN, PASSWORD_ADMIN);

        // PASO 6: Observar el header del sistema (UserHeaderDynamic)
        String bodyAdmin = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean headerAdminVisible =
                bodyAdmin.contains("administracion") ||
                bodyAdmin.contains("usuarios") ||
                bodyAdmin.contains("cursos") ||
                bodyAdmin.contains("pagos");

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 2: HeaderEstudiante visible
        Assert.assertEquals(headerEstudianteVisible, true,
                "PASO 2: debio mostrarse el HeaderEstudiante");
        // Resultado esperado PASO 4: HeaderDocente visible
        Assert.assertEquals(headerDocenteVisible, true,
                "PASO 4: debio mostrarse el HeaderDocente");
        // Resultado esperado PASO 6: UserHeaderDynamic (admin) visible
        Assert.assertEquals(headerAdminVisible, true,
                "PASO 6: debio mostrarse el UserHeaderDynamic con opciones de admin");
    }

    private void loginConCredenciales(String email, String password) throws InterruptedException {
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(email);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(password);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);
    }

    private void cerrarSesion() throws InterruptedException {
        // La SPA guarda el JWT en la cookie "auth_token" (ver tokenStore.js).
        // Para cerrar sesion borramos esa cookie + storages.
        ((JavascriptExecutor) driver).executeScript(
                "document.cookie = 'auth_token=; path=/; max-age=0; SameSite=Strict';"
                + "window.localStorage.clear();"
                + "window.sessionStorage.clear();");
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
