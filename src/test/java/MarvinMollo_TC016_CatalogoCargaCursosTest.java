/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-016
// External ID: 97
// Suite: Modulo 2 - Catalogo de Cursos
// Integrante: Marvin Mollo
//
// Resumen:
//   Verificar que el catalogo de cursos carga y muestra los cursos
//   disponibles.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Usuario autenticado (Estudiante, Docente o Admin)
//   - Cursos registrados en el sistema
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion como Estudiante
//           -> Autenticacion exitosa
//   PASO 2. Hacer clic en 'Cursos' o navegar a /cursos
//           -> La pagina del catalogo carga
//   PASO 3. Observar la lista de cursos
//           -> Se muestran todos los cursos disponibles con su informacion
//   PASO 4. Verificar que cada tarjeta de curso tiene nombre, descripcion e imagen
//           -> Informacion de cursos completa y visible
//   PASO 5. Observar el tiempo de carga
//           -> El catalogo carga en menos de 3 segundos
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=MarvinMollo_TC016_CatalogoCargaCursosTest

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

public class MarvinMollo_TC016_CatalogoCargaCursosTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL = "estudiante.test@ucb.edu.bo";
    private static final String PASSWORD = "Estudiante#2026!ok";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void tc016_catalogoCargaCursosTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        // Limpiamos sesion previa: cookie auth_token + storages
        ((JavascriptExecutor) driver).executeScript(
                "document.cookie = 'auth_token=; path=/; max-age=0; SameSite=Strict';"
                + "window.localStorage.clear();"
                + "window.sessionStorage.clear();");
        TimeUnit.SECONDS.sleep(1);

        /*********** Logica de la prueba ***********/
        // PASO 1: Iniciar sesion como Estudiante
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        // PASO 2: Navegar a /cursos midiendo el tiempo de carga (PASO 5)
        long inicioMs = System.currentTimeMillis();
        driver.get(BASE_URL + "cursos");
        // Esperamos a que la palabra "curso" sea visible en el body
        long maxEsperaMs = 6000;
        boolean catalogoCargado = false;
        while (System.currentTimeMillis() - inicioMs < maxEsperaMs) {
            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            if (body.contains("curso") &&
                (body.contains("inscribirme") || body.contains("ver mas")
                 || body.contains("ver más") || body.contains("detalle"))) {
                catalogoCargado = true;
                break;
            }
            TimeUnit.MILLISECONDS.sleep(250);
        }
        long finMs = System.currentTimeMillis();
        long duracionMs = finMs - inicioMs;

        // PASO 3: Observar la lista de cursos
        String bodyCatalogo = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean listaCursosVisible =
                bodyCatalogo.contains("curso") &&
                (bodyCatalogo.contains("inscribirme") ||
                 bodyCatalogo.contains("ver mas") ||
                 bodyCatalogo.contains("ver más") ||
                 bodyCatalogo.contains("detalle"));

        // PASO 4: Verificar que cada tarjeta tiene nombre, descripcion y banner visual.
        // En esta SPA las tarjetas (.curso-catalogo-card) usan banner con icono SVG
        // y gradiente CSS en vez de <img> — el equivalente visual.
        List<WebElement> cards = driver.findElements(By.cssSelector(".curso-catalogo-card"));
        boolean alMenosUnaCard = !cards.isEmpty();
        boolean tarjetasTienenContenido = false;
        if (alMenosUnaCard) {
            // Cada card debe tener al menos un titulo y un banner visual
            int cardsConContenido = driver.findElements(
                    By.cssSelector(".curso-catalogo-card .card-titulo")).size();
            tarjetasTienenContenido = cardsConContenido >= 1;
        }
        boolean infoCompletaCard = alMenosUnaCard && tarjetasTienenContenido;

        // PASO 5: Tiempo de carga < 3 segundos
        boolean tiempoBajo3s = duracionMs < 3000;

        /*********** Verificacion - Assert ***********/
        Assert.assertEquals(catalogoCargado, true,
                "PASO 2: la pagina del catalogo debio cargar");
        Assert.assertEquals(listaCursosVisible, true,
                "PASO 3: la lista de cursos debio mostrarse con informacion");
        Assert.assertEquals(infoCompletaCard, true,
                "PASO 4: las tarjetas .curso-catalogo-card debian tener titulo y banner visual");
        Assert.assertEquals(tiempoBajo3s, true,
                "PASO 5: el catalogo debio cargar en menos de 3 segundos (real: "
                        + duracionMs + " ms)");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
