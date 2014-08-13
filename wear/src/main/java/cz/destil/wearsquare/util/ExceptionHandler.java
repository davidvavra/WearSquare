package cz.destil.wearsquare.util;

import android.os.Build;

import com.mariux.teleport.lib.TeleportClient;
import com.mariux.teleport.lib.TeleportService;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.destil.wearsquare.BuildConfig;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.event.ExceptionEvent;

/**
 * Sends exceptions to mobile phone before crashing.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    public ExceptionHandler(Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
        mDefaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        App.bus().post(new ExceptionEvent(ex));
        mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
    }

    public static void sendExceptionToPhone(Throwable exception, TeleportClient mTeleportClient) {
        mTeleportClient.sendMessage(buildMessageText(exception), null);
    }

    public static void sendExceptionToPhone(Throwable exception, TeleportService teleportService) {
        teleportService.sendMessage(buildMessageText(exception), null);
    }

    private static String buildMessageText(Throwable exception) {
        String message = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " " + " v" + BuildConfig.VERSION_NAME;
        message += "\n\n";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        message += sw.toString();
        return "exception:" + message;
    }
}