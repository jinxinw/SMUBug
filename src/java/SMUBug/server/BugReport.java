package SMUBug.server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BugReport implements Serializable {

    private List<Bug> bugReport;
    private int[] bugStatus = {11, 30, 31, 32, 60, 90, 91, 92, 96, 80, 25};
    private int[] fixStatus = {20, 31, 32, 33, 35, 36, 37, 43, 44, 45, 53, 80, 81, 82, 87, 89};
    private int[] openStatus = {10, 11, 12, 13, 15, 14, 16, 17, 19, 21, 22, 23, 24, 25, 26, 30, 39, 40, 51, 52};
    private int[] closedStatus = {70, 71, 72, 73, 74, 75, 76, 77, 78, 83, 84, 85, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99};
    private String[] members = {"CHACHCHE", "OWHALL", "VIJIN", "VKALIMUT", "JEOKIM", "HUAL", "JPANEM", "WENSHI", "ZEXU", "XINRYU", "RESANDER", "YAFLI"};

    public BugReport(List<Bug> bugReport) {
        this.bugReport = bugReport;
    }

    public BugReport() {
    }

    public List<Bug> getBugReport() {
        return bugReport;
    }

    public void setBugReport(List<Bug> bugReport) {
        this.bugReport = bugReport;
    }

    public Map<String, Integer[]> getBugSummary(Calendar startDate, Calendar endDate, Map<CriteriaType, String> criteria) {
        Map<String, Integer[]> map = new LinkedHashMap<String, Integer[]>();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal1 = (Calendar) startDate.clone();
        Calendar cal2 = (Calendar) startDate.clone();
        cal2.add(Calendar.WEEK_OF_YEAR, 1);
        while (cal1.before(endDate)) {
            if (cal2.after(endDate)) {
                cal2 = (Calendar) endDate.clone();
            }
            String key = String.format("%s to %s", format.format(cal1.getTime()), format.format(cal2.getTime()));
            int[] value = countBugs(bugReport, cal1, cal2, criteria);
            map.put(key, toIntegerArray(value));
            cal1.add(Calendar.WEEK_OF_YEAR, 1);
            cal2.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return map;
    }

    public Map<String, Integer[]> getBugSummary(Calendar startDate, Calendar endDate) {
        Map<CriteriaType, String> criteria = new HashMap<CriteriaType, String>();
        return getBugSummary(startDate, endDate, criteria);
    }

    private int[] countBugs(List<Bug> bugs, Calendar cal1, Calendar cal2, Map<CriteriaType, String> criteria) {
        int report = 0;
        int fix = 0;
        int[] res = new int[2];
        for (Bug b : bugs) {
            if (matchCriteria(b, criteria)) {
                if (b.getReportedDate().after(cal1) && b.getReportedDate().before(cal2) || b.getReportedDate().equals(cal1)) {
                    report++;
                }
                if (b.getFixedDate() != null) {
                    if (b.getFixedDate().after(cal1) && b.getFixedDate().before(cal2) || b.getFixedDate().equals(cal1)) {
                        fix++;
                    }
                }
            }
        }
        res[0] = report;
        res[1] = fix;
        return res;
    }

    private Integer[] toIntegerArray(int[] intArray) {
        Integer[] result = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = Integer.valueOf(intArray[i]);
        }
        return result;
    }

    private boolean matchCriteria(Bug bug, Map<CriteriaType, String> criteria) {
        boolean res = true;
        for (CriteriaType ct : criteria.keySet()) {
            if (ct == CriteriaType.RELEASE) {
                if (criteria.get(ct).equalsIgnoreCase("ALL")) {
                    res = res && true;
                } else {
                    res = res && bug.getRelease().startsWith(criteria.get(ct));
                }
            }
            if (ct == CriteriaType.BUGTYPE) {
                if (criteria.get(ct).equalsIgnoreCase("BUG")) {
                    res = res && containsStatus(bugStatus, bug.getStatus());
                } else if (criteria.get(ct).equalsIgnoreCase("ER")) {
                    res = res && !containsStatus(bugStatus, bug.getStatus());
                } else {
                    res = res && true;
                }
            }
            if (ct == CriteriaType.TAG) {
                if (criteria.get(ct).equalsIgnoreCase("NONE")) {
                    res = res && true;
                } else {
                    res = res && bug.getTag().contains(criteria.get(ct));
                }
            }
        }
        return res;
    }

    private boolean containsStatus(int[] a, int status) {
        for (int i : a) {
            if (status == i) {
                return true;
            }
        }
        return false;
    }

    public int getTotalNum(Map<CriteriaType, String> criteria) {
        int i = 0;
        for (Bug b : bugReport) {
            if (matchCriteria(b, criteria)) {
                i++;
            }
        }
        return i;
    }

    public int getNumByStatus(int status, Map<CriteriaType, String> criteria) {
        int i = 0;
        for (Bug b : bugReport) {
            if (b.getStatus() == status && matchCriteria(b, criteria)) {
                i++;
            }
        }
        return i;
    }

    public int getOpenNum(Map<CriteriaType, String> criteria) {
        int i = 0;
        for (Bug b : bugReport) {
            if (containsStatus(openStatus, b.getStatus()) && matchCriteria(b, criteria)) {
                i++;
            }
        }
        return i;
    }

    public int getClosedNum(Map<CriteriaType, String> criteria) {
        int i = 0;
        for (Bug b : bugReport) {
            if (containsStatus(closedStatus, b.getStatus()) && matchCriteria(b, criteria)) {
                i++;
            }
        }
        return i;
    }

    public int getFixedNum(Map<CriteriaType, String> criteria) {
        int i = 0;
        for (Bug b : bugReport) {
            if (containsStatus(fixStatus, b.getStatus()) && matchCriteria(b, criteria)) {
                i++;
            }
        }
        return i;
    }

    public List<List<Bug>> getBugDetail(Calendar cal1, Calendar cal2, Map<CriteriaType, String> criteria) {
        List<List<Bug>> res = new ArrayList<List<Bug>>();
        List<Bug> report = new ArrayList<Bug>();
        List<Bug> fix = new ArrayList<Bug>();
        for (Bug b : bugReport) {
            if (matchCriteria(b, criteria)) {
                //System.out.println(b.getReportedDate().getTime() + " " + cal1.getTime() + " " + cal2.getTime());
                if (b.getReportedDate().after(cal1) && b.getReportedDate().before(cal2) || b.getReportedDate().equals(cal1)) {
                    report.add(b);
                }
                if (b.getFixedDate() != null) {
                    if (b.getFixedDate().after(cal1) && b.getFixedDate().before(cal2) || b.getFixedDate().equals(cal1)) {
                        fix.add(b);
                    }
                }
            }
        }
        res.add(report);
        res.add(fix);
        return res;
    }

    public List<List<Bug>> getBugDetail(Calendar cal1, Calendar cal2) {
        Map<CriteriaType, String> criteria = new HashMap<CriteriaType, String>();
        return getBugDetail(cal1, cal2, criteria);
    }

    public Map<String, Integer> countBugByAssinee(Map<CriteriaType, String> criteria) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        int[] counts = new int[members.length];
        for (Bug b : bugReport) {
            for (int i = 0; i < members.length; i++) {
                if (b.getAssignee().equals(members[i]) && containsStatus(openStatus, b.getStatus()) && matchCriteria(b, criteria)) {
                    counts[i]++;
                }
            }
        }
        for (int j = 0; j < members.length; j++) {
            map.put(members[j], counts[j]);
        }
        return map;
    }
}
