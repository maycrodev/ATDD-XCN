/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-007
// External ID: 88
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Christian Coronel
//
// Resumen:
//   Verificar que el usuario puede cerrar sesion correctamente.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion
//   - Usuario autenticado en el sistema
//     (cuenta sembrada: estudiante.test@ucb.edu.bo)
//
// Pasos del TestLink:
//   PASO 1. Iniciar sesion con credenciales validas
//           -> Usuario autenticado en el sistema
//   PASO 2. Localizar el boton o menu de 'Cerrar sesion' en el header
//           -> Boton de cierre de sesion visible
//   PASO 3. Hacer clic en 'Cerrar sesion'
//           -> El sistema cierra la sesion del usuario
//   PASO 4. Observar la redireccion
//           -> El sistema redirige a la pagina de inicio publica
//   PASO 5. Intentar navegar a una ruta protegida (ej. /perfil)
//           -> El sistema redirige al login, confirmando que la sesion fue cerrada
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=ChristianCoronel_TC007_CerrarSesionTest

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChristianCoronel_TC007_CerrarSesionTest {

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
    public void tc007_cerrarSesionTest() throws Exception {

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
        // PASO 1: Iniciar sesion con credenciales validas
        driver.findElement(By.cssSelector("button.btn-login")).click();
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(EMAIL);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("button.submit-btn[type='submit']")).click();
        TimeUnit.SECONDS.sleep(5);

        // El JWT esta en la cookie "auth_token" (ver tokenStore.js de la SPA)
        Object tokenInicial = ((JavascriptExecutor) driver).executeScript(
                "var m = document.cookie.match(/(?:^|; )auth_token=([^;]*)/);"
                + "return m ? decodeURIComponent(m[1]) : null;");
        boolean autenticado = tokenInicial != null && tokenInicial.toString().length() > 0;

        // PASO 2: Localizar el boton o menu de 'Cerrar sesion' en el header.
        // En esta SPA el "Cerrar sesion" esta dentro del menu de usuario
        // (avatar circular con las iniciales).
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(2);
        List<WebElement> botonesNavbar = driver.findElements(
                By.cssSelector(".navbar-actions button"));
        for (WebElement btn : botonesNavbar) {
            String t = btn.getText().trim();
            // El avatar muestra 1-3 caracteres (iniciales)
            if (t.length() > 0 && t.length() <= 3) {
                btn.click();
                break;
            }
        }
        TimeUnit.SECONDS.sleep(2);

        boolean botonLogoutVisible = false;
        WebElement botonLogout = null;
        List<WebElement> botones = driver.findElements(By.tagName("button"));
        for (WebElement b : botones) {
            if (b.getText().toLowerCase().contains("cerrar sesi")) {
                botonLogoutVisible = true;
                botonLogout = b;
                break;
            }
        }

        // PASO 3: Hacer clic en 'Cerrar sesion'
        if (botonLogout != null) {
            botonLogout.click();
            TimeUnit.SECONDS.sleep(3);
        }

        // PASO 4: Observar la redireccion (debe estar en la pagina publica)
        String urlTrasLogout = driver.getCurrentUrl();
        Object tokenTrasLogout = ((JavascriptExecutor) driver).executeScript(
                "var m = document.cookie.match(/(?:^|; )auth_token=([^;]*)/);"
                + "return m ? decodeURIComponent(m[1]) : null;");
        boolean sesionCerrada = tokenTrasLogout == null
                || tokenTrasLogout.toString().isEmpty();
        boolean enPaginaPublica = urlTrasLogout.equals(BASE_URL)
                || urlTrasLogout.endsWith("/");

        // PASO 5: Intentar navegar a /perfil
        driver.get(BASE_URL + "perfil");
        TimeUnit.SECONDS.sleep(3);
        String bodyTrasPerfil = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean bloquearonPerfil = !bodyTrasPerfil.contains("editar perfil");

        /*********** Verificacion - Assert ***********/
        // Resultado esperado PASO 1: autenticacion exitosa
        Assert.assertEquals(autenticado, true,
                "PASO 1: el usuario debio autenticarse correctamente");
        // Resultado esperado PASO 2: boton de cierre de sesion visible
        Assert.assertEquals(botonLogoutVisible, true,
                "PASO 2: el boton 'Cerrar sesion' debio aparecer en el menu de usuario");
        // Resultado esperado PASO 3: el sistema cierra la sesion
        Assert.assertEquals(sesionCerrada, true,
                "PASO 3: el token JWT debio eliminarse de la cookie auth_token");
        // Resultado esperado PASO 4: redirige a la pagina publica
        Assert.assertEquals(enPaginaPublica, true,
                "PASO 4: el sistema debio redirigir a la pagina de inicio publica");
        // Resultado esperado PASO 5: bloquea /perfil
        Assert.assertEquals(bloquearonPerfil, true,
                "PASO 5: tras logout, /perfil debio bloquearse / redirigir");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
