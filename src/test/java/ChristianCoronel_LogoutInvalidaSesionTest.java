/****************************************************************/
// Historia de Usuario: Como usuario autenticado, quiero que al hacer
// logout mi token sea borrado y no pueda volver a acceder a /perfil
// hasta que vuelva a loguearme.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-06:
// Validar que tras iniciar sesion y cerrar sesion desde el menu de
// usuario, el token desaparece de localStorage y la navegacion a
// /perfil ya NO renderiza datos privados.
//
// PASO 1. Hacer login como estudiante.
// PASO 2. Abrir el menu de usuario (avatar con iniciales).
// PASO 3. Click en "Cerrar sesion".
// PASO 4. Intentar navegar a /perfil.
// Resultado Esperado: localStorage.token = null, y el contenido de
// /perfil ya no incluye datos privados.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=ChristianCoronel_LogoutInvalidaSesionTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChristianCoronel_LogoutInvalidaSesionTest {

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
    public void logoutBorraTokenYBloqueaPerfilTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        TimeUnit.SECONDS.sleep(1);

        // Login previo
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        // Asegurar landing
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);

        /*********** Logica de la prueba ***********/
        // Abrir menu de usuario (boton circular con iniciales en navbar)
        List<WebElement> avatares = driver.findElements(
                By.cssSelector(".navbar-actions button"));
        for (WebElement btn : avatares) {
            String txt = btn.getText().trim();
            if (txt.length() > 0 && txt.length() <= 3) {
                btn.click();
                break;
            }
        }
        TimeUnit.SECONDS.sleep(2);

        // Click en "Cerrar sesion" del menu desplegable
        List<WebElement> botones = driver.findElements(By.tagName("button"));
        for (WebElement b : botones) {
            if (b.getText().toLowerCase().contains("cerrar sesi")) {
                b.click();
                break;
            }
        }
        TimeUnit.SECONDS.sleep(3);

        // Intentar acceder a perfil
        driver.get(BASE_URL + "perfil");
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        Object token = ((JavascriptExecutor) driver).executeScript(
                "return window.localStorage.getItem('token');");
        boolean tokenAusente = token == null || token.toString().isEmpty();
        Assert.assertEquals(true, tokenAusente);

        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean datosPrivadosVisibles =
                bodyText.contains("mi historial academico") ||
                bodyText.contains("editar perfil");
        Assert.assertEquals(false, datosPrivadosVisibles);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
