package ch.epfl.chacun.multiplayer;

import ch.epfl.chacun.ActionEncoder;
import ch.epfl.chacun.GameState;
import javafx.application.Platform;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class WebSocketClient implements WebSocket.Listener {
    private final CountDownLatch latch;
    private final Consumer<String> applyAction;

    public WebSocketClient(CountDownLatch latch, Consumer<String> applyAction) {
        this.latch = latch;
        this.applyAction = applyAction;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        System.out.println("onText received " + data);
        latch.countDown();
        Platform.runLater(() -> applyAction.accept(data.toString()));
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("Bad day! " + webSocket.toString());
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("onClose: statusCode=" + statusCode + ", reason=" + reason);
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }
}
