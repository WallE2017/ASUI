package pojo;
// Generated 2017-8-29 9:54:56 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * PersonInfo generated by hbm2java
 */
public class PersonInfo  implements java.io.Serializable {


     private int code;
     private String sex;
     private String email;
     private byte[] icon;
     private Date birthDate;
     private String nickname;

    public PersonInfo() {
    }

	
    public PersonInfo(int code) {
        this.code = code;
    }
    public PersonInfo(int code, String nickname){
        this.code = code;
        this.nickname = nickname;
    }
    public PersonInfo(int code, String sex, String email, byte[] icon, Date birthDate, String nickname) {
       this.code = code;
       this.sex = sex;
       this.email = email;
       this.icon = icon;
       this.birthDate = birthDate;
       this.nickname = nickname;
    }
   
    public int getCode() {
        return this.code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    public String getSex() {
        return this.sex;
    }
    
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public byte[] getIcon() {
        return this.icon;
    }
    
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
    public Date getBirthDate() {
        return this.birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public String getNickname() {
        return this.nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }




}


