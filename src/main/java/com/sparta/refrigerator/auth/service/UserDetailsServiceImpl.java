package com.sparta.refrigerator.auth.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(userId).orElseThrow( () -> new UsernameNotFoundException("아이디, 비밀번호를 확인해주세요."));

        return new UserDetailsImpl(user);
    }

}
