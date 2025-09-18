package SINCREW.CrewBase.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;


    // 기본 생성자 (JPA 필수)
    public UserEntity() {}

    // 편리한 생성자
    public UserEntity(String email, String password, String phone, String role, String username) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.username = username;
    }

}