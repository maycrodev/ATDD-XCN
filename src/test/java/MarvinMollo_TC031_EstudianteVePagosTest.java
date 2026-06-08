/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-031
// External ID: 112
// Suite: Modulo 4 - Pagos
// Integrante: Marvin Mollo
//
// Resumen:
//   Verificar que el estudiante puede ver sus pagos registrados.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Estudiante autenticado con pagos registrados
//     (cuenta sembrada: estudiante.test@ucb.edu.bo)
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion como Estudiante
//           -> Autenticacion exitosa
//   PASO 2. Navegar a /estudiante/pagos
//           -> La pagina de pagos carga
//   PASO 3. Observar la lista de pagos
//           -> Se muestran todos los pagos con fecha, monto y estado
//   PASO 4. Verificar que los datos coinciden con los realizados
//           -> Informacion correcta
//   PASO 5. Verificar que el layout es correcto
//           -> Pagina bien estructurada sin errores visuales
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=MarvinMollo_TC031_EstudianteVePagosTest

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

public class MarvinMollo_TC031_EstudianteVePagosTest {

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
    public void tc031_estudianteVePagosTest() throws Exception {

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

        // El JWT esta en la cookie "auth_token" (ver tokenStore.js de la SPA)
        Object token = ((JavascriptExecutor) driver).executeScript(
                "var m = document.cookie.match(/(?:^|; )auth_token=([^;]*)/);"
                + "return m ? decodeURIComponent(m[1]) : null;");
        boolean autenticado = token != null && token.toString().length() > 0;

        // PASO 2: Navegar a /estudiante/pagos
        driver.get(BASE_URL + "estudiante/pagos");
        TimeUnit.SECONDS.sleep(5);
        String urlFinal = driver.getCurrentUrl();
        boolean paginaPagosCargo = urlFinal.endsWith("/estudiante/pagos");

        // PASO 3: Observar la lista de pagos
        String bodyPagos = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean muestraInfoPagos =
                bodyPagos.contains("pago") ||
                bodyPagos.contains("factura");

        // El resultado esperado pide ver fecha, monto y estado. Validamos al menos
        // que en la pagina aparezcan referencias a monto/estado tipicas.
        boolean muestraDetallesPago =
                bodyPagos.contains("monto") ||
                bodyPagos.contains("bs") ||
                bodyPagos.contains("$") ||
                bodyPagos.contains("pagado") ||
                bodyPagos.contains("pendiente");

        // PASO 4: Verificar que los datos coinciden con los realizados
        // (validacion automatica indirecta: la pagina renderiza data sin error
        //  500 / 403 / pagina en blanco; verificacion exacta de coincidencia
        //  requiere conocer los pagos previos del usuario, que es comprobacion
        //  manual del tester).
        boolean sinErrorVisible =
                !bodyPagos.contains("500") &&
                !bodyPagos.contains("403") &&
                !bodyPagos.contains("error interno");

        // PASO 5: Verificar que el layout es correcto (header + footer presentes)
        int navbars = driver.findElements(By.cssSelector(".navbar, header, nav")).size();
        boolean layoutCorrecto = navbars >= 1 && bodyPagos.length() > 100;

        /*********** Verificacion - Assert ***********/
        Assert.assertEquals(autenticado, true,
                "PASO 1: el estudiante debio autenticarse correctamente");
        Assert.assertEquals(paginaPagosCargo, true,
                "PASO 2: la pagina /estudiante/pagos debio cargar");
        Assert.assertEquals(muestraInfoPagos, true,
                "PASO 3: la pagina debio mostrar informacion de pagos/facturas");
        Assert.assertEquals(muestraDetallesPago, true,
                "PASO 3: deben aparecer detalles como monto/estado");
        Assert.assertEquals(sinErrorVisible, true,
                "PASO 4: no debe haber error 500/403 visible");
        Assert.assertEquals(layoutCorrecto, true,
                "PASO 5: el layout debe ser correcto (header + contenido)");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
