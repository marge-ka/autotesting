
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class IntershopCheckoutPageTests {
    private WebDriver driver;
    private WebDriverWait wait;
    String url = "https://intershop5.skillbox.ru";
    private final int DISCOUNT = 500;
    private final double delta = 0.001;

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
         }

    @After
    public void tearDown() {
        driver.quit();
    }

    private void navigateToCheckout() {
        driver.navigate().to(url);
        driver.findElement(By.xpath("//*[contains(@class, 'widget-wrap')]/*[contains(., 'Книги')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
        driver.findElement(By.xpath("//*[@class='menu']/li/a[.='Оформление заказа']")).click();
        driver.findElement(By.className("showlogin")).click();
        driver.findElement(By.id("username")).sendKeys("user29497694");
        driver.findElement(By.id("password")).sendKeys("29497694@test.ru");
        driver.findElement(By.name("login")).click();
    }

    private void applyCoupon(){
        driver.findElement(By.className("showcoupon")).click();
        driver.findElement(By.id("coupon_code")).sendKeys("sert500");
        driver.findElement(By.name("apply_coupon")).click();
    }

    private void placeOrder() {
        driver.findElement(By.cssSelector("input#billing_first_name")).sendKeys("Иван");
        driver.findElement(By.cssSelector("input#billing_last_name")).sendKeys("Иванов");
        driver.findElement(By.cssSelector("[role='textbox'][title='Russia']")).isSelected();
        driver.findElement(By.cssSelector("[placeholder='Улица и номер дома']")).sendKeys("ул. Центральная, д.1");
        driver.findElement(By.id("billing_city")).sendKeys("г. Иваново");
        driver.findElement(By.id("billing_state")).sendKeys("Ивановская область");
        driver.findElement(By.id("billing_postcode")).sendKeys("153000");
        driver.findElement(By.id("billing_phone")).sendKeys("+7 9999999999");
        driver.findElement(By.id("billing_email")).clear();
        driver.findElement(By.id("billing_email")).sendKeys("test@test.ru");
        driver.findElement(By.cssSelector("textarea#order_comments")).sendKeys("Только новый");
    }

    private Double toDoublePrice(String text) {
        String[] numbers = text.split("\\D+");
        String price = numbers[0];
        return Double.parseDouble(price);
    }

    @Test
    public void testKeyElementsDisplayedOnCheckoutPage() {
        navigateToCheckout();
        Assert.assertTrue("Не отображается логотип", driver.findElement(By.className("site-logo")).isDisplayed());
        Assert.assertTrue("Не отображается строка поиска", driver.findElement(By.name("s")).isDisplayed());
        Assert.assertTrue("Не отображается контактная информация в хедере", driver.findElement(By.className("header-callto")).isDisplayed());
        Assert.assertTrue("Не отображается кнопка \"Выйти\"", driver.findElement(By.className("logout")).isDisplayed());
        Assert.assertFalse("Не отображается Главное меню сайта", driver.findElements(By.xpath("//*[@class='menu']/li")).isEmpty());
        Assert.assertTrue("Не отображается контактная информация в футуре", driver.findElement(By.className("ak-container")).isDisplayed());
        Assert.assertFalse("Не отображается ссылки на разделы сайта в футуре", driver.findElements(By.cssSelector(".widget_pages ul li a")).isEmpty());
    }

    @Test
    public void testKeyElementsOnCheckoutPageAreClickable() {
        navigateToCheckout();
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
    public void testApplyCouponOnCheckoutPage() {
        navigateToCheckout();
        applyCoupon();
        String message = "Купон успешно добавлен.";
        Assert.assertEquals("Нет сообщения о том, что купон применён",
                message, driver.findElement(By.className("woocommerce-message")).getText());
    }

    @Test
    public void testApplyCouponTwiceOnCheckoutPage() {
        navigateToCheckout();
        applyCoupon();
        driver.findElement(By.id("coupon_code")).clear();
        applyCoupon();
      String message = "Coupon code already applied!";
        Assert.assertEquals("Нет сообщения о том, что купон применён",
                message, driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    public void testApplyInvalidCouponOnCheckoutPage() {
        navigateToCheckout();
        driver.findElement(By.className("showcoupon")).click();
        driver.findElement(By.id("coupon_code")).sendKeys("sert5000000");
        driver.findElement(By.name("apply_coupon")).click();
        String message = "Неверный купон.";
        Assert.assertEquals("Нет сообщения о том, что купон применён",
                message, driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    public void testMessageApplyCouponTwiceOnCheckoutPage() {
        navigateToCheckout();
        applyCoupon();
        driver.findElement(By.id("coupon_code")).clear();
        applyCoupon();
        String basket = "Промокод уже применен!";
        Assert.assertEquals("Некорректное сообщение о том, что промокод применен",
                basket, driver.findElement(By.className("woocommerce-error")).getText());
    }

    @Test
    public void testDisplayProduct_when_PlacingOrder () {
        navigateToCheckout();
        Assert.assertTrue("При оформлени заказа не отображается информация о товаре, его количестве и цене",
                driver.findElement(By.className("cart_item")).isDisplayed());
      }

    @Test
    public void testDisplayDiscount_when_PlacingOrder () { // Отображение скидки товара при оформлении заказа
        navigateToCheckout();
        applyCoupon();
        // Получаем изначальную базовую цену
        String basePrice = driver.findElement(By.cssSelector(".product-total  bdi")).getText();
 // Ожидание изменения итоговой цены (salePrice) относительно базовой
        wait.until(driver -> {
            String currentSalePrice = driver.findElement(By.cssSelector(".order-total bdi")).getText();
            return !currentSalePrice.equals(basePrice);
        });
// После ожидания — получаем актуальные значения
        String salePrice = driver.findElement(By.cssSelector(".order-total bdi")).getText();
        double price = toDoublePrice(basePrice) - DISCOUNT;
        double finalPrice = toDoublePrice(salePrice);
        //   System.out.println(salePrice);
        Assert.assertEquals("Неверная итоговая цена товара со скидкой", price,
                finalPrice, delta);// где delta погрешность сравнения (обязательно для сравнения чисел с плавающей точкой-> assertEquals(expected, actual, delta);)
    }

    @Test
    public void testValidatesRequiredFields() {
        navigateToCheckout();
        driver.findElement(By.cssSelector("input#billing_first_name")).clear();
        driver.findElement(By.cssSelector("input#billing_last_name")).clear();
        driver.findElement(By.cssSelector("[placeholder='Улица и номер дома']")).clear();
        driver.findElement(By.id("billing_city")).clear();
        driver.findElement(By.id("billing_state")).clear();
        driver.findElement(By.id("billing_postcode")).clear();
        driver.findElement(By.id("billing_phone")).clear();
        driver.findElement(By.id("billing_email")).clear();
        driver.findElement(By.id("place_order")).click();
        String firstName = "Имя для выставления счета обязательное поле.";
        String lastName = "Фамилия для выставления счета обязательное поле.";
        String address = "Адрес для выставления счета обязательное поле.";
        String city = "Город / Населенный пункт для выставления счета обязательное поле.";
        String state = "Область для выставления счета обязательное поле.";
        String postcode = "Почтовый индекс для выставления счета обязательное поле.";
        String invalidPhone = "неверный номер телефона.";
        String phone = "Телефон для выставления счета обязательное поле.";
        String email = "Адрес почты для выставления счета обязательное поле.";
        Assert.assertTrue("Нет сообщения о незаполненных полях",
                driver.findElement(By.className("woocommerce-error")).isDisplayed());

        Assert.assertEquals("Сообщение не соответствует ожидаемому", firstName,
                driver.findElement(By.xpath("//li[contains(@data-id, 'first_name')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", lastName,
                driver.findElement(By.xpath("//li[contains(@data-id, 'last_name')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", address,
                driver.findElement(By.xpath("//li[contains(@data-id, 'address')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", city,
                driver.findElement(By.xpath("//li[contains(@data-id, 'city')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", state,
                driver.findElement(By.xpath("//li[contains(@data-id, 'state')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", postcode,
                driver.findElement(By.xpath("//li[contains(@data-id, 'postcode')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", invalidPhone,
                driver.findElement(By.xpath("//li[contains(., 'неверный')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", phone,
                driver.findElement(By.xpath("//li[contains(., 'Телефон для выставления')]")).getText());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", email,
                driver.findElement(By.xpath("//li[contains(@data-id, 'email')]")).getText());
    }

    @Test
    public void testBankTransferPayment() {
        navigateToCheckout();
        placeOrder();
        driver.findElement(By.id("payment_method_bacs")).isSelected();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button#place_order"))).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("post-title"), "Заказ получен"));
        String message = "Заказ получен";

        Assert.assertTrue("Нет сообщения что заказ получен",
                driver.findElement(By.className("post-title")).isDisplayed());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", message,
                driver.findElement(By.className("post-title")).getText());
    }


    @Test
    public void testCashOnDelivery() {
        navigateToCheckout();
        placeOrder();
        driver.findElement(By.id("payment_method_cod")).isSelected();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button#place_order"))).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("post-title"), "Заказ получен"));
        String message = "Заказ получен";

        Assert.assertTrue("Нет сообщения что заказ получен",
                driver.findElement(By.className("post-title")).isDisplayed());
        Assert.assertEquals("Сообщение не соответствует ожидаемому", message,
                driver.findElement(By.className("post-title")).getText());
    }
}
