package com.example.AllSpringerBoot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.AllSpringerBoot.dao.MainRepository;
import com.example.AllSpringerBoot.entity.User;

public class UserDetailsServiceImpl implements UserDetailsService{

	
	@Autowired
	private MainRepository mr;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user from database
		
		User user=mr.getUserByName(username);
		if(user==null) {
			throw new UsernameNotFoundException("Could not found username");
		}
		
		CustomeUserDetail cusd=new CustomeUserDetail(user);
		
		return cusd;
	}

}
