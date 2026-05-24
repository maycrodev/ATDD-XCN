/****************************************************************/
// Historia de Usuario: Como administrador quiero acceder al panel
// "/admin" para gestionar cursos, usuarios, pagos y reportes.
//
// Prueba de Aceptacion / Caso de Prueba TC-ROLE-03:
// Validar que tras iniciar sesion con una cuenta ADMINISTRADOR, la
// navegacion a "/admin" SI muestra el menu administrativo con las
// secciones esperadas.
//
// PASO 1. Abrir la SPA y loguearse como administrador.
// PASO 2. Navegar a "/admin".
// PASO 3. Inspeccionar el menu administrativo.
// Resultado Esperado: el menu admin se renderiza y contiene al menos
// una opcion conocida ("Cursos", "Usuarios", "Pagos" o "Reportes").
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=AlanFlorez_AdminAccedeMenuTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AlanFlorez_AdminAccedeMenuTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL_ADMIN = "admin.test@ucb.edu.bo";
    private static final String PASSWORD_ADMIN = "Admin#2026!ok";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void adminAccedeMenuAdministrativoTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL_ADMIN);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD_ADMIN);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(4);

        /*********** Logica de la prueba ***********/
        driver.get(BASE_URL + "admin");
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean menuAdminVisible =
                bodyText.contains("cursos") ||
                bodyText.contains("usuarios") ||
                bodyText.contains("pagos") ||
                bodyText.contains("reportes");
        Assert.assertEquals(true, menuAdminVisible);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
