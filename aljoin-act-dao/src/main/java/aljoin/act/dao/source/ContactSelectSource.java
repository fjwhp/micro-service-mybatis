package aljoin.act.dao.source;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ContactSelectSource {
    private String id;
    private String text;
    private String level;
    private String parent;
    private boolean check;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
