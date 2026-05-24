/****************************************************************/
// Historia de Usuario: Como estudiante quiero acceder al historial de
// mis pagos / facturas para poder descargar comprobantes y verificar
// que las inscripciones fueron procesadas.
//
// Prueba de Aceptacion / Caso de Prueba TC-PAGO-02:
// Validar que tras iniciar sesion, la URL /estudiante/pagos renderiza
// la vista de pagos del estudiante (no redirige, y muestra alguna
// indicacion de "Pagos", "Facturas" o lista de transacciones).
//
// PASO 1. Loguearse como estudiante.
// PASO 2. Navegar a /estudiante/pagos.
// PASO 3. Inspeccionar el contenido renderizado.
// Resultado Esperado: la URL termina en "/estudiante/pagos" y el body
// contiene la palabra "pago" o "factura".
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=MarvinMollo_EstudianteVePagosTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class MarvinMollo_EstudianteVePagosTest {

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
    public void estudiantePagosCargaVistaTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        /*********** Logica de la prueba ***********/
        driver.get(BASE_URL + "estudiante/pagos");
        TimeUnit.SECONDS.sleep(5);

        /*********** Verificacion - Assert ***********/
        String urlFinal = driver.getCurrentUrl();
        Assert.assertEquals(true, urlFinal.endsWith("/estudiante/pagos"));

        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean vistaPagosVisible =
                bodyText.contains("pago") || bodyText.contains("factura");
        Assert.assertEquals(true, vistaPagosVisible);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
