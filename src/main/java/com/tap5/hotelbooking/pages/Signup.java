package com.tap5.hotelbooking.pages;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.tap5.hotelbooking.annotations.AnonymousAccess;
import com.tap5.hotelbooking.domain.CrudServiceDAO;
import com.tap5.hotelbooking.domain.QueryParameters;
import com.tap5.hotelbooking.domain.entities.User;
import com.tap5.hotelbooking.security.AuthenticationException;
import com.tap5.hotelbooking.services.Authenticator;

/**
 * This page the user can create an account
 * 
 * @author karesti
 * @version 1.0
 */
@AnonymousAccess
public class Signup
{

    @Property
    @Validate("username")
    private String username;

    @Property
    @Validate("required, minlength=3, maxlength=50")
    private String fullName;

    @Property
    @Validate("required,email")
    private String email;

    @Property
    private String password;

    @Property
    private String verifyPassword;

    @Property
    private String kaptcha;

    @Inject
    private CrudServiceDAO crudServiceDAO;

    @Component
    private Form registerForm;

    @Inject
    private Messages messages;

    @Inject
    private Authenticator authenticator;

    @InjectPage
    private Signin signin;

    @OnEvent(value = EventConstants.SUCCESS, component = "RegisterForm")
    public Object proceedSignup()
    {
        if (!verifyPassword.equals(password))
        {
            registerForm.recordError(messages.get("error.verifypassword"));

            return null;
        }

        User userVerif = crudServiceDAO.findUniqueWithNamedQuery(
                User.BY_USERNAME_OR_EMAIL,
                QueryParameters.with("username", username).and("email", email).parameters());

        if (userVerif != null)
        {
            registerForm.recordError(messages.get("error.userexists"));

            return null;
        }

        User user = new User(fullName, username, email, password);

        crudServiceDAO.create(user);

        try
        {
            authenticator.login(username, password);
        }
        catch (AuthenticationException ex)
        {
            registerForm.recordError("Authentication process has failed");
            return this;
        }

        return Search.class;
    }
}
