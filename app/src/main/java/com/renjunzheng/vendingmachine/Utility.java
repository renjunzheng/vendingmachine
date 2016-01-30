package com.renjunzheng.vendingmachine;

/**
 * Created by Renjun Zheng on 2016/1/30.
 */
public class Utility {
    /**
     * Helper method to provide the art resource id
     * @param merchandiseId from server response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForMerchandise(int merchandiseId) {
        int resourceId = -1;
        switch (merchandiseId) {
            case 1:
                resourceId = R.drawable.hershey;
                break;
            case 2:
                resourceId = R.drawable.kitkat;
                break;
            case 3:
                resourceId = R.drawable.reeses;
                break;
            case 4:
                resourceId = R.drawable.snickers;
                break;
            default:
                resourceId = -1;
            break;
        }
        return resourceId;
    }
}
