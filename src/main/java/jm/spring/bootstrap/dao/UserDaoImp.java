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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Repository
public class UserDaoImp implements UserDao {

//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager em;


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
        List<User> users = em.createQuery("SELECT DISTINCT u FROM User u JOIN FETCH u.roles r",User.class)
                .getResultList();
        for (User u: users) {
            u.rolesToEnum(); //преобразуем роли в текстовое описание
        }
        return users;
    }

    @Override
    public void saveUser(User user) {
        String[] namesRole = Arrays.stream(user.getEnumRoles())
            .filter(x -> x.equals(Roles.ADMIN) || x.equals(Roles.USER))
            .map(Roles::toString)
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
        if(user.getId() == 0) {
            em.persist(user);
        } else {
            em.merge(user);
        }
    }

    @Override
    public void deleteUser(int id) {
        System.out.println("Буду удалять юзера с id=" + id);
        User user = em.find(User.class, id);
        System.out.println("Нашел юзера " + user);
        em.remove(user);
    }

    @Override
    public User getUser(int id) {
        User user = em.find(User.class, id);
        if(user != null) {
            user.rolesToEnum();
        }
        return user;
    }

    @Override
    public User getUserByName(String username) {
        String hqlRequest = "SELECT u FROM User u JOIN FETCH u.roles r WHERE email = :email";

        List<User> list = em.createQuery(hqlRequest, User.class)
                .setParameter("email", username)
                .getResultList();
        User user = list.isEmpty() ? null: list.get(0);
        if(user != null) {
            user.rolesToEnum();
        }
        return user;
    }


    private Role getRoleByName(String rolename) {
        String hqlRequest = "FROM Role WHERE role = :role";
        List<Role> list = em.createQuery(hqlRequest, Role.class)
                .setParameter("role", rolename)
                .getResultList();
        return list.isEmpty() ? null: list.get(0);
    }

    /**
     * Проверяет наличие записей в БД пользователей
     * Используется при создании временного администратора
     */
    private int getCountUsers() {
        String hqlRequest = "SELECT COUNT(u) FROM User u";
        List<Long> list = em.createQuery(hqlRequest,Long.class).getResultList();
        return (int) (list.isEmpty()?0:list.get(0));
    }

}
