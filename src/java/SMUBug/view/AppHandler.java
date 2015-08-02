/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SMUBug.view;

import SMUBug.server.Bug;
import SMUBug.server.BugReport;
import SMUBug.server.BugSummary;
import SMUBug.server.CriteriaType;
import SMUBug.server.QueryBugDB;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
@SessionScoped
public class AppHandler implements Serializable {

    private UserView user;
    private BugSummaryTableView bugs;
    private LineChartModel areaModel;
    private PieChartModel pieModel1;
    private PieChartModel pieModel2;
    private CriteriaView criteria;
    private BugReport bugReport;
    private BugSummary selectedBug;
    private HorizontalBarChartModel barModel;

    public AppHandler() {
        this.user = new UserView();
        this.bugs = new BugSummaryTableView();
    }

    public UserView getUser() {
        return user;
    }

    public void setUser(UserView user) {
        this.user = user;
    }

    public BugSummaryTableView getBugs() {
        return bugs;
    }

    public void setBugs(BugSummaryTableView bugs) {
        this.bugs = bugs;
    }

    public LineChartModel getAreaModel() {
        return areaModel;
    }

    public void setAreaModel(LineChartModel areaModel) {
        this.areaModel = areaModel;
    }

    public CriteriaView getCriteria() {
        return criteria;
    }

    public void setCriteria(CriteriaView criteria) {
        this.criteria = criteria;
    }

    public PieChartModel getPieModel1() {
        return pieModel1;
    }

    public void setPieModel1(PieChartModel pieModel1) {
        this.pieModel1 = pieModel1;
    }

    public PieChartModel getPieModel2() {
        return pieModel2;
    }

    public void setPieModel2(PieChartModel pieModel2) {
        this.pieModel2 = pieModel2;
    }

    public BugReport getBugReport() {
        return bugReport;
    }

    public void setBugReport(BugReport bugReport) {
        this.bugReport = bugReport;
    }

    public BugSummary getSelectedBug() {
        return selectedBug;
    }

    public void setSelectedBug(BugSummary selectedBug) {
        this.selectedBug = selectedBug;
    }

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(HorizontalBarChartModel barModel) {
        this.barModel = barModel;
    }

    public void click() throws IOException, Exception {
        init();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/SMUBug/faces/stats.xhtml");
    }

    public void generate() throws IOException {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(user.getStartDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(user.getEndDate());
        Map<CriteriaType, String> criteriaMap = new HashMap<CriteriaType, String>();
        bugs = new BugSummaryTableView();
        criteriaMap.put(CriteriaType.RELEASE, criteria.getRelease());
        criteriaMap.put(CriteriaType.BUGTYPE, criteria.getType());
        criteriaMap.put(CriteriaType.TAG, criteria.getTag());
        Map<String, Integer[]> map = bugReport.getBugSummary(cal1, cal2, criteriaMap);
        int i = 1;
        for (String s : map.keySet()) {
            String[] sa = s.split(" to ");
            Integer[] value = map.get(s);
            BugSummary bs = new BugSummary(i++, sa[0], sa[1], value[0], value[1]);
            bs.setReportedList(getBugDetail(toCalendar(sa[0]), toCalendar(sa[1])).get(0));
            bs.setFixedList(getBugDetail(toCalendar(sa[0]), toCalendar(sa[1])).get(1));
            bugs.addBug(bs);
        }
        createAreaModel();
        createBarModel();
        createPieModel1();
        createPieModel2();
        //RequestContext.getCurrentInstance().update("form:bugSum areaChart");
        FacesContext.getCurrentInstance().getExternalContext().redirect("/SMUBug/faces/stats.xhtml");
    }

    private void createAreaModel() {
        areaModel = new LineChartModel();
        LineChartSeries report = new LineChartSeries();
        //report.setFill(true);
        report.setLabel("Reported");
        LineChartSeries fix = new LineChartSeries();
        //fix.setFill(true);
        fix.setLabel("Fixed");
        for (BugSummary bs : bugs.getBugs()) {
            report.set(bs.getWeekNum(), bs.getReportedNum());
            fix.set(bs.getWeekNum(), bs.getFixedNum());
        }

        areaModel.addSeries(report);
        areaModel.addSeries(fix);

        areaModel.setTitle("Weekly Report Trend Chart");
        areaModel.setLegendPosition("nw");
        //areaModel.setStacked(true);
        areaModel.setShowPointLabels(true);

        Axis xAxis = new CategoryAxis("Week");
        areaModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        yAxis.setLabel("Bug Counts");
    }

    private void createBarModel() {
        barModel = new HorizontalBarChartModel();
        ChartSeries assignee = new ChartSeries();
        assignee.setLabel("Assignee");
        Map<CriteriaType, String> criteriaMap = new HashMap<CriteriaType, String>();
        criteriaMap.put(CriteriaType.TAG, criteria.getTag());
        Map<String, Integer> map = bugReport.countBugByAssinee(criteriaMap);
        for (String s : map.keySet()) {
            assignee.set(s, map.get(s));
        }
        barModel.addSeries(assignee);
        String title = criteria.getTag().equals("NONE") ? "ALL" : criteria.getTag();
        barModel.setTitle(String.format("Open Bug Assigment Chart (%s)", title));
        barModel.setLegendPosition("ne");

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Bug Counts");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Members");
    }

    private void createPieModel1() {
        pieModel1 = new PieChartModel();
        Map<CriteriaType, String> criteriaMap = new HashMap<CriteriaType, String>();
        criteriaMap.put(CriteriaType.TAG, criteria.getTag());
        pieModel1.set("status = 11", bugReport.getNumByStatus(11, criteriaMap));
        pieModel1.set("status = 13", bugReport.getNumByStatus(13, criteriaMap));
        pieModel1.set("status = 15", bugReport.getNumByStatus(15, criteriaMap));
        pieModel1.set("other", bugReport.getOpenNum(criteriaMap) - bugReport.getNumByStatus(11, criteriaMap) - bugReport.getNumByStatus(13, criteriaMap) - bugReport.getNumByStatus(15, criteriaMap));
        String title = criteria.getTag().equals("NONE") ? "ALL" : criteria.getTag();
        pieModel1.setTitle(String.format("Open Bug Status (%s)", title));
        pieModel1.setShowDataLabels(true);
        pieModel1.setDataFormat("value");
        pieModel1.setLegendPosition("w");
    }

    private void createPieModel2() {
        pieModel2 = new PieChartModel();
        Map<CriteriaType, String> criteriaMap = new HashMap<CriteriaType, String>();
        criteriaMap.put(CriteriaType.TAG, criteria.getTag());
        pieModel2.set("Open Bugs", bugReport.getOpenNum(criteriaMap));
        pieModel2.set("Closed Bugs", bugReport.getClosedNum(criteriaMap));
        pieModel2.set("Fixed Bugs", bugReport.getFixedNum(criteriaMap));
        pieModel2.set("Other", bugReport.getTotalNum(criteriaMap) - bugReport.getOpenNum(criteriaMap) - bugReport.getClosedNum(criteriaMap) - bugReport.getFixedNum(criteriaMap));

        String title = criteria.getTag().equals("NONE") ? "ALL" : criteria.getTag();
        pieModel2.setTitle(String.format("Bug Distribution (%s)", title));
        pieModel2.setShowDataLabels(true);
        pieModel2.setDataFormat("value");
        pieModel2.setLegendPosition("w");
    }

    public List<List<Bug>> getBugDetail(Calendar cal1, Calendar cal2) {
        if (criteria == null) {
            return bugReport.getBugDetail(cal1, cal2);
        } else {
            Map<CriteriaType, String> criteriaMap = new HashMap<CriteriaType, String>();
            criteriaMap.put(CriteriaType.RELEASE, criteria.getRelease());
            criteriaMap.put(CriteriaType.BUGTYPE, criteria.getType());
            criteriaMap.put(CriteriaType.TAG, criteria.getTag());
            return bugReport.getBugDetail(cal1, cal2, criteriaMap);
        }
    }

    private Calendar toCalendar(String s) {
        String[] sa1 = s.split("/");
        int year = Integer.parseInt(sa1[2]);
        int month = Integer.parseInt(sa1[0]);
        int day = Integer.parseInt(sa1[1]);
        return new GregorianCalendar(year, month - 1, day);
    }

    private void init() throws Exception {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(user.getStartDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(user.getEndDate());
        QueryBugDB qbd = new QueryBugDB();
        bugReport = qbd.generateBugReport(user.getName(), user.getPassword());
        bugs = new BugSummaryTableView();
        criteria = new CriteriaView();
        criteria.setRelease("ALL");
        criteria.setType("ALL");
        criteria.setTag("NONE");
        Map<String, Integer[]> map = bugReport.getBugSummary(cal1, cal2);
        int i = 1;
        for (String s : map.keySet()) {
            String[] sa = s.split(" to ");
            Integer[] value = map.get(s);
            BugSummary bs = new BugSummary(i++, sa[0], sa[1], value[0], value[1]);
            bs.setReportedList(getBugDetail(toCalendar(sa[0]), toCalendar(sa[1])).get(0));
            bs.setFixedList(getBugDetail(toCalendar(sa[0]), toCalendar(sa[1])).get(1));
            bugs.addBug(bs);
        }
        createAreaModel();
        createBarModel();
        createPieModel1();
        createPieModel2();
    }
}
