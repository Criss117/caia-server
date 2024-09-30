package com.solidos.caia.api.users;

import java.util.Optional;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.solidos.caia.api.common.utils.GetSecurityContext;
import com.solidos.caia.api.common.utils.PaginationParams;
import com.solidos.caia.api.common.utils.TokenGenerator;
import com.solidos.caia.api.users.adapters.UserEntityAdapter;
import com.solidos.caia.api.users.dto.CreateUserDto;
import com.solidos.caia.api.users.dto.UserSummaryDto;
import com.solidos.caia.api.users.entities.UserEntity;
import com.solidos.caia.api.users.entities.UserEntity.UserEntityBuilder;
import com.solidos.caia.api.users.repositories.UserRepository;

@Service
public class UserService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Creates a new user.
   *
   * @param createUserDto the user to be created, without the id.
   * @throws IOException
   * @throws ResponseStatusException if the user already exists.
   * @throws InternalException       if there is an error creating the user.
   */
  @Transactional
  public UserEntity createUser(CreateUserDto createUserDto) {
    Optional<UserEntity> existsUser = userRepository.findByEmail(createUserDto.getEmail());

    if (existsUser.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
    }

    UserEntityBuilder userEntityBuilder = UserEntity.builder();

    UserEntity userEntity = userEntityBuilder
        .firstName(createUserDto.getFirstName())
        .lastName(createUserDto.getLastName())
        .email(createUserDto.getEmail())
        .affiliation(createUserDto.getAffiliation())
        .password(passwordEncoder.encode(createUserDto.getPassword()))
        .token(TokenGenerator.generate())
        .build();

    if (userEntity == null) {
      throw new InternalException("UserEntity cannot be null after being built");
    }
    // TODO: Send Email

    try {
      return userRepository.save(userEntity);
    } catch (Exception e) {
      throw new InternalException("Error creating user");
    }
  }

  /**
   * Confirms a user.
   *
   * @param token the user's confirmation token.
   * @throws IOException
   * @throws ResponseStatusException if the user does not exist, or if the user is
   *                                 already confirmed.
   * @throws InternalException       if there is an error confirming the user.
   */
  @Transactional
  public void confirm(String token) {
    Optional<UserEntity> user = userRepository.findByToken(token);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    UserEntity userEntity = user.get();

    if (userEntity.getIsEnabled()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "User already confirmed");
    }

    userEntity.setToken(null);
    userEntity.setAccountNoExpired(true);
    userEntity.setAccountNoLocked(true);
    userEntity.setCredentialsNoExpired(true);
    userEntity.setIsEnabled(true);

    try {
      userRepository.save(userEntity);
    } catch (Exception e) {
      throw new InternalException("Error confirming user");
    }
  }

  public List<UserSummaryDto> findByQuery(String query, Integer page, Integer offSet) {
    String userEmail = GetSecurityContext.getEmail();
    PaginationParams paginationParams = PaginationParams.withQuery(page, offSet, query);

    if (paginationParams.getQuery() == null || paginationParams.getQuery().length() < 3) {
      return userRepository
          .findAllUsers(
              userEmail,
              paginationParams.getPageable())
          .stream()
          .map(u -> UserEntityAdapter.userEntityToUserResume(u))
          .toList();
    }

    return userRepository
        .findByQuery(
            userEmail,
            paginationParams.getQuery(),
            paginationParams.getPageable())
        .stream()
        .map(u -> UserEntityAdapter.userEntityToUserResume(u)).toList();
  }

  public Long findIdByEmail(String email) {
    Optional<Long> id = userRepository.findIdByEmail(email);

    if (id.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    return id.get();
  }

  public UserEntity findByEmail(String email) {
    Optional<UserEntity> user = userRepository.findByEmail(email);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    return user.get();
  }
}
