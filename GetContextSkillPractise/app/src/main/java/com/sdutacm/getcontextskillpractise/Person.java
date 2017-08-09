package com.sdutacm.getcontextskillpractise;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bummer on 2017/8/9.
 */

public class Person implements Parcelable {
   private String name;
    private int age;

    public static Creator<Person> getCREATOR() {
        return CREATOR;
    }

    protected Person(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    public static final Parcelable.Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            Person person = new Person();
            person.name = in.readString(); //读取name
            person.age = in.readInt(); //读取年龄
            return person;
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);  //写出name
        dest.writeInt(age);   //写出age
    }
}
