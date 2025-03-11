    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author herma
 */
@Entity
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByFirstname", query = "SELECT u FROM User u WHERE u.firstname = :firstname"),
    @NamedQuery(name = "User.findByLastname", query = "SELECT u FROM User u WHERE u.lastname = :lastname"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByPhoneNumber", query = "SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "User.findByIsAdmin", query = "SELECT u FROM User u WHERE u.isAdmin = :isAdmin"),
    @NamedQuery(name = "User.findByIsDeleted", query = "SELECT u FROM User u WHERE u.isDeleted = :isDeleted"),
    @NamedQuery(name = "User.findByCreatedAt", query = "SELECT u FROM User u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "User.findByDeletedAt", query = "SELECT u FROM User u WHERE u.deletedAt = :deletedAt")})


public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "firstname")
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "lastname")
    private String lastname;
    @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "phone_number")
    private String phoneNumber;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_admin")
    private boolean isAdmin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    //Singleton EntityManager jobb hívássa
    @PersistenceUnit
    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        if (emf == null) {
        emf = Persistence.createEntityManagerFactory("com.maven_vintage_project_war_1.0-SNAPSHOTPU");
        }
        return emf.createEntityManager();
    }

    public User() {
    }
    
    public User(Integer id) {
        EntityManager em = emf.createEntityManager();
        
        try {
            User u = em.find(User.class, id);
            
            this.id = u.getId(); //0
            this.username = u.getUsername(); //1
            this.firstname = u.getFirstname();  //2
            this.lastname = u.getLastname(); //3
            this.email = u.getEmail(); //4
            this.phoneNumber = u.getPhoneNumber(); //5
            this.isAdmin = u.getIsAdmin(); //6
            this.isDeleted = u.getIsDeleted(); //7
            this.createdAt = u.getCreatedAt(); //8
            this.deletedAt = u.getDeletedAt(); //9
        } catch (Exception ex) {
            System.err.println("Hiba: " + ex.getLocalizedMessage());
        } finally {
            em.clear();
            em.close();
        }
    }

    public User(Integer id, String username, String firstname, String lastname, String email, String phoneNumber, String password, Boolean isAdmin, Boolean isDeleted, Date createdAt, Date deletedAt) {
        this.id = id; //0
        this.username = username; //1
        this.firstname = firstname; //2
        this.lastname = lastname; //3
        this.email = email; //4
        this.phoneNumber = phoneNumber; //5
        this.password = password; //6
        this.isAdmin = isAdmin; //7
        this.isDeleted = isDeleted; //8
        this.createdAt = createdAt; //9
        this.deletedAt = deletedAt; //10
    }
    
public User(String username, String firstname, String lastname, String email, String phoneNumber, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.maven.vintage_project.model.User[ id=" + id + " ]";
    }
    
    
    public User login(String email, String password) {
        EntityManager em = getEntityManager();
        
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("login");
            
            spq.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
            
            spq.setParameter("emailIN", email);
            spq.setParameter("passwordIN", password);
            
            spq.execute();
            
            List<Object[]> resultList = spq.getResultList();
            User toReturn = new User();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Object[] o : resultList) {
                User u = new User(
                        Integer.valueOf(o[0].toString()), //id
                        o[1].toString(), //username
                        o[2].toString(), //firstname
                        o[3].toString(), //lastname
                        o[4].toString(), //email
                        o[5].toString(), //phoneNumber
                        o[6].toString(), //password
                        Boolean.parseBoolean(o[7].toString()), //isAdmin
                        Boolean.parseBoolean(o[8].toString()), //IsDeleted
                        formatter.parse(o[9].toString()), //createdAt
                        o[10] == null ? null : formatter.parse(o[10].toString()) //deletedAt
                );
                toReturn = u;
                System.out.println(u);
            }
            
            System.out.println(toReturn);
            return toReturn;
        } catch (NumberFormatException | ParseException e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public Boolean registerUser(User u) {
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("registration");

            spq.registerStoredProcedureParameter("userIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("firstnameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("lastnameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("phoneIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
            
            /*System.out.println("Passing Parameters to Stored Procedure:");
            System.out.println("Username: " + u.getUsername());
            System.out.println("Firstname: " + u.getFirstname());
            System.out.println("Lastname: " + u.getLastname());
            System.out.println("Email: " + u.getEmail());
            System.out.println("Phone: " + u.getPhoneNumber());
            System.out.println("Password: " + u.getPassword());*/

            spq.setParameter("userIN", u.getUsername());
            spq.setParameter("firstnameIN", u.getFirstname());
            spq.setParameter("lastnameIN", u.getLastname());
            spq.setParameter("emailIN", u.getEmail());
            spq.setParameter("phoneIN", u.getPhoneNumber());
            spq.setParameter("passwordIN", u.getPassword());

            spq.execute();

            return true;
        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public Boolean registerAdmin(User u) {
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("registerAdmin");

            spq.registerStoredProcedureParameter("userIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("firstnameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("lastnameIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("phoneIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);

            spq.setParameter("userIN", u.getUsername());
            spq.setParameter("firstnameIN", u.getFirstname());
            spq.setParameter("lastnameIN", u.getLastname());
            spq.setParameter("emailIN", u.getEmail());
            spq.setParameter("phoneIN", u.getPhoneNumber());
            spq.setParameter("passwordIN", u.getPassword());

            spq.execute();

            return true;
        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public static Boolean isUserExists(String email) {
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("isUserExists");

            spq.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("resultOUT", Boolean.class, ParameterMode.OUT);

            spq.setParameter("emailIN", email);

            spq.execute();

            Boolean result = Boolean.valueOf(spq.getOutputParameterValue("resultOUT").toString());

            return result;
        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }
    //Megkapjuk az összes felhasználót
    public List<User> getAllUser() {
        EntityManager em = getEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllUser");
            spq.execute();

            List<User> toReturn = new ArrayList();
            List<Object[]> resultList = spq.getResultList();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Object[] record : resultList) {
                User u = new User(
                        Integer.valueOf(record[0].toString()), //id
                        record[1].toString(), //username
                        record[2].toString(), //firstname
                        record[3].toString(), //lastname
                        record[4].toString(), //email
                        record[5].toString(), //phoneNum
                        record[6].toString(), //password
                        Boolean.parseBoolean(record[7].toString()), //isAdmin
                        Boolean.parseBoolean(record[8].toString()), //isDeleted
                        formatter.parse(record[9].toString()), //createdAt
                        record[10] == null ? null : formatter.parse(record[10].toString()) //deletedAt
                );

                toReturn.add(u);
            }

            return toReturn;

        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }
    
    public Boolean changePassword(Integer userId, String newPassword, Integer creator) {
        EntityManager em = emf.createEntityManager();

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("changePassword");

            spq.registerStoredProcedureParameter("userIdIN", Integer.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("newPasswordIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("creatorIN", Integer.class, ParameterMode.IN);

            spq.setParameter("userIdIN", userId);
            spq.setParameter("newPasswordIN", newPassword);
            spq.setParameter("creatorIN", creator);

            spq.execute();

            return true;
        } catch (Exception e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return false;
        } finally {
            em.clear();
            em.close();
        }
    }
    
}
