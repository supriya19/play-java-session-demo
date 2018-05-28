package model;

import org.apache.commons.lang3.StringUtils;
import play.data.validation.Constraints;

@Constraints.Validate
public class Profile implements Constraints.Validatable<String> {

    @Constraints.Required
    public String email;
    @Constraints.Required
    public String password;

    @Override
    public String validate() {
        if (authenticate(email, password) == null) {
            // You could also return a key defined in conf/messages
            return "Invalid email or password";
        }
        return null;
    }

    public Object authenticate(String email, String password) {
        //TODO: we can add more conditions
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(password)) {
            return email + password;
        } else {
            return null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
