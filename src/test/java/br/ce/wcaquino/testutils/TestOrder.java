package br.ce.wcaquino.testutils;

public class TestOrder {

    private static StringBuffer buffer = new StringBuffer();

    public static void appendInit() {
        var className = Thread.currentThread().getStackTrace()[2].getClassName();
        var testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        var s = String.format("init %s %s", className, testName);
//        System.out.println(s);
        buffer.append("\n" + s);
    }

    public static void appendFinish() {
        var className = Thread.currentThread().getStackTrace()[2].getClassName();
        var testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        var s = String.format("finish %s %s", className, testName);
//        System.out.println(s);
        buffer.append("\n" + s);
    }

    public static void flush() {
        System.out.println(buffer.toString());
    }

}
