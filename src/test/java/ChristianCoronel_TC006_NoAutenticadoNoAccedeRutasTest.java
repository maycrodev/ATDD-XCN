/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-006
// External ID: 87
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Christian Coronel
//
// Resumen:
//   Verificar que un usuario no autenticado no puede acceder a rutas
//   protegidas.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - No haber iniciado sesion
//
// Pasos del TestLink:
//   PASO 1. Abrir el navegador
//           -> Navegador abierto
//   PASO 2. Navegar directamente a la URL /perfil
//           -> El sistema redirige al login o a la pagina de inicio
//   PASO 3. Intentar navegar a /cursos
//           -> El sistema bloquea el acceso y redirige
//   PASO 4. Intentar navegar a /admin
//           -> El sistema bloquea el acceso y redirige
//   PASO 5. Intentar navegar a /estudiante/pagos
//           -> El sistema bloquea el acceso y redirige
//
// Historia de Usuario:
//   Como visitante no autenticado, quiero que el sistema me impida
//   acceder a rutas protegidas, para garantizar que solo los usuarios
//   con sesion activa puedan ver el contenido privado de la plataforma.
//
// Resultado Esperado:
//   El sistema redirige al usuario fuera de /perfil, /cursos, /admin y
//   /estudiante/pagos cuando no existe sesion activa, impidiendo cualquier
//   acceso a contenido privado sin autenticacion previa.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=ChristianCoronel_TC006_NoAutenticadoNoAccedeRutasTest

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

public class ChristianCoronel_TC006_NoAutenticadoNoAccedeRutasTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc006_noAutenticadoNoAccedeRutasTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        // PASO 1: Abrir el navegador (ya hecho en @BeforeTest, ademas garantizamos
        // que NO existe sesion previa borrando localStorage)
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        // Garantizamos que NO hay sesion previa: borramos cookie auth_token + storages
        ((JavascriptExecutor) driver).executeScript(
                "document.cookie = 'auth_token=; path=/; max-age=0; SameSite=Strict';"
                + "window.localStorage.clear();"
                + "window.sessionStorage.clear();");
        TimeUnit.SECONDS.sleep(1);

        /*********** Logica de la prueba ***********/
        // PASO 2: Navegar directamente a la URL /perfil
        driver.get(BASE_URL + "perfil");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasPerfil = driver.getCurrentUrl();
        String bodyTrasPerfil = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoPerfil =
                !urlTrasPerfil.endsWith("/perfil") ||
                !bodyTrasPerfil.contains("editar perfil");

        // PASO 3: Intentar navegar a /cursos
        driver.get(BASE_URL + "cursos");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasCursos = driver.getCurrentUrl();
        String bodyTrasCursos = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoCursos =
                !urlTrasCursos.endsWith("/cursos") ||
                !bodyTrasCursos.contains("inscribirme");

        // PASO 4: Intentar navegar a /admin
        driver.get(BASE_URL + "admin");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasAdmin = driver.getCurrentUrl();
        String bodyTrasAdmin = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoAdmin =
                !urlTrasAdmin.endsWith("/admin") ||
                !bodyTrasAdmin.contains("administrar");

        // PASO 5: Intentar navegar a /estudiante/pagos
        driver.get(BASE_URL + "estudiante/pagos");
        TimeUnit.SECONDS.sleep(3);
        String urlTrasPagos = driver.getCurrentUrl();
        String bodyTrasPagos = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloqueoPagos =
                !urlTrasPagos.endsWith("/estudiante/pagos") ||
                !bodyTrasPagos.contains("mis pagos");

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 2: redirige fuera de /perfil
        Assert.assertEquals(bloqueoPerfil, true,
                "PASO 2: sin sesion, /perfil debio redirigir al login o landing");
        // Resultado esperado PASO 3: bloquea /cursos
        Assert.assertEquals(bloqueoCursos, true,
                "PASO 3: sin sesion, /cursos debio bloquearse");
        // Resultado esperado PASO 4: bloquea /admin
        Assert.assertEquals(bloqueoAdmin, true,
                "PASO 4: sin sesion, /admin debio bloquearse");
        // Resultado esperado PASO 5: bloquea /estudiante/pagos
        Assert.assertEquals(bloqueoPagos, true,
                "PASO 5: sin sesion, /estudiante/pagos debio bloquearse");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
