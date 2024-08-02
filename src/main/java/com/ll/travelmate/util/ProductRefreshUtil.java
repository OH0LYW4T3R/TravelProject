package com.ll.travelmate.util;

import java.time.LocalDateTime;

public class ProductRefreshUtil {
    public final static Integer CANCELTIMESET = 1;
    public final static Integer PAYMENTTIMESET = 3;
    public final static Integer TRAVELCOMPLEATETIMESET = 30;

     public static boolean isCancelPassed(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime Later = dateTime.plusDays(CANCELTIMESET); // 테스트 완

        return now.isAfter(Later);
    }

     public static boolean isPaymentPassed(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime Later = dateTime.plusDays(PAYMENTTIMESET); // 테스트 완

        return now.isAfter(Later);
    }

    public static boolean isCompletePassed(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime Later = dateTime.plusDays(TRAVELCOMPLEATETIMESET);

        return now.isAfter(Later);
    }

    public static boolean travelCompleteCheck(LocalDateTime dateTime) {
         LocalDateTime now = LocalDateTime.now();
         return now.isAfter(dateTime);
    }

    public static boolean startTimeCheck(LocalDateTime dateTime) {
         LocalDateTime now = LocalDateTime.now();
         return now.isAfter(dateTime);
    }
}
