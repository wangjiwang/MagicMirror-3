package me.weyo.magicmirror.server.controller;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.weyo.magicmirror.server.service.AiService;
import me.weyo.magicmirror.server.service.CommandService;
import me.weyo.magicmirror.server.service.InitService;
import me.weyo.magicmirror.server.service.WebSocketService;
import me.weyo.magicmirror.server.speech.Recorder;

/**
 * @author WeYo
 */
public class InitController extends HttpServlet {

    /** Serial ID */
    private static final long serialVersionUID = 3827530190760653764L;

    private static final Logger LOG = LoggerFactory.getLogger(InitController.class);
    
    private static InitService initService = new InitService();

    public void init() throws ServletException {

        ExecutorService exec = Executors.newSingleThreadExecutor();
        Recorder recorder = new Recorder();
        recorder.registerWebSocketService(new WebSocketService())
                .registerAiService(new AiService())
                .registerCommandService(CommandService.INSTANCE);
        exec.execute(recorder);
        exec.shutdown();
        
        LOG.info("初始化完成");
    }

    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ai = request.getParameter("ai");
        String decodedAi = new String(ai.getBytes("ISO-8859-1"), "utf-8");
        
        try {
            initService.initAi(decodedAi);
        } catch (JSONException e) {
            LOG.error("InitService JSON 解析异常|ai:" + decodedAi, e);
        }
    }
}
