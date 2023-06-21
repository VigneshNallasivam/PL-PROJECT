package com.intelizign.pl.authentication;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intelizign.pl.model.EmployeeModel;

public class UserDetailsImpl implements UserDetails 
{
	private static final long serialVersionUID = 1L;

	private Long id;

	private String emp_name;

	private String empcode;

	private String username;

	@JsonIgnore
	private String password;

	private String email;

	private String designation;

	private String address;
	
	private String country;
	
	private String role_name;
	
	private LocalDateTime date_of_join;

	private String department;
	
	private String mobile_number;
	
	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String emp_name, String empcode, String username, String password, String email,
			String designation, String address, String country, String role_name, LocalDateTime date_of_join,
			String department, String mobile_number,
			Collection<? extends GrantedAuthority> authorities) 
	{
		super();
		this.id = id;
		this.emp_name = emp_name;
		this.empcode = empcode;
		this.username = username;
		this.password = password;
		this.email = email;
		this.designation = designation;
		this.address = address;
		this.country = country;
		this.role_name = role_name;
		this.date_of_join = date_of_join;
		this.department = department;
		this.mobile_number = mobile_number;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(EmployeeModel user) 
	{
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getRole_name().name())).collect(Collectors.toList());

		return new UserDetailsImpl(user.getId(), user.getEmp_name(), user.getEmpcode(), user.getUsername(),
				user.getPassword(), user.getEmail(), user.getDesignation(), user.getAddress(),user.getCountry(),
				user.getRole_name(), user.getDate_of_join(),user.getDepartment(), user.getMobile_number(), authorities);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public LocalDateTime getDate_of_join() {
		return date_of_join;
	}

	public void setDate_of_join(LocalDateTime date_of_join) {
		this.date_of_join = date_of_join;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getEmpcode() {
		return empcode;
	}

	public void setEmp_code(String empcode) {
		this.empcode = empcode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getMobile_number() {
		return mobile_number;
	}

	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) 
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}
