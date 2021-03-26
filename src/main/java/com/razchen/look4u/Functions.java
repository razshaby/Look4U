package com.razchen.look4u;

import org.joda.time.LocalDate;
import org.joda.time.Years;

public class Functions {


    public static int getAge(int _year, int _month, int _day) {

        LocalDate birthDate=new LocalDate(_year,_month,_day);
        LocalDate currentDate=LocalDate.now();
        Years age = Years.yearsBetween(birthDate, currentDate);
        return age.getYears();
    }

}
