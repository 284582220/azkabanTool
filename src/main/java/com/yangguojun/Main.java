package com.yangguojun;

import com.yangguojun.config.SystemConfigUtils;
import com.yangguojun.util.HttpUtil;
import com.yangguojun.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统启动入口
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static String sessionId;

    private static String URL;

    private static String USERNAME;

    private static String PASSWORD;

    private static final String CREATENEWPROJECT = "createproject";

    private static final String UPLOADZIPFILE = "uploadzipfile";

    private static final String DELETEPROJECT = "deleteproject";

    private static final String EXECUTEPROJECT = "executeproject";

    public static void main(String[] args) throws Exception {
        if (args == null) {
            log.info("系统启动");
            throw new Exception("入参不能为空");
        }
        System.out.println("params count:" + args.length);
        String environmentParam = args[0];
        checkEnvironmentParam(environmentParam);
        loadConfigInfo(environmentParam);
        String opsName = args[1];
        if (CREATENEWPROJECT.equals(opsName)) {
            if (args.length != 4 && args.length != 6) {
                throw new Exception("参数不正确，请输入:opsname[createproject]、projectname、description、[zipPath]、[zipName]");
            }
            if (args.length == 4) {
                String projectname = args[2];
                String description = args[3];
                loginAction();
                createProject(projectname, description);
                return;
            }
            String projectname = args[2];
            String description = args[3];
            String zipPath = args[4];
            String zipName = args[5];
            if (!zipName.endsWith(".zip")) {
                throw new Exception("入参参数zip文件名不正确，请以zip结尾:" + zipName);
            }
            loginAction();
            createProject(projectname, description);
            uploadProjectZipFile(projectname, zipPath, zipName);
        } else if (UPLOADZIPFILE.equals(opsName)) {
            if (args.length != 5) {
                throw new Exception("参数不正确，请输入:opsname[uploadzipfile]、projectname、zipPath、zipName");
            }
            String projectname = args[2];
            String zipPath = args[3];
            String zipName = args[4];
            if (!zipName.endsWith(".zip")) {
                throw new Exception("入参参数zip文件名不正确，请以zip结尾:" + zipName);
            }
            loginAction();
            uploadProjectZipFile(projectname, zipPath, zipName);
        } else if (DELETEPROJECT.equals(opsName)) {
            if (args.length != 3) {
                throw new Exception("参数不正确，请输入:opsname[deleteproject]、projectname");
            }
            String projectname = args[2];
            loginAction();
            deleteProject(projectname);
        } else if (EXECUTEPROJECT.equals(opsName)) {
            if (args.length != 4) {
                throw new Exception("参数不正确，请输入:opsname[executeproject]、projectname、flowname");
            }
            String projectname = args[2];
            String flowname = args[3];
            loginAction();
            executeProject(projectname, flowname);
        } else {
            throw new Exception("不支持的操作[" + opsName + "]，目前系统只支持[createproject、uploadzipfile、deleteproject、executeproject]，如需其他操作请联系yangguojun");
        }
    }

    private static void checkEnvironmentParam(String environmentParam) throws Exception{
        if(environmentParam == null || "".equals(environmentParam)){
            throw new Exception("操作类型为空");
        }
        if(!"dev".equals(environmentParam) && !"test".equals(environmentParam) && !"stage".equals(environmentParam) && !"prod".equals(environmentParam)){
            throw new Exception("目前只支持dev、test、stage、prod四个环境");
        }
    }

    private static void loadConfigInfo(String environmentParam) {
        URL = SystemConfigUtils.getProperty("azkaban.web.url.".concat(environmentParam));
        USERNAME = SystemConfigUtils.getProperty("azkaban.web.username");
        PASSWORD = SystemConfigUtils.getProperty("azkaban.web.password");
        log.info("url=" + URL);
        log.info("session.id=" + sessionId);
        log.info("username=" + USERNAME);
        log.info("password=" + PASSWORD);
    }

    private static void loginAction() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "login");
        params.put("username", USERNAME);
        params.put("password", PASSWORD);
        String result = HttpUtil.httpPost(URL, params);
        Map loginResponseEntity = JsonUtil.jsonToObject(result, Map.class);
        if (loginResponseEntity != null && loginResponseEntity.get("session.id") != null) {
            sessionId = (String) loginResponseEntity.get("session.id");
            log.info("session.id=" + sessionId);
        } else {
            String errorMsg = (String) loginResponseEntity.get("error");
            throw new Exception("系统登录失败:" + errorMsg);
        }
    }

    private static void createProject(String projectName, String projectDescription) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("session.id", sessionId);
        params.put("name", projectName);
        params.put("description", projectDescription);
        String result = HttpUtil.httpPost(URL + "/manager?action=create", params);
        Map response = JsonUtil.jsonToObject(result, Map.class);
        if (response != null && response.get("status") != null) {
            String data = (String) response.get("status");
            log.info("createProject result:" + data);
        } else {
            String errorMsg = (String) response.get("error");
            String message = (String) response.get("message");
            throw new Exception("创建失败:error:" + errorMsg + "message:" + message);
        }
    }

    private static void deleteProject(String projectName) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("delete", "true");
        params.put("session.id", sessionId);
        params.put("project", projectName);
        String result = HttpUtil.httpGet(URL + "/manager", params);
        if ("".equals(result)) {
            log.info("结果为空");
            return;
        }
        Map response = JsonUtil.jsonToObject(result, Map.class);
        if (response != null && response.get("status") != null) {
            String data = (String) response.get("status");
            log.info("createProject result:" + data);
        } else {
            throw new Exception("delete fail" + result);
        }
    }

    private static void uploadProjectZipFile(String projectName, String filePath, String fileName) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("session.id", sessionId);
        params.put("project", projectName);
        params.put("ajax", "upload");
        String result = HttpUtil.uploadFiles(URL + "/manager", params, filePath, fileName);
        if ("".equals(result)) {
            log.info("结果为空");
            return;
        }
        Map response = JsonUtil.jsonToObject(result, Map.class);
        if (response != null && response.get("projectId") != null) {
            String projectId = (String) response.get("projectId");
            log.info("upload zip file project id :" + projectId);
            System.out.println("上传成功:" + result);
        } else {
            throw new Exception("uploadProjectZipFile fail" + result);
        }
    }

    private static void executeProject(String projectName, String flowId) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("session.id", sessionId);
        params.put("project", projectName);
        params.put("ajax", "executeFlow");
        params.put("flow", flowId);
        String result = HttpUtil.httpGet(URL + "/executor", params);
        if ("".equals(result)) {
            log.info("结果为空");
            return;
        }
        Map response = JsonUtil.jsonToObject(result, Map.class);
        if (response != null && response.get("error") == null) {
            String projectId = (String) response.get("projectId");
            log.info("upload zip file project id :" + projectId);
        } else {
            throw new Exception("delete fail" + result);
        }
    }

}
