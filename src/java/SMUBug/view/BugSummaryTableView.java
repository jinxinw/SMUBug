package SMUBug.view;

import SMUBug.server.BugSummary;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class BugSummaryTableView implements Serializable {

    private List<BugSummary> bugs;

    public BugSummaryTableView() {
        bugs = new ArrayList<>();
    }

    public List<BugSummary> getBugs() {
        return bugs;
    }

    public void setBugs(List<BugSummary> bugs) {
        this.bugs = bugs;
    }

    public void addBug(BugSummary bug) {
        this.bugs.add(bug);
    }

    public double getWeeklyReportRate() {
        double sum = 0;
        for (BugSummary bs : bugs) {
            sum += bs.getReportedNum();
        }
        Calendar cal1 = toCalendar(bugs.get(0).getWeekStart());
        Calendar cal2 = toCalendar(bugs.get(bugs.size() - 1).getWeekEnd());
        long diffInMillisec = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        int diffInDays = (int) (diffInMillisec / (24 * 60 * 60 * 1000));
        double value = sum * 7.0 / diffInDays;
        value = Double.parseDouble(new DecimalFormat("##.##").format(value));
        return value;
    }

    public double getWeeklyFixRate() {
        double sum = 0;
        for (BugSummary bs : bugs) {
            sum += bs.getFixedNum();
        }
        Calendar cal1 = toCalendar(bugs.get(0).getWeekStart());
        Calendar cal2 = toCalendar(bugs.get(bugs.size() - 1).getWeekEnd());
        long diffInMillisec = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        int diffInDays = (int) (diffInMillisec / (24 * 60 * 60 * 1000));
        double value = sum * 7.0 / diffInDays;
        value = Double.parseDouble(new DecimalFormat("##.##").format(value));
        return value;
    }

    public int getTotalReportNum() {
        int sum = 0;
        for (BugSummary bs : bugs) {
            sum += bs.getReportedNum();
        }
        return sum;
    }

    public int getTotalFixNum() {
        int sum = 0;
        for (BugSummary bs : bugs) {
            sum += bs.getFixedNum();
        }
        return sum;
    }

    public int getTimePeriod() {
        Calendar cal1 = toCalendar(bugs.get(0).getWeekStart());
        Calendar cal2 = toCalendar(bugs.get(bugs.size() - 1).getWeekEnd());
        long diffInMillisec = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        int diffInDays = (int) (diffInMillisec / (24 * 60 * 60 * 1000));
        return diffInDays;
    }

    public int getTotalWeek() {
        return getTimePeriod() / 7 + 1;
    }

    private Calendar toCalendar(String s) {
        String[] sa1 = s.split("/");
        int year = Integer.parseInt(sa1[2]);
        int month = Integer.parseInt(sa1[0]);
        int day = Integer.parseInt(sa1[1]);
        return new GregorianCalendar(year, month - 1, day);
    }
}
