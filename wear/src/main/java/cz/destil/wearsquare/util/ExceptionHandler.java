package cz.destil.wearsquare.util;

import android.os.Build;

import com.mariux.teleport.lib.TeleportClient;
import com.mariux.teleport.lib.TeleportService;

import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.event.ExceptionEvent;
import cz.destil.wearsquare.service.ListenerService;

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
        String message = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " " + Build.DEVICE;
        message += "\n\n";
        message += exception.toString();
        message += "\n";
        for (StackTraceElement element : exception.getStackTrace()) {
            message += element.toString() + "\n";
        }
        return "exception:"+message;
    }
}