/****************************************************************/
// Historia de Usuario: Como sistema quiero que un estudiante NO acceda
// al panel docente (/docente/*) ya que los docentes tienen funciones
// privilegiadas (calificar tareas, ver listas, etc.) que no
// corresponden a otros roles.
//
// Prueba de Aceptacion / Caso de Prueba TC-ROLE-02:
// Validar que tras iniciar sesion como estudiante, intentar navegar a
// "/docente" NO renderiza el menu del docente.
//
// PASO 1. Abrir la SPA y loguearse como estudiante.
// PASO 2. Forzar navegacion a "/docente".
// PASO 3. Inspeccionar lo que muestra la SPA.
// Resultado Esperado: la pantalla del docente NO se muestra (no aparece
// "Panel Docente" / "Mis Cursos Asignados" / "Calificar Tareas").
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=AlanFlorez_EstudianteSinAccesoDocenteTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AlanFlorez_EstudianteSinAccesoDocenteTest {

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
    public void estudianteNoPuedeAccederPanelDocenteTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL_ESTUDIANTE);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD_ESTUDIANTE);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(4);

        /*********** Logica de la prueba ***********/
        driver.get(BASE_URL + "docente");
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean panelDocenteVisible =
                bodyText.contains("calificar tareas") ||
                bodyText.contains("mis cursos asignados") ||
                bodyText.contains("lista de estudiantes");
        Assert.assertEquals(false, panelDocenteVisible);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
