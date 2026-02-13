# Автотесты для интернет-магазина Intershop (учебный проект курса **Автотесты на Java. Базовая часть**)

Набор автоматизированных тестов для функционального тестирования сайта [intershop5.skillbox.ru](https://intershop5.skillbox.ru) на базе Selenium WebDriver и JUnit.

## Содержание тестового покрытия
| Класс тестов                     | Проверяемый функционал                          | Кол-во тестов |
|----------------------------------|-------------------------------------------------|---------------|
| `IntershopAuthTests`             | Регистрация, авторизация, восстановление пароля | 9             |
| `IntershopCartPageTests`         | Работа с корзиной, купоны, скидки               | 17            |
| `IntershopCatalogPageTests`      | Навигация по каталогу, пагинация                | 30            |
| `IntershopCheckoutPageTests`     | Оформление заказа                               | 11            |
| `IntershopHomePageTests`         | Отображение разделов, контактной информации     | 16            |
| `IntershopMyAccountPageTests`    | Список заказов, детали заказа                   | 6             |
| **Итого**                        |                                                 | **89**        |

##  Технологический стек
- **Java:** 24 (рекомендуется) или 8+
- **Тестовый фреймворк:** JUnit 4
- **WebDriver:** Selenium 4.x
- **Браузер:** Google Chrome + ChromeDriver
- **Сборка:** Maven
- **Ожидания:** `WebDriverWait` (explicit) + implicit waits

## Структура проекта
```text
intershop-tests/
├── screenshots/                        # Папка для скриншотов
│   ├── имяТеста_ГГГГММДД_ЧЧММСС.png
│   └── ...
├── src/
│ └── test/
│ └── java/
│ ├── IntershopAuthTests.java           # Авторизация/регистрация
│ ├── IntershopCartPageTests.java       # Корзина
│ ├── IntershopCatalogPageTests.java    # Каталог товаров
│ ├── IntershopCheckoutPageTests.java   # Оформление заказа
│ ├── IntershopHomePageTests.java       # Главная страница
│ └── IntershopMyAccountPageTests.java  # Личный кабинет
├── drivers/
│ └── chromedriver.exe                  # Драйвер Chrome (Windows)
├── pom.xml                             # Конфигурация Maven
└── README.md                           # Документация
```
## Пример минимального pom.xml
Добавьте раздел с зависимостями — это критично для воспроизводимости:
```xml
<dependencies>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.18.1</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Запуск тестов
### Подготовка
 ```bash
 # Скопируйте драйвер в папку проекта
mkdir -p drivers
# Windows: drivers\chromedriver.exe
 ``` 
### Запуск через IDE (IntelliJ IDEA)
1. Откройте нужный класс тестов
2. Кликните правой кнопкой → Run → [ClassName].java
3. Для запуска всех тестов: кликните правой кнопкой на папку test → Run All Tests

### Запуск через Maven
```bash
# Все тесты
mvn test
# Тесты конкретного класса
mvn test -Dtest=IntershopAuthTests
# Один тест
mvn test -Dtest=IntershopAuthTests#testSuccessRegistrationInIntershop
 ``` 
## Учётные данные для тестов
| Тип                     | Логин / Email    | Пароль           | Примечание                      |
|-------------------------|------------------|------------------|---------------------------------|
| Успешная авторизация    | user29497694     | 29497694@test.ru | Пользователь с заказами         |
| Пустая история заказов  | 29501999@test.ru | 29501999@test.ru | Новый пользователь              |
| Тестовый пользователь   | test             | test             | Для проверки ошибок авторизации |

Важно: Для тестов регистрации генерируются уникальные email на основе временной метки:
```java
String user = System.currentTimeMillis() / 60_000 + "@test.ru";
```
## Особенности реализации
### Хелперы и утилиты
* toDoublePrice(String text) — конвертация цены из строки в double (очистка от символов валюты)
* calculatePageCount(WebDriver driver) — расчёт количества страниц в каталоге
* Единый WebDriverWait с таймаутом 5–10 сек для явных ожиданий

### Селекторы
Предпочтение отдаётся:
* id → name → cssSelector → xpath
* Для динамических элементов используются частичные совпадения:
* By.cssSelector("[class*='register']")

### Обработка скидок
* Фиксированный купон sert500 даёт скидку 500 ₽
* Проверка комбинирования промо-скидок и купонов
* Валидация итоговой суммы с погрешностью delta = 0.001

## Ограничения и зависимости
- Тесты завязаны на конкретное состояние сайта `intershop5.skillbox.ru`:
    - Наличие товаров «Холодильник», «Книги» в каталоге
    - Существование тестовых пользователей (`user29497694`, `29501999@test.ru`)
    - Наличие купона `sert500`

## Результаты тестов
После запуска через Maven результаты сохраняются в:
```text
target/surefire-reports/
├── TEST-*.xml          # XML-отчёты для CI/CD
└── *.txt               # Текстовые логи выполнения
```
## Скриншоты падающих тестов
**Реализовано только для класса `IntershopAuthTests.java`:**
При падении тестов в классе `IntershopAuthTests.java` автоматически сохраняются скриншоты.

Путь: screenshots/имяТеста_ГГГГММДД_ЧЧММСС.png

Пример: screenshots/testMessagePasswordRecoveryPage_20260212_143015.png

Для остальных тестовых классов механизм скриншотов **не реализован**.





