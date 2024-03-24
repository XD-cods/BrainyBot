package org.example.DAO;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class UserInfoDAO {
  public static final  SessionFactory sessionFactory = new Configuration()
          .configure("hibernate.config.xml")
          .buildSessionFactory();

}
