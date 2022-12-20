package com.example.oauthjwt.configure;

import com.example.oauthjwt.entity.User;
import com.example.oauthjwt.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = new User();
        try {
            user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("가입된 사용자가 아닙니다."));
        } catch (UsernameNotFoundException e){
            throw  new UsernameNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();

        return new org
                .springframework
                .security
                .core
                .userdetails
                .User(user.getEmail(), "", grantedAuthoritySet);
    }
}
