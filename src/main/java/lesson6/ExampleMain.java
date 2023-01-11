package lesson6;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class ExampleMain {

    public static void main(String[] args) throws IOException {
        SqlSession session = null;
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new
                    SqlSessionFactoryBuilder().build(inputStream);
            session = sqlSessionFactory.openSession();
            lesson6.db.dao.CategoriesMapper categoriesMapper = session.getMapper(lesson6.db.dao.CategoriesMapper.class);
            lesson6.db.model.CategoriesExample example = new lesson6.db.model.CategoriesExample();

            //example.createCriteria().andIdEqualTo(2);
            example.createCriteria().andIdBetween(1,2);
            List<lesson6.db.model.Categories> list = categoriesMapper.selectByExample(example);
            System.out.println("count: "+categoriesMapper.countByExample(example)); // 1
            //System.out.println("id: " + list.get(0).getId() + ", title: " + list.get(0).getTitle()); // id: 1, title: Food
            for (lesson6.db.model.Categories value : list) {
                System.out.println("id: " + value.getId() + ", title: " + value.getTitle());
            }
            lesson6.db.model.Categories categories = new lesson6.db.model.Categories();
            categories.setTitle("test");
            categoriesMapper.insert(categories);
            session.commit();

            lesson6.db.model.CategoriesExample example2 = new lesson6.db.model.CategoriesExample();
            example2.createCriteria().andTitleLike("%test%");
            List<lesson6.db.model.Categories> list2 = categoriesMapper.selectByExample(example2);
            lesson6.db.model.Categories categories2 = list2.get(0);
            categories2.setTitle("test100");
            categoriesMapper.updateByPrimaryKey(categories2);
            session.commit();

            categoriesMapper.deleteByPrimaryKey(categories2.getId());
            session.commit();

//            lesson6.db.model.CategoriesExample example3 = new lesson6.db.model.CategoriesExample();
//            example3.createCriteria().andTitleLike("%Test%");
//            categoriesMapper.deleteByExample(example3);
//            session.commit();

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
