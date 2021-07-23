package com.auriga.TTApp1.model;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Value;

import com.auriga.TTApp1.constants.GenderEnum;
import com.auriga.TTApp1.util.FileUtil;
import com.auriga.TTApp1.util.SecurityUtil;

@Entity
@Table(name="users")
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "name", nullable = false, length = 100)
    private String name;
     
	@Column(nullable = false, unique = true, length = 255)
    private String email;
	
	@Column(nullable = true, length = 255)
    private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private GenderEnum gender; 
	
	@Column(nullable = true)
	private Integer age;
	
	@Column(nullable = true, length = 255)
    private String image;
	
	@Column(name="is_login_active", nullable = false, columnDefinition="boolean default 1")
	private Boolean isLoginActive;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
            )
    private Role role;
    
    @Transient
	private Integer seed;
    
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;   // 5 minutes
    
    @Column(name = "one_time_password")
    private String oneTimePassword;
     
    @Column(name = "otp_requested_time")
    private Date otpRequestedTime;
    
    public User() {
    	super();
    	this.isLoginActive = false;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getOneTimePassword() {
		return oneTimePassword;
	}

	public void setOneTimePassword(String oneTimePassword) {
		this.oneTimePassword = oneTimePassword;
	}

	public Date getOtpRequestedTime() {
		return otpRequestedTime;
	}

	public void setOtpRequestedTime(Date otpRequestedTime) {
		this.otpRequestedTime = otpRequestedTime;
	}
	
	public boolean isOTPExpired() {
		if(this.otpRequestedTime == null) return false;
		
    	long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = this.otpRequestedTime.getTime();
         
        if (otpRequestedTimeInMillis + OTP_VALID_DURATION < currentTimeInMillis) {
            // OTP expires
            return true;
        }
         
        return false;
    }

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public GenderEnum getGender() {
		return gender;
	}

	public void setGender(GenderEnum gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getIsLoginActive() {
		return isLoginActive;
	}

	public void setIsLoginActive(Boolean isLoginActive) {
		this.isLoginActive = isLoginActive;
	}

	public Integer getSeed() {
		return seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}
	
	public String getImageUrl() {
		return FileUtil.getUserImageUrl(this.getImage());
	}
	
	public String getGenderText() {
		return gender.getDisplayValue();
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email + "]";
	}
}


