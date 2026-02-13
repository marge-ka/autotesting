import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class IntershopHomePageTests {
    private WebDriver driver;
    private WebDriverWait wait;
    String url = "http://intershop5.skillbox.ru/";

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testContactInfoInHeader() {
        driver.navigate().to(url);
        var header = driver.findElement(By.className("header-callto")).getText();
        Assert.assertTrue("Нет контактной информации в хедере, или она не совпадает",
                header.contains("+7-999-123-12-12 | skillbox@skillbox.ru"));
        }

    @Test
    public void testSearchBar() {
        driver.navigate().to(url);
        driver.findElement(By.name("s")).sendKeys("книги");
        driver.findElement(By.className("searchsubmit")).click();
        Assert.assertTrue("Кнопка \"Поиск\" не работает",
                driver.getCurrentUrl().contains("?s=%D0%BA%D0%BD%D0%B8%D0%B3%D0%B8"));
    }

    @Test
    public void testLoginButton() {
        driver.get(url);
        driver.findElement(By.className("account")).click();
        Assert.assertEquals("Нет перехода при клике на кнопку \"Войти\" на страницу \"МОЙ АККАУНТ\"",
                "Мой аккаунт — Skillbox", driver.getTitle());
    }

    @Test
    public void testMainMenuItemCatalog() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//*[@class='menu']/li/a[.='Каталог']")).click();
        Assert.assertEquals("Нет перехода при клике на пункт меню \"КАТАЛОГ\"",
                "Каталог — Skillbox", driver.getTitle());
    }

    @Test
    public void testMainMenuItemMyAccount() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//*[@class='menu']/li/a[.='Мой аккаунт']")).click();
        Assert.assertEquals("Нет перехода при клике на пункт меню \"МОЙ АККАУНТ\"",
                "Мой аккаунт — Skillbox", driver.getTitle());
    }

    @Test
    public void testMainMenuItemCart() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//*[@class='menu']/li/a[.='Корзина']")).click();
        Assert.assertEquals("Нет перехода при клике на пункт меню \"КОРЗИНА\"",
                "Корзина — Skillbox", driver.getTitle());
    }

    @Test
    public void testMainMenuItemCheckoutWithoutAuthorization() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//*[contains(@class, 'widget-wrap')]/*[contains(., 'Книги')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
        driver.findElement(By.xpath("//*[@class='menu']/li/a[.='Оформление заказа']")).click();
        Assert.assertEquals("Нет перехода при клике на пункт меню \"ОФОРМЛЕНИЕ ЗАКАЗА\"",
                "Оформление заказа — Skillbox", driver.getTitle());
    }

    @Test
    public void testTransitionToTabletsSection() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//a[.//h4[.='Планшеты']]")).click();
        var tabletsSection = driver.findElement(By.xpath("//h1[.='Планшеты']"));
        Assert.assertEquals("Нет перехода в раздел ПЛАНШЕТЫ", "ПЛАНШЕТЫ", tabletsSection.getText());
    }

    @Test
    public void testTransitionToCamerasSection() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//a[.//h4[.='Фотоаппараты']]")).click();
        var camerasSection = driver.findElement(By.xpath("//h1[.='Фото/видео']"));
        Assert.assertEquals("Нет перехода в раздел ФОТОАППАРАТЫ", "ФОТО/ВИДЕО", camerasSection.getText());
    }

    @Test
    public void testDiscountLabelOnAllItemsInSaleSection() {
        driver.navigate().to(url);
        var discountLable = driver.findElements(
                By.xpath("//section[contains(., 'Распродажа')]//span[.='Скидка!']"));
        Assert.assertTrue("Один или несколько элементов в разделе РАСПРОДАЖА не имеют лейбл \"Скидка!\"",
                discountLable.size() >= 16);
    }
    @Test
    public void testOrderProcessingSection() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//div[@class ='title-bg'][contains(., 'Распродажа')]")).click();
        Assert.assertTrue("Нет перехода/отдельной страницы раздела РАСПРОДАЖА",
                driver.getTitle().contains("Распродажа"));
    }

    @Test
    public void testTransitionToNowOnSale() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//a[.//*[@class='promo-desc-title']]")).click();
        Assert.assertTrue("Нет перехода к товару в разделе Уже в продаже!",
                driver.getCurrentUrl().contains("?product="));
    }

    @Test
    public void testLabelIsNewInNewArrivalsSection() {
        driver.navigate().to(url);
        var discountLable = driver.findElements(
                By.xpath("//li[.//*[@class='label-new']]"));
        Assert.assertTrue("Один или несколько элементов в разделе НОВЫЕ ПОСТУПЛЕНИЯ не имеют лейбл \"Новый!\"",
                discountLable.size() >= 14);
    }
    @Test
    public void testNewArrivalsSection() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//div[@class ='title-bg'][contains(., 'поступления')]")).click();
        Assert.assertTrue("Нет перехода/отдельной страницы раздела НОВЫЕ ПОСТУПЛЕНИЯ",
                driver.getTitle().contains("Новые поступления"));
    }

    @Test
    public void testContactInfoInFooter() {
        driver.navigate().to(url);
        var footer = driver.findElement(By.className("ak-container")).getText();
        Assert.assertTrue("Нет контактной информации в хедере, или она не совпадает",
                footer.contains("+7-999-123-12-12 | skillbox@skillbox.ru"));
    }

    @Test
    public void testFooterPageLinksExist() {
        driver.navigate().to(url);
        List<WebElement> footerLinks = driver.findElements(By.cssSelector(".widget_pages ul li a"));
        List<String> sitePages = Arrays.asList(
                "Все товары", "Главная",
                "Корзина", "Мой аккаунт",
                "Оформление заказа", "Регистрация"
        );
        for (String page : sitePages) {
           boolean text = footerLinks.stream()
                    .anyMatch(link -> link.getText().equals(page));
            Assert.assertTrue("Не отображается ссылка на раздел " + page , text);
        }
    }
}
