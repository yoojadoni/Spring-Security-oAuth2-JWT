package com.example.oauthjwt.configure.OAuth2;

import com.example.oauthjwt.entity.User;
import com.example.oauthjwt.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 인증 완료 후 받은 데이터를 서비스에 접근할 수 있도록  인증정보를 생성하기 위한 서비스
 */
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        //Service 구분 (google, naver등 구분)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        //Service 제공자에 따라 분기처리하여 유저 정보 데이터생성처리.
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Map<String, Object> memberAttribute = oAuth2Attribute.convertToMap();

        Optional<User> user = userRepository.findByEmail((String) memberAttribute.get("email"));

        if(!user.isPresent()){
            User saveUser = User.builder()
                    .email(oAuth2Attribute.getEmail())
                    .userName(oAuth2Attribute.getName())
                    .roles("ROLE_USER")
                    .registrationId(registrationId)
                    .build();
            userRepository.save(saveUser);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), memberAttribute, "email");
    }
}
