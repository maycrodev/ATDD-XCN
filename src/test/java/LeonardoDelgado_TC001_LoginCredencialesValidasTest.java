/****************************************************************/
// Caso de Prueba del Entregable #2 (TestLink): TC-001
// External ID: 82
// Suite: Modulo 1 - Autenticacion y Acceso
// Integrante: Leonardo Delgado
// *** CASO ANCLA OBLIGATORIO — Flujo F1 (Autenticacion) ***
//
// Resumen:
//   Verificar que el usuario puede iniciar sesion con credenciales validas.
//
// Pre-condiciones:
//   - Navegador web (Chrome 120+)
//   - Sistema en ejecucion (frontend y backend)
//   - Tener una cuenta registrada con credenciales conocidas
//     (cuenta sembrada: estudiante.test@ucb.edu.bo / Estudiante#2026!ok)
//
// Pasos del TestLink:
//   PASO 1. Abrir el navegador web
//           -> Navegador abierto correctamente
//   PASO 2. Ingresar la URL del sistema en la barra de direcciones
//           -> La pagina de inicio del sistema carga correctamente
//   PASO 3. Hacer clic en el boton 'Iniciar Sesion' o 'Login'
//           -> Se muestra el formulario de inicio de sesion
//   PASO 4. Ingresar el correo electronico y contrasena validos
//           -> Los campos aceptan el texto ingresado
//   PASO 5. Hacer clic en el boton 'Ingresar'
//           -> El sistema autentica al usuario y redirige al home
//              correspondiente segun rol
//
// Historia de Usuario:
//   Como estudiante registrado en el sistema, quiero poder iniciar sesion
//   con mi correo y contrasena correctos, para acceder a las
//   funcionalidades de mi cuenta de forma segura.
//
// Resultado Esperado:
//   El sistema autentica al usuario exitosamente, genera un token JWT,
//   cierra el formulario de login y redirige al home correspondiente
//   segun el rol del usuario (estudiante, docente o administrador).
/****************************************************************/
// Para ejecutar:
// mvn clean compile test -Dtest=LeonardoDelgado_TC001_LoginCredencialesValidasTest

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

import java.util.concurrent.TimeUnit;

public class LeonardoDelgado_TC001_LoginCredencialesValidasTest {

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
    public void tc001_loginCredencialesValidasTest() throws Exception {

        /*********** Preparacion de la prueba ***********/
        // PASO 1: Abrir el navegador web (hecho en @BeforeTest)
        // PASO 2: Ingresar la URL del sistema
        driver.get(BASE_URL);
        TimeUnit.SECONDS.sleep(3);
        // La SPA guarda el JWT en una cookie llamada "auth_token" (NO en localStorage).
        // Limpiamos cookie + localStorage + sessionStorage para empezar sin sesion.
        ((JavascriptExecutor) driver).executeScript(
                "document.cookie = 'auth_token=; path=/; max-age=0; SameSite=Strict';"
                + "window.localStorage.clear();"
                + "window.sessionStorage.clear();");
        TimeUnit.SECONDS.sleep(1);
        // Resultado esperado PASO 2: la pagina de inicio carga
        String urlInicio = driver.getCurrentUrl();
        boolean paginaInicioCargo = urlInicio.startsWith(BASE_URL);

        /*********** Logica de la prueba ***********/
        // PASO 3: Hacer clic en el boton 'Iniciar Sesion'
        WebElement botonAbrirLogin = driver.findElement(By.cssSelector("button.btn-login"));
        botonAbrirLogin.click();
        TimeUnit.SECONDS.sleep(2);
        // Resultado esperado PASO 3: se muestra el formulario de inicio de sesion
        int modalesAbiertos = driver.findElements(By.cssSelector(".login-container")).size();
        boolean formularioVisible = modalesAbiertos == 1;

        // PASO 4: Ingresar el correo electronico y contrasena validos
        WebElement inputEmail = driver.findElement(By.cssSelector("input[name='email']"));
        WebElement inputPassword = driver.findElement(By.cssSelector("input[name='password']"));
        inputEmail.sendKeys(EMAIL);
        inputPassword.sendKeys(PASSWORD);
        TimeUnit.SECONDS.sleep(1);
        // Resultado esperado PASO 4: los campos aceptan el texto
        boolean camposAceptanTexto =
                inputEmail.getAttribute("value").equals(EMAIL) &&
                inputPassword.getAttribute("value").equals(PASSWORD);

        // PASO 5: Hacer clic en el boton 'Ingresar'
        WebElement botonIngresar = driver.findElement(By.cssSelector("button.submit-btn[type='submit']"));
        botonIngresar.click();
        TimeUnit.SECONDS.sleep(5);

        // Resultado esperado PASO 5: autentica y redirige al home segun rol.
        // El JWT se guarda en la cookie "auth_token" (ver tokenStore.js de la SPA).
        Object token = ((JavascriptExecutor) driver).executeScript(
                "var m = document.cookie.match(/(?:^|; )auth_token=([^;]*)/);"
                + "return m ? decodeURIComponent(m[1]) : null;");
        boolean autenticado = token != null && token.toString().length() > 0;
        int modalesTrasLogin = driver.findElements(By.cssSelector(".login-container")).size();
        boolean modalCerrado = modalesTrasLogin == 0;

        /*********** Verificacion - Assert ***********/
        Assert.assertEquals(paginaInicioCargo, true,
                "PASO 2: la pagina de inicio debio cargar");
        Assert.assertEquals(formularioVisible, true,
                "PASO 3: el formulario de login debio aparecer");
        Assert.assertEquals(camposAceptanTexto, true,
                "PASO 4: los campos debieron aceptar el texto");
        Assert.assertEquals(autenticado, true,
                "PASO 5: el sistema debio autenticar al usuario (token JWT presente)");
        Assert.assertEquals(modalCerrado, true,
                "PASO 5: el modal debio cerrarse tras la autenticacion exitosa");
    }

    @AfterTest
    public void closeDriver() {
        if (driver != null) driver.quit();
    }
}
