import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class IntershopCatalogPageTests {
    private WebDriver driver;
    private WebDriverWait wait;
    String url = "https://intershop5.skillbox.ru/product-category/catalog/";

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

    private int calculatePageCount(WebDriver driver) {
        driver.get(url);
        int numberProductsInPage = 12;
        WebElement countElement = driver.findElement(By.cssSelector("[class*='result']"));
        String text = countElement.getText();
        String[] numbers = text.split("\\D+");
        String lastNumber = numbers[numbers.length - 1];
        return Integer.parseInt(lastNumber) / numberProductsInPage;
    }

    @Test
    public void testKeyElementsDisplayedOnCartPage() {
        driver.navigate().to(url);
        Assert.assertTrue("Не отображается логотип", driver.findElement(By.className("site-logo")).isDisplayed());
        Assert.assertTrue("Не отображается строка поиска",  driver.findElement(By.name("s")).isDisplayed());
        Assert.assertTrue("Не отображается контактная информация в хедере", driver.findElement(By.className("header-callto")).isDisplayed());
        Assert.assertTrue("Не отображается кнопка \"Войти\"", driver.findElement(By.className("account")).isDisplayed());
        Assert.assertFalse("Не отображается Главное меню сайта", driver.findElements(By.xpath("//*[@class='menu']/li")).isEmpty());
        Assert.assertTrue("Не отображается контактная информация в футуре", driver.findElement(By.className("ak-container")).isDisplayed());
        Assert.assertFalse("Не отображается ссылки на разделы сайта в футуре", driver.findElements(By.cssSelector(".widget_pages ul li a")).isEmpty());
    }

    @Test
    public void testKeyElementsOnCartPageAreClickable() {
        driver.navigate().to(url);
        Assert.assertTrue("Логотип не кликабелен", driver.findElement(By.className("site-logo")).isEnabled());
        Assert.assertTrue("Строка поиска не активна", driver.findElement(By.name("s")).isEnabled());
        Assert.assertTrue("Кнопка поиска не кликабельна", driver.findElement(By.className("searchsubmit")).isEnabled());
        Assert.assertTrue("Кнопка \"Войти\" не кликабельна", driver.findElement(By.className("account")).isEnabled());
        var itemsMenu = driver.findElements(By.xpath("//*[@class='menu']/li"));
        for (int i = 1; i <= itemsMenu.size(); i++) {
            var itemMenu = driver.findElement(By.xpath("(//*[@class='menu']/li)[" + i + "]"));
            String item = itemMenu.getText();
            Assert.assertTrue(String.format("Элемент \"%s\" Главного меню не кликабелен", item) , itemMenu.isEnabled());
        }
        var links = driver.findElements(By.cssSelector(".widget_pages ul li a"));
        for (int i = 1; i <= links.size(); i++) {
            var link = driver.findElement(By.cssSelector(".widget_pages ul li:nth-of-type(" + i + ") a"));
            String linkName = link.getText();
            Assert.assertTrue(String.format("Ссылка в футуре на раздел \"%s\" сайта не кликабельна", linkName), link.isEnabled());
        }
    }

    @Test
    public void testCategoriesProductsSection() {
        driver.get(url);
        var products = driver.findElement(By.className("widget_product_categories"));
        Assert.assertTrue("Не отображается раздел \"Категории товаров\"", products.isDisplayed());
    }

    @Test
    public void testMainCategoriesAreVisible() {
        driver.get(url);
        var category = driver.findElement(By.className("product-categories")).getText();
        Assert.assertTrue("Не отображается ссылка в меню справа с неклассифицированными позициями \"Без категории\"", category.contains("Без категории"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Электроника\"", category.contains("Электроника"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Бытовая техника\"", category.contains("Бытовая техника"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Одежда\"", category.contains("Одежда"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Книги\"", category.contains("Книги"));
    }

    @Test
    public void testSubcategoriesAreVisible() {
        driver.get(url);
        var category = driver.findElement(By.className("product-categories")).getText();
        Assert.assertTrue("Не отображается ссылка в меню справа  \"Каталог\"", category.contains("Каталог"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Планшеты\"", category.contains("Планшеты"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Стиральные машины\"", category.contains("Стиральные машины"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Телевизоры\"", category.contains("Телевизоры"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Телефоны\"", category.contains("Телефоны"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Фото/видео\"", category.contains("Фото/видео"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Холодильники\"", category.contains("Холодильники"));
        Assert.assertTrue("Не отображается ссылка в меню справа \"Часы\"", category.contains("Часы"));
    }

    @Test
    public void testNavigateToUncategorizedPage() {
        driver.get(url);
        driver.findElement(By.linkText("Без категории")).click();
        Assert.assertEquals("Нет перехода на страницу товаров \"Без категории\"",
                "Без категории — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToElectronicsCategory() {
        driver.get(url);
        driver.findElement(By.linkText("Электроника")).click();
        Assert.assertEquals("Нет перехода в категорию \"Электроника\"",
                "Электроника — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToHouseholdAppliancesCategory() {
        driver.get(url);
        driver.findElement(By.linkText("Бытовая техника")).click();
        Assert.assertEquals("Нет перехода в категорию \"Бытовая техника\"",
                "Бытовая техника — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToClothCategory() {
        driver.get(url);
        driver.findElement(By.linkText("Одежда")).click();
        Assert.assertEquals("Нет перехода в категорию \"Одежда\"",
                "Одежда — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToBooksCategory() {
        driver.get(url);
        driver.findElement(By.linkText("Книги")).click();
        Assert.assertEquals("Нет перехода в категорию \"Книги\"",
                "Книги — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToCatalogCategories() {
        driver.get(url);
        driver.findElement(By.linkText("Каталог")).click();
        Assert.assertEquals("Нет перехода в категорию \"Каталог\"",
                "Каталог — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToTabletsSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Планшеты")).click();
        Assert.assertEquals("Нет перехода в категорию \"Планшеты\"",
                "Планшеты — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToWashingMachinesSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Стиральные машины")).click();
        Assert.assertEquals("Нет перехода в категорию \"Стиральные машины\"",
                "Стиральные машины — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToTVSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Телевизоры")).click();
        Assert.assertEquals("Нет перехода в категорию \"Телевизоры\"",
                "Телевизоры — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToPhonesSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Телефоны")).click();
        Assert.assertEquals("Нет перехода в категорию \"Телефоны\"",
                "Телефоны — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToPhotoSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Фото/видео")).click();
        Assert.assertEquals("Нет перехода в категорию \"Фото/видео\"",
                "Фото/видео — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToRefrigeratorsSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Холодильники")).click();
        Assert.assertEquals("Нет перехода в категорию \"Холодильники\"",
                "Холодильники — Skillbox", driver.getTitle());
    }

    @Test
    public void testNavigateToWatchSubcategories() {
        driver.get(url);
        driver.findElement(By.linkText("Часы")).click();
        Assert.assertEquals("Нет перехода в категорию \"Часы\"",
                "Часы — Skillbox", driver.getTitle());
    }

    @Test
    public void testProductsSection() {
        driver.get(url);
        var products = driver.findElement(By.className("widget_products"));
        Assert.assertTrue("Нет раздела \"Товары\"", products.isDisplayed());
    }

    @Test
    public void testViewingProductInProductsSection() {
        driver.get(url);
        driver.findElement(By.xpath("//a[contains(., 'Электрогитара')]")).click();
        Assert.assertTrue("Нет перехода по ссылке товара \"Электрогитара\" в разделе ТОВАРЫ",
                driver.getCurrentUrl().contains("gibson-les-paul-studio-2018-vintage-sunburst"));
    }

    @Test
    public void testDisplayNumberOfProductCardInCatalog() {
        driver.get(url);
        var cards = driver.findElements(By.className("type-product"));
        Assert.assertTrue("На первой странице каталога не отображаются карточки товаров",
                !cards.isEmpty() & cards.size() <= 12);
    }

    @Test
    public void testCheckProductLimitOnCatalogPage() {
        driver.get(url);
        for (int i = 2; i <= calculatePageCount(driver); i++) {
            driver.get(url + "page/" + i + "/");
            Assert.assertFalse("На странице каталога не отображаются карточки товаров",
                    driver.findElements(By.className("type-product")).isEmpty());
            Assert.assertTrue("На странице каталога товаров больше 12",
                    driver.findElements(By.className("type-product")).size() >= 12);
        }
    }

    @Test
    public void testProductsSectionExistsOnCatalogPage() {
        for (int i = 2; i <= calculatePageCount(driver); i++) {
            driver.get(url + "page/" + i + "/");
            Assert.assertFalse("Нет раздела \"Товары\" на странице каталога номер: " + i,
                    driver.findElements(By.className("widget_products")).isEmpty());
        }
    }

    @Test
    public void testProductsCategorySectionExistsOnCatalogPage() {
        driver.get(url);
        for (int i = 2; i <= calculatePageCount(driver); i++) {
            driver.get(url + "page/" + i + "/");
            Assert.assertFalse("Нет раздела \"Категории товаров\" на странице каталога номер: " + i,
                    driver.findElements(By.className("widget_product_categories")).isEmpty());
        }
    }

    @Test
    public void testFooterExistsOnCatalogPage() {
        driver.get(url);
        for (int i = 2; i <= calculatePageCount(driver); i++) {
            driver.get(url + "page/" + i + "/");
            Assert.assertFalse("Нет футера на странице каталога номер: " + i,
                    driver.findElements(By.id("colophon")).isEmpty());
        }
    }

    @Test
    public void testPageNavigation() {
        driver.get(url);
        int totalPages = calculatePageCount(driver);

        for (int currentPage = 2; currentPage <= totalPages; currentPage++) {

            int nextPage = currentPage;
            if (currentPage > 2) {
                nextPage = currentPage + 1;
            }
            WebElement nextPageLink = driver.findElement(
                    By.xpath(String.format("//ul[@class='page-numbers']//a[text()='%d']", nextPage)));
            nextPageLink.click();

            driver.get(url + "page/" + currentPage + "/");

            Assert.assertEquals("Отсутствует пагинация на странице " + currentPage,
                    url + "page/" + nextPage + "/", driver.getCurrentUrl());

        }
    }

    @Test
    public void testNavigationByButtonsNextPrev() {
        driver.get(url);
        driver.findElement(By.className("next")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("prev"))).click();
        Assert.assertEquals("Навигация по кнопкам \"→\" и \"←\" не работает",
                url, driver.getCurrentUrl());
    }

    @Test
    public void testAddProductToCartViaSearchBar() {
        driver.get(url);
        driver.findElement(By.name("s")).sendKeys("холодильник");
        driver.findElement(By.className("fa-search")).click();
        driver.findElement(By.xpath("//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
        var productInCart = driver.findElement(By.xpath("//*[@class='product-name'][contains(., 'Холодильн')]"));
        Assert.assertTrue("Отсутствует товар в корзине",
                productInCart.isDisplayed());
    }

    @Test
    public void testAddProductToCartByMenuProductCategory() {
        driver.get(url);
        driver.findElement(By.xpath("//ul[@class='product-categories']//a[contains(., 'Холодильник')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
        var productInCart = driver.findElement(By.xpath("//*[@class='product-name'][contains(., 'Холодильн')]"));
        Assert.assertTrue("Отсутствует товар в корзине",
                productInCart.isDisplayed());
    }

    @Test
    public void testAddProductToCartByCatalog() {
        driver.get(url);
        driver.findElement(By.className("next")).click();
        driver.findElement(By.xpath("//a[contains(., 'В корзину')]")).click();
        driver.findElement(By.xpath("//a[contains(., 'Подробнее')]")).click();
        var productInCart = driver.findElement(By.className("quantity"));
        Assert.assertTrue("Отсутствует товар в корзине",
                productInCart.isDisplayed());
    }
}