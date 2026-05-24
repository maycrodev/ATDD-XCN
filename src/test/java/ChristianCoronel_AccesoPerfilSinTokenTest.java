/****************************************************************/
// Historia de Usuario: Como sistema, quiero que ningun usuario sin
// sesion (sin JWT en localStorage) pueda acceder al perfil privado del
// estudiante "/perfil" para evitar fuga de datos sensibles.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-04:
// Validar que al navegar a "/perfil" sin haber iniciado sesion (sin
// token en localStorage), la SPA redirige al landing publico y NO
// renderiza informacion del perfil.
//
// PASO 1. Abrir el navegador en una sesion limpia (sin localStorage).
// PASO 2. Forzar navegacion a /perfil.
// PASO 3. Inspeccionar URL y contenido renderizado.
// Resultado Esperado: la URL termina en "/" (no en "/perfil") y el
// contenido NO incluye datos privados como "Mi historial academico".
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=ChristianCoronel_AccesoPerfilSinTokenTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class ChristianCoronel_AccesoPerfilSinTokenTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void perfilSinTokenRedirigeTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        // Garantizar que NO hay sesion previa
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
        TimeUnit.SECONDS.sleep(1);

        /*********** Logica de la prueba ***********/
        driver.get(BASE_URL + "perfil");
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        String urlFinal = driver.getCurrentUrl();
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();

        // No debe estar parado en /perfil
        Assert.assertEquals(true, !urlFinal.endsWith("/perfil"));
        // No debe renderizar contenido privado
        boolean datosPrivadosVisibles =
                bodyText.contains("mi historial academico") ||
                bodyText.contains("editar perfil") ||
                bodyText.contains("mis pagos");
        Assert.assertEquals(false, datosPrivadosVisibles);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
