package com.intelizign.pl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intelizign.pl.authentication.UserDetailsImpl;
import com.intelizign.pl.model.EmployeeModel;
import com.intelizign.pl.repositories.EmployeeRepository;
import com.intelizign.pl.utils.JwtUtils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		EmployeeModel user = employeeRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
		return UserDetailsImpl.build(user);
	}

	public UserDetailsServiceImpl(EmployeeRepository employeeRepo) {
		super();
		this.employeeRepo = employeeRepo;
	}

	public UserDetailsServiceImpl() {
		super();
	}
}
