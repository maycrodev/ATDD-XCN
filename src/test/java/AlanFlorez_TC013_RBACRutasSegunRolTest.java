/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-013
// External ID: 94
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Alan Florez
// *** CASO ANCLA OBLIGATORIO — Flujo F2 (Autorizacion / RBAC) ***
//
// Resumen:
//   Verificar que el sistema protege rutas segun rol (RBAC).
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Usuario con rol Estudiante autenticado
//     (cuenta sembrada: estudiante.test@ucb.edu.bo)
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion como Estudiante
//           -> Autenticacion exitosa
//   PASO 2. Intentar navegar manualmente a /docente
//           -> El sistema bloquea el acceso y redirige
//   PASO 3. Intentar navegar a /admin/cursos
//           -> El sistema bloquea el acceso y redirige
//   PASO 4. Intentar navegar a /admin/pagos
//           -> El sistema bloquea el acceso y redirige
//   PASO 5. Navegar a /cursos
//           -> El sistema permite el acceso correctamente
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=AlanFlorez_TC013_RBACRutasSegunRolTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AlanFlorez_TC013_RBACRutasSegunRolTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL_ESTUDIANTE = "estudiante.test@ucb.edu.bo";
    private static final String PASSWORD_ESTUDIANTE = "Estudiante#2026!ok";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc013_rbacRutasSegunRolTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // PASO 1: Iniciar sesion como Estudiante
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL_ESTUDIANTE);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD_ESTUDIANTE);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        // PASO 2: Intentar navegar manualmente a /docente
        driver.get(BASE_URL + "docente");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasDocente = driver.getCurrentUrl();
        String bodyTrasDocente = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoDocente =
                !urlTrasDocente.endsWith("/docente") ||
                !bodyTrasDocente.contains("mis cursos asignados");

        // PASO 3: Intentar navegar a /admin/cursos
        driver.get(BASE_URL + "admin/cursos");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasAdminCursos = driver.getCurrentUrl();
        String bodyTrasAdminCursos = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoAdminCursos =
                !urlTrasAdminCursos.endsWith("/admin/cursos") ||
                !bodyTrasAdminCursos.contains("administrar cursos");

        // PASO 4: Intentar navegar a /admin/pagos
        driver.get(BASE_URL + "admin/pagos");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasAdminPagos = driver.getCurrentUrl();
        String bodyTrasAdminPagos = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoAdminPagos =
                !urlTrasAdminPagos.endsWith("/admin/pagos") ||
                !bodyTrasAdminPagos.contains("gestion de pagos");

        // PASO 5: Navegar a /cursos
        driver.get(BASE_URL + "cursos");
        TimeUnit.SECONDS.sleep(5);
        String urlTrasCursos = driver.getCurrentUrl();
        String bodyTrasCursos = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean accesoCursos =
                urlTrasCursos.endsWith("/cursos") &&
                bodyTrasCursos.contains("curso");

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 2: bloqueo a /docente
        Assert.assertEquals(bloqueoDocente, true,
                "PASO 2: el sistema debio bloquear /docente para un estudiante");
        // Resultado esperado PASO 3: bloqueo a /admin/cursos
        Assert.assertEquals(bloqueoAdminCursos, true,
                "PASO 3: el sistema debio bloquear /admin/cursos para un estudiante");
        // Resultado esperado PASO 4: bloqueo a /admin/pagos
        Assert.assertEquals(bloqueoAdminPagos, true,
                "PASO 4: el sistema debio bloquear /admin/pagos para un estudiante");
        // Resultado esperado PASO 5: acceso permitido a /cursos
        Assert.assertEquals(accesoCursos, true,
                "PASO 5: el sistema debio permitir el acceso a /cursos");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
