package com.example.starlet_be.security;

import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAddress(email).orElseThrow(
                () -> new UsernameNotFoundException("이메일로 사용자를 찾을 수 없음"));
        if(user.getEmail().getVerify().getType() != VerifyType.VERIFY)
            throw new IllegalArgumentException("이메일 인증 혹은 비밀번호 재설정이 필요합니다.");

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail().getAddress())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}
