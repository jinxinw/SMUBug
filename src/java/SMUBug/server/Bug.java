package SMUBug.server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Bug implements Serializable {

    private int lineNum;
    private int id;
    private int status;
    private Calendar reportedDate;
    private Calendar fixedDate;
    private String assignee;
    private String release;
    private String subject;
    private String tag;
    private String srStatus;
    private String urlPrefix = "https://bug.oraclecorp.com/pls/bug/webbug_edit.edit_info_top?rptno=";
    private String srUrlPrefix = "https://bug.oraclecorp.com/pls/bug/customer_bugs_ui.search?prptno=";

    public Bug() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getReportedDate() {
        return reportedDate;
    }

    public String getReportedDateVal() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(reportedDate.getTime());
    }

    public void setReportedDate(Calendar reportedDate) {
        this.reportedDate = reportedDate;
    }

    public Calendar getFixedDate() {
        return fixedDate;
    }

    public String getFixedDateVal() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(fixedDate.getTime());
    }

    public void setFixedDate(Calendar fixedDate) {
        this.fixedDate = fixedDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSrStatus() {
        return srStatus;
    }

    public void setSrStatus(String srStatus) {
        this.srStatus = srStatus;
    }

    public String getURL() {
        return urlPrefix + String.valueOf(id);
    }

    public String getSrURL() {
        return srUrlPrefix + String.valueOf(id);
    }

    @Override
    public String toString() {
        String start = new SimpleDateFormat("MM/dd/yyyy").format(reportedDate.getTime());
        String end = fixedDate == null ? "NULL" : new SimpleDateFormat("MM/dd/yyyy").format(fixedDate.getTime());
        return "Bug{" + "lineNum=" + lineNum + ", id=" + id + ", status=" + status + ", reportedDate=" + start + ", fixedDate=" + end + ", assignee=" + assignee + ", release=" + release + ", subject=" + subject + ", tag=" + tag + ", srStatus=" + srStatus + ", urlPrefix=" + urlPrefix + ", srUrlPrefix=" + srUrlPrefix + '}';
    }

}
