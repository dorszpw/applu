package pl.coddev.applu.data;


/**
 * Created by Piotr Woszczek on 20/05/15.
 */
public class PInfo {
    private static final String TAG = "PInfo";
    private String appname = "";
    private String pname = "";
    private int match = 0;
    private String matcherGroup = "";
    private Boolean removed = false;

    public boolean isSystemPackage() {
        return isSystemPackage;
    }

    public void setIsSystemPackage(boolean isSystemPackage) {
        this.isSystemPackage = isSystemPackage;
    }

    private boolean isSystemPackage = false;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public String getMatcherGroup() {
        return matcherGroup;
    }

    public void setMatcherGroup(String matcherGroup) {
        this.matcherGroup = matcherGroup;
    }


    public Boolean isRemoved() {
        return removed;
    }

    public synchronized void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PInfo)
            return ((PInfo) o).pname.equals(this.pname);
        else
            return false;
    }
}