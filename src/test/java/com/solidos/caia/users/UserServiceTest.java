package com.solidos.caia.users;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.util.InternalException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.solidos.caia.api.common.utils.GetSecurityContext;
import com.solidos.caia.api.common.utils.TokenGenerator;
import com.solidos.caia.api.users.UserService;
import com.solidos.caia.api.users.dto.CreateUserDto;
import com.solidos.caia.api.users.dto.UserSummaryDto;
import com.solidos.caia.api.users.entities.UserEntity;
import com.solidos.caia.api.users.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private UserEntity userCreatedExpected;
  private CreateUserDto createUserDto;

  @BeforeAll
  static void beforeAll() {
    mockStatic(GetSecurityContext.class);
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.createUserDto = CreateUserDto.builder()
        .firstName("Juan")
        .lastName("Ramirez")
        .email("juan@example.com")
        .affiliation("Solidos")
        .password("123456")
        .build();

    this.userCreatedExpected = UserEntity.builder()
        .firstName(
            createUserDto.getFirstName())
        .lastName(
            createUserDto.getLastName())
        .email(
            createUserDto.getEmail())
        .affiliation(createUserDto.getAffiliation())
        .password("encodedPassword")
        .token(TokenGenerator.generate())
        .build();
  }

  // @Test
  // @DisplayName("Test create user success / service")
  // void testCreateUserSuccess() {
  // when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
  // when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encodedPassword");
  // when(userRepository.save(any(UserEntity.class))).thenReturn(this.userCreatedExpected);

  // UserEntity userCreated = userService.createUser(this.createUserDto);

  // assertThat(userCreated).isNotNull();
  // assertEquals(this.userCreatedExpected.getPassword(),
  // userCreated.getPassword());
  // assertEquals(this.userCreatedExpected.getEmail(), userCreated.getEmail());
  // assertEquals(this.userCreatedExpected.getAffiliation(),
  // userCreated.getAffiliation());
  // assertEquals(this.userCreatedExpected.getFirstName(),
  // userCreated.getFirstName());
  // assertEquals(this.userCreatedExpected.getLastName(),
  // userCreated.getLastName());
  // assertEquals(this.userCreatedExpected.getToken(), userCreated.getToken());
  // verify(userRepository).save(any(UserEntity.class));
  // }

  @Test
  @DisplayName("Test create user fail / service")
  void testCreateUserFail() {
    when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.of(this.userCreatedExpected));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.createUser(createUserDto);
    });

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("User already exists", exception.getReason());
    verify(userRepository, never()).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("Test create user internal exception / service")
  void createUser_InternalException() {
    // Arrange

    when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    InternalException exception = assertThrows(InternalException.class, () -> {
      userService.createUser(createUserDto);
    });

    assertEquals("Error creating user", exception.getMessage());
  }

  @Test
  @DisplayName("Test confirm success / service")
  void testConfirmSuccess() {
    when(userRepository.findByToken(this.userCreatedExpected.getToken()))
        .thenReturn(Optional.of(this.userCreatedExpected));
    when(userRepository.save(any(UserEntity.class))).thenReturn(this.userCreatedExpected);

    userService.confirm(this.userCreatedExpected.getToken());

    UserEntity userConfirmed = this.userCreatedExpected;
    userConfirmed.setIsEnabled(true);
    userConfirmed.setAccountNoExpired(true);
    userConfirmed.setAccountNoLocked(true);
    userConfirmed.setCredentialsNoExpired(true);
    userConfirmed.setToken(null);

    assertNull(userConfirmed.getToken());
    assertTrue(userConfirmed.getIsEnabled());
    assertTrue(userConfirmed.getAccountNoExpired());
    assertTrue(userConfirmed.getAccountNoLocked());
    assertTrue(userConfirmed.getCredentialsNoExpired());
    verify(userRepository, times(1)).save(this.userCreatedExpected);
  }

  @Test
  @DisplayName("Test confirm user not found / service")
  void testConfirmUserNotFound() {
    String token = "invalid-token";

    when(userRepository.findByToken(token)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.confirm(token);
    });

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
    verify(userRepository, never()).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("Test confirm user already confirmed / service")
  void confirm_UserAlreadyConfirmed() {
    var userConfirmed = this.userCreatedExpected;
    userConfirmed.setIsEnabled(true);

    when(userRepository.findByToken(userConfirmed.getToken()))
        .thenReturn(Optional.of(userConfirmed));

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.confirm(userConfirmed.getToken());
    });

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("User already confirmed", exception.getReason());
    verify(userRepository, never()).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("Test confirm internal exception / service")
  void confirm_InternalException() {

    when(userRepository.findByToken(userCreatedExpected.getToken())).thenReturn(Optional.of(userCreatedExpected));
    doThrow(new RuntimeException("Database error")).when(userRepository).save(any(UserEntity.class));

    // Act & Assert
    InternalException exception = assertThrows(InternalException.class, () -> {
      userService.confirm(userCreatedExpected.getToken());
    });

    assertEquals("Error confirming user", exception.getMessage());
    verify(userRepository, times(1)).save(userCreatedExpected);
  }

  @Test
  @DisplayName("Test find by query success / service")
  void testFindByQuerySuccess() {
    UserEntity user1 = UserEntity.builder()
        .id(1L)
        .firstName("User 1")
        .lastName("User 1")
        .email("user1@example.com")
        .isEnabled(true).build();

    UserEntity user2 = UserEntity.builder()
        .id(2L)
        .firstName("User 2")
        .lastName("User 2")
        .email("user2@example.com")
        .isEnabled(true).build();

    String query = "user 2";
    Pageable pageable = PageRequest.of(0, 10);

    when(GetSecurityContext.getEmail()).thenReturn(user1.getEmail());
    when(userRepository.findByQuery(user1.getEmail(), query, pageable)).thenReturn(List.of(user2));

    List<UserSummaryDto> users = userService.findByQuery(query, 0, 10);

    assertEquals(1, users.size());
    assertEquals(user2.getFirstName(), users.get(0).getFirstName());
    assertEquals(user2.getLastName(), users.get(0).getLastName());
    assertEquals(user2.getEmail(), users.get(0).getEmail());
    verify(userRepository, times(1)).findByQuery(user1.getEmail(), query, pageable);
  }

  @Test
  @DisplayName("Test find by query invalid query / service")
  void testFindByQueryInvalidQuery() {
    UserEntity user1 = UserEntity.builder()
        .id(1L)
        .firstName("User 1")
        .lastName("User 1")
        .email("user1@example.com")
        .isEnabled(true).build();

    UserEntity user2 = UserEntity.builder()
        .id(2L)
        .firstName("User 2")
        .lastName("User 2")
        .email("user2@example.com")
        .isEnabled(true).build();

    String userAuthEmail = "auth@example.com";
    String query = "ab";
    Pageable pageable = PageRequest.of(0, 10);

    when(GetSecurityContext.getEmail()).thenReturn(userAuthEmail);
    when(userRepository.findAllUsers(userAuthEmail, pageable)).thenReturn(List.of(user1, user2));

    List<UserSummaryDto> users = userService.findByQuery(query, 0, 10);

    assertEquals(2, users.size());
    assertEquals(user1.getFirstName(), users.get(0).getFirstName());
    assertEquals(user1.getLastName(), users.get(0).getLastName());
    assertEquals(user1.getEmail(), users.get(0).getEmail());
    verify(userRepository, never()).findByQuery(any(), any(), any());
    verify(userRepository, times(1)).findAllUsers(userAuthEmail, pageable);
  }

  @Test
  @DisplayName("Test find id by email success / service")
  void testFindIdByEmailSuccess() {
    String email = "user1@example.com";
    Long expectedId = 1L;

    when(userRepository.findIdByEmail(email)).thenReturn(Optional.of(expectedId));

    Long id = userService.findIdByEmail(email);

    assertEquals(expectedId, id);
    verify(userRepository, times(1)).findIdByEmail(email);
  }

  @Test
  @DisplayName("Test find id by email user not found / service")
  void findIdByEmail_UserNotFound() {
    String email = "notfound@example.com";

    when(userRepository.findIdByEmail(eq(email))).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.findIdByEmail(email);
    });

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
  }

  @Test
  @DisplayName("Test find by email success / service")
  void findByEmailSuccess() {
    when(userRepository.findByEmail(userCreatedExpected.getEmail())).thenReturn(Optional.of(userCreatedExpected));
    UserEntity user = userService.findByEmail(userCreatedExpected.getEmail());
    assertEquals(userCreatedExpected, user);
    verify(userRepository, times(1)).findByEmail(userCreatedExpected.getEmail());
  }

  @Test
  @DisplayName("Test find by email not found / service")
  void findByEmailNotFound() {
    when(userRepository.findByEmail(userCreatedExpected.getEmail())).thenReturn(Optional.empty());
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.findByEmail(userCreatedExpected.getEmail());
    });
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
  }

}
