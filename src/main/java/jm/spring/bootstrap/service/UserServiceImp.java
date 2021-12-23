package jm.spring.bootstrap.service;

import jm.spring.bootstrap.dao.UserDao;
import jm.spring.bootstrap.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDao userDao;

    private List<User> users;
    private boolean reloadUsers = true;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.loadUserByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        if(reloadUsers) {
            users = userDao.getUsers();
            reloadUsers = false;
        }
        return users;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userDao.saveUser(user);
        reloadUsers = true;
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userDao.deleteUser(id);
        reloadUsers = true;
    }

    @Override
    public User getUser(int id) {
        return userDao.getUser(id);
    }

    @Override
    public User getUserByName(String username) {
        return userDao.getUserByName(username);
    }
}
