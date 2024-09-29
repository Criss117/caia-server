package com.solidos.caia.users;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.solidos.caia.api.users.entities.UserEntity;
import com.solidos.caia.api.users.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

  @Autowired
  private UserRepository userRepository;
  private List<UserEntity> savedUsers;

  @BeforeEach
  void setUp() {
    UserEntity user = UserEntity.builder()
        .firstName("Juan")
        .lastName("Ramirez")
        .email("juan@example.com")
        .affiliation("Solidos")
        .password("12345")
        .token("token")
        .isEnabled(true)
        .build();

    UserEntity user2 = UserEntity.builder()
        .firstName("Daniel")
        .lastName("Castillo")
        .email("daniel@example.com")
        .affiliation("Solidos")
        .password("12345")
        .token("token2")
        .isEnabled(true)
        .build();

    this.savedUsers = userRepository.saveAll(List.of(user, user2));
  }

  @Test
  @DisplayName("Test save user")
  void testSaveUser() {
    UserEntity user = UserEntity.builder()
        .id(1L)
        .firstName("Juan")
        .lastName("Ramirez")
        .email("f8dZT@example.com")
        .affiliation("Solidos")
        .password("12345")
        .token("token")
        .build();

    UserEntity savedUser = userRepository.save(user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isGreaterThan(0L);
    assertThat(savedUser.getFirstName()).isEqualTo("Juan");
    assertThat(savedUser.getLastName()).isEqualTo("Ramirez");
    assertThat(savedUser.getEmail()).isEqualTo("f8dZT@example.com");
    assertThat(savedUser.getAffiliation()).isEqualTo("Solidos");
    assertThat(savedUser.getPassword()).isEqualTo("12345");
    assertThat(savedUser.getToken()).isEqualTo("token");
  }

  @DisplayName("Test find by email")
  @Test
  void testFindByEmail() {
    UserEntity savedUser = this.savedUsers.get(0);

    UserEntity foundUser = userRepository.findByEmail(savedUser.getEmail()).get();

    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    assertThat(foundUser.getFirstName()).isEqualTo(savedUser.getFirstName());
    assertThat(foundUser.getLastName()).isEqualTo(savedUser.getLastName());
    assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    assertThat(foundUser.getAffiliation()).isEqualTo(savedUser.getAffiliation());
    assertThat(foundUser.getPassword()).isEqualTo(savedUser.getPassword());
    assertThat(foundUser.getToken()).isEqualTo(savedUser.getToken());
  }

  @DisplayName("Test find by token")
  @Test
  void testFindByToken() {
    UserEntity savedUser = this.savedUsers.get(0);

    UserEntity foundUser = userRepository.findByToken(savedUser.getToken()).get();

    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    assertThat(foundUser.getFirstName()).isEqualTo(savedUser.getFirstName());
    assertThat(foundUser.getLastName()).isEqualTo(savedUser.getLastName());
    assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    assertThat(foundUser.getAffiliation()).isEqualTo(savedUser.getAffiliation());
    assertThat(foundUser.getPassword()).isEqualTo(savedUser.getPassword());
    assertThat(foundUser.getToken()).isEqualTo(savedUser.getToken());
  }

  @DisplayName("Test find by query")
  @Test
  void testFindByQuery() {
    Pageable pageable = PageRequest.of(0, 10);
    UserEntity juanUser = this.savedUsers.get(0);
    UserEntity danielUser = this.savedUsers.get(1);
    List<UserEntity> users = userRepository.findByQuery(juanUser.getEmail(),
        danielUser.getFirstName().toLowerCase(),
        pageable);

    System.out.println(danielUser.getFirstName());

    assertThat(users).isNotEmpty();
  }
}
