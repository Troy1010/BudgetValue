package android.util;
// Intentionally changed this^ so that Log doesn't cause unit-test crashes.

public class Log {
    public static int d(String tag, String msg) {
        System.out.println(msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.println(msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.println(msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.println(msg);
        return 0;
    }
}
