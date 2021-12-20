package jm.spring.bootstrap.dao;

import jm.spring.bootstrap.entity.Role;
import jm.spring.bootstrap.entity.Roles;
import jm.spring.bootstrap.entity.User;
import jm.spring.bootstrap.repository.RoleRepository;
import jm.spring.bootstrap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDaoImp implements UserDao {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByName(username);
        if(user == null) {
            if(username.equals(Roles.SUPER_USER_NAME) && (getCountUsers() == 0)) {
                //создаем динамического пользователя для работы с системой
                return new User(1, Roles.SUPER_USER_NAME, "Admin", "Admin",
                        (byte) 32, Roles.SUPER_USER_PASSWORD, Collections.singleton(new Role(Roles.ROLE_ADMIN)));
            }

            throw new UsernameNotFoundException("Пользователь " + username + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        for (User u: users) {
            u.rolesToEnum(); //преобразуем роли в текстовое описание
        }
        return users;
    }

    @Override
    public void saveUser(User user) {
        String[] namesRole = Arrays.stream(user.getEnumRoles())
            .map(Roles::toString)
            .filter(x -> x.equals(Roles.ADMIN) || x.equals(Roles.USER))
            .distinct()
            .map(x -> Roles.ROLE_PREFIX+x)
            .toArray(String[]::new);

        //если ролей нет, то назначаем роль USER
        if(namesRole.length == 0) {
            namesRole = new String[]{Roles.ROLE_USER};
        }

        Set<Role> roles = new HashSet<>();
        for(String name : namesRole) {
            Role role = getRoleByName(name); //получаем роль из БД
            if(role == null) {
                role = new Role(name); //создаем роль, когда её нет в БД
            }
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUser(int id) {
        User user = null;
        Optional<User> optional = userRepository.findById(id);
        if(optional.isPresent()) {
            user = optional.get();
            user.rolesToEnum();
        }
        return user;
    }

    @Override
    public User getUserByName(String username) {
        User user = userRepository.findUserByEmail(username);
        if(user != null) {
            user.rolesToEnum();
        }
        return user;
    }


    private Role getRoleByName(String rolename) {
        return roleRepository.findRoleByName(rolename);
    }

    /**
     * Проверяет наличие записей в БД пользователей
     * Используется при создании временного администратора
     */
    private int getCountUsers() {
        return (int) userRepository.count();
    }

}
