package com.daviipkp.smartstevex.services;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

@Service
public class KeyboardService {

    private Robot robot;
    private boolean isHeadless;

    public KeyboardService() {
        this.isHeadless = GraphicsEnvironment.isHeadless();

        if (this.isHeadless) {
            System.out.println("no screen detected.");
            this.robot = null;
        } else {
            try {
                this.robot = new Robot();
                this.robot.setAutoDelay(40);
                System.out.println("graphics interface detected.");
                
            } catch (Throwable t) { 
                System.err.println("system informed having a screen but no connection: " + t.getMessage());

                this.isHeadless = true; 
                this.robot = null;
            }
        }
    }

    public void typeText(String text) {
        if (this.isHeadless || this.robot == null) {
            return;
        }

        try {
            StringSelection s = new StringSelection(text);
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            cb.setContents(s, s);
            robot.keyPress(KeyEvent.VK_CONTROL);
            clickButton(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (Throwable t) {
            System.err.println("error typing: " + t.getMessage());
        }
    }

    public void clickButton(int key) {
        if (this.isHeadless || this.robot == null) {
            return;
        }
        
        try {
            robot.keyPress(key);
            robot.keyRelease(key);
        } catch (Throwable t) {
            System.err.println("error trying to click button: " + t.getMessage());
        }
    }
}