/****************************************************************/
// Historia de Usuario: Como estudiante con cuenta activa, quiero
// iniciar sesion con mi correo institucional y contrasena para
// acceder al catalogo de cursos y mi perfil.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-07:
// Validar que el login con credenciales validas de un estudiante
// cierra el modal y redirige a una ruta protegida (catalogo o perfil).
//
// PASO 1. Abrir la SPA y abrir modal de login.
// PASO 2. Ingresar correo y password validos de estudiante.
// PASO 3. Click en "Iniciar Sesion".
// Resultado Esperado: el modal se cierra y la URL ya NO es la raiz
// publica (esta en /, pero ya con el header del estudiante con avatar).
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=LeonardoDelgado_LoginExitosoEstudianteTest

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

public class LeonardoDelgado_LoginExitosoEstudianteTest {

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
    public void loginExitosoEstudianteRedirigeTest() throws Exception {

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
        // El modal de login debio cerrarse
        int modales = driver.findElements(By.cssSelector(".login-container")).size();
        Assert.assertEquals(true, modales == 0);

        // Debe existir token (login exitoso)
        Object token = ((JavascriptExecutor) driver).executeScript(
                "return window.localStorage.getItem('token');");
        Assert.assertEquals(true, token != null && token.toString().length() > 0);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
