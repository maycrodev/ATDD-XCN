/****************************************************************/
// Historia de Usuario: Como sistema, quiero rechazar el login con
// correos NO institucionales (@gmail.com, @hotmail.com, etc.) para
// asegurar que solo personal y estudiantes UCB acceden a la plataforma.
//
// Prueba de Aceptacion / Caso de Prueba TC-AUTH-01:
// Validar que el formulario de login muestra un mensaje de error
// cuando el correo ingresado NO termina en "@ucb.edu.bo".
//
// PASO 1. Abrir la SPA en http://localhost:5173/
// PASO 2. Abrir el modal de login (boton "Iniciar Sesion").
// PASO 3. Ingresar correo "intruso@gmail.com" y password cualquiera.
// PASO 4. Hacer click en "Iniciar Sesion".
// Resultado Esperado: aparece un banner de error con el texto
//   relacionado a "correo institucional" o "@ucb.edu.bo".
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=JuanJose_LoginCorreoNoInstitucionalTest

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

public class JuanJose_LoginCorreoNoInstitucionalTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:5173/";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void loginCorreoNoInstitucionalMuestraErrorTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);

        /*********** Logica de la prueba ***********/
        // Abrir modal de login
        WebElement botonAbrirLogin = driver.findElement(By.cssSelector("button.btn-login"));
        botonAbrirLogin.click();
        TimeUnit.SECONDS.sleep(2);

        // Escribir correo NO institucional y password
        WebElement inputEmail = driver.findElement(By.cssSelector("input[name='email']"));
        WebElement inputPassword = driver.findElement(By.cssSelector("input[name='password']"));
        inputEmail.sendKeys("intruso@gmail.com");
        inputPassword.sendKeys("CualquierPass#2026");

        // Enviar formulario
        WebElement botonSubmit = driver.findElement(By.cssSelector("button.submit-btn[type='submit']"));
        botonSubmit.click();
        TimeUnit.SECONDS.sleep(3);

        /*********** Verificacion - Assert ***********/
        WebElement contenedorModal = driver.findElement(By.cssSelector(".login-container"));
        String textoModal = contenedorModal.getText().toLowerCase();
        boolean mensajeDetectado =
                textoModal.contains("institucional") ||
                textoModal.contains("ucb.edu.bo") ||
                textoModal.contains("formato");
        Assert.assertEquals(true, mensajeDetectado);
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
