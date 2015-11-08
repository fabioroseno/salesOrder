package domain;

import entities.Context;
import entities.Repository;
import entities.annotations.Editor;
import entities.annotations.Param;
import entities.annotations.ParameterDescriptor;
import entities.annotations.PropertyDescriptor;
import entities.annotations.UserRoles;
import entities.annotations.Username;
import entities.annotations.View;
import entities.annotations.Views;
import entities.descriptor.PropertyType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Entity
@Table(name = "USERS")
@NamedQueries({
    @NamedQuery(name = "Authentication",
               query = "  From User u"
                     + " Where u.username = :username "
                     + "   and u.password = :password "),    
    @NamedQuery(name = "Administrators",
               query = "From User u Where 'Admin' in elements(u.roles)")})
@Views({
    /**
     * View de Login
     */
    @View(name = "Login",
         title = "Login",
       members = "[#username;#password;login()]",
    namedQuery = "Select new domain.User()",
         roles = "NOLOGGED"),
    /**
     * View de Login
     */
    @View(name = "Logout",
         title = "Logout",
       members = "['':*photo,[*username;"
               + "            *roles;"
               + "            [newPassword(),logout()]]]",
    namedQuery = "From User u Where u = :user",
        params = {@Param(name = "user", value = "#{context.currentUser}")},
         roles = "LOGGED"),
    /**
     * Cadastro de Usuarios
     */
    @View(name = "Users",
         title = "Users",
       members = "User[username,newPhoto();"
               + "     password,newPassword();"
               + "     roles:2]"
               + ",*photo",
      template = "@CRUD+@PAGER",
         roles = "Admin")
})
public class User implements Serializable {

    public enum Role { Admin, Salesman, Cashier, Supervisor }
    
    @Id @GeneratedValue
    private Integer id;
    
    @Username
    @Column(length = 25, unique = true)    
    @NotEmpty(message = "Enter the username")
    private String username;
    
    @Column(length = 32)
    @NotEmpty(message = "Enter the password")
    @Type(type = "entities.security.Password")
    @PropertyDescriptor(secret = true, displayWidth = 25)    
    private String password;
    
    @UserRoles
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch= FetchType.EAGER)
    private List<Role> roles = new ArrayList<Role>();
    
    @Lob
    @Editor(propertyType = PropertyType.IMAGE)
    private byte[] photo;

    public User() {
    }
    
    public User(String username, String password, Role... roles) {
        this.username = username;
        this.password = password;
        this.roles.addAll(Arrays.asList(roles));
    }
    
    // <editor-fold defaultstate="collapsed" desc="Autorização & Autenticação">    
    public String login() {        
        if (Repository.queryCount("Administrators") == 0) {
            User admin = new User(username, password, Role.Admin);
            Repository.save(admin);
            Context.setCurrentUser(admin);
            return "go:domain.User@Users";
        } else {
            List<User> users = Repository.query("Authentication", username, password);
            if (users.size() == 1) 
                Context.setCurrentUser(users.get(0));
             else 
                throw new SecurityException("Username/Password invalid!");
            
        }
        return "go:home";
    }

    static public String logout() {
        Context.clear();
        return "go:domain.User@Login";
    }// </editor-fold>

    public String newPassword(
            @ParameterDescriptor(displayName = "New Password", 
                                      secret = true, 
                                    required = true) 
            String newPassword,
            @ParameterDescriptor(displayName = "Confirm password", 
                                      secret = true, 
                                    required = true) 
            String rePassword) {
        if (newPassword.equals(rePassword)) {
            this.setPassword(newPassword);            
            Repository.save(this);
            return "Password changed successfully!";
        } else {
            throw new SecurityException("The passwords are different!");
        }
    }    
    
    public void newPhoto(@ParameterDescriptor(displayName = "New Photo") 
                         byte[] newPhoto)  {
        this.photo = newPhoto;
        if (id != null) {
            Repository.save(this);
        }
    }

    @Override
    public String toString() {
        return username;
    }
}
