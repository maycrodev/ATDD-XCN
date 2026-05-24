/****************************************************************/
// Historia de Usuario: Como estudiante quiero poder iniciar el pago
// de un curso desde su pantalla de detalle, viendo el monto en USD y
// el boton de PayPal disponible para confirmar la inscripcion.
//
// Prueba de Aceptacion / Caso de Prueba TC-PAGO-01:
// Validar que tras seleccionar un curso desde /cursos y abrir la
// pantalla de detalle, la SPA carga el componente de pago PayPal o
// un boton "Pagar" visible para el estudiante.
//
// PASO 1. Loguearse como estudiante.
// PASO 2. Ir al catalogo /cursos.
// PASO 3. Hacer click sobre el primer curso disponible.
// PASO 4. Verificar que en la pantalla de detalle aparece el boton
//   de "Pagar" / "PayPal".
// Resultado Esperado: el detalle del curso muestra alguna referencia
// a PayPal o un boton de pago.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=MarvinMollo_IniciaFlujoPagoPaypalTest

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

public class MarvinMollo_IniciaFlujoPagoPaypalTest {

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
    public void detalleCursoMuestraBotonPagoPaypalTest() throws Exception {

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
        driver.get(BASE_URL + "cursos");
        TimeUnit.SECONDS.sleep(5);

        // Hacer click sobre el primer "Ver mas" / "Detalle" disponible
        List<WebElement> botones = driver.findElements(By.tagName("button"));
        for (WebElement b : botones) {
            String t = b.getText().toLowerCase();
            if (t.contains("ver mas") || t.contains("ver más")
                    || t.contains("detalle") || t.contains("inscribirme")) {
                b.click();
                break;
            }
        }
        TimeUnit.SECONDS.sleep(5);

        /*********** Verificacion - Assert ***********/
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean pagoVisible =
                bodyText.contains("paypal") ||
                bodyText.contains("pagar") ||
                bodyText.contains("usd") ||
                bodyText.contains("$");
        Assert.assertEquals(true, pagoVisible);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
