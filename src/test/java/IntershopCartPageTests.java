import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class IntershopCartPageTests {
    private WebDriver driver;
    private WebDriverWait wait;
    String url = "https://intershop5.skillbox.ru/cart/";
    private final int DISCOUNT = 500;
    private final double delta = 0.001;

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.navigate().to(url);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    private void addProductToCart() {
        driver.findElement(By.name("s")).sendKeys("холодильник");
        driver.findElement(By.className("fa-search")).click();
        driver.findElement(By.xpath("//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
    }

    private void addDiscountItemToCart() {
        driver.findElement(By.name("s")).sendKeys("книг");
        driver.findElement(By.className("fa-search")).click();
        driver.findElement(By.xpath("//li[contains(., 'Скидка')]//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
    }

    private void applyCoupon() {
        driver.findElement(By.id("coupon_code")).sendKeys("sert500");
        driver.findElement(By.name("apply_coupon")).click();
    }

    private Double toDoublePrice(String text) {
        String[] numbers = text.split("\\D+");
        String price = numbers[0];
        return Double.parseDouble(price);
    }

    @Test
    public void testKeyElementsDisplayedOnCartPage() {
        Assert.assertTrue("Не отображается логотип", driver.findElement(By.className("site-logo")).isDisplayed());
        Assert.assertTrue("Не отображается строка поиска", driver.findElement(By.name("s")).isDisplayed());
        Assert.assertTrue("Не отображается контактная информация в хедере", driver.findElement(By.className("header-callto")).isDisplayed());
        Assert.assertTrue("Не отображается кнопка \"Войти\"", driver.findElement(By.className("account")).isDisplayed());
        Assert.assertFalse("Не отображается Главное меню сайта", driver.findElements(By.xpath("//*[@class='menu']/li")).isEmpty());
        Assert.assertTrue("Не отображается контактная информация в футуре", driver.findElement(By.className("ak-container")).isDisplayed());
        Assert.assertFalse("Не отображается ссылки на разделы сайта в футуре", driver.findElements(By.cssSelector(".widget_pages ul li a")).isEmpty());
    }

    @Test
    public void testKeyElementsOnCartPageAreClickable() {
        Assert.assertTrue("Логотип не кликабелен", driver.findElement(By.className("site-logo")).isEnabled());
        Assert.assertTrue("Строка поиска не активна", driver.findElement(By.name("s")).isEnabled());
        Assert.assertTrue("Кнопка поиска не кликабельна", driver.findElement(By.className("searchsubmit")).isEnabled());
        Assert.assertTrue("Кнопка \"Войти\" не кликабельна", driver.findElement(By.className("account")).isEnabled());
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
    public void testTotalPriceForMultipleItems() {
        addProductToCart();
        addDiscountItemToCart();

        var priceLocator1 = driver.findElement(By.xpath("(//td[@class='product-price']//bdi)[1]")).getText();
        var priceLocator2 = driver.findElement(By.xpath("(//td[@class='product-price']//bdi)[2]")).getText();

        double price1 = toDoublePrice(priceLocator1);
        double price2 = toDoublePrice(priceLocator2);
        double sum = price1 + price2;
        var sumLocator = driver.findElement(By.xpath("//*[@data-title='Сумма']//bdi")).getText();
        double finalPrice = toDoublePrice(sumLocator);
        Assert.assertEquals("Неверная итоговая сумма цен", sum,
                finalPrice, delta);
    }

    @Test
    public void testCartContainsCorrectAmount() {
       addProductToCart();
       addProductToCart();
       addProductToCart();
        var count = driver.findElement(By.cssSelector(".quantity input"));
        String clickCount = "3";
        Assert.assertEquals("В корзине отображается неверное количество товара", clickCount,
                count.getAttribute("value"));
    }

    @Test
    public void testDeletedItem() {
        addProductToCart();
        driver.findElement(By.xpath("//a[contains(., '×')]")).click();

        Assert.assertTrue("Не удаляется товар при клике на кнопку ❎",
                driver.findElement(By.className("cart-empty")).isDisplayed());
    }

    @Test
    public void testRestoringErroneouslyDeletedItem() {
        addDiscountItemToCart();
        driver.findElement(By.xpath("//a[contains(., '×')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Вернуть?')]")).click();

        Assert.assertTrue("Не возвращается товар при клике на ссылку \"Вернуть?\"",
                driver.findElement(By.className("quantity")).isDisplayed());
    }

    @Test
    public void testApplyCoupon() {
        addProductToCart();
        applyCoupon();
        Assert.assertNotNull("Нет сообщения, что купон применен",
                By.className("woocommerce-message"));
        String message = "Купон успешно добавлен.";
        Assert.assertEquals("Нет сообщения, что купон успешно применён", message,
                driver.findElement(By.className("woocommerce-message")).getText());
    }

    @Test
    public void testApplyCouponTwice() {
        addProductToCart();
        applyCoupon();
        driver.navigate().refresh();
        driver.findElement(By.id("coupon_code")).clear();
        applyCoupon();

        var message = driver.findElement(By.className("woocommerce-error"));
        Assert.assertNotNull("Нет сообщения, что купон уже применен",
                message);
        String textMessage = "Coupon code already applied!";
        Assert.assertEquals("Некорректное сообщение, что купон уже применен", textMessage,
                driver.findElement(By.className("woocommerce-error")).getText() );
    }

    @Test
    public void testDeleteCoupon() {
        addProductToCart();
        applyCoupon();
        driver.findElement(By.cssSelector("[class*='remove-']")).click();
        Assert.assertTrue("Нет сообщения, что купон удален",
                driver.findElement(By.cssSelector("[role='alert']")).isDisplayed());
    }

    @Test
    public void testApplyInvalidCouponInCart() {
        addProductToCart();
        driver.findElement(By.cssSelector(".coupon input")).sendKeys("sert50");
        driver.findElement(By.name("apply_coupon")).click();
        Assert.assertTrue("Применилась скидка по неверному купону",
                driver.findElement(By.className("woocommerce-error")).isDisplayed());
    }

    @Test
    public void testAddItemApplyCouponAndCheckout() {
        addProductToCart();
        applyCoupon();
        driver.navigate().refresh();
        driver.findElement(By.cssSelector(".woocommerce a.button.alt")).click();
        Assert.assertTrue("Нет перехода на страницу оформления заказа  при клике на кнопку \"ОФОРМИТЬ ЗАКАЗ\"",
                driver.getCurrentUrl().contains("checkout"));
    }

    @Test
    public void testPriceChangesAfterApplyingCoupon() {
        addProductToCart();
        applyCoupon();

        var discount = driver.findElement(By.className("cart-discount"));
        Assert.assertTrue("Не применилась скидка", discount.isDisplayed());
        Assert.assertTrue("Размер скидки не соответствует номиналу",
                discount.getText().contains("-500,00₽"));

        double totalCost = toDoublePrice(driver.findElement(
                By.cssSelector(".product-subtotal bdi")).getText()) - DISCOUNT;
        double toBePaid = toDoublePrice(driver.findElement(
                By.xpath("//*[@data-title='Сумма']")).getText());
        Assert.assertEquals("Итоговая сумма не пересчиталась после применения скидки",
                totalCost, toBePaid, delta);
    }


    @Test
    public void testCouponAppliedAfterPromotionalDiscount() {
        driver.findElement(By.name("s")).sendKeys("холодильник");
        driver.findElement(By.className("fa-search")).click();

        var priceLocator = (driver.findElement(
                By.xpath("//li[contains(., 'Скидка') and .//a[contains(., 'В корзину')]]//ins")).getText());
        driver.findElement(By.xpath("//li[contains(., 'Скидка')]//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();

        var priceInCart = driver.findElement(By.cssSelector(".product-price bdi")).getText();
        Assert.assertEquals("Цена товара в карточке и в корзине не совпадают", priceLocator, priceInCart);

        applyCoupon();

        String basePrice = driver.findElement(By.cssSelector(".product-subtotal bdi")).getText();

        wait.until(driver -> {
            String currentSalePrice = driver.findElement(By.cssSelector(".cart_totals tr.order-total bdi")).getText();
            return !currentSalePrice.equals(basePrice);
        });

        String salePrice = driver.findElement(By.cssSelector(".cart_totals tr.order-total bdi")).getText();
        double price = toDoublePrice(basePrice) - DISCOUNT;
        double finalPrice = toDoublePrice(salePrice);
        Assert.assertEquals("Неверная итоговая цена товара со скидкой", price,
                finalPrice, delta);
    }
}
