package com.renjunzheng.vendingmachine;

/**
 * Created by Renjun Zheng on 2016/1/30.
 */
public class Utility {
    /**
     * Helper method to provide the art resource id
     * @param merchandiseName from server response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForMerchandise(String merchandiseName) {

        switch (merchandiseName){
            case "hershey":
                return R.drawable.hershey;
            case "kitkat":
                return R.drawable.kitkat;
            case "reeses":
                return R.drawable.reeses;
            case "snickers":
                return R.drawable.snickers;
            default:
                return 0;
        }
    }
}
