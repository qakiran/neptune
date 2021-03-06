package ru.tinkoff.qa.neptune.selenium.functions.target.locator.frame;

import ru.tinkoff.qa.neptune.selenium.functions.target.locator.SwitchesToItself;
import org.openqa.selenium.*;
import org.openqa.selenium.internal.WrapsElement;

import static java.lang.String.format;

public class Frame implements SwitchesToItself {
    private final WebDriver webDriver;
    private final Object frame;

    Frame(WebDriver webDriver, Object frame) {
        this.webDriver = webDriver;
        this.frame = frame;
        try {
            switchToMe();
        }
        finally {
            webDriver.switchTo().parentFrame();
        }
    }

    @Override
    public void switchToMe() {
        Class<?> clazz = frame.getClass();
        if (Integer.class.isAssignableFrom(clazz)) {
            webDriver.switchTo().frame((Integer) frame);
            return;
        }

        if (String.class.isAssignableFrom(clazz)) {
            webDriver.switchTo().frame((String) frame);
            return;
        }

        if (WebElement.class.isAssignableFrom(clazz)) {
            webDriver.switchTo().frame((WebElement) frame);
            return;
        }

        if (WrapsElement.class.isAssignableFrom(clazz)) {
            webDriver.switchTo().frame(((WrapsElement) frame).getWrappedElement());
        }
    }

    public String toString() {
        return format("frame %s", frame);
    }
}
