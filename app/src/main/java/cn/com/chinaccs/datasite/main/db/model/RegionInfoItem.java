package cn.com.chinaccs.datasite.main.db.model;

/**
 * Created by Asky on 2015/12/28.
 */
public class RegionInfoItem {
    // id
    public long ORG_ID;
    // 名称
    public String ORG_NAME;
    // 夫节点id
    public int ORG_PARENT_ID;
    // 节点等级
    public int ORG_LEVEL;
    // 序号
    public int PER_LEVEL;

    public RegionInfoItem() {
    }

    public RegionInfoItem(long ORG_ID, String ORG_NAME, int ORG_PARENT_ID, int ORG_LEVEL, int PER_LEVEL) {
        this.ORG_ID = ORG_ID;
        this.ORG_NAME = ORG_NAME;
        this.ORG_PARENT_ID = ORG_PARENT_ID;
        this.ORG_LEVEL = ORG_LEVEL;
        this.PER_LEVEL = PER_LEVEL;
    }

    @Override
    public String toString() {
        return "RegionInfoItem{" +
                "id='" + ORG_ID + '\'' +
                ", ORG_NAME='" + ORG_NAME + '\'' +
                ", ORG_PARENT_ID='" + ORG_PARENT_ID + '\'' +
                ", ORG_LEVEL='" + ORG_LEVEL + '\'' +
                ", PER_LEVEL='" + PER_LEVEL + '\'' +
                '}';
    }
}
