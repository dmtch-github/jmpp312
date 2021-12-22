package jm.spring.bootstrap.controller;

import jm.spring.bootstrap.entity.Role;
import jm.spring.bootstrap.entity.Roles;
import jm.spring.bootstrap.entity.User;
import jm.spring.bootstrap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin3")
public class TestController {

    public static final String URL_ROOT = "/admin";
    private static final String NAME_URL_ROOT = "urlRoot";
    private static final String COMMAND_REDIRECT = "redirect:";
    private final UserService userService;

    @Autowired
    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String admin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        String stringRoles = roles.stream().map(x -> x.replace(Roles.ROLE_PREFIX,"")).sorted().collect(Collectors.joining(" ","",""));
        model.addAttribute("stringRoles", stringRoles);
        model.addAttribute(NAME_URL_ROOT, URL_ROOT);
        model.addAttribute("urlUser", "/user");
        model.addAttribute("listUsers", userService.getUsers());

        System.out.println("Зашел в индекс");
        return "del_test";
    }

    @GetMapping("/{tab}")
    public String tab(@PathVariable String tab, Model model) {
        model.addAttribute(NAME_URL_ROOT, URL_ROOT);
        switch(tab) {
            case "tab1":
                System.out.println("Зашел в таб1");
                model.addAttribute("listUsers", userService.getUsers());
                return "_table-admin";

            case "tab2":
                System.out.println("Зашел в таб2");
                model.addAttribute("user", new User());
                return "_new_user";
        }
        return "del_empty";
    }


    @GetMapping("/delete/{id}")
    public String tab(@PathVariable Integer id, Model model) {
        System.out.println("Зашел в DELETE-ID");
        return COMMAND_REDIRECT + URL_ROOT;
    }



    /**
     * Сохраняет данные пользователя в БД,
     * динамически меняет права авторизованного администратора
     * и перенаправляет на главную страницу
     */
    @PostMapping(value = "", params = "save")
    public String saveUser(@ModelAttribute("user") User user) {

        System.out.println("Зашел в saveUser " + user);

        userService.saveUser(user);
        //для текущего пользователя делаем динамическую авторизацию: смена прав
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(((UserDetails) auth.getPrincipal()).getUsername().equals(user.getUsername())) {
            List<String> newRoles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            List<GrantedAuthority> auths = getAuthorities(newRoles);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),auth.getCredentials(),auths);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        return COMMAND_REDIRECT + URL_ROOT;
    }






    /**
     * Удаляет пользователя из БД и
     * перенаправляет на главную страницу
     */
    @PostMapping(value = "/{id}", params = "delete")
    public String deleteUser(ModelMap model,
                             @PathVariable("id") int id) {
        System.out.println("Зашел в УДаление Юзера");
//        userService.deleteUser(id);
        return COMMAND_REDIRECT + URL_ROOT;
    }

    /**
     * Открывает форму для редактирования
     * данных пользователя с id
     */
    @PostMapping(value = "/{id}", params = "update")
    public String updateUser(@PathVariable("id") int id,
                             Model model) {
        System.out.println("Зашел в Редактирование Юзера");
//        model.addAttribute("user", userService.getUser(id));
//        model.addAttribute(NAME_URL_ROOT, URL_ROOT);
//        return "edit-user";
        return "del_empty";
    }

    /**
     * Открывает форму для редактирования
     * данных нового пользователя
     */
    @PostMapping(value = "", params = "add")
    public String addUser(ModelMap model) {
        User user = new User();
//        user.setTextRoles(Roles.USER);
        user.setEnumRoles(new Roles[]{Roles.USER});
        model.addAttribute("user", user);
        model.addAttribute(NAME_URL_ROOT, URL_ROOT);
        return "del_edit-user";
    }


    private List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> auths = new ArrayList<>();
        if(!roles.isEmpty()) {
            for (String s : roles) {
                auths.add(new SimpleGrantedAuthority(s));
            }
        }
        return auths;
    }

    /**
     * Перенаправляет на главную страницу.
     * Используется при отмене операций: Сохранить, Изменить
     */
    @PostMapping(value = "", params = "redirect")
    public String redirect(ModelMap model) {
        return COMMAND_REDIRECT + URL_ROOT;
    }


}
