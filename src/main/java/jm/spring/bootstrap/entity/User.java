package jm.spring.bootstrap.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jm.spring.bootstrap.entity.validate.SizeNotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter

@Entity
@Table(name="users242")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="email")
    @Email(message = "(обязательный параметр)")
    private String email;   //уникальное значение

    @Column(name="name")
    @SizeNotBlank(min = 4, message = "(минимум 4 символа)")
    private String name;

    @Column(name="last_name")
    @SizeNotBlank(min = 4, message = "(минимум 4 символа)")
    private String lastName;

    
    @Column(name="age")
    @Min(value = 12, message = "(от 12 лет)")
    private byte age;

    @Column(name="password")
    @SizeNotBlank(min = 3, message = "(минимум 3 символа)")
    private String password;

    @Transient
    private Roles[] enumRoles;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinTable(name = "users242_roles242",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User(int id, String email, String name, String lastName, byte age, String password, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Преобразует роли в enum[]
     * Вход ROLE_USER
     * Выход USER
     */
    public void rolesToEnum() {
        enumRoles = roles.stream()
                .map(x -> x.getName().replace(Roles.ROLE_PREFIX,""))
                .map(x -> Roles.valueOf(x))
                .sorted()
                .toArray(Roles[]::new);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", enumRoles='" + enumRoles + '\'' +
                ", roles=" + roles +
                '}';
    }
}
