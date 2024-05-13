package org.wolt.woltproject.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.config.mail.MailConfig;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.*;
import org.wolt.woltproject.maps.UserMap;
import org.wolt.woltproject.models.LoginRequest;
import org.wolt.woltproject.models.LoginResponse;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.repositories.UserRepository;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtUtil;
    private final PasswordEncoder encoder;
    private final UserRepository repository;
    private final UserMap map;

    private final MailConfig mailConfig;


    private int activationCode;

    public void register(UserRequestDto userRequestDto){
        log.info("ActionLog.AuthService register method is started");
        if(repository.findByUsername(userRequestDto.getUsername()).isPresent()){
            throw new UserAlreadyExistException("The username is already taken");
        }

        UserEntity entity = map.toEntity(userRequestDto);
        entity.setPassword(encoder.encode(userRequestDto.getPassword()));
        repository.save(entity);
        log.info("ActionLog.AuthService register method is finished");
    }

    public ResponseEntity<?> login(LoginRequest request){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            log.info("authentication details: {}", authentication);
            String username = authentication.getName();
            UserEntity entity = (UserEntity) repository.findByUsername(username).orElseThrow();
            String token = jwtUtil.createToken(entity);

            LoginResponse response = new LoginResponse(username,token);
            return ResponseEntity.status(HttpStatus.OK).header("userId", String.valueOf(entity.getUserId())).body(response);
        }catch (BadCredentialsException e){
            throw new NotFoundException("Invalid Username or password");
        }catch (Throwable e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void forgotPassword(){

    }

    public void deleteAccount(){

    }





    public void sendActivationMail(HttpServletRequest request){
        Integer userId = jwtUtil.getUserId(jwtUtil.resolveClaims(request));
        UserEntity user = repository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        String mail = user.getEmail();
        if (!LocalDateTime.now().minusMinutes(3).isAfter(user.getCodeSentTime())){
            throw new ActivationCodeJustSent("Activation Code just sent. You can receive new code right now");
        }
        Random random = new Random();
        activationCode = random.nextInt(9999 - 1000 + 1) + 1000;
        try{
            mailConfig.sendEmail(mail,"Activation Code","Your activation code is " +activationCode);
        }catch (MessagingException | UnsupportedEncodingException e){
            throw new RuntimeException(e.getMessage());
        }

        user.setActivationCode(activationCode);
        user.setCodeSentTime(LocalDateTime.now());
        repository.save(user);

    }


    public Boolean inputCode(Integer activationCode, HttpServletRequest request){
        Integer userId = jwtUtil.getUserId(jwtUtil.resolveClaims(request));
        UserEntity user = repository.findById(userId).orElseThrow();
        if (user.getActivationCode() == 0){
            throw new NotAccessibleMethod("You Haven't received any code");
        }
        if (Objects.equals(activationCode, user.getActivationCode())){
            user.setActivated(true);
            repository.save(user);
        }else {
            throw new WrongActivationCode("Wrong Activation Code");
        }
        return user.isActivated();
    }



}
