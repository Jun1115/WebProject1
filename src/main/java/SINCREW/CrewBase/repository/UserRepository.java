package SINCREW.CrewBase.repository;

import SINCREW.CrewBase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByPhone(String phone);
    Boolean existsByEmail(String email);

    //identifier을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    UserEntity findByPhone(String username);
    UserEntity findByEmail(String username);
}