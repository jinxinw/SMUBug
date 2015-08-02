package SMUBug.server;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class BugSummary implements Serializable {

    private int weekNum;
    private String weekStart;
    private String weekEnd;
    private int reportedNum;
    private int fixedNum;
    private List<Bug> reportedList;
    private List<Bug> fixedList;

    public BugSummary(int weekNum, String weekStart, String weekEnd, int reportedNum, int fixedNum) {
        this.weekNum = weekNum;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.reportedNum = reportedNum;
        this.fixedNum = fixedNum;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public String getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(String weekStart) {
        this.weekStart = weekStart;
    }

    public String getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(String weekEnd) {
        this.weekEnd = weekEnd;
    }

    public int getReportedNum() {
        return reportedNum;
    }

    public void setReportedNum(int reportedNum) {
        this.reportedNum = reportedNum;
    }

    public int getFixedNum() {
        return fixedNum;
    }

    public void setFixedNum(int fixedNum) {
        this.fixedNum = fixedNum;
    }

    public List<Bug> getReportedList() {
        return reportedList;
    }

    public void setReportedList(List<Bug> reportedList) {
        this.reportedList = reportedList;
    }

    public List<Bug> getFixedList() {
        return fixedList;
    }

    public void setFixedList(List<Bug> fixedList) {
        this.fixedList = fixedList;
    }

}
