/****************************************************************/
// Historia de Usuario: Como estudiante autenticado, quiero que mi
// sesion sea recordada (token JWT persistido) para no tener que
// re-loguearme en cada navegacion.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-05:
// Validar que un login exitoso persiste un token JWT en localStorage
// del navegador y que el header pasa a mostrar el avatar del usuario.
//
// PASO 1. Abrir la SPA y limpiar localStorage.
// PASO 2. Abrir modal de login.
// PASO 3. Ingresar credenciales validas y enviar.
// PASO 4. Leer localStorage.token via JavaScript.
// Resultado Esperado: existe un token (string no vacio) en localStorage
// y el boton "Iniciar Sesion" ya NO se muestra en el header.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=ChristianCoronel_LoginExitosoGuardaTokenTest

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

public class ChristianCoronel_LoginExitosoGuardaTokenTest {

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
    public void loginExitosoGuardaTokenEnLocalStorageTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        TimeUnit.SECONDS.sleep(1);

        /*********** Logica de la prueba ***********/
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        /*********** Verificacion - Assert ***********/
        // Buscar token en localStorage (clave esperada: "token")
        Object token = ((JavascriptExecutor) driver).executeScript(
                "return window.localStorage.getItem('token');");
        boolean tokenPresente = token != null && token.toString().length() > 0;
        Assert.assertEquals(true, tokenPresente);

        // El boton "Iniciar Sesion" del header debe haber desaparecido
        int botonesLogin = driver.findElements(By.cssSelector("button.btn-login")).size();
        Assert.assertEquals(true, botonesLogin == 0);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
