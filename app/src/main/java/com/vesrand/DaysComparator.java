package com.vesrand;

import java.util.Comparator;

public class DaysComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        return dayToInt(o1)-dayToInt(o2);
    }

    private int dayToInt(String day){
        switch (day){
            case "Пн":
                return -6;
            case "Вт":
                return -5;
            case "Ср":
                return -4;
            case "Чт":
                return -3;
            case "Пт":
                return -2;
            case "Сб":
                return -1;
            case "Вс":
                return 0;
            default:
                return 0; //тут добавим дни года
        }
    }
    public static String intToDay(int dayNum){
        switch (dayNum){
            case 1:
                return "Пн";
            case 2:
                return "Вт";
            case 3:
                return "Ср";
            case 4:
                return "Чт";
            case 5:
                return "Пт";
            case 6:
                return "Сб";
            case 7:
                return "Вс";
            default:
                return "0";
        }
    }
}
