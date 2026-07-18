package com.duk3lo.lumineriabase.presence;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class LauncherBridge {

    private static final Gson GSON = new Gson();
    private static final BlockingQueue<JsonObject> QUEUE = new LinkedBlockingQueue<>();
    private static volatile boolean started = false;
    private static Integer port;
    private static String profileId;

    public static synchronized void start() {
        if (started) return;
        started = true;

        String portEnv = System.getenv("LUMINERIA_IPC_PORT");
        String idEnv = System.getenv("LUMINERIA_PROFILE_ID");
        if (portEnv == null || idEnv == null) {
            return; // no se lanzó desde Lumineria Launcher, no hacemos nada
        }
        port = Integer.parseInt(portEnv);
        profileId = idEnv;

        Thread worker = new Thread(LauncherBridge::workerLoop, "lumineria-launcher-bridge");
        worker.setDaemon(true);
        worker.start();
    }

    public static void sendStatus(int playersOnline, int maxPlayers, String serverName, String serverIconUrl) {
        if (!started || port == null) return;

        JsonObject json = new JsonObject();
        json.addProperty("type", "status_update");
        json.addProperty("profile_id", profileId);
        json.addProperty("players_online", playersOnline);
        json.addProperty("max_players", maxPlayers);
        if (serverName != null) json.addProperty("server_name", serverName);
        if (serverIconUrl != null) json.addProperty("server_icon", serverIconUrl);

        QUEUE.offer(json);
    }

    private static void workerLoop() {
        Socket socket = null;
        BufferedWriter writer = null;

        while (true) {
            try {
                JsonObject json = QUEUE.take();

                if (socket == null || socket.isClosed()) {
                    socket = new Socket("127.0.0.1", port);
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                }

                writer.write(GSON.toJson(json));
                writer.write("\n");
                writer.flush();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException e) {
                // el launcher no está escuchando o se cortó la conexión; se reintenta con el próximo mensaje
                socket = null;
                writer = null;
            }
        }
    }

    private LauncherBridge() {}
}