package com.olx.pages;

import com.olx.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import java.util.Map;
import java.time.Duration;

public class HomePage {
	
	private final WebDriver driver;
    private static final String URL = "https://www.olx.in/";
    
    public HomePage() {
        // Grabs the specific driver for the current parallel thread
        this.driver = WebDriverFactory.getDriver();
    }
    
    public void open() {
        driver.get(URL);
    }
    
    public String getTitle() {
        return driver.getTitle();
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public List<String> getClickableLinks() {
        return (List<String>) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('a'))"
                        + ".filter(link => {"
                        + "const style = window.getComputedStyle(link);"
                        + "return link.href && link.getClientRects().length > 0"
                        + " && style.display !== 'none' && style.visibility !== 'hidden';"
                        + "})"
                        + ".map(link => {"
                        + "const text = (link.innerText || link.getAttribute('aria-label') || '').trim();"
                        + "return text ? text + ' (' + link.href + ')' : link.href;"
                        + "});"
        );
    }

    public void clickHeaderAccountIcon() {
        clickFirstAvailable(
                By.cssSelector("button[aria-label*='account' i], button[aria-label*='profile' i], button[aria-label*='login' i], a[aria-label*='account' i], a[aria-label*='profile' i], a[aria-label*='login' i]"),
                By.xpath("//*[self::button or self::a][contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]")
        );
    }

    public void clickContinueWithPhone() {
        clickFirstAvailable(By.xpath("//*[self::button or self::a][translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='continue with phone']"));
    }

    public void enterPhoneNumber(String phoneNumber) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='tel'], input[name*='phone' i], input[placeholder*='phone' i], input[placeholder*='mobile' i]")));
        phoneInput.clear();
        phoneInput.sendKeys(phoneNumber);
    }

    public void clickNext() {
        clickFirstAvailable(By.xpath("//button[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='next']"),
                By.cssSelector("input[type='submit'][value='Next'], input[type='button'][value='Next']"));
    }

    public void clickForSaleHousesAndApartments() {
        clickFirstAvailable(By.xpath("//a[normalize-space(.)='For Sale: Houses & Apartments']"));
        waitForUrlSegment("c1725");
        waitForPropertyResults();
    }

    public void selectLocationInLeftPane(String location) {
        By locationLink = locationLink(location);
        if ("Indore".equalsIgnoreCase(location) && !hasVisibleLink(locationLink)) {
            clickLocationLink(locationLink("Madhya Pradesh"));
            waitForUrlSegment("madhya-pradesh_g2001162");
        }

        clickLocationLink(locationLink);
        if ("Indore".equalsIgnoreCase(location)) {
            waitForUrlSegment("indore_g4058916");
        } else if ("Nipania".equalsIgnoreCase(location)) {
            waitForUrlSegment("nipania_g1000000000002265");
        }
        waitForPropertyResults();
    }

    public void waitForPropertyResults() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver ->
                Boolean.TRUE.equals(((JavascriptExecutor) currentDriver).executeScript(
                        "return document.readyState === 'complete' && "
                                + "Array.from(document.querySelectorAll(\"a[href*='/item/']\"))"
                                + ".some(link => link.getClientRects().length > 0);"
                )));
    }

    public void sortPropertiesByPriceLowToHigh() {
        for (WebElement dropdown : driver.findElements(By.tagName("select"))) {
            Select select = new Select(dropdown);
            WebElement priceOption = select.getOptions().stream()
                    .filter(option -> {
                        String text = option.getText().toLowerCase();
                        return text.contains("price") && text.contains("low") && text.contains("high");
                    })
                    .findFirst()
                    .orElse(null);
            if (priceOption != null) {
                select.selectByVisibleText(priceOption.getText());
                waitForPropertyResults();
                return;
            }
        }

        clickAfterScroll(
                By.cssSelector("[data-aut-id='sortByCategory']"),
                By.xpath("//*[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='sort by : date published' "
                        + "or translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='sort by: date published' "
                        + "or translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='date published']"));
        clickAfterScroll(
                By.xpath("//*[translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='price: low to high' "
                        + "or translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='price low to high']"),
                By.xpath("//*[self::button or self::li or @role='option'][contains("
                        + "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'price') "
                        + "and contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'low')]"));
        waitForPropertyResults();
    }
    
    @SuppressWarnings("unchecked")
    public long clickLowestPricedProperty() {
        List<Map<String, Object>> listings = (List<Map<String, Object>>) ((JavascriptExecutor) driver).executeScript(
                "const listings = new Map();"
                        + "for (const link of document.querySelectorAll(\"a[href*='/item/']\")) {"
                        + "if (!link.getClientRects().length || getComputedStyle(link).visibility === 'hidden') continue;"
                        + "const text = (link.innerText || '').trim();"
                        + "const match = text.match(/(?:₹|Rs\\.?)\\s*([\\d,]+)/i);"
                        + "if (!match) continue;"
                        + "const price = Number(match[1].replace(/,/g, ''));"
                        + "if (!listings.has(link.href)) listings.set(link.href, {href: link.href, text, price});"
                        + "}"
                        + "return Array.from(listings.values()).sort((a, b) => a.price - b.price);"
        );

        if (listings.isEmpty()) {
            throw new org.openqa.selenium.NoSuchElementException("No visible property listings with a displayed price were found");
        }

        Map<String, Object> lowestListing = listings.get(0);
        String href = (String) lowestListing.get("href");
        String itemPath = href.substring(href.indexOf("/item/"));
        long price = ((Number) lowestListing.get("price")).longValue();
        By propertyLink = By.xpath("//a[contains(@href, '" + itemPath + "')]");
        
        // --- NEW CODE: Handle Google Vignette Ad Overlay ---
        closeGoogleVignetteAd();
        // ---------------------------------------------------

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement property = wait.until(ExpectedConditions.elementToBeClickable(propertyLink));
        
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", property);
                
        // Click using Javascript as a fallback if normal click is intercepted
        try {
            wait.until(ExpectedConditions.elementToBeClickable(propertyLink)).click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            System.out.println("Normal click intercepted, trying JS click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", property);
        }
        
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.urlContains("/item/"));
        return price;
    }

    // --- NEW METHOD TO CLOSE ADS ---
    private void closeGoogleVignetteAd() {
        try {
            // Wait max 3 seconds to see if the ad overlay appears
            WebDriverWait adWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            
            // Common locators for Google Vignette close buttons
            By adCloseButton = By.xpath("//div[contains(@id, 'google_vignette')]//div[@aria-label='Close'] | //div[contains(@id, 'google_vignette')]//button | //*[contains(@id, 'dismiss-button')]");
            
            WebElement closeBtn = adWait.until(ExpectedConditions.elementToBeClickable(adCloseButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
            System.out.println("Google Vignette ad detected and closed.");
            
            // Give it a second to disappear
            Thread.sleep(1000); 
        } catch (org.openqa.selenium.TimeoutException | InterruptedException e) {
            // Ad didn't appear or wasn't clickable, continue normally
            System.out.println("No Google Vignette ad appeared.");
        }
    }
    // -------------------------------

    private void clickAfterScroll(By... locators) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                return;
            } catch (org.openqa.selenium.TimeoutException
                    | org.openqa.selenium.ElementClickInterceptedException ignored) {
                // Try the next visible sort-control locator.
            }
        }
        throw new org.openqa.selenium.NoSuchElementException("Could not click a sort control for any supplied locator");
    }
    
    private By locationLink(String location) {
        String slug = location.toLowerCase().replace(' ', '-');
        return By.xpath("//a[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
                + "'abcdefghijklmnopqrstuvwxyz'), '" + slug + "_g') and "
                + "(normalize-space(.)='" + location + "' or starts-with(normalize-space(.), '"
                + location + " ('))]");
    }

    private void clickLocationLink(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", link);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
    
    private boolean hasVisibleLink(By locator) {
        try {
            return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            return false;
        }
    }

    private void waitForUrlSegment(String segment) {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.urlContains(segment));
    }
    
    private void clickFirstAvailable(By... locators) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return;
            } catch (org.openqa.selenium.TimeoutException ignored) {
                // Try the next accessible locator used by OLX's responsive header/login dialog.
            }
        }
        throw new org.openqa.selenium.NoSuchElementException("Could not find a clickable element for any supplied locator");
    }
} 	