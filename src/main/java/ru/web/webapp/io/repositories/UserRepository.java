package ru.web.webapp.io.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.webapp.io.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findUserByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);

    @Query(value="select * from users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
            countQuery="select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
            nativeQuery = true)
    Page<UserEntity> findAllByUsersWithConfirmedEmailAddresses(Pageable pageableRequest);

    @Query(value="select * from users u where u.first_name = ?1", nativeQuery = true)
    List<UserEntity> findAllByFirstName(String firstName);

    @Query(value="select * from users u where u.last_name = :lastName", nativeQuery = true)
    List<UserEntity> findAllByLastName(@Param("lastName") String lastName);

    @Query(value="select * from users u where u.first_name like %:keyword% or u.last_name like %:keyword%",
            nativeQuery = true)
    List<UserEntity> findAllByKeyword(@Param("keyword") String keyword);

    @Query(value="select u.first_name, u.last_name from users u " +
            "where u.first_name like %:keyword% or u.last_name like %:keyword%", nativeQuery = true)
    List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value="update users u set u.EMAIL_VERIFICATION_STATUS = :emailVerificationStatus where u.user_id = :userId",
            nativeQuery = true)
    void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
                                                     @Param("userId") String userId);


    @Query("select user from UserEntity user where user.userId = :userId")
    UserEntity findUserEntityByUserId(@Param("userId") String userId);

    @Query("select user.firstName, user.lastName from UserEntity user where user.userId = :userId")
    List<Object[]> findUserEntityFullNameById(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value="update UserEntity u set u.emailVerificationStatus = :emailVerificationStatus where u.userId = :userId")
    void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
                                           @Param("userId") String userId);
}
