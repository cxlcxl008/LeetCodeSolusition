package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * SqlBeanGeneralUtils
 * 根据JAVA实体生成SQL建表语句工具
 *
 * @author liuzhi
 * @version 1.0
 * @date 2019-07-25 11:35
 */
public class SqlBeanGeneralUtils {

    private static final char CH_A = 'A';
    private static final char CH_Z = 'Z';
    private static final char CH_UNDER_LINE = '_';
    private static Map<String, String> property2SqlColumnMap = new HashMap<>();

    static {
        property2SqlColumnMap.put("integer", "INT");
        property2SqlColumnMap.put("short", "tinyint");
        property2SqlColumnMap.put("long", "bigint");
        property2SqlColumnMap.put("bigdecimal", "decimal(19,2)");
        property2SqlColumnMap.put("double", "double precision not null");
        property2SqlColumnMap.put("float", "float");
        property2SqlColumnMap.put("boolean", "tinyint");
        property2SqlColumnMap.put("timestamp", "datetime");
        property2SqlColumnMap.put("date", "datetime");
        property2SqlColumnMap.put("string", "VARCHAR(500)");
    }

    public static void main(String[] args) throws Exception {
//        String s = generateSql("com.sohu.mrd.domain.beans.CommentEntity", "gb_commont", "id", null);
//        System.out.println(s);

        String str = generalEntity(
                "jdbc:mysql://127.0.0.1:3306/support-user",
                "root",
                "root",
                "lg_user",
                "com.my.in.out.goods.model.entity", "lg_");
        System.out.println(str);

    }

    private static String generateSql(String className, String tableName, String primaryKey, String filePath) {
        try {
            Class<?> clz = Class.forName(className);
            Field[] fields = clz.getDeclaredFields();
            StringBuilder column = new StringBuilder();
            for (Field f : fields) {
                if (f.getName().equals(primaryKey)) {
                    continue;
                }
                column.append(getColumnSql(f));
            }
            String sqlPrimaryKey = convertToUnderlineName(primaryKey);
            String sqlText = "\n DROP TABLE IF EXISTS `" + tableName + "`; " +
                    " \n CREATE TABLE `" + tableName + "`  (" +
                    " \n \t`" + sqlPrimaryKey + "` bigint(20) NOT NULL AUTO_INCREMENT," +
                    " \n \t" + column +
                    " \n \t `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    " \n PRIMARY KEY (`" + sqlPrimaryKey + "`)" +
                    " \n ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='自动生成表';";
            if (StringUtils.isNotBlank(filePath)) {
                stringToSql(sqlText, filePath);
            }
            return sqlText;
        } catch (ClassNotFoundException e) {
            //log.debug("SQL生成异常：", e);
            return null;
        }
    }

    private static String getColumnSql(Field field) {
        String tpl = "\n \t`%s` %s DEFAULT %s NOT NULL COMMENT '', ";
        String typeName = field.getType().getSimpleName().toLowerCase();
        String sqlType = property2SqlColumnMap.get(typeName);
        if (sqlType == null || sqlType.isEmpty()) {
            //log.info(field.getName() + ":" + field.getType().getName() + " 需要单独创建表");
            return "";
        }
        String column = convertToUnderlineName(field.getName());
        String defaultValue = "''";
        if ("INT".equals(sqlType) || "tinyint".equals(sqlType) || "bigint".equals(sqlType) || "decimal(19,2)".equals(sqlType)
                || "double precision not null".equals(sqlType) || "float".equals(sqlType)) {
            defaultValue = "0";
        }
        return String.format(tpl, column, sqlType.toUpperCase(), defaultValue);
    }

    private static void stringToSql(String str, String path) {
        byte[] sourceByte = str.getBytes();
        try {
            File file = new File(path);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(sourceByte);
            outStream.flush();
            outStream.close();
            System.out.println("生成成功");
        } catch (Exception e) {
            //log.debug("保存SQL文件异常：", e);
        }
    }

    private static String convertToUnderlineName(String camelName) {
        StringBuilder sb = new StringBuilder(camelName);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c >= CH_A && c <= CH_Z) {
                sb.deleteCharAt(i);
                char lowerCh = (char) (c + 32);
                if (i != 0) {
                    sb.insert(i, CH_UNDER_LINE);
                    sb.insert(i + 1, lowerCh);
                } else {
                    sb.insert(i, lowerCh);
                }
            }
        }
        return sb.toString();
    }

    private static String convertToCamalName(String name) {
        StringBuilder sb = new StringBuilder(name);
        for (int i = 1; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c == CH_UNDER_LINE) {
                int i1 = i + 1;
                if (i1 < sb.length()) {
                    String c1 = String.valueOf(sb.charAt(i1)).toUpperCase();
                    sb.replace(i1, i1 + 1, c1);
                }
                sb.deleteCharAt(i);
            }
        }
        return sb.toString();
    }

    public static List<String> generalEntitys(String dbUrl, String dbUsername, String dbPassword, String[] tableNames, String packageName, String removePrefix, String filePath) throws Exception {
        List<String> result = new ArrayList<>();
        for (String tableName : tableNames) {
            String str = generalEntity(dbUrl, dbUsername, dbPassword, tableName, packageName, removePrefix);
            result.add(str);
            if (StringUtils.isNotBlank(str)) {
                String className = convertClassName(tableName, removePrefix);
                for (String content : result) {
                    File file = new File(filePath, className + ".java");
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(content);
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
        }
        return result;
    }

    public static List<String> generalEntitys(String dbUrl, String dbUsername, String dbPassword, String[] tableNames, String packageName, String removePrefix) throws Exception {
        List<String> result = new ArrayList<>();
        for (String tableName : tableNames) {
            result.add(generalEntity(dbUrl, dbUsername, dbPassword, tableName, packageName, removePrefix));
        }
        return result;
    }

    public static List<String> generalEntitys(String dbUrl, String dbUsername, String dbPassword, String[] tableNames, String packageName) throws Exception {
        return generalEntitys(dbUrl, dbUsername, dbPassword, tableNames, packageName, null);
    }

    public static String generalEntity(String dbUrl, String dbUsername, String dbPassword, String tableName, String packageName) throws Exception {
        return generalEntity(dbUrl, dbUsername, dbPassword, tableName, packageName, null);
    }

    public static String generalEntity(String dbUrl, String dbUsername, String dbPassword, String tableName, String packageName, String removePrefix) throws Exception {
        String resultStr = "";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        if (!conn.isClosed()) {
            System.out.println("已成功链接数据库！");
            // 数据库操作;
            String sql = "show full columns from " + tableName;
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery(sql);

            resultStr = getEntityString(rs, tableName, packageName, removePrefix);
            st.close();//关闭资源
        }

        conn.close();//关闭数据库
        return resultStr;
    }

    private static String getEntityString(ResultSet rs, String tableName, String packageName, String removePrefix) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder importSb = new StringBuilder();
        importSb.append("import com.alibaba.fastjson.annotation.JSONField;\n");
        importSb.append("import lombok.Data;\n\n");
        importSb.append("import javax.persistence.GenerationType;;\n\n");
        importSb.append("import javax.persistence.Table;\n\n");
        importSb.append("import java.io.Serializable;\n");

        StringBuilder fileSb = new StringBuilder();
        String className = convertClassName(tableName, removePrefix);
        fileSb.append("/**\n" +
                " * " + className + "\n*\n" +
                " *\n" +
                " * @author liuzhi\n" +
                " * @version 1.0\n" +
                " * @date " + sdf.format(new Date()) + "\n" +
                " */\n");
        fileSb.append("@Data\n");
        fileSb.append("@Table(name = \"" + tableName + "\")\n");
        fileSb.append("public class ");
        fileSb.append(className);
        fileSb.append(" implements Serializable {\n\n");

        boolean dateFlag = false;
        boolean idFlag = false;

        while (rs.next()) {
            String field = rs.getString("Field");
            String type = rs.getString("Type");
            String comment = rs.getString("Comment");
            StringBuilder fieldSb = new StringBuilder();
            fieldSb.append("\t/**\n");
            fieldSb.append("\t * ");
            fieldSb.append(comment);
            fieldSb.append("\n");
            fieldSb.append("\t */\n");
            if ("id".equals(field)) {
                fieldSb.append("\t@Id\n");
                fieldSb.append("\t@GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                if (!idFlag) {
                    idFlag = true;
                    importSb.append("import javax.persistence.Id;\n");
                    importSb.append("import javax.persistence.GeneratedValue;\n");
                }
            }
            fieldSb.append("\t@JSONField(name=\"");
            fieldSb.append(field);
            fieldSb.append("\")\n");
            fieldSb.append("\tprivate ");
            String type1 = getType(type);
            if ("Date".equals(type1)) {
                if (!dateFlag) {
                    dateFlag = true;
                    importSb.append("import java.util.Date;\n");
                }
            }
            fieldSb.append(type1);
            fieldSb.append(" ");
            fieldSb.append(convertToCamalName(field));
            fieldSb.append(" ;\n\n");

            fileSb.append(fieldSb);
        }
        fileSb.append("\n}");

        return "package " + packageName + ";\n\n" + importSb.toString() + "\n" + fileSb.toString();
    }

    private static String getType(String sqlType) {
        // 去掉长度
        if (sqlType.contains("(")) {
            sqlType = sqlType.substring(0, sqlType.indexOf("("));
        }
        if ("int".equals(sqlType) || "tinyint".equals(sqlType)) {
            return "Integer";
        } else if ("bigint".equals(sqlType)) {
            return "Long";
        } else if ("double".equals(sqlType)) {
            return "Double";
        } else if ("datetime".equals(sqlType) || "timestamp".equals(sqlType)) {
            return "Date";
        } else {
            return "String";
        }
    }

    private static String convertClassName(String tableName, String removePrefix) {
        String tempName = tableName;
        if (StringUtils.isNotBlank(removePrefix) && tableName.startsWith(removePrefix)) {
            tempName = StringUtils.substring(tableName, tableName.indexOf(removePrefix) + removePrefix.length());
        }
        String className = convertToCamalName(tempName);
        className = className.substring(0, 1).toUpperCase() + className.substring(1);
        return className + "Entity";
    }

}
