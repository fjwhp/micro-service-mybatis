package aljoin.ioa.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class ActWorkingVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private int no;
    private String bpmnName;
    private int bpmnNums;
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

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }

    public int getBpmnNums() {
        return bpmnNums;
    }

    public void setBpmnNums(int bpmnNums) {
        this.bpmnNums = bpmnNums;
    }
}
