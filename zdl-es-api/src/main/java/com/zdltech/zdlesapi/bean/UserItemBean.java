package com.zdltech.zdlesapi.bean;

import org.springframework.stereotype.Component;

@Component
public class UserItemBean {
    //用户名
    private String userName;
    //age
    private int age;

    public UserItemBean() {

    }

    public UserItemBean(String name, int age) {
        this.userName = name;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserItemBean{" +
                "userName='" + userName + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
