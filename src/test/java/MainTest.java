import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class MainTest {

    private static final String targetURL = "https://www.sberbank.ru/ru/person";
    private static WebDriver driver;
    private static Wait<WebDriver> wait;
    private static Actions action;
    private static ChromeOptions options;
    private static JavascriptExecutor js;

    @BeforeAll
    static void initialization() {
        System.setProperty("webdriver.chrome.driver", "webdrivers/chromedriver");
        options = new ChromeOptions().addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        action = new Actions(driver);
        js = (JavascriptExecutor) driver;
    }

    @BeforeEach
    void testPrepare() {
        driver.get(targetURL);
    }

    @ParameterizedTest
    @MethodSource("argsListProvider")
    void runTest(List<String> arguments) throws InterruptedException {

        //навигация по сайту
        WebElement insuranceMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//span[text()='Страхование']"
        )));
        action.moveToElement(insuranceMenu).build().perform();

        WebElement travelInsuranceLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@href='/ru/person/bank_inshure/insuranceprogram/life/travel']")));
        action.moveToElement(travelInsuranceLink).build().perform();
        travelInsuranceLink.click();

        WebElement pageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "(//h1[text()='Страхование путешественников'])[2]"
        )));
        Assertions.assertEquals("Страхование путешественников", pageHeader.getText(),
                "Заголовок отсутствует/не соответствует требуемому");

        WebElement checkoutOnlineButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//b[text()='Оформить онлайн']"
        )));
        checkoutOnlineButton.click();

        WebElement insuranceCoverageAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//h3[text()='Минимальная']"
        )));
        js.executeScript("arguments[0].scrollIntoView(true);", insuranceCoverageAmount);
        Thread.sleep(1000);
        insuranceCoverageAmount.click();

        WebElement checkoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//button[text()='Оформить']"
        )));
        js.executeScript("arguments[0].scrollIntoView(true);", checkoutButton);
        Thread.sleep(1000);
        checkoutButton.click();




        //создание веб-элементов полей
        WebElement lastnameInsured = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Фамилия / Surname']")));
        WebElement nameInsured = driver.findElement(By.xpath("//input[@placeholder='Имя / Name']"));
        WebElement birthdayInsured = driver.findElement(By.xpath("//input[@id='birthDate_vzr_ins_0']"));
        WebElement lastnamePolicyholder = driver.findElement(By.xpath("//input[@id='person_lastName']"));
        WebElement namePolicyholder = driver.findElement(By.xpath("//input[@id='person_firstName']"));
        WebElement middlenamePolicyholder = driver.findElement(By.xpath("//input[@id='person_middleName']"));
        WebElement birthdayPolicyholder = driver.findElement(By.xpath("//input[@id='person_birthDate']"));
        WebElement citizenshipPolicyholder = driver.findElement(By.xpath("//label[text()='гражданин РФ']"));
        WebElement sexPolicyholder = driver.findElement(By.xpath("//label[text()='Мужской']"));
        WebElement passportSeries = driver.findElement(By.xpath("//input[@id='passportSeries']"));
        WebElement passportNumber = driver.findElement(By.xpath("//input[@id='passportNumber']"));
        WebElement documentDate = driver.findElement(By.xpath("//input[@id='documentDate']"));
        WebElement documentIssue = driver.findElement(By.xpath("//input[@id='documentIssue']"));

        //заполнение формы тестовыми значениями
        insertValue(lastnameInsured, arguments.get(0));
        insertValue(nameInsured, arguments.get(1));
        insertValue(birthdayInsured, arguments.get(2));
        insertValue(lastnamePolicyholder, arguments.get(3));
        insertValue(namePolicyholder, arguments.get(4));
        insertValue(middlenamePolicyholder, arguments.get(5));
        insertValue(birthdayPolicyholder, arguments.get(6));
        citizenshipPolicyholder.click();
        sexPolicyholder.click();
        insertValue(passportSeries, arguments.get(7));
        insertValue(passportNumber, arguments.get(8));
        insertValue(documentDate, arguments.get(9));
        insertValue(documentIssue, arguments.get(10));

        //проверка корректности заполнения формы тестовыми значениями
        Assertions.assertAll("input values",
                () -> Assertions.assertEquals(arguments.get(0), lastnameInsured.getAttribute("value"),
                        "Ошибка ввода фамилии застрахованного"),
                () -> Assertions.assertEquals(arguments.get(1), nameInsured.getAttribute("value"),
                        "Ошибка ввода имени застрахованного"),
                () -> Assertions.assertEquals(arguments.get(2), birthdayInsured.getAttribute("value"),
                        "Ошибка ввода даты рождения застрахованного"),
                () -> Assertions.assertEquals(arguments.get(3), lastnamePolicyholder.getAttribute("value"),
                        "Ошибка ввода фамилии страхователя"),
                () -> Assertions.assertEquals(arguments.get(4), namePolicyholder.getAttribute("value"),
                        "Ошибка ввода имени страхователя"),
                () -> Assertions.assertEquals(arguments.get(5), middlenamePolicyholder.getAttribute("value"),
                        "Ошибка ввода отчества страхователя"),
                () -> Assertions.assertEquals(arguments.get(6), birthdayPolicyholder.getAttribute("value"),
                        "Ошибка ввода даты рождения страхователя"),
                () -> Assertions.assertEquals(arguments.get(7), passportSeries.getAttribute("value"),
                        "Ошибка ввода серии паспорта"),
                () -> Assertions.assertEquals(arguments.get(8), passportNumber.getAttribute("value"),
                        "Ошибка ввода номера паспорта"),
                () -> Assertions.assertEquals(arguments.get(9), documentDate.getAttribute("value"),
                        "Ошибка ввода даты выдачи паспорта"),
                () -> Assertions.assertEquals(arguments.get(10), documentIssue.getAttribute("value"),
                        "Ошибка ввода поля 'Кем выдан'")
                );

        //отправка и проверка сообщения об ошибке
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();
        WebElement errorMessage = driver.findElement(By.xpath("//div[@class='alert-form alert-form-error']"));
        Assertions.assertEquals("При заполнении данных произошла ошибка", errorMessage.getText(),
                "С сообщением ошибки что-то не так");
    }

    //аргументы
    static Stream<List<String>> argsListProvider() {
        return Stream.of(
                Arrays.asList(
                        "Иванов",           // #0   lastnameInsured
                        "Иван",             // #1   nameInsured
                        "20.03.1990",       // #2   birthdayInsured
                        "Петров",           // #3   lastnamePolicyholder
                        "Максим",           // #4   namePolicyholder
                        "Андреевич",        // #5   middlenamePolicyholder
                        "01.01.1980",       // #6   birthdayPolicyholder
                        "4608",             // #7   passportSeries
                        "255955",           // #8   passportNumber
                        "30.05.2005",       // #9   documentDate
                        "лол кек чебурек"   // #10   documentIssue
                ),
                Arrays.asList(
                        "Йцукен",
                        "Кверти",
                        "20.03.1980",
                        "Гнеку",
                        "Макукерсим",
                        "Андррпавыеевич",
                        "01.01.1995",
                        "4618",
                        "255975",
                        "30.11.2015",
                        "лол кек чебуреклол кек чебурек"
                ),
                Arrays.asList(
                        "Сидоров",
                        "Артем",
                        "30.03.1980",
                        "Орлов",
                        "Кирилл",
                        "Олегович",
                        "20.01.1993",
                        "5008",
                        "111955",
                        "20.20.2014",
                        "лол кек чебуреклол кек чебуреклол кек чебурек"
                )
        );
    }

    //метод ввода данных
    static void insertValue(WebElement element, String value) throws InterruptedException {
        element.click();
        for (int j = 0; j < 5; j++) {
            element.clear();
            for (int i = 0; i < value.length(); i++) {
                element.sendKeys(String.valueOf(value.charAt(i)));
                Thread.sleep(10);
            }
            if (element.getAttribute("value").equals(value))
                break;
        }
    }

    @AfterEach
    void testClose() {

    }

    @AfterAll
    static void closeTests() {
        driver.quit();
    }
}
