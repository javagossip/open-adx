package top.openadexchange.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.mybatisflex.core.keygen.CustomKeyGenerator;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Objects;

public class MyBatisFlexCodeGenerator {

    private static final String DB_URL = """
            jdbc:mysql://101.126.128.177:13306/open-adexchange?\
            allowPublicKeyRetrieval=true&remarks=true&\
            useInformationSchema=true&useUnicode=true&characterEncoding=utf-8&\
            useSSL=false&serverTimezone=UTC&tinyInt1isBit=true
            """;
    private static final String PROJECT_NAME = "open-adexchange-codegen";
    private static final String ENTITIES_MODULE = "open-adexchange-model";
    private static final String DAO_MODULE = "open-adexchange-dao";
    private static final String ENTITIES_PACKAGE = "top.openadexchange.model";
    private static final String BASE_PACKAGE = "top.openadexchange";

    public static void main(String[] args) {
        String path = Objects.requireNonNull(MyBatisFlexCodeGenerator.class.getResource("/")).getPath();
        String projectPath = path.substring(0, path.indexOf(PROJECT_NAME));

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(DB_URL);
        dataSource.setUsername("root");
        dataSource.setPassword("Hello%1234");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        String[] tables = new String[]{
                "sys_entity_code"
        };
        genEntities(projectPath, dataSource, tables);
        generateOthers(projectPath, dataSource, tables);
    }

    private static void generateOthers(String path, DataSource dataSource, String[] tables) {
        GlobalConfig globalConfig = createGlobalConfigForOthers(path);
        globalConfig.setGenerateTable(tables);
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

    private static GlobalConfig createGlobalConfigForOthers(String path) {
        return createGlobalConfigUseStyle1(path);
    }

    private static void genEntities(String path, DataSource dataSource, String[] generateTables) {
        GlobalConfig globalConfig = createGlobalConfigForEntities(path);
        globalConfig.setGenerateTable(generateTables);
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

    private static GlobalConfig createGlobalConfigForEntities(String projectPath) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSourceDir(projectPath + String.format("%s/src/main/java", ENTITIES_MODULE));
        //设置根包
        //globalConfig.setBasePackage("com.hexunion.mos.domain");
        globalConfig.setEntityPackage(ENTITIES_PACKAGE);
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        globalConfig.setEntityJdkVersion(21);

        globalConfig.setEntityOverwriteEnable(true);
        globalConfig.setMapperXmlOverwriteEnable(true);

        return globalConfig;
    }

    public static GlobalConfig createGlobalConfigUseStyle1(String projectPath) {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSourceDir(projectPath + String.format("%s/src/main/java", DAO_MODULE));
        globalConfig.setMapperXmlPath(projectPath + String.format("%s/src/main/resources/mapper", DAO_MODULE));
        //设置根包
        globalConfig.setBasePackage("top.openadexchange");
        globalConfig.setEntityJdkVersion(21);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperXmlGenerateEnable(true);
        globalConfig.setMapperAnnotation(true);
        globalConfig.setControllerGenerateEnable(false);
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        globalConfig.setServiceClassSuffix("Dao");
        globalConfig.setServiceImplClassSuffix("DaoImpl");
        globalConfig.setAuthor("top.openadexchange");
        globalConfig.setEntityPackage(ENTITIES_PACKAGE);
        globalConfig.setMapperPackage(globalConfig.getBasePackage() + ".mapper");
        globalConfig.setServicePackage(globalConfig.getBasePackage() + ".dao");
        globalConfig.setServiceImplPackage(globalConfig.getBasePackage() + ".dao.impl");

        globalConfig.setServiceOverwriteEnable(true);
        globalConfig.setServiceImplOverwriteEnable(true);
        globalConfig.setMapperOverwriteEnable(true);
        globalConfig.setEntityOverwriteEnable(true);
        globalConfig.setMapperXmlOverwriteEnable(true);

        return globalConfig;
    }
}
