package com.example.starlet_be.security;

import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.domains.verify.entity.VerifyType;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
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
        // 계정 정지 기능이 포함되어 불가피하게 DB조회 로직 추가
        User user = userRepository.findByEmailAddress(email).orElseThrow(
                () -> new UsernameNotFoundException("이메일로 사용자를 찾을 수 없음"));
        if(user.getEmail().getVerify().getType() != VerifyType.VERIFY)
            throw new CustomException(ErrorCode.NOT_VERIFY_USER);

        // 이때 반환하는 User는 시큐리티에 있는 User임
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail().getAddress())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}
