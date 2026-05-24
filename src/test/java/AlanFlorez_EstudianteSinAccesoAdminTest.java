/****************************************************************/
// Historia de Usuario: Como sistema, quiero que un usuario con rol
// ESTUDIANTE NO pueda acceder a las pantallas de administracion
// (/admin/*) para preservar el principio de menor privilegio.
//
// Prueba de Aceptacion / Caso de Prueba TC-ROLE-01:
// Validar que tras iniciar sesion como estudiante, intentar navegar a
// la URL "/admin" redirige fuera del panel admin (la SPA muestra el
// landing publico, no el AdminMenu).
//
// PASO 1. Abrir la SPA y hacer login como estudiante.
// PASO 2. Esperar a que cargue la vista privada del estudiante.
// PASO 3. Forzar navegacion directa a la URL /admin.
// PASO 4. Inspeccionar la URL final y el contenido renderizado.
// Resultado Esperado: la SPA NO renderiza el panel admin (no aparece
// titulo "Panel de Administracion") y la URL fue redirigida.
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=AlanFlorez_EstudianteSinAccesoAdminTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AlanFlorez_EstudianteSinAccesoAdminTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";
    private static final String EMAIL_ESTUDIANTE = "estudiante.test@ucb.edu.bo";
    private static final String PASSWORD_ESTUDIANTE = "Estudiante#2026!ok";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void estudianteNoPuedeAccederPanelAdminTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        // Login como estudiante
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL_ESTUDIANTE);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD_ESTUDIANTE);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(4);

        /*********** Logica de la prueba ***********/
        // Forzar navegacion a /admin
        driver.get(BASE_URL + "admin");
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        String urlFinal = driver.getCurrentUrl();
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();

        // El estudiante NO debe ver el panel de administracion
        boolean panelAdminVisible =
                bodyText.contains("administrar cursos") ||
                bodyText.contains("panel de administracion") ||
                bodyText.contains("gestion de usuarios");
        Assert.assertEquals(false, panelAdminVisible);

        // La URL no debe seguir mostrando /admin (debe haber redirigido al landing o login)
        Assert.assertEquals(true, !urlFinal.endsWith("/admin") || bodyText.contains("iniciar sesion"));
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
