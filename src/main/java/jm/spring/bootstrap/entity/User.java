package jm.spring.bootstrap.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter

@Entity
@Table(name="users312")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="email")
    private String email;   //уникальное значение

    @Column(name="name")
    private String name;

    @Column(name="last_name")
    private String lastName;

    
    @Column(name="age")
    private byte age;

    @Column(name="password")
    private String password;

    @Transient
    private Roles[] enumRoles;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinTable(name = "users312_roles312",
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
                .map(Roles::valueOf)
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
