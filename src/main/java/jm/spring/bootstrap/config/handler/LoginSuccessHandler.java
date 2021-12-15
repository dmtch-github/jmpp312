package jm.spring.bootstrap.config.handler;

import jm.spring.bootstrap.controller.AdminController;
import jm.spring.bootstrap.controller.UserController;
import jm.spring.bootstrap.entity.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String url = "/";
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains(Roles.ROLE_ADMIN)) {
            url = AdminController.URL_ROOT;
        } else if(roles.contains(Roles.ROLE_USER)) {
            url = UserController.URL_ROOT;
        }
        response.sendRedirect(url);
    }
}