package com.daviipkp.smartstevex.services;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

@Service
public class KeyboardService {

    private Robot robot;
    private final boolean isHeadless;

    public KeyboardService() {
        this.isHeadless = GraphicsEnvironment.isHeadless();

        if (this.isHeadless) {
            System.out.println("Headless environment detected!");
            this.robot = null;
        } else {
            try {
                this.robot = new Robot();
                this.robot.setAutoDelay(40);
            } catch (AWTException e) {
                System.err.println("Error " + e.getMessage());
                this.robot = null;
            }
        }
    }

    public void typeText(String text) {
        if (this.isHeadless || this.robot == null) {
            System.out.println("KeyboardService ignored 'typeText'.");
            return;
        }

        StringSelection s = new StringSelection(text);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(s, s);
        try {
            robot.keyPress(KeyEvent.VK_CONTROL);
            clickButton(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickButton(int key) {
        if (this.isHeadless || this.robot == null) {
            return;
        }
        
        robot.keyPress(key);
        robot.keyRelease(key);
    }
}