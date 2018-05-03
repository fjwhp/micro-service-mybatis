package aljoin.ioa.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class FillOpinionShowVO {
    /**
     * 意見內容
     */
    private String content;
    /**
     * 當前用戶
     */
    private String fullName;
    /**
     * 當前時間
     */
    private String currDate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCurrDate() {
        return currDate;
    }

    public void setCurrDate(String currDate) {
        this.currDate = currDate;
    }
}
