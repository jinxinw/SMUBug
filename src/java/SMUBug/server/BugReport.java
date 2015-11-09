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
    //private String[] members = {"CHACHCHE", "OWHALL", "VIJIN", "VKALIMUT", "JEOKIM", "HUAL", "JPANEM", "WENSHI", "ZEXU", "XINRYU", "RESANDER", "YAFLI"};

    private final int[] SMU_BUG_DEV = {11, 13, 17, 25, 40};
    private final int[] SMU_BUG_FIX = {60, 80, 74, 83, 84, 90, 91, 92, 93, 96};
    private final int[] SMU_BUG_SENDBACK = {30, 31, 32, 36, 43, 44};

    private final int[] SMU_ER_DEV = {15, 19, 25};
    private final int[] SMU_ER_FIX = {60, 82, 98, 94, 97};
    private final int[] SMU_ER_SENDBACK = {20};

    private final int[] SMU_DEV = {11, 13, 15, 17, 19, 25, 40};
    private final int[] SMU_FIX = {60, 74, 80, 82, 83, 84, 90, 91, 92, 93, 94, 96, 97};
    private final int[] SMU_SENDBACK = {20, 30, 31, 32, 36, 43, 44};

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
        Map<String, Integer[]> map = new LinkedHashMap<>();
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
        int sendBack = 0;
        int sr = 0;
        int[] res = new int[4];
        for (Bug b : bugs) {
            if (matchCriteria(b, criteria)) {
                boolean added = false;
                if (b.getReportedDate().after(cal1) && b.getReportedDate().before(cal2) || b.getReportedDate().equals(cal1)) {
                    report++;
                    if (b.getSrStatus().equals("OPEN") && !added) {
                        sr++;
                        added = true;
                    }
                }
                if (b.getFixedDate() != null) {
                    if ((b.getFixedDate().after(cal1) && b.getFixedDate().before(cal2) || b.getFixedDate().equals(cal1)) && containsStatus(SMU_FIX, b.getStatus())) {
                        fix++;
                        if (b.getSrStatus().equals("OPEN") && !added) {
                            sr++;
                            added = true;
                        }
                    }
                }
                if (b.getFixedDate() != null) {
                    if ((b.getFixedDate().after(cal1) && b.getFixedDate().before(cal2) || b.getFixedDate().equals(cal1)) && containsStatus(SMU_SENDBACK, b.getStatus())) {
                        sendBack++;
                        if (b.getSrStatus().equals("OPEN") && !added) {
                            sr++;
                            added = true;
                        }
                    }
                }
            }
        }
        res[0] = report;
        res[1] = fix;
        res[2] = sendBack;
        res[3] = sr;
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
            if ((containsStatus(SMU_DEV, b.getStatus()) || containsStatus(SMU_SENDBACK, b.getStatus())) && matchCriteria(b, criteria)) {
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
        List<Bug> sendBack = new ArrayList<Bug>();
        List<Bug> sr = new ArrayList<Bug>();
        for (Bug b : bugReport) {
            if (matchCriteria(b, criteria)) {
                boolean added = false;
                //System.out.println(b.getReportedDate().getTime() + " " + cal1.getTime() + " " + cal2.getTime());
                if (b.getReportedDate().after(cal1) && b.getReportedDate().before(cal2) || b.getReportedDate().equals(cal1)) {
                    report.add(b);
                    if (b.getSrStatus().equals("OPEN") && !added) {
                        sr.add(b);
                        added = true;
                    }
                }
                if (b.getFixedDate() != null) {
                    if ((b.getFixedDate().after(cal1) && b.getFixedDate().before(cal2) || b.getFixedDate().equals(cal1)) && containsStatus(SMU_FIX, b.getStatus())) {
                        fix.add(b);
                    }
                    if (b.getSrStatus().equals("OPEN") && !added) {
                        sr.add(b);
                        added = true;
                    }
                }
                if (b.getFixedDate() != null) {
                    if ((b.getFixedDate().after(cal1) && b.getFixedDate().before(cal2) || b.getFixedDate().equals(cal1)) && containsStatus(SMU_SENDBACK, b.getStatus())) {
                        sendBack.add(b);
                        if (b.getSrStatus().equals("OPEN") && !added) {
                            sr.add(b);
                            added = true;
                        }
                    }
                }
            }
        }
        res.add(report);
        res.add(fix);
        res.add(sendBack);
        res.add(sr);
        return res;
    }

    public List<List<Bug>> getBugDetail(Calendar cal1, Calendar cal2) {
        Map<CriteriaType, String> criteria = new HashMap<CriteriaType, String>();
        return getBugDetail(cal1, cal2, criteria);
    }

    public Map<String, Integer> countBugByAssinee(Map<CriteriaType, String> criteria) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Bug b : bugReport) {
            if ((containsStatus(SMU_DEV, b.getStatus()) || containsStatus(SMU_SENDBACK, b.getStatus())) && matchCriteria(b, criteria)) {
                if (map.containsKey(b.getAssignee())) {
                    int count = map.get(b.getAssignee());
                    map.put(b.getAssignee(), count + 1);
                } else {
                    map.put(b.getAssignee(), 1);
                }
            }
        }
        return map;
    }

    public List<String> getAllReleases(Calendar startDate) {
        List<String> res = new ArrayList<String>();
        for (Bug b : bugReport) {
            if (b.getReportedDate().after(startDate) || (b.getFixedDate() != null && b.getFixedDate().after(startDate))) {
                if (!res.contains(b.getRelease())) {
                    res.add(b.getRelease());
                }
            }
        }
        return res;
    }

    public List<String> getAllTargets(Calendar startDate) {
        List<String> res = new ArrayList<String>();
        for (Bug b : bugReport) {
            if (b.getReportedDate().after(startDate) || (b.getFixedDate() != null && b.getFixedDate().after(startDate))) {
                String[] tags = b.getTag().split(",");
                for (String s : tags) {
                    s = s.trim();
                    if (!res.contains(s) && !s.isEmpty()) {
                        res.add(s);
                    }
                }
            }
        }
        return res;
    }

    public int[] getBugStatus() {
        return bugStatus;
    }

    public void setBugStatus(int[] bugStatus) {
        this.bugStatus = bugStatus;
    }
}
