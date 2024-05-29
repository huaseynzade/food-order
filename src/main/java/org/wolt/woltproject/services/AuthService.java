package org.wolt.woltproject.services;

import com.sun.net.httpserver.Headers;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.wolt.woltproject.entities.KeysEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.*;
import org.wolt.woltproject.maps.UserMap;
import org.wolt.woltproject.models.*;
import org.wolt.woltproject.repositories.KeyRepository;
import org.wolt.woltproject.repositories.UserRepository;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    private final KeyRepository keyRepository;
    private final UserMap map;

    private final MailConfig mailConfig;


    public void register(UserRequestDto userRequestDto) {
        log.info("ActionLog.AuthService.register method is started");
        if (repository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("The username is already taken");
        }

        UserEntity entity = map.toEntity(userRequestDto);
        entity.setPassword(encoder.encode(userRequestDto.getPassword()));
        repository.save(entity);
        log.info("ActionLog.AuthService register method is finished");
    }

    public ResponseEntity<?> login(LoginRequest request) {
        log.info("ActionLog.AuthService.login method is started for username {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            log.info("authentication details: {}", authentication);
            String username = authentication.getName();
            UserEntity entity = repository.findByUsername(username).orElseThrow();
            String token = jwtUtil.createToken(entity);

            LoginResponse response = new LoginResponse(username, token);
            ResponseEntity<LoginResponse> responseEntity = ResponseEntity.status(HttpStatus.OK).header("userId", String.valueOf(entity.getUserId())).body(response);
            log.info("ActionLog.AuthService.login method is finished for username {}", username);
            return responseEntity;


        } catch (BadCredentialsException e) {
            throw new NotFoundException("Invalid Username or password");
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void forgotPassword(String email, HttpSession session) {
        UserEntity user = repository.findByEmail(email).orElseThrow(() -> new NotFoundException("User nor found with this email"));
        log.info("ActionLog.AuthService.forgotPassword method is started for email {}", email);
        int resetCode = generateResetCode();
        sendEmailReset(email, resetCode);
        assignKey(user, resetCode);
        session.setAttribute("Email", email);
        log.info("ActionLog.AuthService.forgotPassword method is finished for email {} and set attribute session as {}", email, session.getAttribute("Email"));
    }


    public void changePassword(ResetPasswordDto resetPasswordDto, Integer key, HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("Email");
        if (email == null) {
            throw new NotAccessibleMethod("You Can't access to this method");
        }
        log.info("ActionLog.AuthService.changePassword method is started for email {}", email);
        UserEntity user = repository.findByEmail(email).orElseThrow();
        Integer sentKey = keyRepository.findByUserId(user).orElseThrow().getReset_key();
        checkPasswordMatch(resetPasswordDto);
        validateAndSetPassword(sentKey, key, resetPasswordDto.getPassword(), user);
        keyRepository.deleteByUserId(user);
        log.info("ActionLog.AuthService.changePassword method is finished for email {}", email);

    }


    public void sendActivationMail(HttpServletRequest request) {
        log.info("ActionLog.AuthService.sendActivationMail method is started");

        Integer userId = jwtUtil.getUserId(jwtUtil.resolveClaims(request));
        UserEntity user = repository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        String mail = user.getEmail();

//        checkIfCodeAlreadySentIn3Minutes(user);

        int activationCode = generateActivationCode();
        sendActivationMail(mail, activationCode);

        user.setActivationCode(activationCode);
        user.setCodeSentTime(LocalDateTime.now());
        repository.save(user);
        log.info("ActionLog.AuthService.sendActivationMail method is finished");

    }


    public void inputCode(Integer activationCode, HttpServletRequest request) {
        log.info("ActionLog.AuthService.inputCode method is started");

        Integer userId = jwtUtil.getUserId(jwtUtil.resolveClaims(request));
        UserEntity user = repository.findById(userId).orElseThrow();

        if (user.getActivationCode() == 0) {
            throw new NotAccessibleMethod("You Haven't received any code");
        }

        checkEquality(activationCode, user);
        log.info("ActionLog.AuthService.inputCode method is finished");
    }



    //    Seperated Methods for short main methods
    //
    //
    //
    //
    //
    //
    public void checkPasswordMatch(ResetPasswordDto resetPasswordDto) {
        String passwordOne = resetPasswordDto.getPassword();
        String passwordTwo = resetPasswordDto.getPassword2();
        if (!passwordOne.equals(passwordTwo)) {
            throw new PasswordsDoesntMatch("Passwords Doesnt match");
        }
    }

    public void assignKey(UserEntity user, Integer resetCode) {
        KeysEntity user_key;
        if (keyRepository.findByUserId(user).isPresent()) {
            user_key = keyRepository.findByUserId(user).orElseThrow();
            user_key.setReset_key(resetCode);
        } else {
            user_key = KeysEntity.builder().userId(user).reset_key(resetCode).build();
        }
        keyRepository.save(user_key);
    }

    public void sendEmailReset(String email, Integer resetCode) {
        try {
            mailConfig.sendEmail(email, "Reset Password Code", "Your key is " + resetCode);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendActivationMail(String mail, Integer activationCode) {

        try {
            mailConfig.sendEmail(mail, "Activation Code", "Your activation code is " + activationCode);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Integer generateResetCode() {
        Random random = new Random();
        return random.nextInt(999999 - 100000 + 1) + 100000;
    }

    public Integer generateActivationCode() {
        Random random = new Random();
        return random.nextInt(9999 - 1000 + 1) + 1000;
    }

    public void validateAndSetPassword(Integer sentKey, Integer key, String password, UserEntity user) {
        if (Objects.equals(sentKey, key)) {
            String encodedPassword = encoder.encode(password);
            user.setPassword(encodedPassword);
            repository.save(user);
        } else {
            throw new KeysDoesntMatch("Keys Doesn't match");
        }
    }

//    public void checkIfCodeAlreadySentIn3Minutes(UserEntity user) {
//        if (!LocalDateTime.now().minusMinutes(3).isAfter(user.getCodeSentTime())) {
//            throw new ActivationCodeJustSent("Activation Code just sent. You can receive new code right now");
//        }
//    }

    public void checkEquality(Integer activationCode, UserEntity user) {
        if (Objects.equals(activationCode, user.getActivationCode())) {
            user.setActivated(true);
            repository.save(user);
        } else {
            throw new WrongActivationCode("Wrong Activation Code");
        }
    }
}
