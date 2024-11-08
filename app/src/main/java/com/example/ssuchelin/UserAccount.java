package com.example.ssuchelin;

public class UserAccount {
    private String idToken;
    private String emailId;
    private String password;
    private String personName;
    private String studentId;
    private String realStudentId;

    // Firebase에서 필요로 하는 기본 생성자
    public UserAccount() {}

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getStudentId() {
        return idToken;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getRealStudentId() {
        return realStudentId;
    }

    public void setRealStudentId(String realStudentId) {
        this.realStudentId = realStudentId;
    }
}