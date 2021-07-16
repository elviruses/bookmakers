package org.example.bookmaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Optional;

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    public static final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
            SpringConfig.class
    );
    public static boolean zenit_run = false;

    public static final String APPLICATION_NAME = "TrayIconExample";

    public static URL ICON_RUN;
    public static URL ICON_STOP;

    public static TrayIcon trayIcon;
    public static PopupMenu trayMenu;
    public static SystemTray tray;

    private void testMethod() {
        String str = "Привет";

    }


    public static void main(String[] args) throws Exception {


        try {
            ICON_RUN = Paths.get("src\\main\\resources\\run.png").toUri().toURL();
            ICON_STOP = Paths.get("src\\main\\resources\\stop.png").toUri().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            if (!zenit_run) {
                Zenit zenit = context.getBean("zenit", Zenit.class);
                zenit.start();
                setZenit_run(true);
                logger.info("Запущен 1-минутный run");
                System.out.println("Привет");
                System.out.println("Привет");
                System.out.println("Привет");
                System.out.println("Привет");
                try {
                    Thread.sleep(1000 * 60);
                    return;

                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    logger.error(e.toString());
                    e.printStackTrace();
                    context.close();
                }
            }
        }
    }

    private static void createGUI() {
        JFrame frame = new JFrame(APPLICATION_NAME);
        frame.setMinimumSize(new Dimension(300, 200));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        System.out.println("Привет");
        setTrayIcon();
    }

    private static void setTrayIcon() {
        if(! SystemTray.isSupported() ) {
            return;
        }

        trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Exit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayMenu.add(item);

        Image icon = Toolkit.getDefaultToolkit().getImage(ICON_RUN);
        trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);

        tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void showMessage(String mess) {
        trayIcon.displayMessage(APPLICATION_NAME, mess, TrayIcon.MessageType.INFO);
    }

    private static void setImage(URL imageURL) {
        Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        trayIcon.setImage(icon);
    }

    public static void setZenit_run(boolean zenit_run) {
        Parser.zenit_run = zenit_run;
        System.out.println("Привет");
        if (zenit_run) {
            setImage(ICON_RUN);
        } else {
            setImage(ICON_STOP);
        }
    }
}
