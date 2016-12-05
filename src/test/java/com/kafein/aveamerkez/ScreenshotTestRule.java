package com.kafein.aveamerkez;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.FileOutputStream;

import static com.kafein.aveamerkez.AveaMerkez.driver;

/**
 * Created by AUT via kafein on 05.12.2016.
 */
class ScreenshotTestRule implements MethodRule {
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                    if (!frameworkMethod.getName().contains("kalan"))
                    captureScreenshot(frameworkMethod.getName()+"_SUCCESS");
                } catch (Throwable t) {
                    captureScreenshot(frameworkMethod.getName()+"_FAIL");
                    throw t; // rethrow to allow the failure to be reported to JUnit
                }
            }

            public void captureScreenshot(String fileName) {
                try {
                    new File("C:\\Logs").mkdirs(); // Insure directory is there
                    FileOutputStream out = new FileOutputStream("C:\\Logs\\" + fileName + ".png");
                    out.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
                    out.close();
                } catch (Exception e) {
                    // No need to crash the tests if the screenshot fails
                }
            }
        };
    }
}
