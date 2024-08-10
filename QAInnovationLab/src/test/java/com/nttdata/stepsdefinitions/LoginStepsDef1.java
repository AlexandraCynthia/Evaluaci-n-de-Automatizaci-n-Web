package com.nttdata.stepsdefinitions;

import com.nttdata.steps.InventorySteps1;
import com.nttdata.steps.LoginSteps1;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static com.nttdata.core.DriverManager1.getDriver;
import static com.nttdata.core.DriverManager1.screenShot;
import static org.junit.Assert.assertEquals;


public class LoginStepsDef1 {
    private WebDriver driver;
    private InventorySteps1 inventorySteps(WebDriver driver){
        return new InventorySteps1(driver);
    }
    private WebDriverWait wait;


    @Given("estoy en la página de la tienda")
    public void estoyEnLaPáginaDeLaTienda() {
        driver = getDriver();
        driver.get("https://qalab.bensg.com/store");
        screenShot();
    }

    @And("me logueo con mi usuario {string} y clave {string}")
    public void meLogueoConMiUsuarioYClave(String user, String password) {
        LoginSteps1 loginSteps = new LoginSteps1(driver);
        loginSteps.typeUser(user);
        loginSteps.typePassword(password);
        loginSteps.login();
        screenShot();
    }

    @When("navego a la categoria {string} y subcategoria {string}")
    public void navegoALaCategoriaYSubcategoria(String arg0, String arg1) {
        driver = getDriver();
        driver.get("https://qalab.bensg.com/store/es/3-clothes");
        driver.get("https://qalab.bensg.com/store/es/4-men");
        screenShot();

    }

    @And("agrego {int} unidades del primer producto al carrito")
    public void agregoUnidadesDelPrimerProductoAlCarrito(int cantidad) {
        WebElement cantidadInput = driver.findElement(By.xpath("(//input[@type='number'])[1]"));
        cantidadInput.sendKeys(String.valueOf(cantidad));

        // Encuentra y hace clic en el botón de agregar al carrito del primer producto
        WebElement agregarAlCarritoButton = driver.findElement(By.xpath("(//button[contains(text(),'Añadir al carrito')])[1]"));
        agregarAlCarritoButton.click();
    }

    @Then("valido en el popup la confirmación del producto agregado")
    public void validoEnElPopupLaConfirmaciónDelProductoAgregado() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myModalLabel")));
        WebElement mensajeConfirmacion = popup.findElement(By.xpath("//div[contains(text(),'Producto añadido correctamente a su carrito de compra')]"));
    }

    @And("valido en el popup que el monto total sea calculado correctamente")
    public void validoEnElPopupQueElMontoTotalSeaCalculadoCorrectamente() {
        double totalCalculado = 0.0;

        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myModalLabel")));
        List<WebElement> precios = popup.findElements(By.className("price"));
        List<WebElement> cantidades = popup.findElements(By.className("js-cart-line-product-quantity"));

        // Calcular el total sumando el precio por cantidad de cada producto
        for (int i = 0; i < precios.size(); i++) {
            double precio = Double.parseDouble(precios.get(i).getText().replace("PEN", ""));
            int cantidad = Integer.parseInt(cantidades.get(i).getText());
            totalCalculado += precio * cantidad;
        }

    }

    @When("finalizo la compra")
    public void finalizoLaCompra() {
        WebElement finalizarCompraButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn")));
        finalizarCompraButton.click();

    }

    @Then("valido el titulo de la pagina del carrito")
    public void validoElTituloDeLaPaginaDelCarrito() {
        String tituloActual = driver.getTitle();
        String tituloEsperado = "CARRITO";
        assertEquals("El título de la página del carrito no es el esperado", tituloEsperado, tituloActual);
    }

    @And("vuelvo a validar el calculo de precios en el carrito")
    public void vuelvoAValidarElCalculoDePreciosEnElCarrito() {
        WebElement carritoPagina = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn")));
        // Obtener una lista de los elementos de precio de cada producto en el carrito
        List<WebElement> precios = carritoPagina.findElements(By.className("product-price"));

        // Obtener una lista de los elementos de cantidad de cada producto en el carrito
        List<WebElement> cantidades = carritoPagina.findElements(By.className("js-cart-line-product-quantity"));

        double totalCalculado = 0.0;

        // Calcular el total sumando el precio por cantidad de cada producto
        for (int i = 0; i < precios.size(); i++) {

            double precio = Double.parseDouble(precios.get(i).getText().replace("PEN", "").trim().replace(",", "."));
            int cantidad = Integer.parseInt(cantidades.get(i).getText());
            totalCalculado += precio * cantidad;
        }

        // Obtener el total mostrado en el carrito
        WebElement totalElemento = carritoPagina.findElement(By.className("value"));
        double totalMostrado = Double.parseDouble(totalElemento.getText().replace("PEN", "").trim().replace(",", "."));

        // Verificar que el total calculado coincide con el total mostrado
        assertEquals("El monto total calculado no coincide con el monto total mostrado en el carrito", totalCalculado, totalMostrado, 0.01);
    }


}


