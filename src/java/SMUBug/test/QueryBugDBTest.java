/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SMUBug.test;

import SMUBug.server.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vijin
 */
public class QueryBugDBTest {
    // Bug Production DB TNSNAME

    private final String productionBugDB = "(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=amogridxp01-scan.us.oracle.com)(PORT = 1523)) (CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = bugap_adx.us.oracle.com)))";
    // User Acceptance Test DB TNSNAME
    private final String userAcceptanceBugDB = "(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=ON)(ADDRESS=(PROTOCOL=tcp)(HOST=atd205-crs.us.oracle.com)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=atd205-crs.us.oracle.com)(PORT=1522))(ADDRESS=(PROTOCOL=tcp)(HOST=atd206-crs.us.oracle.com)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=atd206-crs.us.oracle.com)(PORT=1522))(ADDRESS=(PROTOCOL=tcp)(HOST=atd207-crs.us.oracle.com)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=atd207-crs.us.oracle.conm)(PORT=1522))(ADDRESS=(PROTOCOL=tcp)(HOST=atd208-crs.us.oracle.com)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=atd208-crs.us.oracle.com)(PORT=1522)))(CONNECT_DATA=(SERVICE_NAME=bugau.us.oracle.com)))";
    public Connection conn;

    public QueryBugDBTest() {
    }

    public void openConnection(String username, String password) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oops! Can't find class oracle.jdbc.driver.OracleDriver");
            System.exit(1);
        }
        System.out.println("open connection");
        //conn = DriverManager.getConnection("jdbc:oracle:thin:@" + userAcceptanceDB, username, password);
        conn = DriverManager.getConnection("jdbc:oracle:thin:@" + productionBugDB, username, password);
    }

    public void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
            System.out.println("close connection");
            conn = null;
        }
    }

    public void test(String query) {
        Statement stmt = null;
        ResultSet rset = null;
        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);
            int lineNo = 1;
            while (rset.next()) {
                System.out.println(lineNo++);
                int count = rset.getMetaData().getColumnCount();
                System.out.println(count);
                for (int i=1; i<=count; i++){
                    System.out.println(rset.getMetaData().getColumnLabel(i) + ": " + rset.getString(i));
                }
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Darn! A SQL error: " + e.getMessage());
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException ignore) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    public List<Bug> execute(String query) {
        Statement stmt = null;
        ResultSet rset = null;
        List<Bug> list = new ArrayList<Bug>();
        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);
            int lineNo = 1;
            while (rset.next()) {
                Bug bug = new Bug();
                bug.setLineNum(lineNo++);
                bug.setId(Integer.parseInt(rset.getString(1)));
                bug.setReportedDate(toCalendar(rset.getString(2)));
                if (rset.getString(3) == null) {
                    bug.setFixedDate(null);
                } else {
                    bug.setFixedDate(toCalendar(rset.getString(3)));
                }
                bug.setAssignee(rset.getString(4));
                bug.setRelease(rset.getString(5));
                bug.setSubject(rset.getString(6));
                bug.setStatus(Integer.parseInt(rset.getString(7)));
                bug.setTag(rset.getString(8) == null ? "" : rset.getString(8));
                list.add(bug);
            }
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Darn! A SQL error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException ignore) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                }
            }
        }
        return list;
    }

    public String generateSql() {
        String col = "h.rptno, h.rptdate, h.fixed_date, h.programmer, h.utility_version, h.subject, h.status, bt.tags";
        //String sql = String.format("select %s from rpthead h , bug_tags bt where h.product_id in (10027) and h.category in ('SNAPMGR') AND h.rptno = bt.rptno order by h.rptno desc", col);
        String sql = String.format("select %s from rpthead h left join bug_tags bt on h.rptno = bt.rptno where h.product_id in (10027) and h.category in ('SNAPMGR') order by h.rptno desc", col);
        return sql;
    }

    public String generateSql(String productID, String component, String subcomponent) {
        String col = "h.rptno, h.rptdate, h.fixed_date, h.programmer, h.utility_version, h.subject, h.status, bt.tags, cb.sr_status";
        String condition = String.format("h.product_id in (%s) and h.category in ('%s') and h.SUB_COMPONENT in ('%s')", productID, component, subcomponent);
        String order = "order by h.rptno desc";
        String sql = String.format("select %s from rpthead h, bug_tags bt, customer_bugs cb where cb.rptno=h.rptno and h.rptno = bt.rptno and UPPER(cb.sr_status) = 'OPEN' and %s %s", col, condition, order);
        return sql;
    }

    public List<Bug> getAllBugs() {
        String sql = generateSql();
        List<Bug> res = execute(sql);
        return res;
    }

    public BugReport generateBugReport(String username, String password, String productID, String component, String subcomponent) {
        BugReport br = new BugReport();
        try {
            openConnection(username, password);
            br.setBugReport(getAllBugs());
            closeConnection();
        } catch (SQLException ex) {
            try {
                closeConnection();
                throw ex;
            } catch (SQLException ex1) {
                Logger.getLogger(QueryBugDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return br;
    }

    private Calendar toCalendar(String s) {
        String[] sa = s.split(" ");
        String[] sa1 = sa[0].split("-");
        int year = Integer.parseInt(sa1[0]);
        int month = Integer.parseInt(sa1[1]);
        int day = Integer.parseInt(sa1[2]);
        return new GregorianCalendar(year, month - 1, day);
    }

    public static void main(String[] args) {
        QueryBugDBTest q = new QueryBugDBTest();
        try {
            q.openConnection("VIJIN", "198800Jxw1");
            q.test(q.generateSql("10026", "NAS", "STMF"));
            q.closeConnection();
        } catch (SQLException ex) {
            try {
                q.closeConnection();
            } catch (SQLException ex1) {
                Logger.getLogger(QueryBugDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }
}
