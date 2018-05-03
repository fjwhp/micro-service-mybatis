package aljoin.ioa.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class ActApprovalVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private int no;
    private String userName;
    private int newNums;
    private int stayNums;
    private int handleNums;
    private String jobsName;
    private String showDate;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNewNums() {
        return newNums;
    }

    public void setNewNums(int newNums) {
        this.newNums = newNums;
    }

    public int getStayNums() {
        return stayNums;
    }

    public void setStayNums(int stayNums) {
        this.stayNums = stayNums;
    }

    public int getHandleNums() {
        return handleNums;
    }

    public void setHandleNums(int handleNums) {
        this.handleNums = handleNums;
    }

    public String getJobsName() {
        if (jobsName == null) {
            jobsName = "";
        }
        return jobsName;
    }

    public void setJobsName(String jobsName) {
        this.jobsName = jobsName;
    }

}
