import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class IntershopMyAccountPageTests {
    private WebDriver driver;
    private WebDriverWait wait;
    String url = "https://intershop5.skillbox.ru";

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    private void authenticate() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("username")).sendKeys("user29497694");
        driver.findElement(By.id("password")).sendKeys("29497694@test.ru");
        driver.findElement(By.name("login")).click();
    }

    private void authOrdersEmpty() {
        driver.navigate().to(url);
        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("username")).sendKeys("29501999@test.ru");
        driver.findElement(By.id("password")).sendKeys("29501999@test.ru");
        driver.findElement(By.name("login")).click();
    }

    @Test
    public void testKeyElementsDisplayedOnMyAccountPage() {
        authenticate();
        Assert.assertTrue("Не отображается логотип", driver.findElement(By.className("site-logo")).isDisplayed());
        Assert.assertTrue("Не отображается строка поиска", driver.findElement(By.name("s")).isDisplayed());
        Assert.assertTrue("Не отображается контактная информация в хедере", driver.findElement(By.className("header-callto")).isDisplayed());
        Assert.assertTrue("Не отображается кнопка \"Выйти\"", driver.findElement(By.className("logout")).isDisplayed());
        Assert.assertFalse("Не отображается Главное меню сайта", driver.findElements(By.xpath("//*[@class='menu']/li")).isEmpty());
        Assert.assertTrue("Не отображается контактная информация в футуре", driver.findElement(By.className("ak-container")).isDisplayed());
        Assert.assertFalse("Не отображается ссылки на разделы сайта в футуре", driver.findElements(By.cssSelector(".widget_pages ul li a")).isEmpty());
    }

    @Test
    public void testKeyElementsOnMyAccountPageAreClickable() {
        authenticate();
        Assert.assertTrue("Логотип не кликабелен", driver.findElement(By.className("site-logo")).isEnabled());
        Assert.assertTrue("Строка поиска не активна", driver.findElement(By.name("s")).isEnabled());
        Assert.assertTrue("Кнопка поиска не кликабельна", driver.findElement(By.className("searchsubmit")).isEnabled());
        Assert.assertTrue("Кнопка \"Выйти\" не кликабельна", driver.findElement(By.className("logout")).isEnabled());
        var itemsMenu = driver.findElements(By.xpath("//*[@class='menu']/li"));
        for (int i = 1; i <= itemsMenu.size(); i++) {
            var itemMenu = driver.findElement(By.xpath("(//*[@class='menu']/li)[" + i + "]"));
            String item = itemMenu.getText();
            Assert.assertTrue(String.format("Элемент \"%s\" Главного меню не кликабелен", item), itemMenu.isEnabled());
        }
        var links = driver.findElements(By.cssSelector(".widget_pages ul li a"));
        for (int i = 1; i <= links.size(); i++) {
            var link = driver.findElement(By.cssSelector(".widget_pages ul li:nth-of-type(" + i + ") a"));
            String linkName = link.getText();
            Assert.assertTrue(String.format("Ссылка в футуре на раздел \"%s\" сайта не кликабельна", linkName), link.isEnabled());
        }
    }

    @Test
    public void testDisplayEmptyOrderList() {
        authOrdersEmpty();
        driver.findElement(By.xpath("//a[contains(., 'Заказы')]")).click();
        Assert.assertTrue("Не отображается информация, что список пуст", driver.findElement(By.cssSelector("[class*='MyAccount-content']")).isDisplayed());
        Assert.assertTrue("Не отображается кнопка \"Смотреть товары\"", driver.findElement(By.className("button")).isDisplayed());
    }

    @Test
    public void testGoCatalogFromEmptyOrderList() {
        authOrdersEmpty();
        driver.findElement(By.xpath("//a[contains(., 'Заказы')]")).click();
        driver.findElement(By.className("button")).click();
        Assert.assertEquals("Нет перехода в раздел  \"ВСЕ ТОВАРЫ\" при клике на кнопку \"Смотреть товары\"",
                "Товары — Skillbox", driver.getTitle());
    }

    @Test
    public void testDisplayOrdersList() {
        authenticate();
        driver.findElement(By.xpath("//a[contains(., 'Заказы')]")).click();
        Assert.assertTrue("Не отображается список заказов", driver.findElement(By.cssSelector("[class*='MyAccount-content']")).isDisplayed());
        Assert.assertFalse("Заказы не отображаются", driver.findElements(By.xpath("//a[contains(., '№')]")).isEmpty());
    }

    @Test
    public void testDisplayOrderDetails() {
        authenticate();
        driver.findElement(By.xpath("//a[contains(., 'Заказы')]")).click();
        driver.findElement(By.cssSelector("a.button")).click();
        String text = "Детали заказа";
        Assert.assertEquals("Не отображается перечень заказов", text, driver.findElement(By.xpath("//h2[contains(., 'Детали заказа')]")).getText());
        Assert.assertTrue("Не отображается наименование товара и его цена", driver.findElement(By.className("order_item")).isDisplayed());
        Assert.assertTrue("Не отображается общая цена заказа", driver.findElement(By.xpath("//tr[contains(., 'Subtotal:')]")).isDisplayed());
        Assert.assertTrue("Не отображается способ оплаты", driver.findElement(By.xpath("//tr[contains(., 'Payment method:')]")).isDisplayed());
        Assert.assertTrue("Не отображается цена заказа со скидкой", driver.findElement(By.xpath("//tr[contains(., 'Total:')]")).isDisplayed());
    }
}
