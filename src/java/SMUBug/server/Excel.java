package SMUBug.server;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Excel {

    private WritableWorkbook book;

    public Excel() {
        try {
            book = createExcel();
        } catch (IOException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
                Calendar cal1 = Calendar.getInstance();
        cal1.set(2014, Calendar.NOVEMBER, 2);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2014, Calendar.NOVEMBER, 2);
        System.out.println(cal1.before(cal2));
        System.out.println(cal1.after(cal2));
        System.out.println(cal1.equals(cal2));
    }

    public WritableWorkbook createExcel() throws IOException {
        File file = new File("bug_stat.xls");
        file.delete();
        WritableWorkbook book = Workbook.createWorkbook(new File("bug_stat.xls"));
        return book;
    }

    public void writeContent(List<Map<String, Integer>> list, List<String> titles, WritableSheet sheet) throws WriteException, IOException {

        sheet.addCell(new Label(0, 0, "Week Start"));
        sheet.addCell(new Label(1, 0, "Week End"));
        for (int i = 0; i < titles.size(); i++) {
            sheet.addCell(new Label(2 + i, 0, titles.get(i)));
        }
        int j = 1;
        for (String s : list.get(0).keySet()) {
            String[] sa = s.split(" to ");
            sheet.addCell(new Label(0, j, sa[0]));
            sheet.addCell(new Label(1, j, sa[1]));
            j++;
        }
        for (int i = 0; i < list.size(); i++) {
            int k = 1;
            for (String s : list.get(i).keySet()) {
                sheet.addCell(new Number(2 + i, k, list.get(i).get(s)));
                k++;
            }
        }
    }

    public void addSheet(List<Map<String, Integer>> list, List<String> titles, String version, int index) throws Exception {
        writeContent(list, titles, book.createSheet(version, 0));
    }

    public void writeBook() throws Exception {
        book.write();
        book.close();
    }
}