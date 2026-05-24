/****************************************************************/
// Historia de Usuario: Como estudiante autenticado, quiero ver el
// catalogo completo de cursos extraacademicos para elegir en cual
// inscribirme y pagar.
//
// Prueba de Aceptacion / Caso de Prueba TC-CURSO-01:
// Validar que tras iniciar sesion como estudiante y navegar a /cursos,
// la pantalla renderiza la grilla de cursos disponibles (al menos un
// item / card de curso).
//
// PASO 1. Loguearse como estudiante.
// PASO 2. Navegar a /cursos.
// PASO 3. Esperar a que la grilla de cursos cargue.
// Resultado Esperado: la pagina /cursos contiene al menos un curso
// listado (la palabra "curso" aparece y hay >=1 elemento card).
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=MarvinMollo_CatalogoMuestraCursosTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class MarvinMollo_CatalogoMuestraCursosTest {

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
    public void catalogoCursosMuestraAlMenosUnCursoTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        // Login estudiante
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        /*********** Logica de la prueba ***********/
        driver.get(BASE_URL + "cursos");
        TimeUnit.SECONDS.sleep(5);

        /*********** Verificacion - Assert ***********/
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean palabraCursoPresente = bodyText.contains("curso");
        Assert.assertEquals(true, palabraCursoPresente);

        // Al menos un boton "Ver mas", "Inscribirme" o "Detalles" implica curso renderizado
        boolean hayCallToAction =
                bodyText.contains("ver mas") ||
                bodyText.contains("ver más") ||
                bodyText.contains("inscribirme") ||
                bodyText.contains("detalle");
        Assert.assertEquals(true, hayCallToAction);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
