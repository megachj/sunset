package sunset.java.etc.excel;

public class Application {

    public static void main(String[] args) throws Exception {
        ExcelHandlerImpl handler = new ExcelHandlerImpl("input.xlsx", "output.txt");
        handler.handle();
    }
}
