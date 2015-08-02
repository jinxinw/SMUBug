package SMUBug.view;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class UserView implements Serializable {

    private String name;
    private String password;
    private Date startDate;
    private Date endDate;

    public UserView() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getMessage() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        if (startDate == null) {
            return "";
        } else {
            return String.format("Generating Bug Report from %s to %s ...", format.format(startDate), format.format(endDate));
        }
    }

}
